package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.ContactConfirmationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthUserControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    AuthUserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void signinPage() throws Exception {
        mockMvc.perform(get("/signin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div[1]/div/h1").string("Вход"));
    }

    @Test
    void signupPage() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div[1]/div/h1").string("Регистрация"));
    }

    @Test
    void requestContactConfirmation() throws Exception {
        ContactConfirmationDto confirmationDto = ContactConfirmationDto.builder()
                .contact("user1@mail.ru")
                .build();
        mockMvc.perform(post("/requestContactConfirmation")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(confirmationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    void approveContact() throws Exception {
        ContactConfirmationDto confirmationDto = ContactConfirmationDto.builder()
                .contact("user1@mail.ru")
                .code("111111")
                .build();
        mockMvc.perform(post("/requestContactConfirmation")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(confirmationDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    void userRegistration() throws Exception {
        mockMvc.perform(post("/registration")
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Boom")
                        .param("email", "boom@mail.ru")
                        .param("phone", "+37548754547")
                        .param("password", "111111"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div[2]/main/form/div/div[1]/div[1]/label/span/text()").string("Регистрация прошла успешно"));
    }

    @Test
    void login() throws Exception {
        mockMvc.perform(formLogin("/signin").user("user@mail.ru").password("111111"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails("user@mail.ru")
    void my() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/h1").string("Мои книги"));
    }

    @Test
    void unauthorizedAccessToMyPageFailTest() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }

    @Test
    @WithUserDetails("user@mail.ru")
    void profile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/header/div[1]/div/div/div[3]/div/a[4]/span[1]")
                        .string("User One"));
    }
}
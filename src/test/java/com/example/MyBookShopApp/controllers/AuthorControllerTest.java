package com.example.MyBookShopApp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    AuthorControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void authorsPage() throws Exception {
        mockMvc.perform(get("/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/div/div/div[2]/div/div[1]/a")
                        .string("Arlott Anni"));
    }

    @Test
    void authorsSlugPage() throws Exception {
        mockMvc.perform(get("/authors/author-hxp-112"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/h1/text()")
                        .string("Anni Arlott"));
    }

    @Test
    void booksByAuthor() throws Exception {
        mockMvc.perform(get("/books/author/author-hxp-112"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/div[2]/div[2]/strong/a").string("Trauma"));
    }

    @Test
    void bookByAuthorPage() throws Exception {
        mockMvc.perform((get("/books/author/page/author-hxp-112")
                        .param("offset", "1")
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value("24"))
                .andExpect(jsonPath("$.books[0].id").value("126"));
    }
}
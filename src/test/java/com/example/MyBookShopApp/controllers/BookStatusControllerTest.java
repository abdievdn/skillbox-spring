package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.InteractionWithBookDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookStatusControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    BookStatusControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void booksCart() {
    }

    @Test
    void cartPage() {
    }

    @Test
    void postponedPage() {
    }

    @Test
    void changeBookStatus() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .booksIds("book-kim-856")
                .status("KEPT")
                .build();
        mockMvc.perform(post("/books/changeBookStatus")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @WithUserDetails("user@mail.ru")
    void changeBookStatusWithUser() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .booksIds("book-kim-856")
                .status("KEPT")
                .build();
        mockMvc.perform(post("/books/changeBookStatus")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    void removeFromCart() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .booksIds("book-kim-856")
                .status("UNLINK")
                .build();
        mockMvc.perform(post("/books/changeBookStatus/remove/cart")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("CART", "book-kim-856")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    @WithUserDetails("user@mail.ru")
    void removeFromCartWithUser() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .booksIds("book-kim-856")
                .status("UNLINK")
                .build();
        mockMvc.perform(post("/books/changeBookStatus/remove/cart")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("CART", "book-kim-856")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    void removeFromPostponed() throws Exception{
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .booksIds("book-kim-856")
                .status("UNLINK")
                .build();
        mockMvc.perform(post("/books/changeBookStatus/remove/postponed")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("KEPT", "book-kim-8561")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }
}
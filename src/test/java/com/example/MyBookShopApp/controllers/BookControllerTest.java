package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.InteractionWithBookDto;
import com.example.MyBookShopApp.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerTest {

    private final MockMvc mockMvc;
    private final BookService bookService;

    @Autowired
    BookControllerTest(MockMvc mockMvc, BookService bookService) {
        this.mockMvc = mockMvc;
        this.bookService = bookService;
    }

    @Test
    void recommendedBooksPage() throws Exception {
        mockMvc.perform(get("/books/recommended/page")
                        .param("offset", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value("10"))
                .andExpect(jsonPath("$.books[0].slug").value("book-mup-581"));

    }

    @Test
    void recentBooksSlider() throws Exception {
        mockMvc.perform(get("/books/recent/slider")
                        .param("offset", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value("10"))
                .andExpect(jsonPath("$.books[0].slug").value("book-eip-868"));

    }

    @Test
    void RecentBooksFromToPage() throws Exception {
        mockMvc.perform(get("/books/recent/page")
                        .param("from", "01.01.2020")
                        .param("to", "01.01.2021")
                        .param("offset", "0")
                        .param("limit", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value("20"))
                .andExpect(jsonPath("$.books[0].slug").value("book-con-045"));
    }

    @Test
    void recentBooks() throws Exception {
        mockMvc.perform(get("/books/recent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div[1]/div/main/div/div[2]/div[1]/div[2]/strong/a")
                        .string("Nocturno 29"));
    }

    @Test
    void popularBooksPage() throws Exception {
        mockMvc.perform(get("/books/popular/page")
                        .param("offset", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value("10"))
                .andExpect(jsonPath("$.books[0].slug").value("book-vab-276"));
    }

    @Test
    void popularBooks() throws Exception {
        mockMvc.perform(get("/books/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/div/div[2]/div[1]/div[2]/strong/a")
                        .string("Man Of The Moment"));
    }

    @Test
    void bookPage() throws Exception {
        mockMvc.perform(get("/books/book-whw-095"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div/div/main/div/div[1]/div[2]/div[1]/div[1]/a")
                        .string("Houlridge Skipper"));

    }

    @Test
    @WithUserDetails("user@mail.ru")
    void rateBook() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .bookId("book-kim-856")
                .value((short) 5)
                .build();
        mockMvc.perform(post("/books/rateBook")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

    @Test
    void rateBookDenied() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .bookId("book-kim-856")
                .value((short) 5)
                .build();
        mockMvc.perform(post("/books/rateBook")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails("user@mail.ru")
    void bookReview() throws Exception {
        InteractionWithBookDto interactionWithBookDto = InteractionWithBookDto.builder()
                .bookId("book-kim-856")
                .text("fine")
                .build();
        mockMvc.perform(post("/books/bookReview")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(interactionWithBookDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("true"));
    }

}
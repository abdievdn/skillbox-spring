package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SearchServiceTest {

    private final SearchService searchService;

    @MockBean
    private BookRepository bookRepository;

    private BookEntity book1;
    private List<BookEntity> booksList;
    private Page<BookEntity> booksPage;

    @Autowired
    SearchServiceTest(SearchService searchService) {
        this.searchService = searchService;
    }

    @BeforeEach
    void setUp() {
        book1 = BookEntity.builder()
                .id(1)
                .slug("book1")
                .title("Success")
                .authorsLink(new ArrayList<>())
                .tagsLink(new ArrayList<>())
                .genreLink(new Book2GenreEntity(1, book1, new GenreEntity()))
                .isBestseller((short) 1)
                .price(100)
                .discount((short) 5)
                .pubDate(LocalDate.of(2022, 5, 12))
                .build();
        booksList = new ArrayList<>();
        booksList.add(book1);
        booksPage = new PageImpl<>(booksList);
    }

    @AfterEach
    void tearDown() {
        book1 = null;
        booksList = null;
        booksPage = null;
    }

    @Test
    void getPageOfSearchResultBooks() throws CommonErrorException {
        Mockito.when(bookRepository.findAllByTitleContainingIgnoreCase(Mockito.matches("Success"), Mockito.any()))
                .thenReturn(booksPage);
        BooksPageDto booksPageDto = searchService.getPageOfSearchResultBooks("Success", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().size(), 1);
        assertEquals(booksPageDto.getBooks().get(0).getTitle(), "Success");
        assertNotEquals(booksPageDto.getBooks().get(0).getTitle(), "Failed");
    }

    @Test
    void getPageOfGoogleBooksApiSearchResult() {
    }
}
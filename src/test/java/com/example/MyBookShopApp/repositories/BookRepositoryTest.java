package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookRepositoryTest {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void findAllByTitleContainingIgnoreCase() {
        String token = "head";
        Pageable page = PageRequest.of(0, 10);
        Page<BookEntity> bookListByTitleContaining = bookRepository.findAllByTitleContainingIgnoreCase(token, page);
        assertNotNull(bookListByTitleContaining);
        assertFalse(bookListByTitleContaining.isEmpty());
        for (BookEntity book : bookListByTitleContaining) {
            assertThat(book.getTitle()).contains(token);
        }
    }

    @Test
    void findAllByIsBestsellerOrderByPriceAsc() {
        Pageable page = PageRequest.of(0, 10);
        Page<BookEntity> bookIsBestsellerList = bookRepository.findAllByIsBestsellerOrderByPriceAsc((short) 1, page);
        assertNotNull(bookIsBestsellerList);
        assertFalse(bookIsBestsellerList.isEmpty());
        assertThat(bookIsBestsellerList.getSize()).isGreaterThan(1);
    }
}
package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.BookReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BookReviewServiceTest {

    private final BookReviewService bookReviewService;

    @Autowired
    BookReviewServiceTest(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @MockBean
    BookReviewRepository bookReviewRepository;
    @MockBean
    BookService bookService;

    private List<BookReviewEntity> reviewList;
    private BookReviewEntity review1, review2, review3;
    private BookEntity book;

    @BeforeEach
    void setUp() {
        review1 = new BookReviewEntity(new BookEntity(), new UserEntity(), LocalDateTime.now(), "review 1");
        review2 = new BookReviewEntity(new BookEntity(), new UserEntity(), LocalDateTime.now(), "review 2");
        review3 = new BookReviewEntity(new BookEntity(), new UserEntity(), LocalDateTime.now(), "review 3");
        reviewList = List.of(review1, review2, review3);
        book = new BookEntity();
        book.setTitle("Some Title");
        book.setReviews(reviewList);
    }

    @AfterEach
    void tearDown() {
        reviewList = null;
        review1 = review2 = review3 = null;
        book = null;
    }

    @Test
    void getBookReviewList() {
        Mockito
                .doReturn(book)
                .when(bookService)
                .getBookBySlug(Mockito.any());
        List<BookReviewEntity> reviews = bookReviewService.getBookReviewList("slug");
        assertNotNull(reviews);
        assertTrue(
                reviews.contains(review1) &&
                        reviews.contains(review2) &&
                        reviews.contains(review3));
        Mockito.verify(bookService, Mockito.times(1)).getBookBySlug(Mockito.any());
    }

    @Test
    void saveBookReview() {
        bookReviewRepository.save(review1);
        Mockito.verify(bookReviewRepository, Mockito.times(1)).save(Mockito.any());
    }
}
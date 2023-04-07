package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.repositories.BookReviewRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReviewService {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final BookReviewRepository bookReviewRepository;

    public List<BookReviewEntity> getBookReviewList(String slug) {
        return bookService.getBookBySlug(slug).getBook2Reviews();
    }

    public void saveBookReview(String slug, String text) {
                bookReviewRepository.save(new BookReviewEntity(
                        bookService.getBookBySlug(slug),
                        userRepository.findById(1).orElseThrow(),
                        LocalDateTime.now(),
                        text));
    }
}

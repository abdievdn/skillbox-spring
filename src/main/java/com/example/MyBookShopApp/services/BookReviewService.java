package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.repositories.BookReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReviewService {

    private final BookService bookService;
    private final BookReviewRepository bookReviewRepository;
    private final UserService userService;

    @ServiceProcessTrackable
    public List<BookReviewEntity> getBookReviewList(String slug) {
        return bookService.getBookBySlug(slug).getReviews()
                .stream()
                .sorted(Comparator.comparing(BookReviewEntity::getTime).reversed())
                .collect(Collectors.toList());
    }

    @ServiceProcessTrackable
    public void saveBookReview(String slug, String text, Principal principal) {
        bookReviewRepository.save(new BookReviewEntity(
                bookService.getBookBySlug(slug),
                userService.getCurrentUserByPrincipal(principal),
                LocalDateTime.now(),
                text));
    }
}

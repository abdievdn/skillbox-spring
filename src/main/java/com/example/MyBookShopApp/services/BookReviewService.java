package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.user.UserContactEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import com.example.MyBookShopApp.repositories.BookReviewRepository;
import com.example.MyBookShopApp.repositories.UserContactRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookReviewService {

    private final BookService bookService;
    private final UserRepository userRepository;
    private final UserContactRepository contactRepository;
    private final BookReviewRepository bookReviewRepository;

    public List<BookReviewEntity> getBookReviewList(String slug) {
        return bookService.getBookBySlug(slug).getBook2Reviews();
    }

    public void saveBookReview(String slug, String text, Principal principal) {
        UserContactEntity contact = contactRepository.findByContact(principal.getName()).orElse(null);
        UserEntity user = userRepository.findByContactsEquals(contact).orElse(null);
        bookReviewRepository.save(new BookReviewEntity(
                bookService.getBookBySlug(slug),
                user,
                LocalDateTime.now(),
                text));
    }
}

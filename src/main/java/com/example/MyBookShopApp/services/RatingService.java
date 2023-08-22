package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.RatingDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.BookRatingRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final BookRatingRepository bookRatingRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public RatingDto getBookRatingBySlug(BookEntity book) {
        List<BookRatingEntity> bookRatings = book.getRatings();
        return RatingDto.builder()
                .value((short) Math.round(bookRatings
                        .stream()
                        .mapToInt(BookRatingEntity::getValue)
                        .average()
                        .orElse(0)))
                .count(bookRatings.size())
                .values2Count(bookRatings
                        .stream()
                        .collect(Collectors.groupingBy(
                                BookRatingEntity::getValue, collectingAndThen(counting(), Long::intValue)))
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (k, v) -> v,
                                LinkedHashMap::new)))
                .build();
    }

    @ServiceProcessTrackable
    public void saveBookRating(String slug, short value, Principal principal) {
        BookEntity book = bookRepository.findBySlug(slug).orElseThrow();
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        Optional<BookRatingEntity> bookRatingEntity = bookRatingRepository.findByUserAndBook(user, book);
        if (bookRatingEntity.isPresent()) {
            bookRatingEntity.get().setValue(value);
            bookRatingRepository.save(bookRatingEntity.get());
        } else {
            bookRatingRepository.save(new BookRatingEntity(book, user, value));
        }
    }
}

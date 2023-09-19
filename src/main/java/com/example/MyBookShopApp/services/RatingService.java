package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookRatingDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.ReviewLikeDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.errors.EntityNotFoundError;
import com.example.MyBookShopApp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final BookRatingRepository bookRatingRepository;
    private final BookReviewRatingRepository reviewRatingRepository;
    private final BookReviewLikeRepository reviewLikeRepository;
    private final BookReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public BookRatingDto getBookRatingBySlug(BookEntity book) {
        List<BookRatingEntity> bookRatings = book.getRatings();
        return BookRatingDto.builder()
                .value((short) getBookRating(bookRatings))
                .count(bookRatings.size())
                .values2Count(getCountOfRatingValues(bookRatings))
                .build();
    }

    private double getBookRating(List<BookRatingEntity> bookRatings) {
        return bookRatings
                .stream()
                .mapToDouble(BookRatingEntity::getValue)
                .average()
                .orElse(0);
    }

    private HashMap<Short, Integer> getCountOfRatingValues(List<BookRatingEntity> bookRatings) {
        return bookRatings
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
                        HashMap::new));
    }

    @ServiceProcessTrackable
    public ResultDto saveBookRating(String slug, short value, Principal principal) throws EntityNotFoundError {
        BookEntity book = bookRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundError("Книга не найдена!"));
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        Optional<BookRatingEntity> bookRatingEntity = bookRatingRepository.findByUserAndBook(user, book);
        if (bookRatingEntity.isPresent()) {
            bookRatingEntity.get().setValue(value);
            bookRatingRepository.save(bookRatingEntity.get());
        } else {
            bookRatingRepository.save(new BookRatingEntity(book, user, value));
        }
        return new ResultDto(true);
    }

    public List<BookEntity> getBooksByRatingAndCount() {
        Map<BookEntity, Integer> booksRatingMap = new HashMap<>();
        bookRatingRepository.findAll().forEach(r -> {
            if (!booksRatingMap.containsKey(r.getBook())) {
                booksRatingMap.put(r.getBook(), Integer.valueOf(r.getValue()));
            } else {
                booksRatingMap.put(r.getBook(), booksRatingMap.get(r.getBook()) + r.getValue());
            }
        });
        return booksRatingMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(o -> o.getKey().getPubDate())))
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public ResultDto setLikeToReview(ReviewLikeDto likeDto, Principal principal) throws EntityNotFoundError {
        UserEntity currentUser = userService.getCurrentUserByPrincipal(principal);
        BookReviewEntity review = reviewRepository.findById(likeDto.getReviewId())
                .orElseThrow(() -> new EntityNotFoundError("Отзыв не найден!"));
        saveLikeForReview(review, currentUser, likeDto.getValue());
        saveRatingForReview(review);
        return new ResultDto(true);
    }

    private void saveRatingForReview(BookReviewEntity review) {
        BookReviewRatingEntity reviewRating = reviewRatingRepository.findByReview(review)
                .orElse(BookReviewRatingEntity.builder()
                        .review(review)
                        .build());
        reviewRating.setValue((short) getAverageRatingValueForReview(review));
        reviewRatingRepository.save(reviewRating);
    }

    private void saveLikeForReview(BookReviewEntity review, UserEntity user, short value) {
            BookReviewLikeEntity like = reviewLikeRepository.findByReviewAndUser(review, user)
                    .orElse(BookReviewLikeEntity.builder()
                            .review(review)
                            .user(user)
                            .time(LocalDateTime.now())
                            .build());
            like.setValue(value);
            reviewLikeRepository.save(like);
    }

    private int getAverageRatingValueForReview(BookReviewEntity review) {
        double res = (((double) review.getLikesCountByValue((short) 1) /
                (review.getLikesCountByValue((short) 1) + review.getLikesCountByValue((short) -1))) / 2);
        return (int) Math.round(res * 10);
    }
}

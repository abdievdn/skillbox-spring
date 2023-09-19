package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookReviewRatingRepository extends JpaRepository<BookReviewRatingEntity, Integer> {

    Optional<BookReviewRatingEntity> findByReview(BookReviewEntity review);
}

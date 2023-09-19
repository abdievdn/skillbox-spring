package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {
    Optional<BookReviewLikeEntity> findByReviewAndUser(BookReviewEntity review, UserEntity user);
}

package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {
    Optional<BookRatingEntity> findByUserAndBook(UserEntity user, BookEntity bookEntity);
}

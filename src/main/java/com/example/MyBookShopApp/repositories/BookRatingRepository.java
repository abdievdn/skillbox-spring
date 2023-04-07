package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {
    Optional<BookRatingEntity> findByUserAndBook(UserEntity user, BookEntity bookEntity);
}

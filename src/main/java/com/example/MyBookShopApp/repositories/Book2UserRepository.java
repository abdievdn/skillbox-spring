package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    Optional<Book2UserEntity> findByBookAndUser(BookEntity book, UserEntity user);
}

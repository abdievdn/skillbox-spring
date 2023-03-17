package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {
}

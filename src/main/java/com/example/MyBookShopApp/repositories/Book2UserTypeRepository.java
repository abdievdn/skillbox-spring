package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {
    Book2UserTypeEntity findByCode(BookStatus code);
}

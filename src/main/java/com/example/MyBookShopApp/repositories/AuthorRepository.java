package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    Optional<AuthorEntity> findBySlug(String slug);
}

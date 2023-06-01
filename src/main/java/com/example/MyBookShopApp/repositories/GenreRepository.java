package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {

    List<GenreEntity> findAllByParent(GenreEntity parent);

    Optional<GenreEntity> findBySlug(String slug);
}

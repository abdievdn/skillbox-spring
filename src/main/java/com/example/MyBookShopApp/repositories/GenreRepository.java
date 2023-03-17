package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.book.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {

    List<GenreEntity> findAllByParentId(Integer parentId);

    List<GenreEntity> findAllByParent(GenreEntity parent);

    Optional<GenreEntity> findByParentId(Integer parentId);

    Optional<GenreEntity> findBySlug(String slug);

    Integer countAllById(Integer id);

    Integer countGenreEntitiesByParentId(Integer id);
}

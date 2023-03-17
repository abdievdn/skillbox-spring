package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    Optional<TagEntity> findBySlug(String slug);
}

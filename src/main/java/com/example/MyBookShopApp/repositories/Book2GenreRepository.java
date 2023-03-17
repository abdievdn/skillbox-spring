package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Book2GenreRepository extends JpaRepository<Book2GenreEntity, Integer> {

    Integer countAllByGenreId(Integer genreId);

}

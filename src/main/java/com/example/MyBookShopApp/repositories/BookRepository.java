package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    Page<BookEntity> findAllByTitleContainingIgnoreCase(String bookTitle, Pageable page);

    Page<BookEntity> findAllByIsBestsellerOrderByPriceAsc(short isBestseller, Pageable page);

    Page<BookEntity> findAllByPubDateBetweenOrderByPubDateDesc(LocalDate fromDate, LocalDate toDate, Pageable page);

    Page<BookEntity> findAllByOrderByPubDateDesc(Pageable page);

    List<BookEntity> findAllBySlugIn(Collection<String> slug);

    Optional<BookEntity> findBySlug(String slug);
}

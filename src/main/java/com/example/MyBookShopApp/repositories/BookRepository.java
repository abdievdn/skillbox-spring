package com.example.MyBookShopApp.repositories;

import com.example.MyBookShopApp.data.struct.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    List<BookEntity> findAllByTitleContainingIgnoreCase(String bookTitle);

    List<BookEntity> findAllByPriceBetween(Integer min, Integer max);

    List<BookEntity> findAllByPriceIs(Integer price);

    @Query("from BookEntity where isBestseller = 1")
    List<BookEntity> findBestsellers();

    @Query(value = "SELECT * FROM book WHERE discount = (SELECT MAX(discount) FROM book)", nativeQuery = true)
    List<BookEntity> findAllWithMaxDiscount();

    Page<BookEntity> findAllByTitleContainingIgnoreCase(String bookTitle, Pageable page);

    Page<BookEntity> findAllByIsBestsellerOrderByPriceAsc(Short isBestseller, Pageable page);

    Page<BookEntity> findAllByPubDateBetweenOrderByPubDateDesc(LocalDate fromDate, LocalDate toDate, Pageable page);

    Page<BookEntity> findAllByOrderByPubDateDesc(Pageable page);

    List<BookEntity> findAllById(Integer id);

}

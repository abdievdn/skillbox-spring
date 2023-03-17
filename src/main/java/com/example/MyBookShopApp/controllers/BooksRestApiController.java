package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.config.SpringfoxConfig;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.services.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liquibase.pro.packaged.A;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = SpringfoxConfig.BOOK_TAG)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BooksRestApiController {

    private final BookService bookService;

    @ApiOperation("list of books by the content of characters in the title of the book")
    @GetMapping("/books/by-title")
    public ResponseEntity<List<BookEntity>> booksByTitle(@RequestParam("title") String bookTitle) {
        return ResponseEntity.ok(bookService.getBooksByTitle(bookTitle));
    }

    @ApiOperation("list of books by the price")
    @GetMapping("/books/by-price")
    public ResponseEntity<List<BookEntity>> booksByPrice(@RequestParam("price") Integer price) {
        return ResponseEntity.ok(bookService.getBooksWithPrice(price));
    }
    @ApiOperation("list of books by the range of price from min to max")
    @GetMapping("/books/by-price-range")
    public ResponseEntity<List<BookEntity>> booksByTitle(@RequestParam("min") Integer min, @RequestParam("max") Integer max) {
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max));
    }
    @ApiOperation("list of books by the max discount")
    @GetMapping("/books/by-max-discount")
    public ResponseEntity<List<BookEntity>> booksByMaxDiscount() {
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount());
    }

    @ApiOperation("list of books that are bestsellers (bestseller is equals 1)")
    @GetMapping("/books/bestsellers")
    public ResponseEntity<List<BookEntity>> booksBestsellers() {
        return ResponseEntity.ok(bookService.getBestsellers());
    }

}

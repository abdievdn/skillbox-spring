package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;

    @GetMapping({"/books/recommended/page", "/books/recommended/slider"})
    @ResponseBody
    public BooksPageDto recommendedBooks(@RequestParam("offset") Integer offset,
                                               @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfRecommendedBooks(offset, size));
    }

    @GetMapping("/books/recent/slider")
    @ResponseBody
    public BooksPageDto recentBooks(@RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfRecentBooks(offset, size));
    }

    @GetMapping("/books/recent/page")
    @ResponseBody
    public BooksPageDto recentBooks(@RequestParam("from") String from,
                                    @RequestParam("to") String to,
                                    @RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, size));
    }

    @GetMapping("/books/recent")
    public String recentBooks() {
        return "/books/recent";
    }

    @GetMapping({"/books/popular/page", "/books/popular/slider"})
    @ResponseBody
    public BooksPageDto popularBooks(@RequestParam("offset") Integer offset,
                                           @RequestParam("limit") Integer size) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset, size));
    }

    @GetMapping("/books/popular")
    public String popularBooks() {
        return "/books/popular";
    }

    @GetMapping("/postponed")
    public String postponedBooks() {
        return "postponed";
    }
}

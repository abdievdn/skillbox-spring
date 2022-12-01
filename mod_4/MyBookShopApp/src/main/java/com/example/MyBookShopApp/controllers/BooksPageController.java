package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BooksPageController {

    private final BookService bookService;

    @ModelAttribute("booksList")
    public List<Book> booksList() {
        return bookService.getBooksData();
    }

    @GetMapping("/books/recent")
    public String booksRecentPage() {
        return "/books/recent";
    }

    @GetMapping("/books/popular")
    public String booksPopularPage() {
        return "/books/popular";
    }

    @GetMapping("/postponed")
    public String booksPostponed() {
        return "postponed";
    }

}

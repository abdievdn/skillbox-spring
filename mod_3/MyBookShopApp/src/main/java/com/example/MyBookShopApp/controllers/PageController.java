package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

@Controller
public class PageController {

    private final BookService bookService;

    @Autowired
    public PageController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("bookData", bookService.getBooksData());
        return "index";
    }

    @GetMapping("/authors")
    public String authorsPage(Model model) {
        model.addAttribute("authorsData", bookService.getAuthorsData());
        Logger.getLogger(PageController.class.getName()).info(bookService.getAuthorsData().toString());

        return "authors/index";
    }

    @GetMapping("/genres")
    public String genresPage(Model model) {
        return "genres/index";
    }
}

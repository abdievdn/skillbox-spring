package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DefaultController {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_SIZE = 10;

    private final BookService bookService;
    private final TagService tagService;


    @ModelAttribute("recommendedBooks")
    public List<BookDto> recommendedBooksModel() {
        return bookService.getPageOfRecommendedBooks(DEFAULT_OFFSET, DEFAULT_SIZE).getBooks();
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooksModel() {
        return bookService.getPageOfRecentBooks(DEFAULT_OFFSET, DEFAULT_SIZE).getBooks();
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooksModel() {
        return bookService.getPageOfPopularBooks(DEFAULT_OFFSET, DEFAULT_SIZE).getBooks();
    }

    @ModelAttribute("tagsMap")
    public Map<TagEntity, Integer> tagsMapModel() {
        return tagService.getTagsWithValues();
    }

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }
}

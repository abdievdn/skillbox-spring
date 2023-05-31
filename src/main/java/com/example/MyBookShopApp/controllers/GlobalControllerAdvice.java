package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.TagService;
import com.example.MyBookShopApp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final BookService bookService;
    private final TagService tagService;
    private final UserService userService;

    @ModelAttribute("currentUser")
    public UserDto getCurrentUser(Principal principal) {
        return userService.getCurrentUserDto(principal);
    }

    @ModelAttribute("booksList")
    public List<BookEntity> booksListModel() {
        return bookService.getBooksData();
    }

    @ModelAttribute("recommendedBooks")
    public List<BookDto> recommendedBooksModel() {
        return bookService.getPageOfRecommendedBooks(0, 20);
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooksModel() {
        return bookService.getPageOfRecentBooks(0, 20);
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooksModel() {
        return bookService.getPageOfPopularBooks(0, 20);
    }

    @ModelAttribute("tagsList")
    public List<TagEntity> tagsListModel() {
        return tagService.getAllTags();
    }

    @ModelAttribute("tagsMap")
    public Map<TagEntity, Integer> tagsMapModel() {
        return tagService.getTagsWithValues();
    }
}
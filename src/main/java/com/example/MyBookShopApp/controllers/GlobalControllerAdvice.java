package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.NoLogging;
import com.example.MyBookShopApp.controllers.enums.SearchSource;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.TagService;
import com.example.MyBookShopApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@NoLogging
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    public static final int DEFAULT_SLIDER_SIZE = 10;
    public static final int DEFAULT_SLIDER_OFFSET = 0;
    private final BookService bookService;
    private final TagService tagService;
    private final UserService userService;


    @ModelAttribute("searchOptions")
    public List<String> searchOptions() {
        return List.of(SearchSource.BOOKSHOP.toString(),
                SearchSource.GOOGLE.toString());
    }

    @ModelAttribute("currentUser")
    public UserDto getCurrentUser(Principal principal) {
        return userService.getCurrentUserDto(principal);
    }

    @ModelAttribute("recommendedBooks")
    public List<BookDto> recommendedBooksModel() {
        return bookService.getPageOfRecommendedBooks(DEFAULT_SLIDER_OFFSET, DEFAULT_SLIDER_SIZE).getBooks();
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooksModel() {
        return bookService.getPageOfRecentBooks(DEFAULT_SLIDER_OFFSET, DEFAULT_SLIDER_SIZE).getBooks();
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooksModel() {
        return bookService.getPageOfPopularBooks(DEFAULT_SLIDER_OFFSET, DEFAULT_SLIDER_SIZE).getBooks();
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
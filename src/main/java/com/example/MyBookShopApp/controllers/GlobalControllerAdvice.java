package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.genre.GenreEntity;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerAdvice {

    private final BookService bookService;
    private final GenreService genreService;

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordModel() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResults")
    public List<BookEntity> searchResultsModel() {
        return new ArrayList<>();
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

    @ModelAttribute("genresList")
    public List<GenreEntity> genresListModel() {
        return genreService.getGenresData();
    }

    @ModelAttribute("genresBreadcrumbs")
    public List<GenreEntity> genresBreadcrumbsModel() {
        return new ArrayList<>();
    }

    @ModelAttribute("booksByGenreList")
    public List<BookDto> booksByGenreListModel() {
        return new ArrayList<>();
    }

    @ModelAttribute("genreSlug")
    public String genreSlugModel() {
        return "";
    }
}
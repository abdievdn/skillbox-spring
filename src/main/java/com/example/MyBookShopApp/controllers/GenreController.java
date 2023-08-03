package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.aspect.annotations.NoLogging;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final BookService bookService;

    @NoLogging
    @ModelAttribute("genresList")
    public List<GenreEntity> genresList() {
        return genreService.getGenresData();
    }

    @NoLogging
    @ModelAttribute("genresBreadcrumbs")
    public List<GenreEntity> genresBreadcrumbsModel() {
        return new ArrayList<>();
    }

    @NoLogging
    @ModelAttribute("booksByGenreList")
    public List<BookDto> booksByGenreListModel() {
        return new ArrayList<>();
    }

    @GetMapping("/genres")
    public String genresPage() {
        return "/genres/index";
    }

    @ControllerParamsCatch
    @GetMapping("/genres/{slug}")
    public String genresIdPage(@PathVariable(value = "slug", required = false) String slug, Model model) {
        model.addAttribute("booksByGenreList", bookService.getBooksByGenreAndSubGenres(slug, 0, 20));
        model.addAttribute("genreId", slug);
        model.addAttribute("genreBreadcrumbs", genreService.getGenreBreadcrumbs(slug));
        return "/genres/slug";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @ResponseBody
    @GetMapping("/genres/page/{slug}")
    public BooksPageDto genresIdPage(@PathVariable(value = "slug", required = false) String slug,
                                     @RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer size) {
        return bookService.getBooksByGenreAndSubGenres(slug, offset, size);
    }
}

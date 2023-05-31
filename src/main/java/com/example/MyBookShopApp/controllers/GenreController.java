package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
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

    @ModelAttribute("genresList")
    public List<GenreEntity> genresList() {
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

    @GetMapping("/genres")
    public String genresPage() {
        return "/genres/index";
    }

    @GetMapping("/genres/{slug}")
    public String genresIdPage(@PathVariable(value = "slug", required = false) String slug, Model model) {
        model.addAttribute("booksByGenreList", genreService.getBooksByGenreAndSubGenres(slug, 0, 20));
        model.addAttribute("genreId", slug);
        model.addAttribute("genreBreadcrumbs", genreService.getGenreBreadcrumbs(slug));
        return "/genres/slug";
    }

    @ResponseBody
    @GetMapping("/genres/page/{slug}")
    public BooksPageDto genresIdPage(@PathVariable(value = "slug", required = false) String slug,
                                     @RequestParam("offset") Integer offset,
                                     @RequestParam("limit") Integer size) {
        return new BooksPageDto(genreService.getBooksByGenreAndSubGenres(slug, offset, size));
    }
}

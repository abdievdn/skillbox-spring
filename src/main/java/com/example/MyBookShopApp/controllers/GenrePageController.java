package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import com.example.MyBookShopApp.services.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GenrePageController {

    private final GenreService genreService;
    @ModelAttribute("genresList")
    public List<GenreEntity> genresList() {
        return genreService.getGenresData();
    }

    @GetMapping("/genres")
    public String genresPage() {
        return "/genres/index";
    }

    @GetMapping("/genres/{id}")
    public String genresidPage(@PathVariable(value = "id", required = false) Integer id, Model model) {
        model.addAttribute("booksByGenreList", genreService.getBooksByGenreAndSubGenres(id, 0, 20));
        model.addAttribute("genreId", id);
        model.addAttribute("genreBreadcrumbs", genreService.getGenreBreadcrumbs(id));
        return "/genres/slug";
    }

    @GetMapping("/genres/page/{id}")
    @ResponseBody
    public BooksPageDto genresidPage(@PathVariable(value = "id", required = false) Integer id,
                                       @RequestParam("offset") Integer offset,
                                       @RequestParam("limit") Integer size) {
        return new BooksPageDto(genreService.getBooksByGenreAndSubGenres(id, offset, size));
    }
}

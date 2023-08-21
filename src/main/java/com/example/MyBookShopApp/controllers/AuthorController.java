package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.aspect.annotations.NoLogging;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.services.AuthorService;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    private void setModelAttributes(Model model, String slug, int size) {
        model.addAttribute("authorData", authorService.getAuthorData(slug));
        model.addAttribute("authorBooks", bookService.getBooksByAuthor(slug, 0, size));
        model.addAttribute("authorId", slug);
    }

    @NoLogging
    @ModelAttribute("authorsMap")
    private Map<String, List<AuthorEntity>> authorsMap() {
        return authorService.getAuthorsMap();
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "/authors/index";
    }

    @ControllerParamsCatch
    @GetMapping("/authors/{slug}")
    public String authorsSlugPage(@PathVariable(value = "slug", required = false) String slug, Model model) {
        setModelAttributes(model, slug, 10);
        return "/authors/slug";
    }

    @ControllerParamsCatch
    @GetMapping("/books/author/{slug}")
    public String booksByAuthor(@PathVariable(value = "slug", required = false) String slug, Model model) {
        setModelAttributes(model, slug, 20);
        return "/books/author";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/books/author/page/{slug}")
    @ResponseBody
    public BooksPageDto bookByAuthorPage(@PathVariable(value = "slug", required = false) String slug,
                                         @RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer size) {
        return bookService.getBooksByAuthor(slug, offset, size);
    }
}

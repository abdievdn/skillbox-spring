package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.config.SpringfoxConfig;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.struct.author.AuthorEntity;
import com.example.MyBookShopApp.services.AuthorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Api(tags = SpringfoxConfig.AUTHOR_TAG)
public class AuthorPageController {

    private final AuthorService authorService;
    private String id;

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorEntity>> authorsMap() {
        return authorService.getAuthorsMap();
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/authors")
    public String authorsPage() {
        return "/authors/index";
    }

    @GetMapping("/authors/{id}")
    public String authorsidPage(@PathVariable(value = "id", required = false) Integer id, Model model) {
        setModelAttributes(model, id, 10);
        model.addAttribute("booksCount", AuthorService.booksCount);
        return "/authors/slug";
    }

    private void setModelAttributes(Model model, Integer id, int size) {
        model.addAttribute("authorData", authorService.getAuthorData(id));
        model.addAttribute("authorBooks", authorService.getBooksByAuthor(id, 0, size));
        model.addAttribute("authorId", id);
    }

    @GetMapping("/authors/page/{id}")
    @ResponseBody
    public BooksPageDto recentBooks(@PathVariable(value = "id", required = false) Integer id,
                                    @RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer size) {
        return new BooksPageDto(authorService.getBooksByAuthor(id, offset, size));
    }

    @GetMapping("/books/author/{id}")
    public String booksByAuthor(@PathVariable(value = "id", required = false) Integer id, Model model) {
        setModelAttributes(model, id, 20);
        return "/books/author";
    }

    @GetMapping("/books/author/page/{id}")
    @ResponseBody
    public BooksPageDto bookByAuthorPage(@PathVariable(value = "id", required = false) Integer id,
                                         @RequestParam("offset") Integer offset,
                                         @RequestParam("limit") Integer size) {
        return new BooksPageDto(authorService.getBooksByAuthor(id, offset, size));
    }
}

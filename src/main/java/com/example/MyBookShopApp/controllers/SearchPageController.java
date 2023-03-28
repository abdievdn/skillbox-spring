package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchPageController {

    private final BookService bookService;

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) String searchWord, Model model) throws CommonErrorException {
        List<BookDto> searchResultBooks = bookService.getPageOfSearchResultBooks(searchWord, 0, 20);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchResults", searchResultBooks);
        model.addAttribute("booksCount", BookService.booksSearchCount);
        return "/search/index";
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getSearchPage(@RequestParam("offset") Integer offset,
                                      @RequestParam("limit") Integer size,
                                      @PathVariable(value = "searchWord", required = false) String searchWord) throws CommonErrorException {
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWord, offset, size), BookService.booksSearchCount);
    }
}

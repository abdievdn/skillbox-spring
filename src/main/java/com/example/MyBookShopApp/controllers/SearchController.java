package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BookService bookService;

    @ControllerParamsCatch
    @GetMapping(value = {"/search", "/search/{searchWord}/{searchType}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) String searchWord,
                                   @PathVariable(value = "searchType", required = false) String searchType,
                                   Model model) throws CommonErrorException {
        BooksPageDto searchResultBooks = new BooksPageDto();
        if (searchType.equals("default")) {
            searchResultBooks = bookService.getPageOfSearchResultBooks(searchWord, 0, 20);
        } else if (searchType.equals("google")) {
            searchResultBooks = bookService.getPageOfGoogleBooksApiSearchResult(searchWord, 0, 20);
        }
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchResults", searchResultBooks.getBooks());
        model.addAttribute("booksCount", searchResultBooks.getCount());
        return "/search/index";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/search/page/{searchWord}/{searchType}")
    @ResponseBody
    public BooksPageDto getSearchPage(@RequestParam("offset") Integer offset,
                                      @RequestParam("limit") Integer size,
                                      @PathVariable(value = "searchWord", required = false) String searchWord,
                                      @PathVariable(value = "searchType", required = false) String searchType) throws CommonErrorException {
        BooksPageDto searchResultBooks = new BooksPageDto();
        if (searchType.equals("default")) {
            searchResultBooks = bookService.getPageOfSearchResultBooks(searchWord, offset, size);
        } else if (searchType.equals("google")) {
            searchResultBooks = bookService.getPageOfGoogleBooksApiSearchResult(searchWord, offset, size);
        }
        return searchResultBooks;
    }
}

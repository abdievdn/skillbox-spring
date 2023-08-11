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

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchController {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_SIZE = 20;
    private final BookService bookService;

    @ControllerParamsCatch
    @GetMapping(value = {"/search/{searchType}/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) String searchWord,
                                   @PathVariable(value = "searchType") String searchType,
                                   Model model) throws CommonErrorException {
        BooksPageDto searchResultBooks = getBooksPageDto(DEFAULT_OFFSET, DEFAULT_SIZE, searchWord, searchType);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchResults", searchResultBooks.getBooks());
        model.addAttribute("booksCount", searchResultBooks.getCount());
        return "/search/index";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/search/page/{searchType}/{searchWord}")
    @ResponseBody
    public BooksPageDto getSearchPage(@RequestParam("offset") Integer offset,
                                      @RequestParam("limit") Integer size,
                                      @PathVariable(value = "searchWord", required = false) String searchWord,
                                      @PathVariable(value = "searchType", required = false) String searchType) throws CommonErrorException {
        return getBooksPageDto(offset, size, searchWord, searchType);
    }

    private BooksPageDto getBooksPageDto(Integer offset, Integer size, String searchWord, String searchType) throws CommonErrorException {
        BooksPageDto searchResultBooks = new BooksPageDto();
        if (searchType.equals("BookShop")) {
            searchResultBooks = bookService.getPageOfSearchResultBooks(searchWord, offset, size);
        } else if (searchType.equals("Google")) {
            searchResultBooks = bookService.getPageOfGoogleBooksApiSearchResult(searchWord, offset, size);
        }
        return searchResultBooks;
    }
}

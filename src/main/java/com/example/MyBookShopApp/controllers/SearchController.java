package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.controllers.enums.SearchSource;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.services.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchController {

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_SIZE = 20;

    private final SearchService searchService;

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping(value = {"/search/{searchSource}/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) String searchWord,
                                   @PathVariable(value = "searchSource") String searchSource,
                                   Model model) throws CommonErrorException {
        BooksPageDto searchResultBooks = getBooksPageDto(DEFAULT_OFFSET, DEFAULT_SIZE, searchWord, searchSource);
        model.addAttribute("searchSource", searchSource);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchResults", searchResultBooks.getBooks());
        model.addAttribute("booksCount", searchResultBooks.getCount());
        return "/search/index";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @GetMapping("/search/page/{searchSource}/{searchWord}")
    @ResponseBody
    public BooksPageDto getSearchPage(@RequestParam("offset") int offset,
                                      @RequestParam("limit") int size,
                                      @PathVariable(value = "searchWord", required = false) String searchWord,
                                      @PathVariable(value = "searchSource", required = false) String searchSource) throws CommonErrorException {
        return getBooksPageDto(offset, size, searchWord, searchSource);
    }

    private BooksPageDto getBooksPageDto(int offset, int size, String searchWord, String searchSource) throws CommonErrorException {
        BooksPageDto searchResultBooks = new BooksPageDto();
        switch (SearchSource.valueOf(searchSource)) {
            case BOOKSHOP:
                searchResultBooks = searchService.getPageOfSearchResultBooks(searchWord, offset, size);
                break;
            case GOOGLE:
                searchResultBooks = searchService.getPageOfGoogleBooksApiSearchResult(searchWord, offset, size);
                break;
            default:
                break;
        }
        return searchResultBooks;
    }
}

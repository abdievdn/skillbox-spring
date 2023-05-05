package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.ChangeBookStatusDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.services.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookStatusController {

    private final BookService bookService;

    @ModelAttribute(name = "booksStatusList")
    public List<BookDto> booksCart() {
        return new ArrayList<>();
    }

    @GetMapping("/cart")
    public String cartPage(@CookieValue(value = "cart", required = false) String contents,
                           Model model) {
        if (contents != null && !contents.equals("")) {
            model.addAttribute("booksStatusList", bookService.getBooksStatusList(contents));
        }
        return "cart";
    }

    @GetMapping("/postponed")
    public String postponedPage(@CookieValue(value = "postponed", required = false) String contents,
                                Model model) {
        if (contents != null && !contents.equals("")) {
            model.addAttribute("booksStatusList", bookService.getBooksStatusList(contents));
        }
        return "postponed";
    }

    @PostMapping("/changeBookStatus")
    @ResponseBody
    public ResultDto changeBookStatus(@RequestBody ChangeBookStatusDto bookStatusDto,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        bookService.addBookToCookie(bookStatusDto.getStatus(), bookStatusDto.getBooksIds(), request, response);
        return new ResultDto(true);
    }


    @PostMapping("/changeBookStatus/remove/cart")
    @ResponseBody
    public ResultDto removeCart(@RequestBody ChangeBookStatusDto bookStatusDto,
                                @CookieValue(name = "cart", required = false) String contents,
                                HttpServletResponse response, String status) {
        bookService.removeBookFromCookie(bookStatusDto.getBooksIds(), contents, response, "cart");
        return new ResultDto(true);
    }

    @PostMapping("/changeBookStatus/remove/postponed")
    @ResponseBody
    public ResultDto removePostponed(@RequestBody ChangeBookStatusDto bookStatusDto,
                                     @CookieValue(name = "postponed", required = false) String contents,
                                     HttpServletResponse response, String status) {
        bookService.removeBookFromCookie(bookStatusDto.getBooksIds(), contents, response, "postponed");
        return new ResultDto(true);
    }
}

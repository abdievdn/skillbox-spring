package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.aspect.annotations.NoLogging;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.InteractionWithBooksDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.services.BookStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookStatusController {

    private final BookStatusService bookStatusService;

    @NoLogging
    @ModelAttribute(name = "booksStatusList")
    private List<BookDto> getBooksStatusList() {
        return new ArrayList<>();
    }

    @NoLogging
    @ModelAttribute(name = "booksIds")
    private List<String> getBooksIds() {
        return new ArrayList<>();
    }

    @ControllerParamsCatch
    @GetMapping("/cart")
    public String cartPage(@CookieValue(value = "CART", required = false) String contents,
                           Model model, Principal principal) {
        List<BookDto> books = bookStatusService.getBooksStatusList(BookStatus.CART, contents, principal);
        model.addAttribute("booksStatusList", books);
        model.addAttribute("booksIds", bookStatusService.getSlugsString(books));

        return "cart";
    }

    @ControllerParamsCatch
    @GetMapping("/postponed")
    public String postponedPage(@CookieValue(value = "KEPT", required = false) String contents,
                                Model model, Principal principal) {
        List<BookDto> books = bookStatusService.getBooksStatusList(BookStatus.KEPT, contents, principal);
        model.addAttribute("booksStatusList", books);
        model.addAttribute("booksIds", bookStatusService.getSlugsString(books));
        return "postponed";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/changeBookStatus")
    @ResponseBody
    public ResultDto changeBookStatus(@RequestBody InteractionWithBooksDto interaction,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      Principal principal) throws CommonErrorException {
        return bookStatusService.setBooksStatus(
                BookStatus.valueOf(interaction.getStatus()),
                interaction.getBookId(),
                principal, request, response);
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/changeBookStatus/remove/cart")
    @ResponseBody
    public ResultDto removeFromCart(@RequestBody InteractionWithBooksDto interaction,
                                    HttpServletRequest request, HttpServletResponse response, Principal principal) {
        return bookStatusService.removeBooks(interaction.getBookId(), BookStatus.CART.name(), request, response, principal);
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/changeBookStatus/remove/postponed")
    @ResponseBody
    public ResultDto removeFromPostponed(@RequestBody InteractionWithBooksDto interaction,
                                         HttpServletRequest request, HttpServletResponse response, Principal principal) {
        return bookStatusService.removeBooks(interaction.getBookId(), BookStatus.KEPT.name(), request, response, principal);
    }
}

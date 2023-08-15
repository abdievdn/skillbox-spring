package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.aspect.annotations.NoLogging;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.InteractionWithBookDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
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
@RequestMapping("/books")
public class BookStatusController {

    private final BookStatusService bookStatusService;

    @NoLogging
    @ModelAttribute(name = "booksStatusList")
    private List<BookDto> booksCart() {
        return new ArrayList<>();
    }

    @ControllerParamsCatch
    @GetMapping("/cart")
    public String cartPage(@CookieValue(value = "CART", required = false) String contents,
                           Model model, Principal principal) {
        model.addAttribute("booksStatusList", bookStatusService.getBooksStatusList(BookStatus.CART, contents, principal));
        return "cart";
    }

    @ControllerParamsCatch
    @GetMapping("/postponed")
    public String postponedPage(@CookieValue(value = "KEPT", required = false) String contents,
                                Model model, Principal principal) {
        model.addAttribute("booksStatusList", bookStatusService.getBooksStatusList(BookStatus.KEPT, contents, principal));
        return "postponed";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/changeBookStatus")
    @ResponseBody
    public ResultDto changeBookStatus(@RequestBody InteractionWithBookDto interaction,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      Principal principal) {
        String status = interaction.getStatus();
        String slug = interaction.getBooksIds();
        bookStatusService.addBookStatus(BookStatus.valueOf(status), slug, principal, request, response);
        return new ResultDto(true);
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/changeBookStatus/remove/cart")
    @ResponseBody
    public ResultDto removeFromCart(@RequestBody InteractionWithBookDto interaction,
                                    @CookieValue(name = "CART", required = false) String contents,
                                    HttpServletResponse response, Principal principal) {
        bookStatusService.removeBook(interaction.getBooksIds(), BookStatus.CART.name(), contents, response, principal);
        return new ResultDto(true);
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PostMapping("/changeBookStatus/remove/postponed")
    @ResponseBody
    public ResultDto removeFromPostponed(@RequestBody InteractionWithBookDto interaction,
                                         @CookieValue(name = "KEPT", required = false) String contents,
                                         HttpServletResponse response, Principal principal) {
        bookStatusService.removeBook(interaction.getBooksIds(), BookStatus.KEPT.name(), contents, response, principal);
        return new ResultDto(true);
    }
}

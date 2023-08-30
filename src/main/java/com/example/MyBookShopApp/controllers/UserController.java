package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.TransactionsPageDto;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.services.BookService;
import com.example.MyBookShopApp.services.PaymentService;
import com.example.MyBookShopApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final BookService bookService;
    private final PaymentService paymentService;
    private final UserService userService;

    @ModelAttribute("transactionsList")
    public TransactionsPageDto transactionListModel(Principal principal) {
        return paymentService.getTransactionsList(
                DefaultController.DEFAULT_OFFSET, DefaultController.DEFAULT_SIZE, principal);
    }

    private void userBooksModel(Model model, Principal principal, boolean isArchived) {
        model.addAttribute("userBooks", bookService.getPageOfCurrentUserBooks(
                principal, DefaultController.DEFAULT_OFFSET, DefaultController.DEFAULT_SIZE, isArchived));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my")
    public String myBooks(Principal principal, Model model) {
        userBooksModel(model, principal, false);
        return "my";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my/page")
    @ResponseBody
    public BooksPageDto myBooksPage(@RequestParam("offset") int offset,
                                    @RequestParam("limit") int size,
                                    Principal principal) {
        return bookService.getPageOfCurrentUserBooks(
                principal, offset, size, false);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myarchive")
    public String myArchiveBooks(Principal principal, Model model) {
        userBooksModel(model, principal, true);
        return "myarchive";
    }


    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myarchive/page")
    @ResponseBody
    public BooksPageDto myArchiveBooksPage(@RequestParam("offset") int offset,
                                           @RequestParam("limit") int size,
                                           Principal principal) {
        return bookService.getPageOfCurrentUserBooks(
                principal, offset, size, true);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @ControllerParamsCatch
    @ControllerResponseCatch
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/transactions")
    @ResponseBody
    public TransactionsPageDto getTransactionsPage(@RequestParam("offset") int offset,
                                                   @RequestParam("limit") int size,
                                                   Principal principal) {
        return paymentService.getTransactionsList(offset, size, principal);
    }

    @ControllerParamsCatch
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/profile/change")
    public String profileChange(UserDto userDto, Model model, Principal principal) {
        model.addAttribute("result", userService.changeProfileData(userDto, principal));
        return "/profile";
    }
}

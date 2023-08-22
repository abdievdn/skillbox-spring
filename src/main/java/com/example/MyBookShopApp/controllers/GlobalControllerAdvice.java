package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.NoLogging;
import com.example.MyBookShopApp.controllers.enums.SearchSource;
import com.example.MyBookShopApp.data.dto.UserDto;
import com.example.MyBookShopApp.services.BookStatusService;
import com.example.MyBookShopApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@NoLogging
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;
    private final BookStatusService bookStatusService;

    @ModelAttribute("searchOptions")
    public List<String> searchOptions() {
        return List.of(SearchSource.BOOKSHOP.toString(),
                SearchSource.GOOGLE.toString());
    }

    @ModelAttribute("currentUser")
    public UserDto getCurrentUser(Principal principal) {
        return userService.getCurrentUserDto(principal);
    }

    @ModelAttribute("cartBooksCount")
    public int cartBooksCount(@CookieValue(value = "CART", required = false) String contents, Principal principal) {
        return bookStatusService.getCountOfCartBooks(contents, principal);
    }

    @ModelAttribute("keptBooksCount")
    public int keptBooksCount(@CookieValue(value = "KEPT", required = false) String contents, Principal principal) {
        return bookStatusService.getCountOfPostponedBooks(contents, principal);
    }

    @ModelAttribute("userBooksCount")
    public int userBooksCount(Principal principal) {
        return bookStatusService.getCountOfAllUserBooks(principal);
    }
}
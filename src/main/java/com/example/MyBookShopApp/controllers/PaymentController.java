package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.payment.PaymentDto;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.services.BookStatusService;
import com.example.MyBookShopApp.services.PaymentService;
import com.example.MyBookShopApp.services.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final BookStatusService bookStatusService;
    private final PaymentService paymentService;


    @ControllerResponseCatch
    @ControllerParamsCatch
    @GetMapping("/books/pay")
    public void createPayment(@CookieValue(value = "CART", required = false) String contents,
                            Principal principal,
                            HttpServletResponse response) throws IOException {
        List<BookDto> bookDtoList = bookStatusService.getBooksStatusList(BookStatus.CART, contents, principal);
        ResponseEntity<PaymentDto> responseEntity = paymentService.getPaymentTransaction(bookDtoList);
        response.sendRedirect(Objects.requireNonNull(responseEntity.getBody()).getConfirmation().getConfirmationUrl());
    }

    @GetMapping("/books/cart/paid")
    public void getSuccessPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieUtil.deleteCookieByName(request, response, "CART", "/books");
        response.sendRedirect("/books/cart");
    }
}

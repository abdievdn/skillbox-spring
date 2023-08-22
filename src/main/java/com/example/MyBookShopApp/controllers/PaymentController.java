package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.aspect.annotations.ControllerParamsCatch;
import com.example.MyBookShopApp.aspect.annotations.ControllerResponseCatch;
import com.example.MyBookShopApp.data.dto.PaymentDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.yookassa.YooPaymentDto;
import com.example.MyBookShopApp.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @ControllerResponseCatch
    @ControllerParamsCatch
    @ResponseBody
    @PostMapping("/payment")
    public ResultDto balancePayment(@RequestBody PaymentDto paymentDto, Principal principal) {
        ResponseEntity<YooPaymentDto> paymentEntity = paymentService.postBalancePaymentTransaction(paymentDto);
        if (paymentEntity.getBody() != null) {
            YooPaymentDto yooPaymentDto = paymentEntity.getBody();
            String paymentURL = Objects.requireNonNull(yooPaymentDto.getConfirmation().getConfirmationUrl());
            if (!paymentURL.isEmpty()) {
                new Thread(() -> {
                    try {
                        paymentService.checkPaymentStatus(yooPaymentDto, principal);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                return new ResultDto(true, paymentURL, "");
            }
        }
        return new ResultDto("Ошибка при запросе на пополнение баланса.");
    }
}

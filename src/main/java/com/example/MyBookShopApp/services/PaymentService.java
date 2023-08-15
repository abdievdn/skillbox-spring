package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.payment.AmountDto;
import com.example.MyBookShopApp.data.dto.payment.ConfirmationDto;
import com.example.MyBookShopApp.data.dto.payment.PaymentDto;
import com.example.MyBookShopApp.data.dto.payment.PaymentMethodDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    public static final String PAYMENT_URL = "https://api.yookassa.ru/v3/payments";

    private final RestTemplate restTemplate;

    @Value("${yookassa.shop.ID}")
    private String shopId;

    @Value("${yookassa.shop.KEY}")
    private String shopKey;

    private HttpEntity<PaymentDto> getPaymentEntity(List<BookDto> cartBooks) {
        int paymentSumTotal = cartBooks.stream().mapToInt(BookDto::getDiscountPrice).sum();
        PaymentDto payment = PaymentDto.builder()
                .amount(AmountDto.builder()
                        .currency("RUB")
                        .value(Integer.toString(paymentSumTotal))
                        .build())
                .paymentMethodData(PaymentMethodDataDto.builder()
                        .type("bank_card")
                        .build())
                .confirmation(ConfirmationDto.builder()
                        .type("redirect")
                        .returnUrl("http://localhost:8085/books/cart/paid")
                        .build())
                .capture(true)
                .description("Платеж на сумму: " + paymentSumTotal)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(shopId, shopKey);
        headers.add("Idempotence-Key", UUID.randomUUID().toString());
        return new HttpEntity<>(payment, headers);
    }

    @ServiceProcessTrackable
    public ResponseEntity<PaymentDto> getPaymentTransaction(List<BookDto> bookDtoList) {
        HttpEntity<PaymentDto> paymentDtoHttpEntity = getPaymentEntity(bookDtoList);
        return restTemplate.exchange(
                PAYMENT_URL,
                HttpMethod.POST,
                paymentDtoHttpEntity,
                PaymentDto.class);
    }
}

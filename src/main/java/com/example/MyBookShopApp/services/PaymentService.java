package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.PaymentDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.dto.TransactionDto;
import com.example.MyBookShopApp.data.dto.TransactionsPageDto;
import com.example.MyBookShopApp.data.dto.yookassa.YooAmountDto;
import com.example.MyBookShopApp.data.dto.yookassa.YooConfirmationDto;
import com.example.MyBookShopApp.data.dto.yookassa.YooPaymentDto;
import com.example.MyBookShopApp.data.dto.yookassa.YooPaymentMethodDataDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.payments.BalanceTransactionEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${yookassa.shop.URL}")
    public String paymentUrl;

    private final UserService userService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final BalanceTransactionRepository balanceTransactionRepository;
    private final RestTemplate restTemplate;

    @Value("${yookassa.shop.ID}")
    private String shopId;

    @Value("${yookassa.shop.KEY}")
    private String shopKey;

    private HttpEntity<YooPaymentDto> getPaymentEntity(PaymentDto paymentDto) {
        YooPaymentDto payment = YooPaymentDto.builder()
                .amount(YooAmountDto.builder()
                        .currency("RUB")
                        .value(paymentDto.getSum())
                        .build())
                .paymentMethodData(YooPaymentMethodDataDto.builder()
                        .type("bank_card")
                        .build())
                .confirmation(YooConfirmationDto.builder()
                        .type("redirect")
                        .returnUrl("http://localhost:8085/profile")
                        .build())
                .description("Пополнение баланса " +
                        paymentDto.getTime() +
                        " на сумму: " + paymentDto.getSum())
                .capture(true)
                .build();
        HttpHeaders headers = getHttpHeaders();
        return new HttpEntity<>(payment, headers);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(shopId, shopKey);
        headers.add("Idempotence-Key", UUID.randomUUID().toString());
        return headers;
    }

    public ResponseEntity<YooPaymentDto> postBalancePaymentTransaction(PaymentDto paymentDto) {
        HttpEntity<YooPaymentDto> paymentDtoHttpEntity = getPaymentEntity(paymentDto);
        return restTemplate.exchange(
                paymentUrl,
                HttpMethod.POST,
                paymentDtoHttpEntity,
                YooPaymentDto.class);
    }

    @Async
    public void checkPaymentStatus(YooPaymentDto yooPaymentDto, Principal principal) throws InterruptedException {
        Boolean isPaid = false;
        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(5);
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        while (!isPaid) {
            if (LocalDateTime.now().isAfter(expiredTime)) {
                log.info("PAYMENT expired");
                break;
            }
            isPaid = getPaymentStatus(yooPaymentDto.getId());
            Thread.sleep(1000);
        }
        if (isPaid) {
            double sum = Double.parseDouble(yooPaymentDto.getAmount().getValue());
            saveUserBalance(user, (int) sum);
            log.info("PAYMENT true");
        }
    }

    private Boolean getPaymentStatus(String paymentId) {
        ResponseEntity<YooPaymentDto> responseEntity = restTemplate.exchange(
                paymentUrl + "/" + paymentId,
                HttpMethod.GET,
                new HttpEntity<>(getHttpHeaders()),
                YooPaymentDto.class
        );
        if (responseEntity.getBody() != null) {
            return responseEntity.getBody().getPaid();
        } else {
            return false;
        }
    }

    private void saveUserBalance(UserEntity user, int sum) {
        if (user != null) {
            user.setBalance(user.getBalance() + sum);
            userRepository.save(user);
            BalanceTransactionEntity balanceTransaction = buildBalanceTransactionEntity(user.getId(), 0, sum, "Пополнение баланса.");
            balanceTransactionRepository.save(balanceTransaction);
        }
    }

    public ResultDto postBooksPaymentTransaction(List<String> slugs, UserEntity user) throws CommonErrorException {
        for (String slug : slugs) {
            BookEntity book = bookRepository.findBySlug(slug)
                    .orElseThrow(() -> new CommonErrorException("Неизвестная ошибка."));
            if (checkBooksPayment(book, user)) {
                Optional<Book2UserEntity> book2User = book2UserRepository.findByBookAndUser(book, user);
                if (book2User.isPresent()) {
                    Book2UserEntity book2UserEntity = book2User.get();
                    book2UserEntity.setType(book2UserTypeRepository.findByCode(BookStatus.PAID));
                    balanceTransactionRepository.save(buildBalanceTransactionEntity(
                            book2UserEntity.getUser().getId(),
                            book2UserEntity.getBook().getId(),
                            -book2UserEntity.getBook().getDiscountPrice(),
                            "Покупка книги: " + book2UserEntity.getBook().getTitle()));
                    book2UserRepository.save(book2UserEntity);
                }
            } else {
                return new ResultDto(true, "Недостаточно средств. Пополните баланс.", "");
            }
        }
        return new ResultDto(true);
    }

    private Boolean checkBooksPayment(BookEntity book, UserEntity user) {
        int totalPrice = book.getDiscountPrice();
        int userBalance = user.getBalance();
        if (totalPrice <= userBalance) {
            user.setBalance(userBalance - totalPrice);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    private BalanceTransactionEntity buildBalanceTransactionEntity(int userId, int bookId, int sum, String description) {
        return BalanceTransactionEntity.builder()
                .time(LocalDateTime.now())
                .userId(userId)
                .bookId(bookId)
                .value(sum)
                .description(description)
                .build();
    }

    public TransactionsPageDto getTransactionsList(int offset, int size, Principal principal) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        Page<BalanceTransactionEntity> transactions =
                balanceTransactionRepository.findAllByUserIdOrderByTimeDesc(user.getId(), PageRequest.of(offset, size));
        return TransactionsPageDto.builder()
                .count((int) transactions.getTotalElements())
                .transactions(getTransactionsDtoList(transactions.getContent()))
                .build();
    }

    private List<TransactionDto> getTransactionsDtoList(List<BalanceTransactionEntity> transactions) {
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        transactions.forEach(t -> transactionDtoList.add(TransactionDto.builder()
                .time(Timestamp.valueOf(t.getTime()))
                .value(t.getValue())
                .description(t.getDescription())
                .build()));
        return transactionDtoList;
    }
}

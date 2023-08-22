package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String hash;
    private Timestamp time;
    private String sum;
    private Boolean paymentStatus;
}

package com.example.MyBookShopApp.data.google.api.books;

import lombok.Data;

@Data
public class RetailPrice {
    public double amount;
    public String currencyCode;
    public int amountInMicros;
}

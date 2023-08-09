package com.example.MyBookShopApp.data.google.api.books;

import lombok.Data;

@Data
public class ListPrice {
    public int amount;
    public String currencyCode;
    public int amountInMicros;
}

package com.example.MyBookShopApp.data.google.api.books;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SaleInfo {
    public String country;
    public String saleability;
    public boolean isEbook;
    public ListPrice listPrice;
    public RetailPrice retailPrice;
    public String buyLink;
    public ArrayList<Offer> offers;
}

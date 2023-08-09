package com.example.MyBookShopApp.data.google.api.books;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Root {
    public String kind;
    public int totalItems;
    public ArrayList<Item> items;
}

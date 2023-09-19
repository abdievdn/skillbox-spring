package com.example.MyBookShopApp.data.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BookRatingDto {
    short value;
    int count;
    Map<Short, Integer> valuesForCount;
}

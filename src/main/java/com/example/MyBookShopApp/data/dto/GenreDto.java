package com.example.MyBookShopApp.data.dto;

import lombok.Data;

@Data
public class GenreDto {
    private int id;
    private String name;
    private String slug;
    private GenreDto parent;
}

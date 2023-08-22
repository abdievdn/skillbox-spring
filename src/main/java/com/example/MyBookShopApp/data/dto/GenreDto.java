package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {

    private int id;
    private String name;
    private String slug;
    private GenreDto parent;
}

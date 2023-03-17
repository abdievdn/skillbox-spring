package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Integer id;
    private String slug;
    private String image;
    private List<String> authors;
    private String title;
    private Short discount;
    private Boolean isBestseller;
    private Integer rating;
    private String status;
    private Integer price;
    private Integer discountPrice;
}

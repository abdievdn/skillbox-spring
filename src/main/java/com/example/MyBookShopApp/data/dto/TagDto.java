package com.example.MyBookShopApp.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagDto {
    private Integer id;
    private String name;
    private String slug;
}

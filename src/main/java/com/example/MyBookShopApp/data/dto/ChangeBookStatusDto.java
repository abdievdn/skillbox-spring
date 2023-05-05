package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBookStatusDto {
    private String booksIds;
    private String booksId;
    private String status;
}

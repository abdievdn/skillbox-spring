package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionWithBookDto {
    private String booksIds;
    private String booksId;
    private String status;
    private String bookId;
    private String text;
    private Short value;
}

package com.example.MyBookShopApp.data.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionWithBooksDto {
    private String booksId;
    private String status;
    private String bookId;
    private String text;
    private short value;
}

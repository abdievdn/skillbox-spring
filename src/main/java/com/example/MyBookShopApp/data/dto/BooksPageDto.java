package com.example.MyBookShopApp.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class BooksPageDto {

    private Long count;
    private List<BookDto> books;

    public BooksPageDto(List<BookDto> books) {
        this.books = books;
        this.count = (long) books.size();
    }

    public BooksPageDto(List<BookDto> books, Long count) {
        this.books = books;
        this.count = count;
    }
}

package com.example.MyBookShopApp.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "Books Page DTO", description = "List of limited page of books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooksPageDto {

    @Schema(description = "Count of books")
    private long count;
    @Schema(description = "List of books")
    private List<BookDto> books;

    public BooksPageDto(List<BookDto> books) {
        this.books = books;
        this.count = books.size();
    }
}

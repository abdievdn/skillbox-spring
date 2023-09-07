package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.data.entity.book.file.BookFileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(name = "Book DTO", description = "Book's properties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private int id;
    @Schema(description = "Book's identify code")
    private String slug;
    @Schema(description = "Book's cover picture")
    private String image;
    @Schema(description = "List of authors for book")
    private List<AuthorDto> authors;
    @Schema(description = "List of tagss for book")
    private List<TagDto> tags;
    @Schema(description = "Title of book")
    private String title;
    @Schema(description = "Description of book")
    private String description;
    @Schema(description = "Book's genre")
    private GenreDto genre;
    @Schema(description = "Discount for book")
    private short discount;
    @Schema(description = "Is book is bestseller - 'true' or 'false'")
    private Boolean isBestseller;
    @Schema(description = "Rating for book from 0 to 5 stars")
    private RatingDto rating;
    @Schema(description = "Status of book", nullable = true)
    private String status;
    @Schema(description = "Price for book")
    private int price;
    @Schema(description = "Discount price of book")
    private int discountPrice;
    @Schema(description = "List of download links for book")
    private List<BookFileEntity> files;
}

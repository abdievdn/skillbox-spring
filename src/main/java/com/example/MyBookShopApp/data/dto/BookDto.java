package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.data.entity.book.file.BookFileEntity;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private Integer id;
    private String slug;
    private String image;
    private List<AuthorDto> authors;
    private List<TagDto> tags;
    private String title;
    private String description;
    private GenreDto genre;
    private Short discount;
    private Boolean isBestseller;
    private RatingDto rating;
    private String status;
    private Integer price;
    private Integer discountPrice;
    private List<BookFileEntity> files;
}

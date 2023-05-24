package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.file.BookFileEntity;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
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
    private List<AuthorEntity> authors;
    private List<TagEntity> tags;
    private String title;
    private String description;
    private GenreEntity genre;
    private Short discount;
    private Boolean isBestseller;
    private RatingDto rating;
    private String status;
    private Integer price;
    private Integer discountPrice;
    private List<BookFileEntity> files;
}

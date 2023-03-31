package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.data.struct.author.AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
    private Short rating;
    private String status;
    private Integer price;
    private Integer discountPrice;
    private List<BookFileEntity> files;
}

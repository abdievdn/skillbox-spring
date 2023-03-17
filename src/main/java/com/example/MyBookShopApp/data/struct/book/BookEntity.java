package com.example.MyBookShopApp.data.struct.book;

import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "entity represents a book")
@Data
@Entity
@Table(name = "book")
public class BookEntity {

    @ApiModelProperty("auto generated id by db")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "SMALLINT")
    private Short discount;

    @ApiModelProperty("if isBestseller = 1 the book is considered to be bestseller and else isBestseller = 0")
    @Column(name = "is_bestseller", columnDefinition = "SMALLINT")
    private Short isBestseller;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ApiModelProperty("publication date of the book")
    @Column(name = "pub_date", columnDefinition = "DATE")
    private LocalDate pubDate;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    private Integer price;

    private String image;

    @ApiModelProperty("mnemonic identity - the sequence of characters")
    private String slug;

    @OneToMany(mappedBy = "book")
    private List<Book2AuthorEntity> book2authors = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Book2GenreEntity> book2genres = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Book2UserEntity> book2users = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Book2TagEntity> book2tags = new ArrayList<>();
}

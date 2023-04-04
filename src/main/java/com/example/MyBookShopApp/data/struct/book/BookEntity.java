package com.example.MyBookShopApp.data.struct.book;

import com.example.MyBookShopApp.data.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "entity represents a book")
@Getter
@Setter
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
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @OneToMany(mappedBy = "book2Author")
    private List<Book2AuthorEntity> book2Authors = new ArrayList<>();

    @OneToOne(mappedBy = "book2Genre")
    private Book2GenreEntity genre2Book;

    @OneToMany(mappedBy = "book2User")
    private List<Book2UserEntity> book2Users = new ArrayList<>();

    @OneToMany(mappedBy = "book2Tag")
    private List<Book2TagEntity> book2Tags = new ArrayList<>();

    public Integer discountPrice() {
        return price - (price * discount / 100);
    }

    @OneToMany(mappedBy = "book2File")
    private List<BookFileEntity> bookFileList = new ArrayList<>();

    @OneToMany(mappedBy = "book2Rating")
    private List<BookRatingEntity> book2Ratings = new ArrayList<>();

    @OneToMany(mappedBy = "book2Review")
    private List<BookReviewEntity> book2Reviews = new ArrayList<>();

    public Short getCommonRatingValue() {
        return (short) Math.round(book2Ratings
                .stream()
                .mapToInt(BookRatingEntity::getValue)
                .average()
                .orElse(0));
    }
}

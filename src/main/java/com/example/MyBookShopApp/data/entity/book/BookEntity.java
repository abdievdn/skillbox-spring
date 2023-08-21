package com.example.MyBookShopApp.data.entity.book;

import com.example.MyBookShopApp.data.entity.book.file.BookFileEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "SMALLINT")
    private Short discount;

    @Column(name = "is_bestseller", columnDefinition = "SMALLINT")
    private Short isBestseller;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "pub_date", columnDefinition = "DATE")
    private LocalDate pubDate;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    private Integer price;

    private String image;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @OneToMany(mappedBy = "book")
    private List<Book2AuthorEntity> authorsLink = new ArrayList<>();

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, optional = false)
    private Book2GenreEntity genreLink;

    @OneToMany(mappedBy = "book")
    private List<Book2UserEntity> usersLink = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Book2TagEntity> tagsLink = new ArrayList<>();

    public Integer discountPrice() {
        return price - (price * discount / 100);
    }

    @OneToMany(mappedBy = "book")
    private List<BookFileEntity> bookFileList = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<BookRatingEntity> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<BookReviewEntity> reviews = new ArrayList<>();
}

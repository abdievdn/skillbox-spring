package com.example.MyBookShopApp.data.struct.user;

import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.book.review.BookReviewLikeEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private int balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @OneToMany(mappedBy = "user")
    private List<Book2UserEntity> user2books = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookRatingEntity> valuesForBooks = new ArrayList<>();

    @OneToMany(mappedBy = "userRating")
    private List<BookReviewRatingEntity> userRatingValues = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> userLikeValues = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<UserContactEntity> contacts = new ArrayList<>();
}

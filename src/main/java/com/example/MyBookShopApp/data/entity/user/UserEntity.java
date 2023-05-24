package com.example.MyBookShopApp.data.entity.user;

import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewLikeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

package com.example.MyBookShopApp.data.entity.user;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.entity.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.security.jwt.JWTBlacklistEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private List<Book2UserEntity> booksLink = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookRatingEntity> bookRatings = new ArrayList<>();

    @OneToMany(mappedBy = "userRating")
    private List<BookReviewRatingEntity> bookReviewRatings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> bookReviewLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> bookReviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserContactEntity> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<JWTBlacklistEntity> jwtList = new ArrayList<>();
}

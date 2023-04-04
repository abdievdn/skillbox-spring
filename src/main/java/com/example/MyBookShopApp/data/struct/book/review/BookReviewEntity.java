package com.example.MyBookShopApp.data.struct.book.review;

import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "book_review")
public class BookReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", columnDefinition = "INT NOT NULL")
    private BookEntity book2Review;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private UserEntity userReview;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @OneToMany(mappedBy = "reviewRating")
    private List<BookReviewRatingEntity> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "reviewLike")
    private List<BookReviewLikeEntity> likes = new ArrayList<>();

    public Integer getCommonRatingValue() {
        return (int) Math.round(ratings
                .stream()
                .mapToInt(BookReviewRatingEntity::getValue)
                .average()
                .orElse(0));
    }
}

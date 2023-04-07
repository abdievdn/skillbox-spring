package com.example.MyBookShopApp.data.struct.book.review;

import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookReviewRatingEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "book_review")
public class BookReviewEntity {

    public BookReviewEntity(BookEntity book, UserEntity user, LocalDateTime time, String text) {
        this.book = book;
        this.user = user;
        this.time = time;
        this.text = text;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", columnDefinition = "INT NOT NULL")
    private BookEntity book;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private UserEntity user;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @OneToMany(mappedBy = "reviewRating")
    private List<BookReviewRatingEntity> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "review")
    private List<BookReviewLikeEntity> likes = new ArrayList<>();

    public Integer getCommonRatingValue() {
        return (int) Math.round(ratings
                .stream()
                .mapToInt(BookReviewRatingEntity::getValue)
                .average()
                .orElse(0));
    }

    public Integer getLikesCountByValue(Short value) {
        return  (int) likes
                .stream()
                .filter(l -> l.getValue() == value)
                .count();
    }
}

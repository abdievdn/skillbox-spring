package com.example.MyBookShopApp.data.struct.book.rating;

import com.example.MyBookShopApp.data.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "book_review_rating")
public class BookReviewRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Short value;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", columnDefinition = "INT NOT NULL")
    private BookReviewEntity reviewRating;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" ,columnDefinition = "INT NOT NULL")
    private UserEntity userRating;
}

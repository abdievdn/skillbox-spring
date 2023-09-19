package com.example.MyBookShopApp.data.entity.book.rating;

import com.example.MyBookShopApp.data.entity.book.review.BookReviewEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_review_rating")
public class BookReviewRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private short value;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", columnDefinition = "INT NOT NULL")
    private BookReviewEntity review;
}

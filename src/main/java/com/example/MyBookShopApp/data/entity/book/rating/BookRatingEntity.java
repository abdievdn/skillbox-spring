package com.example.MyBookShopApp.data.entity.book.rating;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_rating")
public class BookRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Short value;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", columnDefinition = "INT NOT NULL")
    private BookEntity book;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" ,columnDefinition = "INT NOT NULL")
    private UserEntity user;

    public BookRatingEntity(BookEntity book, UserEntity user, short value) {
        this.book = book;
        this.user = user;
        this.value = value;
    };
}

package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.genre.GenreEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "book2genre")
public class Book2GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", columnDefinition = "INT NOT NULL")
    private BookEntity book2Genre;

    @ManyToOne
    @JoinColumn(name = "genre_id", columnDefinition = "INT NOT NULL")
    private GenreEntity genre;
}

package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.struct.author.AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "book2author")
public class Book2AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", columnDefinition = "INT NOT NULL")
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", columnDefinition = "INT NOT NULL")
    private AuthorEntity author;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private int sortIndex;

}

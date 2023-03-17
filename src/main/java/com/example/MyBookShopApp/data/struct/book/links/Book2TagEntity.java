package com.example.MyBookShopApp.data.struct.book.links;

import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "book2tag")
public class Book2TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", columnDefinition = "INT NOT NULL")
    private BookEntity book;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", columnDefinition = "INT NOT NULL")
    private TagEntity tag;
}

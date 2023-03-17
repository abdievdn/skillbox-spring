package com.example.MyBookShopApp.data.struct.book.genre;

import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "genre")
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Transient
    private Integer booksCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", columnDefinition = "INT")
    private GenreEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GenreEntity> genres = new ArrayList<>();

    @OneToMany(mappedBy = "genre")
    private List<Book2GenreEntity> genre2books = new ArrayList<>();
}

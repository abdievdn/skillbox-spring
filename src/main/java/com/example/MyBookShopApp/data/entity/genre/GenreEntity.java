package com.example.MyBookShopApp.data.entity.genre;

import com.example.MyBookShopApp.data.entity.book.links.Book2GenreEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private List<Book2GenreEntity> booksLink = new ArrayList<>();
}

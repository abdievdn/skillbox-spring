package com.example.MyBookShopApp.data.entity.book.file;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "book_file")
public class BookFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", referencedColumnName = "id", columnDefinition = "INT NOT NULL")
    private BookFileTypeEntity type;

    private String path;

    private String hash;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity book;

    @Override
    public String toString() {
        return type.getName();
    }
}

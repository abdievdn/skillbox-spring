package com.example.MyBookShopApp.data.entity.book.file;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "book_file_type")
public class BookFileTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToOne(mappedBy = "type")
    private BookFileEntity bookFile;
}

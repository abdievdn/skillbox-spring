package com.example.MyBookShopApp.data.struct.book.file;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "book_file")
public class BookFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type_id")
    private Integer typeId;

    private String path;

    private String hash;
}

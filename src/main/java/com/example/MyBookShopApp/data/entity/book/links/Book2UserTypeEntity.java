package com.example.MyBookShopApp.data.entity.book.links;

import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "book2user_type")
public class Book2UserTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private BookStatus code;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @OneToMany(mappedBy = "type")
    List<Book2UserEntity> books;
}

package com.example.MyBookShopApp.data.entity.tag;

import com.example.MyBookShopApp.data.entity.book.links.Book2TagEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "tag")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "tag")
    private List<Book2TagEntity> booksLink = new ArrayList<>();
}

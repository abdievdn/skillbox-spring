package com.example.MyBookShopApp.data.entity.author;

import com.example.MyBookShopApp.data.entity.book.links.Book2AuthorEntity;
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
@Table(name = "author")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "first_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "VARCHAR(255) NOT NULL")
    private String lastName;

    private String photo;

    private String slug;

    @OneToMany(mappedBy = "author")
    private List<Book2AuthorEntity> booksLink = new ArrayList<>();

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}

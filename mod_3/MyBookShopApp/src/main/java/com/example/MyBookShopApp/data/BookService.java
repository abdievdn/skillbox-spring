package com.example.MyBookShopApp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.expression.Lists;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> getBooksData() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setPrice(rs.getString("price"));
            book.setPriceOld(rs.getString("priceOld"));
            return book;
        });
        return new ArrayList<>(books);
    }

    public List<AuthorsByLetter> getAuthorsData() {
        List<String> authors = jdbcTemplate.query("SELECT author FROM books",
                (ResultSet rs, int rowNum) -> rs.getString("author"));
        return authors
                .stream()
                .collect(Collectors.groupingBy(a -> a.charAt(0)))
                .entrySet()
                .stream()
                .map(entry -> new AuthorsByLetter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}

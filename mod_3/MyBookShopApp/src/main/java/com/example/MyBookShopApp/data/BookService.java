package com.example.MyBookShopApp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
        List<AuthorsByLetter> authorsByLetters = new ArrayList<>();
        List<String> authors = jdbcTemplate.query("SELECT author FROM books",
                (ResultSet rs, int rowNum) -> rs.getString("author"));
        Collections.sort(authors);
        authors.forEach(a -> {
            String letter = a.substring(0, 1);
            if (authorsByLetters == null) {
                addAuthor(authorsByLetters, a, letter);
            }
            boolean isLetterFind = false;
            for (AuthorsByLetter l : authorsByLetters) {
                if (l.getLetter().equals(letter)) {
                    l.getAuthors().add(a);
                    isLetterFind = true;
                    break;
                }
            }
            if (isLetterFind == false) {
                addAuthor(authorsByLetters, a, letter);
            }
        });
        return authorsByLetters;
    }

    private void addAuthor(List<AuthorsByLetter> authorsByLetters, String a, String letter) {
        AuthorsByLetter newAuthors = new AuthorsByLetter();
        newAuthors.setAuthors(new ArrayList<>());
        newAuthors.getAuthors().add(a);
        newAuthors.setLetter(letter);
        authorsByLetters.add(newAuthors);
    }
}

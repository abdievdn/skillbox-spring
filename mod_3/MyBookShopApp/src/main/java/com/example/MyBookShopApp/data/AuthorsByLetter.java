package com.example.MyBookShopApp.data;

import java.util.List;

public class AuthorsByLetter {
    private String letter;
    private List<String> authors;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "AuthorsByLetter{" +
                "letter='" + letter + '\'' +
                ", authors=" + authors +
                '}';
    }
}

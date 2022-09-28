package com.example.MyBookShopApp.data;

import java.util.List;

public class AuthorsByLetter {
    private Character letter;
    private List<String> authors;

    public Character getLetter() {
        return letter;
    }

    public void setLetter(Character letter) {
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

    public AuthorsByLetter(Character letter, List<String> authors) {
        this.letter = letter;
        this.authors = authors;
    }
}

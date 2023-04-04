package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.struct.author.AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookService bookService;
    public static Integer booksCount;

    public Map<String, List<AuthorEntity>> getAuthorsMap() {
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy(a -> a.getLastName().substring(0, 1)));
    }

    public AuthorEntity getAuthorData(Integer id) {
        return authorRepository.findById(id).orElseThrow();
    }

    public List<BookDto> getBooksByAuthor(Integer id, Integer offset, Integer size) {
        AuthorEntity author = getAuthorData(id);
        List<BookEntity> authorBooks = new ArrayList<>();
        for (Book2AuthorEntity a : author.getAuthor2books()) {
            authorBooks.add(a.getBook2Author());
        }
        AuthorService.booksCount = authorBooks.size();
        return bookService.getBooksPage(offset, size, authorBooks);
    }
}

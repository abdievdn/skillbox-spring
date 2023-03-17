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

    public AuthorEntity getAuthorData(String slug) {
        return authorRepository.findBySlug(slug).orElseGet(AuthorEntity::new);
    }

    public List<BookDto> getBooksByAuthor(String slug, Integer offset, Integer size) {
        AuthorEntity author = getAuthorData(slug);
        List<BookEntity> authorBooks = new ArrayList<>();
        for (Book2AuthorEntity a : author.getAuthor2books()) {
            authorBooks.add(a.getBook());
        }
        AuthorService.booksCount = authorBooks.size();
        return bookService.getBooksPage(offset, size, authorBooks);
    }

//    public List<String> getAuthorNamesByBook(BookEntity book) {
//        List<String> authors = new ArrayList<>();
//        for (Book2AuthorEntity a : book2AuthorRepository.findAllByBookId(book.getId())) {
//            Optional<AuthorEntity> author = authorRepository.findById(a.getAuthorId());
//            if (author.isPresent()) {
//                String authorName = author.get().getFirstName() + " " + author.get().getLastName();
//                authors.add(authorName);
//            }
//        }
//        return authors;
//    }
}

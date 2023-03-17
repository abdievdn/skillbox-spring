package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.struct.book.genre.GenreEntity;
import com.example.MyBookShopApp.repositories.Book2GenreRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.GenreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final Book2GenreRepository book2GenreRepository;
    private final BookService bookService;

    public List<BookDto> getBooksByGenreAndSubGenres(String slug, Integer offset, Integer size) {
        List<BookEntity> booksByGenre = getBooksByGenre(getGenreEntity(slug));
        return bookService.getBooksPage(offset, size, booksByGenre);
    }

    private GenreEntity getGenreEntity(String slug) {
        GenreEntity genre = genreRepository.findBySlug(slug).orElseGet(GenreEntity::new);
        return genre;
    }

    private List<BookEntity> getBooksByGenre(GenreEntity genre) {
        List<BookEntity> books = new ArrayList<>();
        for (Book2GenreEntity b : genre.getGenre2books()) {
            books.add(bookRepository.findById(b.getBook().getId()).orElseThrow());
        }
        if (!genre.getGenres().isEmpty()) {
            for (GenreEntity g : genre.getGenres()) {
                books.addAll(getBooksByGenre(g));
            }
        }
        return books;
    }

    public List<GenreEntity> getGenreBreadcrumbs(String slug) {
        GenreEntity genre = getGenreEntity(slug);
        List<GenreEntity> breadcrumbs = new ArrayList<>();
        while (true) {
            breadcrumbs.add(0, genre);
            if (genre.getParent() == null) {
                break;
            }
            genre = genre.getParent();
        }
        return breadcrumbs;
    }

    public List<GenreEntity> getGenresData() {
        List<GenreEntity> genres = genreRepository.findAllByParentId(null);
        setBooksCountToGenre(genres);
        return genres;
    }

    private void setBooksCountToGenre(List<GenreEntity> genres) {
        Integer count = 0;
        for (GenreEntity g : genres) {
            count = book2GenreRepository.countAllByGenreId(g.getId());
            g.setBooksCount(count);
            if (!g.getGenres().isEmpty()) {
                setBooksCountToGenre(g.getGenres());
                for (GenreEntity c : g.getGenres()) {
                    g.setBooksCount(g.getBooksCount() + c.getBooksCount());
                }
            }
        }
    }
}


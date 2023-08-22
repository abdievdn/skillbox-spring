package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.GenreDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.repositories.Book2GenreRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final Book2GenreRepository book2GenreRepository;

    private GenreEntity getGenreEntity(String slug) {
        return genreRepository.findBySlug(slug).orElseGet(GenreEntity::new);
    }

    @ServiceProcessTrackable
    public List<BookEntity> getBooksByGenre(String slug) {
        GenreEntity genre = getGenreEntity(slug);
        List<BookEntity> books = new ArrayList<>();
        for (Book2GenreEntity b : genre.getBooksLink()) {
            books.add(bookRepository.findById(b.getBook().getId()).orElseThrow());
        }
        if (hasChildren(genre)) {
            for (GenreEntity g : genre.getGenres()) {
                books.addAll(getBooksByGenre(g.getSlug()));
            }
        }
        return books;
    }

    @ServiceProcessTrackable
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

    @ServiceProcessTrackable
    public List<GenreEntity> getGenresData() {
        List<GenreEntity> genres = genreRepository.findAllByParent(null);
        setBooksCountToGenre(genres);
        return genres;
    }

    public GenreDto getGenreDto(GenreEntity genre) {
        return GenreDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .slug(genre.getSlug())
                .build();
    }

    private void setBooksCountToGenre(List<GenreEntity> genres) {
        int count;
        for (GenreEntity g : genres) {
            count = book2GenreRepository.countAllByGenreId(g.getId());
            g.setBooksCount(count);
            if (hasChildren(g)) {
                setBooksCountToGenre(g.getGenres());
                for (GenreEntity c : g.getGenres()) {
                    g.setBooksCount(g.getBooksCount() + c.getBooksCount());
                }
            }
        }
    }

    private boolean hasChildren(GenreEntity genre) {
        return genre.getGenres() != null && !genre.getGenres().isEmpty();
    }
}


package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.GenreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenreServiceTest {

    private final GenreService genreService;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    BookRepository bookRepository;

    private GenreEntity genre, genreChild;

    @Autowired
    GenreServiceTest(GenreService genreService) {
        this.genreService = genreService;
    }

    @BeforeEach
    void setUp() {
        genre = GenreEntity.builder()
                .id(1)
                .slug("genre")
                .name("genre")
                .parent(null)
                .build();
        genreChild = GenreEntity.builder()
                .id(2)
                .slug("genre-child")
                .name("genre-child")
                .parent(genre)
                .build();
        genre.setGenres(List.of(genreChild));
    }

    @AfterEach
    void tearDown() {
        genre = null;
        genreChild = null;
    }

    @Test
    void getBooksByGenre() {
        Book2GenreEntity book2GenreEntity = new Book2GenreEntity();
        BookEntity book = BookEntity.builder()
                .id(1)
                .slug("book")
                .genreLink(book2GenreEntity)
                .build();
        book2GenreEntity = Book2GenreEntity.builder()
                .id(1)
                .book(book)
                .genre(genre)
                .build();
        genre.setBooksLink(List.of(book2GenreEntity));
        Mockito.doReturn(Optional.of(genre))
                .when(genreRepository)
                .findBySlug("genre");
        Mockito.doReturn(Optional.of(book))
                .when(bookRepository)
                .findById(1);
        List<BookEntity> books = genreService.getBooksByGenre("genre");
        assertNotNull(books);
        assertEquals(books.size(), 1);
        assertEquals(books.get(0).getSlug(), "book");

    }

    @Test
    void getGenreBreadcrumbs() {
        Mockito.doReturn(Optional.of(genreChild))
                .when(genreRepository)
                .findBySlug("genre-child");
        List<GenreEntity> genres = genreService.getGenreBreadcrumbs("genre-child");
        assertNotNull(genres);
        assertEquals(genres.size(), 2);
        assertEquals(genres.get(0).getSlug(), "genre");
        assertEquals(genres.get(1).getSlug(), "genre-child");
    }

    @Test
    void getGenresData() {
        Mockito.doReturn(List.of(genre))
                .when(genreRepository)
                .findAllByParent(null);
        List<GenreEntity> genres = genreService.getGenresData();
        assertNotNull(genres);
        assertNull(genres.get(0).getParent());
        assertEquals(genres.get(0).getGenres().get(0).getParent(), genre);
    }
}
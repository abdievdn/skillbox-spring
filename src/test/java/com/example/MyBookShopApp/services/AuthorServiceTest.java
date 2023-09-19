package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.errors.EntityNotFoundError;
import com.example.MyBookShopApp.repositories.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthorServiceTest {

    private final AuthorService authorService;

    @Autowired
    AuthorServiceTest(AuthorService authorService) {
        this.authorService = authorService;
    }

    @MockBean
    private AuthorRepository authorRepository;

    private List<AuthorEntity> authors;
    private AuthorEntity author1, author2, author3, author4, author5;
    private List<Book2AuthorEntity> book2AuthorEntityList;
    private BookEntity book1, book2, book3;

    @BeforeEach
    void setUp() {
        book1 = new BookEntity();
        book1.setTitle("Some");
        book1.setGenreLink(new Book2GenreEntity(1, book1, new GenreEntity()));
        book1.setIsBestseller((short) 1);
        book1.setPrice(123);
        book1.setDiscount((short) 20);
        book2 = new BookEntity();
        book2.setTitle("Good Time");
        book2.setGenreLink(new Book2GenreEntity(2, book2, new GenreEntity()));
        book2.setIsBestseller((short) 0);
        book2.setPrice(231);
        book2.setDiscount((short) 10);
        book3 = new BookEntity();
        book3.setTitle("Forever");
        book2AuthorEntityList = List.of(
                new Book2AuthorEntity(1, book1, author1, 1),
                new Book2AuthorEntity(2, book2, author1, 1));
        author1 = new AuthorEntity(1, "", "Adam", "Borne", "", "1ab", book2AuthorEntityList);
        author2 = new AuthorEntity(2, "", "James", "Corn", "", "2jc", new ArrayList<>());
        author3 = new AuthorEntity(3, "", "Rebecca", "Smith", "", "3rs", new ArrayList<>());
        author4 = new AuthorEntity(4, "", "Jackie", "Braun", "", "4jb", new ArrayList<>());
        author5 = new AuthorEntity(5, "", "Philip", "Morris", "", "5pm", new ArrayList<>());
        authors = List.of(author1, author2, author3, author4, author5);
    }

    @AfterEach
    void tearDown() {
        authors = null;
        author1 = author2 = author3 = author4 = author5 = null;
        book1 = book2 = book3 = null;
        book2AuthorEntityList = null;
    }

    @Test
    void getAuthorsMap() {
        Mockito.doReturn(authors)
                .when(authorRepository)
                .findAll();
        Map<String, List<AuthorEntity>> authorsMap = authorService.getAuthorsMap();
        List<AuthorEntity> listOfEntitiesFromAuthorMapValues = new ArrayList<>();
        authorsMap.values().forEach(listOfEntitiesFromAuthorMapValues::addAll);

        assertNotNull(authorsMap);
        assertTrue(listOfEntitiesFromAuthorMapValues.contains(author1));
        assertTrue(listOfEntitiesFromAuthorMapValues.contains(author2));
        assertTrue(listOfEntitiesFromAuthorMapValues.contains(author3));
        assertTrue(listOfEntitiesFromAuthorMapValues.contains(author4));
        assertTrue(listOfEntitiesFromAuthorMapValues.contains(author5));
        Mockito.verify(authorRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getAuthorData() throws EntityNotFoundError {
        Mockito.doReturn(Optional.of(author1))
                .when(authorRepository)
                .findBySlug("1ab");
        AuthorEntity author = authorService.getAuthorEntity("1ab");
        assertNotNull(author);
        assertEquals(author, author1);
        Mockito.verify(authorRepository, Mockito.times(1)).findBySlug(Mockito.any());
    }


    @Test
    void getAuthorDto() {
    }
}
package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.RatingDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.*;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceTest {

    private final BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private Book2UserRepository book2UserRepository;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private GenreRepository genreRepository;
    @MockBean
    private RatingService ratingService;

    private BookEntity book1, book2;
    private List<BookEntity> booksList;
    private Page<BookEntity> booksPage;

    @Autowired
    BookServiceTest(BookService bookService) {
        this.bookService = bookService;
    }

    @BeforeEach
    void setUp() {
        book1 = BookEntity.builder()
                .id(1)
                .slug("book1")
                .title("Success")
                .authorsLink(new ArrayList<>())
                .tagsLink(new ArrayList<>())
                .genreLink(new Book2GenreEntity(1, book1, new GenreEntity()))
                .isBestseller((short) 1)
                .price(100)
                .discount((short) 5)
                .pubDate(LocalDate.of(2022, 5, 12))
                .build();
        book2 = BookEntity.builder()
                .id(2)
                .slug("book2")
                .title("Another")
                .authorsLink(new ArrayList<>())
                .tagsLink(new ArrayList<>())
                .genreLink(new Book2GenreEntity(2, book2, new GenreEntity()))
                .isBestseller((short) 1)
                .price(300)
                .discount((short) 0)
                .pubDate(LocalDate.of(2020, 5, 12))
                .build();
        booksList = new ArrayList<>();
        booksList.add(book1);
        booksPage = new PageImpl<>(booksList);
    }

    @AfterEach
    void tearDown() {
        book1 = null;
        book2 = null;
        booksList = null;
        booksPage = null;
    }

    @Test
    void getBookBySlug() {
        Mockito.doReturn(Optional.of(book1))
                .when(bookRepository)
                .findBySlug("book1");
        BookEntity book = bookService.getBookBySlug("book1");
        assertNotNull(book);
        assertEquals(book, this.book1);
        assertEquals(book.getTitle(), "Success");
    }

    @Test
    void getBookDtoBySlug() {
        Mockito.doReturn(Optional.of(book1))
                .when(bookRepository)
                .findBySlug("book1");
        Mockito.doReturn(new RatingDto())
                .when(ratingService)
                .getBookRatingBySlug(Mockito.any());
        BookDto bookDto = bookService.getBookDtoBySlug("book1");
        assertNotNull(bookDto);
        assertEquals(bookDto.getId(), 1);
        assertEquals(bookDto.getSlug(), "book1");
        assertEquals(bookDto.getTitle(), "Success");
    }

    @Test
    void getPageOfSearchResultBooks() throws CommonErrorException {
        Mockito.when(bookRepository.findAllByTitleContainingIgnoreCase(Mockito.matches("Success"), Mockito.any()))
                .thenReturn(booksPage);
        BooksPageDto booksPageDto = bookService.getPageOfSearchResultBooks("Success", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().size(), 1);
        assertEquals(booksPageDto.getBooks().get(0).getTitle(), "Success");
        assertNotEquals(booksPageDto.getBooks().get(0).getTitle(), "Failed");
    }

    @Test
    void getPageOfRecommendedBooks() {
        Mockito.when(bookRepository.findAllByIsBestsellerOrderByPriceAsc(Mockito.eq((short) 1), Mockito.any()))
                .thenReturn(booksPage);
        BooksPageDto booksPageDto = bookService.getPageOfRecommendedBooks(0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getIsBestseller(), book1.getIsBestseller() == 1);
    }

    @Test
    void getPageOfRecentBooks() {
        Mockito.when(bookRepository.findAllByOrderByPubDateDesc(Mockito.any()))
                .thenReturn(booksPage);
        Mockito.when(bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(booksPage);
        BooksPageDto booksPageDto = bookService.getPageOfRecentBooks(0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getId(), 1);
        booksPageDto = bookService.getPageOfRecentBooks("01.01.2022", "01.01.2023", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getId(), 1);
    }

    @Test
    void getPageOfPopularBooks() {
        Book2UserEntity book2User1 = Book2UserEntity.builder()
                .id(1)
                .book(book1)
                .type(new Book2UserTypeEntity(1, BookStatus.PAID, BookStatus.PAID.name(), new ArrayList<>()))
                .build();
        Book2UserEntity book2User2 = Book2UserEntity.builder()
                .id(2)
                .book(book2)
                .type(new Book2UserTypeEntity(2, BookStatus.CART, BookStatus.CART.name(), new ArrayList<>()))
                .build();
        List<Book2UserEntity> book2UserEntityList = new ArrayList<>();
        book2UserEntityList.add(book2User1);
        book2UserEntityList.add(book2User2);
        Mockito.doReturn(book2UserEntityList)
                .when(book2UserRepository)
                .findAll();
        Mockito.when(bookRepository.findById(1))
                .thenReturn(Optional.of(book1));
        Mockito.when(bookRepository.findById(2))
                .thenReturn(Optional.of(book2));
        BooksPageDto booksPageDto = bookService.getPageOfPopularBooks(0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getId(), 1);
        assertEquals(booksPageDto.getBooks().get(1).getId(), 2);
    }

    @Test
    void getBooksByAuthor() {
        List<Book2AuthorEntity> book2AuthorEntityList = new ArrayList<>();
        AuthorEntity author = AuthorEntity.builder()
                .id(1)
                .slug("author")
                .booksLink(book2AuthorEntityList)
                .build();
        book2AuthorEntityList.add(new Book2AuthorEntity(1, book1, author, 0));
        Mockito.when(authorRepository.findBySlug("author"))
                .thenReturn(Optional.of(author));
        BooksPageDto booksPageDto = bookService.getBooksByAuthor("author", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), "book1");
        assertEquals(booksPageDto.getCount(), 1);
    }

    @Test
    void getBooksByTag() {
        List<Book2TagEntity> book2TagEntityList = new ArrayList<>();
        TagEntity tag = TagEntity.builder()
                .id(1)
                .slug("tag")
                .booksLink(book2TagEntityList)
                .build();
        book2TagEntityList.add(new Book2TagEntity(1, book1, tag));
        Mockito.when(tagRepository.findById(1))
                .thenReturn(Optional.of(tag));
        BooksPageDto booksPageDto = bookService.getBooksByTag(1, 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getTitle(), "Success");
    }

    @Test
    void getBooksByGenreAndSubGenres() {
        List<Book2GenreEntity> book2GenreEntityList = new ArrayList<>();
        GenreEntity genre = GenreEntity.builder()
                .id(1)
                .slug("genre")
                .parent(null)
                .booksLink(book2GenreEntityList)
                .build();
        book2GenreEntityList.add(new Book2GenreEntity(1, book1, genre));
        Mockito.when(genreRepository.findBySlug("genre"))
                .thenReturn(Optional.of(genre));
        Mockito.when(bookRepository.findById(1))
                .thenReturn(Optional.of(book1));
        BooksPageDto booksPageDto = bookService.getBooksByGenreAndSubGenres("genre", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getTitle(), "Success");
    }
}
package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.*;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.errors.EntityNotFoundError;
import com.example.MyBookShopApp.repositories.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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
    private BookRatingRepository ratingRepository;

    @MockBean
    private UserRepository userRepository;

    private String slug1, slug2;
    private BookEntity book1, book2;
    private List<BookEntity> booksList;
    private Page<BookEntity> booksPage;
    private List<Book2UserEntity> book2UserEntityList;
    Principal principal;
    UserEntity user;

    @Autowired
    BookServiceTest(BookService bookService) {
        this.bookService = bookService;
    }

    @BeforeEach
    void setUp() {
        slug1 = "book1";
        slug2 = "book2";
        book1 = BookEntity.builder()
                .id(1)
                .slug(slug1)
                .authorsLink(new ArrayList<>())
                .tagsLink(new ArrayList<>())
                .genreLink(new Book2GenreEntity(1, book1, new GenreEntity()))
                .isBestseller((short) 1)
                .price(100)
                .discount((short) 5)
                .pubDate(LocalDate.of(2022, 5, 12))
                .ratings(List.of(BookRatingEntity.builder()
                        .value((short) 5)
                        .build()))
                .build();
        book2 = BookEntity.builder()
                .id(2)
                .slug(slug2)
                .authorsLink(new ArrayList<>())
                .tagsLink(new ArrayList<>())
                .genreLink(new Book2GenreEntity(2, book2, new GenreEntity()))
                .isBestseller((short) 0)
                .price(300)
                .discount((short) 0)
                .pubDate(LocalDate.of(2020, 5, 12))
                .ratings(new ArrayList<>())
                .build();
        booksList = List.of(book1, book2);
        booksPage = new PageImpl<>(booksList);
        book2UserEntityList = List.of(
                Book2UserEntity.builder()
                        .book(book1)
                        .type(Book2UserTypeEntity.builder()
                                .code(BookStatus.PAID)
                                .build())
                        .build(),
                Book2UserEntity.builder()
                        .book(book2)
                        .type(Book2UserTypeEntity.builder()
                                .code(BookStatus.ARCHIVED)
                                .build())
                        .build());
        principal = () -> "1";
        user = UserEntity.builder()
                .id(1)
                .password("111")
                .booksLink(book2UserEntityList)
                .build();
    }

    @AfterEach
    void tearDown() {
        book1 = null;
        book2 = null;
        booksList = null;
        booksPage = null;
        slug1 = slug2 = null;
        book2UserEntityList = null;
        user = null;
        principal = null;
    }

    @Test
    void getBookBySlug() {
        Mockito.doReturn(Optional.of(book1))
                .when(bookRepository)
                .findBySlug(slug1);
        BookEntity book = bookService.getBookBySlug(slug1);
        assertNotNull(book);
        assertEquals(book, this.book1);
        assertEquals(book.getSlug(), slug1);
    }

    @Test
    void getBooksBySlugs() {
        String[] slugs = {slug1, slug2};
        Mockito.doReturn(booksList)
                .when(bookRepository)
                .findAllBySlugIn(List.of(slugs));
        List<BookEntity> booksListBySlugs = bookService.getBooksBySlugs(slugs);
        assertNotNull(booksListBySlugs);
        assertFalse(booksListBySlugs.isEmpty());
        assertEquals(booksListBySlugs.get(1).getSlug(), slug2);
    }

    @Test
    void getBookDtoBySlug() {
        Mockito.doReturn(Optional.of(book2))
                .when(bookRepository)
                .findBySlug(slug2);
        BookDto bookDto = bookService.getBookDtoBySlug(slug2, null);
        assertNotNull(bookDto);
        assertEquals(bookDto.getSlug(), slug2);
        assertEquals(bookDto.getPrice(), 300);
        assertNotEquals(bookDto.getSlug(), slug1);

    }

    @Test
    void saveBookToUser() {
        bookService.saveBookToUser(BookStatus.VIEWED, book1, new UserEntity());
        Mockito.verify(book2UserRepository, Mockito.times(1)).save(Mockito.any(Book2UserEntity.class));
    }

    @Test
    void getBookDtoListFromBookEntityPage() {
        List<BookDto> bookDtoList = bookService.getBookDtoListFromBookEntityPage(booksPage);
        assertNotNull(bookDtoList);
        assertFalse(bookDtoList.isEmpty());
        assertEquals(bookDtoList.get(0).getSlug(), book1.getSlug());
        assertEquals(bookDtoList.get(1).getSlug(), book2.getSlug());
    }

    @Test
    void getPageOfRecommendedBooks() {
        Mockito.doReturn(List.of(BookRatingEntity.builder()
                                .book(book1)
                                .value((short) 5)
                                .build(),
                        BookRatingEntity.builder()
                                .book(book2)
                                .value((short) 1)
                                .build()))
                .when(ratingRepository)
                .findAll();
        BooksPageDto booksPageDto = bookService.getPageOfRecommendedBooks(0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getIsBestseller(), true);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), slug1);
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
    void getPageOfBooksByAuthor() throws EntityNotFoundError {
        List<Book2AuthorEntity> book2AuthorEntityList = new ArrayList<>();
        AuthorEntity author = AuthorEntity.builder()
                .id(1)
                .slug("author")
                .booksLink(book2AuthorEntityList)
                .build();
        book2AuthorEntityList.add(new Book2AuthorEntity(1, book1, author, 0));
        Mockito.when(authorRepository.findBySlug("author"))
                .thenReturn(Optional.of(author));
        BooksPageDto booksPageDto = bookService.getPageOfBooksByAuthor("author", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), slug1);
        assertEquals(booksPageDto.getCount(), 1);
    }

    @Test
    void getPageOfBooksByTag() {
        List<Book2TagEntity> book2TagEntityList = new ArrayList<>();
        TagEntity tag = TagEntity.builder()
                .id(1)
                .slug("tag")
                .booksLink(book2TagEntityList)
                .build();
        book2TagEntityList.add(new Book2TagEntity(1, book1, tag));
        Mockito.when(tagRepository.findById(1))
                .thenReturn(Optional.of(tag));
        BooksPageDto booksPageDto = bookService.getPageOfBooksByTag(1, 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), slug1);
    }

    @Test
    void getPageOfBooksByGenreAndSubGenres() {
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
        BooksPageDto booksPageDto = bookService.getPageOfBooksByGenreAndSubGenres("genre", 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), slug1);
    }

    @Test
    void getPageOfCurrentUserBooks() {
        Mockito.doReturn(Optional.of(user))
                .when(userRepository)
                .findById(1);
        BooksPageDto booksPageDto = bookService.getPageOfCurrentUserBooks(principal, 0, 10, false);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().size(), 1);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), slug1);
    }

    @Test
    void getPageOfCurrentUserHistoryBooks() {
        Mockito.doReturn(Optional.of(user))
                .when(userRepository)
                .findById(1);
        BooksPageDto booksPageDto = bookService.getPageOfCurrentUserHistoryBooks(principal, 0, 10);
        assertNotNull(booksPageDto);
        assertEquals(booksPageDto.getBooks().size(), 1);
        assertEquals(booksPageDto.getBooks().get(0).getSlug(), slug1);
    }

    @Test
    void bookEntityListToBookDtoList() {
        List<BookDto> bookDtoList = bookService.bookEntityListToBookDtoList(booksList);
        assertNotNull(bookDtoList);
        assertEquals(bookDtoList.size(), 2);
        assertEquals(bookDtoList.get(1).getSlug(), slug2);
    }
}
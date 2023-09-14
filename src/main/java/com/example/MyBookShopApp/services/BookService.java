package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.Book2UserTypeRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final RatingService ratingService;
    private final UserService userService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final GenreService genreService;

    public BookEntity getBookBySlug(String slug) {
        return bookRepository.findBySlug(slug).orElse(null);
    }

    public List<BookEntity> getBooksBySlugs(String[] slugs) {
        return bookRepository.findAllBySlugIn(List.of(slugs));
    }

    public BookDto getBookDtoBySlug(String slug, Principal principal) {
        UserEntity currentUser = userService.getCurrentUserByPrincipal(principal);
        BookEntity book = getBookBySlug(slug);
        if (currentUser != null) {
            saveBookToUser(BookStatus.VIEWED, book, currentUser);
        }
        return buildBookDto(book);
    }

    public void saveBookToUser(BookStatus status, BookEntity book, UserEntity user) {
        Book2UserEntity book2User = book2UserRepository.findByBookAndUser(book, user)
                .orElse(Book2UserEntity.builder()
                        .user(user)
                        .book(book)
                        .type(book2UserTypeRepository.findByCode(status))
                        .build());
        if (!status.equals(BookStatus.VIEWED)) {
            book2User.setType(book2UserTypeRepository.findByCode(status));
        }
        book2User.setTime(LocalDateTime.now());
        book2UserRepository.save(book2User);
    }

    public List<BookDto> getBookDtoListFromBookEntityPage(Page<BookEntity> booksPage) {
        return bookEntityListToBookDtoList(booksPage.getContent());
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfRecommendedBooks(int offset, int size) {
        List<BookEntity> books = ratingService.getBooksByRatingAndCount();
        return new BooksPageDto(getPageOfBookDtoAsList(books, offset, size));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfRecentBooks(int offset, int size) {
        Page<BookEntity> books = bookRepository.findAllByOrderByPubDateDesc(PageRequest.of(offset, size));
        return new BooksPageDto(getBookDtoListFromBookEntityPage(books));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfRecentBooks(String fromDate, String toDate, int offset, int size) {
        Pageable page = PageRequest.of(offset, size);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (fromDate == null || toDate == null) {
            return getPageOfRecentBooks(offset, size);
        }
        Page<BookEntity> books = bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(
                LocalDate.parse(fromDate, dateTimeFormatter), LocalDate.parse(toDate, dateTimeFormatter), page);
        return new BooksPageDto(getBookDtoListFromBookEntityPage(books));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfPopularBooks(int offset, int size) {
        List<BookEntity> popularBooks = getPopularBooksMap()
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet()
                .stream()
                .map(k -> bookRepository.findById(k).orElseGet(BookEntity::new))
                .collect(Collectors.toList());
        return new BooksPageDto(getPageOfBookDtoAsList(popularBooks, offset, size));
    }

    @ServiceProcessTrackable
    private Map<Integer, Double> getPopularBooksMap() {
        List<Book2UserEntity> allBooks2Users = book2UserRepository.findAll();
        Map<Integer, Double> popularBooksMap = new TreeMap<>();
        for (Book2UserEntity b : allBooks2Users) {
            double popIndex = 0;
            switch (b.getType().getCode()) {
                case VIEWED:
                    if (b.getTime().isAfter(LocalDateTime.now().minusWeeks(1))) {
                        popIndex = 0.2;
                    }
                    break;
                case KEPT:
                    popIndex = 0.4;
                    break;
                case CART:
                    popIndex = 0.7;
                    break;
                case PAID:
                    popIndex = 1;
                    break;
                default:
                    popIndex = 0;
                    break;
            }
            if (popularBooksMap.containsKey(b.getBook().getId())) {
                popIndex = popIndex + popularBooksMap.get(b.getBook().getId());
            }
            popularBooksMap.put(b.getBook().getId(), popIndex);
        }
        return popularBooksMap;
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfBooksByAuthor(String slug, int offset, int size) {
        AuthorEntity author = authorService.getAuthorData(slug);
        List<BookEntity> authorBooks = new ArrayList<>();
        for (Book2AuthorEntity a : author.getBooksLink()) {
            authorBooks.add(a.getBook());
        }
        return new BooksPageDto(authorBooks.size(), getPageOfBookDtoAsList(authorBooks, offset, size));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfBooksByTag(int id, int offset, int size) {
        List<BookEntity> books = new ArrayList<>();
        TagEntity tag = tagService.getTagById(id);
        for (Book2TagEntity b : tag.getBooksLink()) {
            books.add(b.getBook());
        }
        return new BooksPageDto(getPageOfBookDtoAsList(books, offset, size));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfBooksByGenreAndSubGenres(String slug, int offset, int size) {
        List<BookEntity> booksByGenre = genreService.getBooksByGenre(slug);
        return new BooksPageDto(getPageOfBookDtoAsList(booksByGenre, offset, size));
    }

    public BooksPageDto getPageOfCurrentUserBooks(Principal principal, int offset, int size, Boolean isArchived) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        List<BookEntity> books = user.getBooksLink()
                .stream()
                .sorted(Comparator.comparing(
                        Book2UserEntity::getType, (b1, b2) -> Integer.compare(b2.getId(), b1.getId())))
                .filter(b -> isArchived == isArchivedBook(b) && !isViewedBook(b))
                .map(Book2UserEntity::getBook)
                .collect(Collectors.toList());
        return new BooksPageDto(getPageOfBookDtoAsList(books, offset, size));
    }

    private Boolean isArchivedBook(Book2UserEntity book2User) {
        return book2User.getType().getCode().equals(BookStatus.ARCHIVED);
    }

    private Boolean isViewedBook(Book2UserEntity book2User) {
        return book2User.getType().getCode().equals(BookStatus.VIEWED);
    }

    public BooksPageDto getPageOfCurrentUserHistoryBooks(Principal principal, int offset, int size) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        List<BookEntity> books = user.getBooksLink()
                .stream()
                .filter(b -> !isArchivedBook(b))
                .sorted(Comparator.comparing(Book2UserEntity::getTime).reversed())
                .map(Book2UserEntity::getBook)
                .collect(Collectors.toList());
        return new BooksPageDto(getPageOfBookDtoAsList(books, offset, size));
    }

    private List<BookDto> getPageOfBookDtoAsList(List<BookEntity> books, int offset, int size) {
        PagedListHolder<BookEntity> page = new PagedListHolder<>(books);
        page.setPageSize(size);
        if (offset >= page.getPageCount()) {
            return new ArrayList<>();
        }
        page.setPage(offset);
        return bookEntityListToBookDtoList(page.getPageList());
    }

    private BookDto buildBookDto(BookEntity bookEntity) {
        if (bookEntity != null) {
            BookDto book = BookDto.builder()
                    .id(bookEntity.getId())
                    .slug(bookEntity.getSlug())
                    .image(bookEntity.getImage())
                    .authors(bookEntity.getAuthorsLink()
                            .stream()
                            .map(a -> authorService.getAuthorDto(a.getAuthor()))
                            .collect(Collectors.toList()))
                    .tags(bookEntity.getTagsLink()
                            .stream()
                            .map(t -> tagService.getTagDto(t.getTag()))
                            .collect(Collectors.toList()))
                    .title(bookEntity.getTitle())
                    .description(bookEntity.getDescription())
                    .genre(genreService.getGenreDto(bookEntity.getGenreLink().getGenre()))
                    .discount(bookEntity.getDiscount())
                    .isBestseller(bookEntity.getIsBestseller() == 1)
                    .rating(ratingService.getBookRatingBySlug(bookEntity))
                    .price(bookEntity.getPrice())
                    .discountPrice(bookEntity.getDiscountPrice())
                    .files(bookEntity.getBookFileList())
                    .build();
            UserEntity user = userService.getCurrentUser();
            if (user != null) {
                Optional<Book2UserEntity> book2User = book2UserRepository.findByBookAndUser(bookEntity, user);
                book2User.ifPresent(book2UserEntity -> book.setStatus(book2UserEntity.getType().getCode().name()));
            }
            return book;
        }
        return BookDto.builder().build();
    }

    public List<BookDto> bookEntityListToBookDtoList(List<BookEntity> books) {
        List<BookDto> booksDto = new ArrayList<>();
        for (BookEntity b : books) {
            booksDto.add(buildBookDto(b));
        }
        return booksDto;
    }
}
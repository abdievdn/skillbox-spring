package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final RatingService ratingService;
    private final UserService userService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final GenreService genreService;

    public BookEntity getBookBySlug(String slug) {
        return bookRepository.findBySlug(slug).orElse(null);
    }

    public BookDto getBookDtoBySlug(String slug) {
        return getBookDto(getBookBySlug(slug));
    }

    public List<BookDto> getBookDtoListFromBookEntityPage(Page<BookEntity> booksPage) {
        return bookEntityListToBookDtoList(booksPage.getContent());
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfRecommendedBooks(Integer offset, Integer size) {
        Page<BookEntity> books = bookRepository.findAllByIsBestsellerOrderByPriceAsc((short) 1, PageRequest.of(offset, size));
        return new BooksPageDto(getBookDtoListFromBookEntityPage(books));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfRecentBooks(Integer offset, Integer size) {
        Page<BookEntity> books = bookRepository.findAllByOrderByPubDateDesc(PageRequest.of(offset, size));
        return new BooksPageDto(getBookDtoListFromBookEntityPage(books));
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfRecentBooks(String fromDate, String toDate, Integer offset, Integer size) {
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
    public BooksPageDto getPageOfPopularBooks(Integer offset, Integer size) {
        List<BookEntity> popularBooks = getPopularBooksMap()
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet()
                .stream()
                .map(k -> bookRepository.findById(k).orElseGet(BookEntity::new))
                .collect(Collectors.toList());
        return new BooksPageDto(getPageOfBookDtoAsList(offset, size, popularBooks));
    }

    @ServiceProcessTrackable
    private Map<Integer, Double> getPopularBooksMap() {
        List<Book2UserEntity> allBooks2Users = book2UserRepository.findAll();
        Map<Integer, Double> popularBooksMap = new TreeMap<>();
        for (Book2UserEntity b : allBooks2Users) {
            double popIndex;
            switch (b.getType().getCode()) {
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
    public BooksPageDto getBooksByAuthor(String slug, Integer offset, Integer size) {
        AuthorEntity author = authorService.getAuthorData(slug);
        List<BookEntity> authorBooks = new ArrayList<>();
        for (Book2AuthorEntity a : author.getBooksLink()) {
            authorBooks.add(a.getBook());
        }
        return new BooksPageDto((long) authorBooks.size(), getPageOfBookDtoAsList(offset, size, authorBooks));
    }

    @ServiceProcessTrackable
    public BooksPageDto getBooksByTag(Integer id, Integer offset, Integer size) {
        List<BookEntity> books = new ArrayList<>();
        TagEntity tag = tagService.getTagById(id);
        for (Book2TagEntity b : tag.getBooksLink()) {
            books.add(b.getBook());
        }
        return new BooksPageDto(getPageOfBookDtoAsList(offset, size, books));
    }

    @ServiceProcessTrackable
    public BooksPageDto getBooksByGenreAndSubGenres(String slug, Integer offset, Integer size) {
        List<BookEntity> booksByGenre = genreService.getBooksByGenre(slug);
        return new BooksPageDto(getPageOfBookDtoAsList(offset, size, booksByGenre));
    }

    private List<BookDto> getPageOfBookDtoAsList(Integer offset, Integer size, List<BookEntity> books) {
        PagedListHolder<BookEntity> page = new PagedListHolder<>(books);
        page.setPageSize(size);
        if (offset >= page.getPageCount()) {
            return new ArrayList<>();
        }
        page.setPage(offset);
        return bookEntityListToBookDtoList(page.getPageList());
    }

    private BookDto getBookDto(BookEntity bookEntity) {
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
                    .discountPrice(bookEntity.discountPrice())
                    .files(bookEntity.getBookFileList())
                    .build();
            UserEntity user = userService.getCurrentUser();
            if (user != null) {
                Optional<Book2UserEntity> book2User = book2UserRepository.findByBookAndUser(bookEntity, user);
                book2User.ifPresent(book2UserEntity -> book.setStatus(book2UserEntity.getType().toString()));
            }
            return book;
        }
        return BookDto.builder().build();
    }

    public List<BookDto> bookEntityListToBookDtoList(List<BookEntity> books) {
        List<BookDto> booksDto = new ArrayList<>();
        for (BookEntity b : books) {
            booksDto.add(getBookDto(b));
        }
        return booksDto;
    }
}
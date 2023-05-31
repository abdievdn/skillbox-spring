package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.Book2UserTypeRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final RatingService ratingService;
    private final UserService userService;
    public static Long booksSearchCount;

    public BookEntity getBookBySlug(String slug) {
        return bookRepository.findBySlug(slug).orElseThrow();
    }

    public BookDto getBookDtoBySlug(String slug) {
        return getBookDto(getBookBySlug(slug));
    }

    public List<BookEntity> getBooksData() {
        return bookRepository.findAll();
    }

    public List<BookDto> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer size) throws CommonErrorException {
        if (searchWord == null || searchWord.length() < 1) {
            throw new CommonErrorException("Не введено значение поиска!");
        } else {
            Pageable page = PageRequest.of(offset, size);
            Page<BookEntity> books = bookRepository.findAllByTitleContainingIgnoreCase(searchWord, page);
            BookService.booksSearchCount = books.getTotalElements();
            return bookEntityListToBookDtoList(books.getContent());
        }
    }

    public List<BookDto> getPageOfRecommendedBooks(Integer offset, Integer size) {
        return bookEntityListToBookDtoList(
                bookRepository.findAllByIsBestsellerOrderByPriceAsc(
                                (short) 1,
                                PageRequest.of(offset, size))
                        .getContent());
    }

    public List<BookDto> getPageOfRecentBooks(Integer offset, Integer size) {
        Pageable page = PageRequest.of(offset, size);
        return bookEntityListToBookDtoList(bookRepository.findAllByOrderByPubDateDesc(page).getContent());
    }

    public List<BookDto> getPageOfRecentBooks(String fromDate, String toDate, Integer offset, Integer size) {
        Pageable page = PageRequest.of(offset, size);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (fromDate == null || toDate == null) {
            return bookEntityListToBookDtoList(bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(
                    LocalDate.now().minusMonths(1),
                    LocalDate.now(), page).getContent());
        }
        return bookEntityListToBookDtoList(bookRepository.findAllByPubDateBetweenOrderByPubDateDesc(
                LocalDate.parse(fromDate, dateTimeFormatter),
                LocalDate.parse(toDate, dateTimeFormatter),
                page).getContent());
    }

    public List<BookDto> getPageOfPopularBooks(Integer offset, Integer size) {
        List<Book2UserEntity> allBooks2User = book2UserRepository.findAll();
        Map<Integer, Double> popularBooksMap = new TreeMap<>();
        for (Book2UserEntity b : allBooks2User) {
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
        List<BookEntity> popularBooks = popularBooksMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet()
                .stream()
                .map(k -> bookRepository.findById(k).orElseGet(BookEntity::new))
                .collect(Collectors.toList());
        return getBooksPage(offset, size, popularBooks);
    }

    public List<BookDto> getBooksPage(Integer offset, Integer size, List<BookEntity> authorBooks) {
        PagedListHolder<BookEntity> page = new PagedListHolder<>(authorBooks);
        page.setPageSize(size);
        if (offset >= page.getPageCount()) {
            return new ArrayList<>();
        }
        page.setPage(offset);
        return bookEntityListToBookDtoList(page.getPageList());
    }

    private BookDto getBookDto(BookEntity bookEntity) {
        BookDto book = BookDto.builder()
                .id(bookEntity.getId())
                .slug(bookEntity.getSlug())
                .image(bookEntity.getImage())
                .authors(bookEntity.getBook2Authors()
                        .stream()
                        .map(Book2AuthorEntity::getAuthor)
                        .collect(Collectors.toList()))
                .tags(bookEntity.getBook2Tags()
                        .stream()
                        .map(Book2TagEntity::getTag)
                        .collect(Collectors.toList()))
                .title(bookEntity.getTitle())
                .description(bookEntity.getDescription())
                .genre(bookEntity.getGenre2Book().getGenre())
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

    private List<BookDto> bookEntityListToBookDtoList(List<BookEntity> books) {
        List<BookDto> booksDto = new ArrayList<>();
        books.forEach(b -> booksDto.add(getBookDto(b)));
        return booksDto;
    }

    public void saveBookImage(String slug, String savePath) {
        BookEntity book = getBookBySlug(slug);
        book.setImage(savePath);
        bookRepository.save(book);
    }

    public void addBookStatus(BookStatus status, String slug, Principal principal,
                              HttpServletRequest request, HttpServletResponse response) {
        if (principal != null) {
            addBookToUser(status, slug, principal);
        } else {
            addBookToCookie(status.name(), slug, request, response);
        }
    }

    private void addBookToUser(BookStatus status, String id, Principal principal) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        List<Book2UserEntity> book2UserEntityList = user.getUser2books();
        if (book2UserEntityList.isEmpty()) {
            book2UserRepository.save(Book2UserEntity.builder()
                    .book(bookRepository.findBySlug(id).orElseThrow())
                    .user(user)
                    .type(book2UserTypeRepository.findByCode(status))
                    .time(LocalDateTime.now())
                    .build());
        } else {
            book2UserEntityList.forEach(b -> {
                if (b.getUser().equals(user) && !b.getType().getCode().equals(status)) {
                    b.setType(book2UserTypeRepository.findByCode(status));
                    book2UserRepository.save(b);
                }
            });
        }
    }

    private void addBookToCookie(String status, String slug,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if (!status.equals("")) {
            if (status.equals(BookStatus.CART.name())) {
                checkAndRemoveFromCookie(BookStatus.KEPT.name(), slug, request, response);
            }
            if (status.equals(BookStatus.KEPT.name())) {
                checkAndRemoveFromCookie(BookStatus.CART.name(), slug, request, response);
            }
            Cookie getCookie = WebUtils.getCookie(request, status);
            if (getCookie == null) {
                Cookie cookie = new Cookie(status, slug);
                cookie.setPath("/books");
                response.addCookie(cookie);
            } else {
                String contents = getCookie.getValue();
                if (!contents.contains(slug)) {
                    StringJoiner stringJoiner = new StringJoiner("/");
                    stringJoiner.add(contents).add(slug);
                    Cookie cookie = new Cookie(status, stringJoiner.toString());
                    cookie.setPath("/books");
                    response.addCookie(cookie);
                }
            }
        }
    }

    private void checkAndRemoveFromCookie(String removedCookieName, String slug,
                                          HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, removedCookieName);
        if (cookie != null) {
            removeBookFromCookie(slug, removedCookieName, cookie.getValue(), response);
        }
    }

    public List<BookDto> getBooksStatusList(BookStatus status, String contents, Principal principal) {
        List<BookEntity> books = new ArrayList<>();
        if (principal != null) {
            getBooksFromUser(status, principal, books);
        } else {
            return getBooksFromCookie(contents);
        }
        return bookEntityListToBookDtoList(books);
    }

    private void getBooksFromUser(BookStatus status, Principal principal, List<BookEntity> books) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        user.getUser2books().forEach(b -> {
            if (b.getType().getCode().equals(status)) {
                books.add(b.getBook());
            }
        });
    }

    private List<BookDto> getBooksFromCookie(String contents) {
        String[] cookieSlugs = contents.split("/");
        List<BookEntity> booksFromCookie = bookRepository.findAllBySlugIn(cookieSlugs);
        return bookEntityListToBookDtoList(booksFromCookie);
    }

    public void removeBook(String slug, String cookieName, String contents, HttpServletResponse response, Principal principal) {
        if (principal != null) {
            removeBookFromUser(slug, principal);
        } else {
            removeBookFromCookie(slug, cookieName, contents, response);
        }
    }

    private void removeBookFromUser(String slug, Principal principal) {
        BookEntity book = bookRepository.findBySlug(slug).orElseThrow();
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        book2UserRepository.deleteByBookAndUser(book, user);
    }

    private void removeBookFromCookie(String slug, String cookieName, String contents, HttpServletResponse response) {
        if (contents != null && !contents.equals("")) {
            if (contents.contains(slug)) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(contents.split("/")));
                cookieBooks.remove(slug);
                Cookie cookie = new Cookie(cookieName, String.join("/", cookieBooks));
                cookie.setPath("/books");
                response.addCookie(cookie);
            }
        }
    }
}
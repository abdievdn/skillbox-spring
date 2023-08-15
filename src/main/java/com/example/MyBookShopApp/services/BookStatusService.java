package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.Book2UserTypeRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class BookStatusService {

    private final BookRepository bookRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;
    private final Book2UserRepository book2UserRepository;
    private final BookService bookService;
    private final UserService userService;

    @ServiceProcessTrackable
    public void addBookStatus(BookStatus status, String slug, Principal principal,
                              HttpServletRequest request, HttpServletResponse response) {
        if (principal != null) {
            addBookToUser(status, slug, principal);
        } else {
            addBookToCookie(status.name(), slug, request, response);
        }
    }

    private void addBookToUser(BookStatus status, String slug, Principal principal) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        BookEntity book = bookRepository.findBySlug(slug).orElseThrow();
        List<Book2UserEntity> book2UserEntityList = user.getBooksLink();
        if (!book2UserEntityList.isEmpty()) {
            for (Book2UserEntity b : book2UserEntityList) {
                if (b.getUser().equals(user) &&
                        b.getBook().equals(book) &&
                        !b.getType().getCode().equals(status)) {
                    b.setType(book2UserTypeRepository.findByCode(status));
                    book2UserRepository.save(b);
                    return;
                }
            }
        }
        book2UserRepository.save(Book2UserEntity.builder()
                .book(book)
                .user(user)
                .type(book2UserTypeRepository.findByCode(status))
                .time(LocalDateTime.now())
                .build());
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

    @ServiceProcessTrackable
    public List<BookDto> getBooksStatusList(BookStatus status, String contents, Principal principal) {
        List<BookEntity> books = new ArrayList<>();
        if (principal != null) {
            getBooksFromUser(status, principal, books);
        } else {
            return getBooksFromCookie(contents);
        }
        return bookService.bookEntityListToBookDtoList(books);
    }

    private void getBooksFromUser(BookStatus status, Principal principal, List<BookEntity> books) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        user.getBooksLink().forEach(b -> {
            if (b.getType().getCode().equals(status)) {
                books.add(b.getBook());
            }
        });
    }

    private List<BookDto> getBooksFromCookie(String contents) {
        if (contents != null && !contents.isEmpty()) {
            String[] cookieSlugs = contents.split("/");
            List<BookEntity> booksFromCookie = bookRepository.findAllBySlugIn(cookieSlugs);
            return bookService.bookEntityListToBookDtoList(booksFromCookie);
        } else {
            return new ArrayList<>();
        }
    }

    @ServiceProcessTrackable
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
        Book2UserEntity book2User = book2UserRepository.findByBookAndUser(book, user).orElseThrow();
        book2UserRepository.delete(book2User);
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

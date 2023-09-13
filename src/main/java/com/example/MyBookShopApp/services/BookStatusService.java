package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.ResultDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.entity.enums.BookStatus;
import com.example.MyBookShopApp.data.entity.user.UserEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.Book2UserTypeRepository;
import com.example.MyBookShopApp.services.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookStatusService {

    private final Book2UserTypeRepository book2UserTypeRepository;
    private final Book2UserRepository book2UserRepository;
    private final BookService bookService;
    private final UserService userService;
    private final PaymentService paymentService;

    @ServiceProcessTrackable
    public ResultDto setBooksStatus(BookStatus status, String slug, Principal principal,
                                    HttpServletRequest request, HttpServletResponse response) throws CommonErrorException {
        List<String> slugs = getSlugsList(slug);
        if (principal != null) {
            UserEntity user = userService.getCurrentUserByPrincipal(principal);
            if (status.equals(BookStatus.PAID)) {
                return paymentService.postBooksPaymentTransaction(slugs, user);
            }
            for (String s : slugs) {
                BookEntity book = bookService.getBookBySlug(s);
                if (isBookBelongsToUser(book, user) &&
                        (status.equals(BookStatus.CART) || status.equals(BookStatus.KEPT))) {
                    return new ResultDto("Вы уже приобрели эту книгу");
                }
                bookService.saveBookToUser(status, book, user);
            }
        } else {
            setBookToCookie(status.name(), slugs, request, response);
        }
        return new ResultDto(true);
    }

    public Boolean isBookBelongsToUser(BookEntity book, UserEntity user) {
        return book2UserRepository.findByBookAndUser(book, user).filter(book2User ->
                book2User.getType().getCode().equals(BookStatus.PAID) ||
                        book2User.getType().getCode().equals(BookStatus.ARCHIVED)).isPresent();
    }

    private List<String> getSlugsList(String slug) {
        List<String> slugs = new ArrayList<>();
        if (slug.contains(",")) {
            slugs = List.of(slug.split(","));
        } else {
            slugs.add(slug);
        }
        return slugs;
    }

    public String getSlugsString(List<BookDto> books) {
        return books
                .stream()
                .map(BookDto::getSlug)
                .collect(Collectors.joining(","));
    }

    private void setBookToCookie(String status, List<String> slugs,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if (!status.equals("")) {
            if (status.equals(BookStatus.CART.name())) {
                checkAndRemoveFromCookie(BookStatus.KEPT.name(), slugs, request, response);
            }
            if (status.equals(BookStatus.KEPT.name())) {
                checkAndRemoveFromCookie(BookStatus.CART.name(), slugs, request, response);
            }
            Cookie getCookie = WebUtils.getCookie(request, status);
            String cookieSlugs = String.join("/", slugs);
            if (getCookie == null) {
                Cookie cookie = new Cookie(status, cookieSlugs);
                cookie.setPath("/");
                response.addCookie(cookie);
            } else {
                String contents = getCookie.getValue();
                if (!contents.contains(cookieSlugs)) {
                    StringJoiner stringJoiner = new StringJoiner("/");
                    stringJoiner.add(contents).add(cookieSlugs);
                    Cookie cookie = new Cookie(status, stringJoiner.toString());
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }

    private void checkAndRemoveFromCookie(String removedCookieName, List<String> slugs,
                                          HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, removedCookieName);
        if (cookie != null) {
            String contents = cookie.getValue();
            if (contents != null && !contents.equals("")) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(contents.split("/")));
                if (cookieBooks.containsAll(slugs)) {
                    cookieBooks.removeAll(slugs);
                    if (!cookieBooks.isEmpty()) {
                        cookie = new Cookie(removedCookieName, String.join("/", cookieBooks));
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    } else {
                        CookieUtil.deleteCookieByName(request, response, removedCookieName);
                    }
                }
            }
        }
    }

    @ServiceProcessTrackable
    public List<BookDto> getBooksStatusList(BookStatus status, String contents, Principal principal) {
        if (principal != null) {
            return bookService.bookEntityListToBookDtoList(getBooksFromUser(status, principal));
        } else {
            return getBooksFromCookie(contents);
        }
    }

    public int getCountOfCartBooks(String contents, Principal principal) {
        if (principal != null) {
            return getBooksFromUser(BookStatus.CART, principal).size();
        } else {
            return getBooksFromCookie(contents).size();
        }
    }

    public int getCountOfPostponedBooks(String contents, Principal principal) {
        if (principal != null) {
            return getBooksFromUser(BookStatus.KEPT, principal).size();
        } else {
            return getBooksFromCookie(contents).size();
        }
    }

    public int getCountOfAllUserBooks(Principal principal) {
        if (principal != null) {
            UserEntity user = userService.getCurrentUserByPrincipal(principal);
            if (isUserHasBooks(user)) {
                return (int) user.getBooksLink()
                        .stream()
                        .filter(b -> !b.getType().getCode().equals(BookStatus.VIEWED))
                        .count();
            }
        }
        return 0;
    }

    private boolean isUserHasBooks(UserEntity user) {
        return user.getBooksLink() != null;
    }

    private List<BookEntity> getBooksFromUser(BookStatus status, Principal principal) {
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        List<BookEntity> books = new ArrayList<>();
        if (isUserHasBooks(user)) {
            user.getBooksLink().forEach(b -> {
                if (b.getType().getCode().equals(status)) {
                    books.add(b.getBook());
                }
            });
        }
        return books;
    }

    private List<BookDto> getBooksFromCookie(String contents) {
        if (contents != null && !contents.isEmpty()) {
            String[] cookieSlugs = contents.split("/");
            List<BookEntity> booksFromCookie = bookService.getBooksBySlugs(cookieSlugs);
            return bookService.bookEntityListToBookDtoList(booksFromCookie);
        } else {
            return new ArrayList<>();
        }
    }

    @ServiceProcessTrackable
    public ResultDto removeBooks(String slug, String cookieName,
                                 HttpServletRequest request, HttpServletResponse response, Principal principal) {
        if (principal != null) {
            removeBookFromCartOrKept(slug, principal);
        } else {
            checkAndRemoveFromCookie(cookieName, List.of(slug), request, response);
        }
        return new ResultDto(true);
    }

    private void removeBookFromCartOrKept(String slug, Principal principal) {
        BookEntity book = bookService.getBookBySlug(slug);
        UserEntity user = userService.getCurrentUserByPrincipal(principal);
        Book2UserEntity book2User = book2UserRepository.findByBookAndUser(book, user).orElseThrow();
        book2User.setType(book2UserTypeRepository.findByCode(BookStatus.VIEWED));
        book2UserRepository.save(book2User);
    }
}

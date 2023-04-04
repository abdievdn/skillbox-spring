package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.RatingDto;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.data.struct.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.data.struct.user.UserEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import com.example.MyBookShopApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final Book2UserRepository book2UserRepository;

    private final UserRepository userRepository;
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

    public Page<BookEntity> getPageOfBooks(Integer offset, Integer size) {
        Pageable page = PageRequest.of(offset, size);
        return bookRepository.findAll(page);
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
        Pageable page = PageRequest.of(offset, size);
        return bookEntityListToBookDtoList(bookRepository.findAllByIsBestsellerOrderByPriceAsc((short) 1, page).getContent());
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
            switch (b.getType().getId()) {
                case 1:
                    popIndex = 0.4;
                    break;
                case 2:
                    popIndex = 0.7;
                    break;
                case 3:
                    popIndex = 1;
                    break;
                default:
                    popIndex = 0;
                    break;
            }
            if (popularBooksMap.containsKey(b.getBook2User().getId())) {
                popIndex = popIndex + popularBooksMap.get(b.getBook2User().getId());
            }
            popularBooksMap.put(b.getBook2User().getId(), popIndex);
        }
        List<BookEntity> popularBooks = popularBooksMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet()
                .stream()
                .map(k -> bookRepository.findById(k).get())
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
        UserEntity user = userRepository.findById(1).orElseThrow();
        return BookDto.builder()
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
                .rating(getBookRatingBySlug(bookEntity))
                .status(bookEntity.getBook2Users()
                        .stream()
                        .filter(u -> u.getUser().equals(user))
                        .map(Book2UserEntity::getType)
                        .toString())
                .price(bookEntity.getPrice())
                .discountPrice(bookEntity.discountPrice())
                .files(bookEntity.getBookFileList())
                .build();
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

    public List<BookDto> getBooksStatusList(String contents) {
        String[] cookieSlugs = contents.split("/");
        List<BookEntity> booksFromCookie = bookRepository.findAllBySlugIn(cookieSlugs);
        return bookEntityListToBookDtoList(booksFromCookie);
    }

    public void addBookToCookie(String status, String slug,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        String cookieName = "";
        if (status.equals("CART")) {
            cookieName = "cart";
            Cookie cookie = getCookie(request, "postponed");
            if (cookie != null) {
                removeBookFromCookie(slug, cookie.getValue(), response, "postponed");
            }
        }
        if (status.equals("KEPT")) {
            cookieName = "postponed";
            Cookie cookie = getCookie(request, "cart");
            if (cookie != null) {
                removeBookFromCookie(slug, cookie.getValue(), response, "cart");
            }
        }
        if (!cookieName.equals("")) {
            Cookie getCookie = getCookie(request, cookieName);
            if (getCookie == null) {
                Cookie cookie = new Cookie(cookieName, slug);
                cookie.setPath("/books");
                response.addCookie(cookie);
            } else {
                String contents = getCookie.getValue();
                if (!contents.contains(slug)) {
                    StringJoiner stringJoiner = new StringJoiner("/");
                    stringJoiner.add(contents).add(slug);
                    Cookie cookie = new Cookie(cookieName, stringJoiner.toString());
                    cookie.setPath("/books");
                    response.addCookie(cookie);
                }
            }
        }
    }

    private Cookie getCookie(HttpServletRequest request, String cookieName) {
        return WebUtils.getCookie(request, cookieName);
    }

    public void removeBookFromCookie(String slug, String contents, HttpServletResponse response, String status) {
        if (contents != null && !contents.equals("")) {
            if (contents.contains(slug)) {
                ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(contents.split("/")));
                cookieBooks.remove(slug);
                Cookie cookie = new Cookie(status, String.join("/", cookieBooks));
                cookie.setPath("/books");
                response.addCookie(cookie);
            }
        }
    }

    private RatingDto getBookRatingBySlug(BookEntity book) {
        List<BookRatingEntity> bookRatings = book.getBook2Ratings();
        return RatingDto.builder()
                .value((short) Math.round(bookRatings
                        .stream()
                        .mapToInt(BookRatingEntity::getValue)
                        .average()
                        .orElse(0)))
                .count(bookRatings.size())
                .values2Count(bookRatings
                        .stream()
                        .collect(Collectors.groupingBy(
                                BookRatingEntity::getValue,
                                collectingAndThen(counting(), Long::intValue)))
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (k, v) -> v,
                                LinkedHashMap::new)))
                .build();
    }

}

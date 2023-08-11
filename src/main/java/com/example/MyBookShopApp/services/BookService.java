package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.AuthorDto;
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
import com.example.MyBookShopApp.data.google.api.books.Item;
import com.example.MyBookShopApp.data.google.api.books.Root;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.Book2UserRepository;
import com.example.MyBookShopApp.repositories.Book2UserTypeRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    private final AuthorService authorService;
    private final TagService tagService;
    private final GenreService genreService;
    private final RestTemplate restTemplate;

    public BookEntity getBookBySlug(String slug) {
        return bookRepository.findBySlug(slug).orElse(null);
    }

    public BookDto getBookDtoBySlug(String slug) {
        return getBookDto(getBookBySlug(slug));
    }

    private List<BookDto> getBookDtoListFromBookEntityPage(Page<BookEntity> booksPage) {
        return bookEntityListToBookDtoList(booksPage.getContent());
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfSearchResultBooks(String searchWord, Integer offset, Integer size) throws CommonErrorException {
        if (searchWord == null || searchWord.length() < 1) {
            throw new CommonErrorException("Не введено значение поиска!");
        } else {
            Page<BookEntity> books = bookRepository.findAllByTitleContainingIgnoreCase(searchWord, PageRequest.of(offset, size));
            return new BooksPageDto(books.getTotalElements(), getBookDtoListFromBookEntityPage(books));
        }
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

    private List<BookDto> bookEntityListToBookDtoList(List<BookEntity> books) {
        List<BookDto> booksDto = new ArrayList<>();
        for (BookEntity b : books) {
            booksDto.add(getBookDto(b));
        }
        return booksDto;
    }

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
        return bookEntityListToBookDtoList(books);
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
            return bookEntityListToBookDtoList(booksFromCookie);
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

    @Value("${google.books.api.key}")
    private String googleApiKey;

    public BooksPageDto getPageOfGoogleBooksApiSearchResult(String searchWord, Integer offset, Integer size) {
        String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes" +
                "?q=" + searchWord +
                "&key=" + googleApiKey +
                "&filter=paid-ebooks" +
                "&startIndex=" + offset +
                "&maxResults=" + size;
        Root root = restTemplate.getForEntity(REQUEST_URL, Root.class).getBody();
        ArrayList<BookDto> booksList = new ArrayList<>();
        if (root != null) {
            for (Item item : root.getItems()) {
                BookDto book = new BookDto();
                if (item.getVolumeInfo() != null && item.getSaleInfo() != null) {
                    book = BookDto.builder()
                            .authors(item.getVolumeInfo().getAuthors().stream()
                                    .map(a -> AuthorDto.builder()
                                            .firstName(a.substring(0, a.indexOf(" ")))
                                            .lastName(a.substring(a.indexOf(" ") + 1, a.length() - 1))
                                            .build())
                                    .collect(Collectors.toList()))
                            .title(item.getVolumeInfo().getTitle())
                            .image(item.getVolumeInfo().getImageLinks().getThumbnail())
                            .price((int) item.getSaleInfo().getRetailPrice().getAmount())
                            .discountPrice(item.getSaleInfo().getListPrice().getAmount())
                            .build();
                }
                booksList.add(book);
            }
        }
        return new BooksPageDto(booksList);
    }
}
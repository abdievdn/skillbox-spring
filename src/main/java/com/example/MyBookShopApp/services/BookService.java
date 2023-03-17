package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.book.links.Book2UserEntity;
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
    public static Long booksSearchCount;

    public List<BookEntity> getBooksData() {
        return bookRepository.findAll();
    }

    public Page<BookEntity> getPageOfBooks(Integer offset, Integer size) {
        Pageable page = PageRequest.of(offset, size);
        return bookRepository.findAll(page);
    }

    public List<BookEntity> getBooksByTitle(String bookTitle) {
        return bookRepository.findAllByTitleContainingIgnoreCase(bookTitle);
    }

    public List<BookEntity> getBooksWithPriceBetween(Integer min, Integer max) {
        return bookRepository.findAllByPriceBetween(min, max);
    }

    public List<BookEntity> getBooksWithPrice(Integer price) {
        return bookRepository.findAllByPriceIs(price);
    }

    public List<BookEntity> getBooksWithMaxDiscount() {
        return bookRepository.findAllWithMaxDiscount();
    }

    public List<BookEntity> getBestsellers() {
        return bookRepository.findBestsellers();
    }

    public List<BookDto> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer size) {
        Pageable page = PageRequest.of(offset, size);
        Page<BookEntity> books = bookRepository.findAllByTitleContainingIgnoreCase(searchWord, page);
        BookService.booksSearchCount = books.getTotalElements();
        return bookEntityListToBookDtoList(books.getContent());
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
            double popIndex = 0;
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
        return BookDto.builder()
                .id(bookEntity.getId())
                .slug(bookEntity.getSlug())
                .image(bookEntity.getImage())
                .authors(bookEntity.getBook2authors()
                        .stream()
                        .map(b2a -> b2a.getAuthor().toString())
                        .collect(Collectors.toList()))
                .title(bookEntity.getTitle())
                .discount(bookEntity.getDiscount())
                .isBestseller(bookEntity.getIsBestseller() == 1)
                .rating(5)
                .status("")
                .price(bookEntity.getPrice())
                .discountPrice(bookEntity.getPrice() -
                        (bookEntity.getPrice() * bookEntity.getDiscount() / 100))
                .build();
    }

    private List<BookDto> bookEntityListToBookDtoList(List<BookEntity> books) {
        List<BookDto> booksDto = new ArrayList<>();
        books.forEach(b -> booksDto.add(getBookDto(b)));
        return booksDto;
    }
}

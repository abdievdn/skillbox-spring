package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.google.api.books.Item;
import com.example.MyBookShopApp.data.google.api.books.Root;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.mappers.BookMapper;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final BookRepository bookRepository;
    private final BookService bookService;
    private final RestTemplate restTemplate;
    private final BookMapper bookMapper;

    @Value("${google.books.api.KEY}")
    private String googleApiKey;

    @ServiceProcessTrackable
    public BooksPageDto getPageOfSearchResultBooks(String searchWord, int offset, int size) throws CommonErrorException {
        if (searchWord == null || searchWord.length() < 1) {
            throw new CommonErrorException("Не введено значение поиска!");
        } else {
            Page<BookEntity> books = bookRepository.findAllByTitleContainingIgnoreCase(searchWord, PageRequest.of(offset, size));
            return new BooksPageDto(books.getTotalElements(), bookService.getBookDtoListFromBookEntityPage(books));
        }
    }

    @ServiceProcessTrackable
    public BooksPageDto getPageOfGoogleBooksApiSearchResult(String searchWord, int offset, int size) {
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
                    book = bookMapper.toBookDto(item.getVolumeInfo(), item.getSaleInfo());
                }
                booksList.add(book);
            }
        }
        return new BooksPageDto(booksList);
    }
}

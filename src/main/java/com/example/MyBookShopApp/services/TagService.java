package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.BookDto;
import com.example.MyBookShopApp.data.struct.book.BookEntity;
import com.example.MyBookShopApp.data.struct.tag.TagEntity;
import com.example.MyBookShopApp.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final BookService bookService;
    public static String tagName;

    public List<TagEntity> getAllTags() {
        return tagRepository.findAll();
    }

    public Map<TagEntity, Integer> getTagsWithValues() {
        Map<TagEntity, Integer> tags = new HashMap<>();
        for (TagEntity t : getAllTags()) {
            tags.put(t, t.getTag2books().size());
        }
        return tags;
    }

    public List<BookDto> getBooksByTag(String slug, Integer offset, Integer size) {
        List<BookEntity> books = new ArrayList<>();
        TagEntity tag = tagRepository.findBySlug(slug).orElseGet(TagEntity::new);
        tag.getTag2books().forEach(b -> books.add(b.getBook()));
        TagService.tagName = tag.getName();
        return bookService.getBooksPage(offset, size, books);
    }
}
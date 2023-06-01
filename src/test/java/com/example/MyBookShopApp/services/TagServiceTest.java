package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.TagDto;
import com.example.MyBookShopApp.data.entity.book.links.Book2TagEntity;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.repositories.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TagServiceTest {

    private final TagService tagService;

    @MockBean
    TagRepository tagRepository;

    private TagEntity tag1, tag2;
    List<TagEntity> tags;

    @Autowired
    TagServiceTest(TagService tagService) {
        this.tagService = tagService;
    }

    @BeforeEach
    void setUp() {
        tag1 = TagEntity.builder()
                .id(1)
                .name("tag1")
                .slug("tag1")
                .booksLink(List.of(new Book2TagEntity(), new Book2TagEntity()))
                .build();
        tag2 = TagEntity.builder()
                .id(2)
                .slug("tag2")
                .booksLink(List.of(new Book2TagEntity()))
                .build();
        tags = List.of(tag1, tag2);
    }

    @AfterEach
    void tearDown() {
        tag1 = null;
        tag2 = null;
        tags = null;
    }

    @Test
    void getAllTags() {
        Mockito.doReturn(tags)
                .when(tagRepository)
                .findAll();
        List<TagEntity> allTags = tagService.getAllTags();
        assertNotNull(allTags);
        assertEquals(allTags.size(), 2);
        assertEquals(allTags.get(0), tag1);
    }

    @Test
    void getTagsWithValues() {
        Mockito.doReturn(tags)
                .when(tagRepository)
                .findAll();
        Map<TagEntity, Integer> tagsMap = tagService.getTagsWithValues();
        assertNotNull(tagsMap);
        assertEquals(tagsMap.get(tag1), 2);
        assertEquals(tagsMap.get(tag2), 1);
    }

    @Test
    void getTagById() {
        Mockito.doReturn(Optional.of(tag1))
                .when(tagRepository)
                .findById(1);
        TagEntity tag = tagService.getTagById(1);
        assertNotNull(tag);
        assertEquals(tag.getSlug(), "tag1");
    }

    @Test
    void getTagDto() {
        TagDto tagDto = tagService.getTagDto(tag1);
        assertNotNull(tagDto);
        assertEquals(tagDto.getName(), tag1.getName());
        assertEquals(tagDto.getSlug(), "tag1");
    }
}
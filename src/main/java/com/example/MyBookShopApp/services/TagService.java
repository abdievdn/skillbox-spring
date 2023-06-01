package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.dto.TagDto;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import com.example.MyBookShopApp.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagEntity> getAllTags() {
        return tagRepository.findAll();
    }

    public Map<TagEntity, Integer> getTagsWithValues() {
        Map<TagEntity, Integer> tags = new HashMap<>();
        for (TagEntity t : getAllTags()) {
            tags.put(t, t.getBooksLink().size());
        }
        return tags;
    }

    public TagEntity getTagById(Integer id) {
        return tagRepository.findById(id).orElseThrow();
    }

    public TagDto getTagDto(TagEntity tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }
}

package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
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

    @ServiceProcessTrackable
    public Map<TagEntity, Integer> getTagsWithValues() {
        Map<TagEntity, Integer> tags = new HashMap<>();
        for (TagEntity t : getAllTags()) {
            tags.put(t, t.getBooksLink().size());
        }
        return tags;
    }

    public TagEntity getTagById(int id) {
        return tagRepository.findById(id).orElseThrow();
    }

}

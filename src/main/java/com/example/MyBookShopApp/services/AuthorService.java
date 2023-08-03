package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.AuthorDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @ServiceProcessTrackable
    public Map<String, List<AuthorEntity>> getAuthorsMap() {
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy(a -> a.getLastName().substring(0, 1)));
    }

    @ServiceProcessTrackable
    public AuthorEntity getAuthorData(String slug) {
        return authorRepository.findBySlug(slug).orElse(null);
    }

    public AuthorDto getAuthorDto(AuthorEntity author) {
        return AuthorDto.builder()
                .id(author.getId())
                .description(author.getDescription())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .slug(author.getSlug())
                .photo(author.getPhoto())
                .build();
    }
}

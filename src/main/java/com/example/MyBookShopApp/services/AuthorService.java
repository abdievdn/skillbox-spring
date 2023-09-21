package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.dto.AuthorDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import com.example.MyBookShopApp.errors.EntityNotFoundError;
import com.example.MyBookShopApp.mappers.AuthorMapper;
import com.example.MyBookShopApp.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @ServiceProcessTrackable
    public Map<String, List<AuthorEntity>> getAuthorsMap() {
        List<AuthorEntity> authors = authorRepository.findAll();
        return authors.stream().collect(Collectors.groupingBy(a -> a.getLastName().substring(0, 1)));
    }

    public AuthorEntity getAuthorEntity(String slug) throws EntityNotFoundError {
        return authorRepository.findBySlug(slug).orElseThrow(() -> new EntityNotFoundError("Автор не найден!"));
    }

    @ServiceProcessTrackable
    public AuthorDto getAuthorDto(String slug) throws EntityNotFoundError {
        return authorMapper.toAuthorDto(getAuthorEntity(slug));
    }
}

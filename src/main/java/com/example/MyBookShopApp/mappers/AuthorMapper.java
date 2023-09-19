package com.example.MyBookShopApp.mappers;

import com.example.MyBookShopApp.data.dto.AuthorDto;
import com.example.MyBookShopApp.data.entity.author.AuthorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto toAuthorDto(AuthorEntity author);
}

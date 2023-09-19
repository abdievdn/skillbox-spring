package com.example.MyBookShopApp.mappers;

import com.example.MyBookShopApp.data.dto.GenreDto;
import com.example.MyBookShopApp.data.entity.genre.GenreEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDto toGenreDto(GenreEntity genre);
}

package com.example.MyBookShopApp.mappers;

import com.example.MyBookShopApp.data.dto.TagDto;
import com.example.MyBookShopApp.data.entity.tag.TagEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto toTagDto(TagEntity tag);
}

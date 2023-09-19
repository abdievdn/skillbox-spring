package com.example.MyBookShopApp.mappers;

import com.example.MyBookShopApp.data.dto.BookRatingDto;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.rating.BookRatingEntity;
import com.example.MyBookShopApp.services.RatingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;

@Mapper(componentModel = "spring", uses = {RatingService.class})
public interface RatingMapper {

    @Mapping(target = "value", source = "book.ratings")
    @Mapping(target = "count", expression = "java(book.getRatings().size())")
    @Mapping(target = "valuesForCount", source = "book.ratings")
    BookRatingDto toBookRatingDto(BookEntity book);

    default short mapValue(List<BookRatingEntity> bookRatings) {
        return (short) bookRatings
                .stream()
                .mapToDouble(BookRatingEntity::getValue)
                .average()
                .orElse(0);
    }

    default Map<Short, Integer> mapValuesForCount(List<BookRatingEntity> bookRatings) {
        return bookRatings
                .stream()
                .collect(Collectors.groupingBy(
                        BookRatingEntity::getValue, collectingAndThen(counting(), Long::intValue)))
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (k, v) -> v,
                        HashMap::new));
    }
}

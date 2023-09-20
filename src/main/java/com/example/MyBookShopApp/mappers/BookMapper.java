package com.example.MyBookShopApp.mappers;

import com.example.MyBookShopApp.data.dto.*;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.google.api.books.SaleInfo;
import com.example.MyBookShopApp.data.google.api.books.VolumeInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {

    AuthorMapper AUTHOR_MAPPER = Mappers.getMapper(AuthorMapper.class);
    GenreMapper GENRE_MAPPER = Mappers.getMapper(GenreMapper.class);
    TagMapper TAG_MAPPER = Mappers.getMapper(TagMapper.class);
    RatingMapper RATING_MAPPER = Mappers.getMapper(RatingMapper.class);

    @Mapping(target = "authors", source = "book")
    @Mapping(target = "tags", source = "book")
    @Mapping(target = "genre", source = "book")
    @Mapping(target = "isBestseller", source = "book")
    @Mapping(target = "rating", source = "book")
    BookDto toBookDto(BookEntity book);

    default List<AuthorDto> mapAuthors(BookEntity book) {
        return book.getAuthorsLink()
                .stream()
                .map(a -> AUTHOR_MAPPER.toAuthorDto(a.getAuthor()))
                .collect(Collectors.toList());
    }

    default List<TagDto> mapTags(BookEntity book) {
        return book.getTagsLink()
                .stream()
                .map(t -> TAG_MAPPER.toTagDto(t.getTag()))
                .collect(Collectors.toList());
    }

    default GenreDto mapGenre(BookEntity book) {
        return GENRE_MAPPER.toGenreDto(book.getGenreLink().getGenre());
    }

    default BookRatingDto mapRating(BookEntity book) {
        return RATING_MAPPER.toBookRatingDto(book);
    }

    default boolean mapIsBestseller(BookEntity book) {
        return book.getIsBestseller() == 1;
    }

    @Mapping(target = "authors", source = "volumeInfo")
    @Mapping(target = "image", source = "volumeInfo.imageLinks.thumbnail")
    @Mapping(target = "price", source = "saleInfo")
    @Mapping(target = "discountPrice", source = "saleInfo.listPrice.amount")
    BookDto toBookDto(VolumeInfo volumeInfo, SaleInfo saleInfo);

    default List<AuthorDto> mapAuthors(VolumeInfo volumeInfo) {
        return volumeInfo.getAuthors().stream()
                .map(a ->
                        new AuthorDto(a.substring(0, a.indexOf(" ")), a.substring(a.indexOf(" ") + 1, a.length() - 1)))
                .collect(Collectors.toList());
    }

    default int mapPrice(SaleInfo saleInfo) {
        return (int) saleInfo.getRetailPrice().getAmount();
    }
}

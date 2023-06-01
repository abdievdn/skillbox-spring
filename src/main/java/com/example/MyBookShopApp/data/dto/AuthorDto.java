package com.example.MyBookShopApp.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDto {

    private Integer id;

    private String description;

    private String firstName;

    private String lastName;

    private String photo;

    private String slug;

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}

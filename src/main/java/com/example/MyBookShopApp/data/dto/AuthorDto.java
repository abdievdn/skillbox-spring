package com.example.MyBookShopApp.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Author DTO", description = "Author's characteristics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @Schema(description = "Author's unique id number")
    private int id;
    @Schema(description = "Author's biography description")
    private String description;
    @Schema(description = "Author's first name")
    private String firstName;
    @Schema(description = "Author's last name")
    private String lastName;
    @Schema(description = "Author's photo picture")
    private String photo;
    @Schema(description = "Author's unique identify code")
    private String slug;

    public AuthorDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}

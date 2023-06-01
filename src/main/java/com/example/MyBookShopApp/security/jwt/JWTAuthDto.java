package com.example.MyBookShopApp.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JWTAuthDto {

    private String result;
    private String error;

    public JWTAuthDto(String result) {
        this.result = result;
    }
}

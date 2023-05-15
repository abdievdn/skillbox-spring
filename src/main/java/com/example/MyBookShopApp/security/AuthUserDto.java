package com.example.MyBookShopApp.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDto {

    private String name;
    private String email;
    private String phone;
    private String password;
}

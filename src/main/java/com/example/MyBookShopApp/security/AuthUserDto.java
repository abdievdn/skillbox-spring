package com.example.MyBookShopApp.security;

import lombok.Data;

@Data
public class AuthUserDto {

    private String name;
    private String email;
    private String phone;
    private String password;
}

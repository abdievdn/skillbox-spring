package com.example.MyBookShopApp.data.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String mail;
    private String phone;
    private String mailCode;
    private String phoneCode;
    private int balance;
}

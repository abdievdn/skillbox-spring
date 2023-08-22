package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String mail;
    private String phone;
    private String mailCode;
    private String phoneCode;
    private int balance;
}

package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactConfirmationDto {
    private String contact;
    private String code;
    private String type;
}

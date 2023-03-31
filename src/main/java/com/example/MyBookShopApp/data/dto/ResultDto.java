package com.example.MyBookShopApp.data.dto;

import lombok.Data;

@Data
public class ResultDto {
    Boolean result;
    String error;

    public ResultDto(Boolean result) {
        this.result = result;
    }

    public ResultDto(String error) {
        this.result = false;
        this.error = error;
    }
}

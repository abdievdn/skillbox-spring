package com.example.MyBookShopApp.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    Boolean result;
    String value;
    String error;

    public ResultDto(Boolean result) {
        this.result = result;
    }

    public ResultDto(String error) {
        this.result = false;
        this.error = error;
    }
}

package com.example.MyBookShopApp.data.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RatingDto {
    Short value;
    Integer count;
    Map<Short, Integer> values2Count;
}

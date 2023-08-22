package com.example.MyBookShopApp.data.dto.yookassa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YooAmountDto {
    private String value;
    private String currency;
}

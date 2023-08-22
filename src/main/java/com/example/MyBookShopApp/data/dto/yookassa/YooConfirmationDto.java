package com.example.MyBookShopApp.data.dto.yookassa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YooConfirmationDto {
    private String type;
    @JsonProperty("return_url")
    private String returnUrl;
    @JsonProperty("confirmation_url")
    private String confirmationUrl;
}

package com.example.MyBookShopApp.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactConfirmationDto {
    @JsonProperty("confirmation_type")
    private String confirmationType;
    private String contact;
    @JsonProperty("contact_type")
    private String contactType;
    private String code;
}

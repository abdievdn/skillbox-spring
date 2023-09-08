package com.example.MyBookShopApp.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Contact Confirmation DTO", description = "Data for confirm user's contact")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactConfirmationDto {

    @Schema(description = "Type for confirmation of contact", allowableValues = {"signin", "signup"})
    @JsonProperty("confirmation_type")
    private String confirmationType;
    @Schema(description = "User's contact")
    private String contact;
    @Schema(description = "Type of contact", allowableValues = {"mail", "phone"})
    @JsonProperty("contact_type")
    private String contactType;
    @Schema(description = "Code which were generated for confirmation of contact")
    private String code;
}

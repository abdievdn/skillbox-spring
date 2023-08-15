package com.example.MyBookShopApp.data.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private String id;
    private AmountDto amount;
    @JsonProperty("payment_method_data")
    private PaymentMethodDataDto paymentMethodData;
    private ConfirmationDto confirmation;
    private Boolean capture;
    private String description;
}

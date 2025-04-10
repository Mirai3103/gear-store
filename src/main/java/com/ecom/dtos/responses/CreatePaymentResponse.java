package com.ecom.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponse {
    private String orderId;
    @Builder.Default
    private boolean isRedirect = false;
    private String redirectUrl;
    @Builder.Default
    private boolean isSuccess = true;
}

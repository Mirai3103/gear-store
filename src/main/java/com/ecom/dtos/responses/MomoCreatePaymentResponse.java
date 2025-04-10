package com.ecom.dtos.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MomoCreatePaymentResponse {
    private  String partnerCode;
    private  String requestId;
    private  String orderId;
    private  float amount;
    private  long responseTime;
    private  String message;
    private  int resultCode;
    private  String payUrl;
    private  String deeplink;
    private  String qrCodeUrl;
    private  String deeplinkWebInApp;
}


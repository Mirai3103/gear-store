package com.ecom.dtos.responses;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor

@Data
public class MomoCallbackParam {

    private String partnerCode;
    private String orderId;
    private String requestId;
    private float amount;
    private String orderInfo;
    private String orderType;
    private String transId;
    private int resultCode;
    private String message;
    private String payType;
    private long responseTime;
    private String extraData;
    private String signature;


}

package com.ecom.service.impl;

import com.ecom.dtos.responses.CreatePaymentResponse;
import com.ecom.service.IPaymentStrategy;
import org.springframework.stereotype.Service;

@Service
public class CODPaymentStrategy implements IPaymentStrategy {
    @Override
    public CreatePaymentResponse createPayment(String orderId, Double amount, String paymentType, String currency) {
        return CreatePaymentResponse.builder().orderId(orderId).build();
    }
}

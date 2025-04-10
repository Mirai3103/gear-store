package com.ecom.service;

import com.ecom.dtos.responses.CreatePaymentResponse;

public interface IPaymentStrategy {
    public CreatePaymentResponse createPayment(String orderId, Double amount, String paymentType, String currency);
}

package com.ecom.service;

import com.ecom.service.impl.CODPaymentStrategy;
import com.ecom.service.impl.MomoQrPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFactory {
    private final MomoQrPaymentStrategy momoQrPaymentStrategy;
    private  final  CODPaymentStrategy codPaymentStrategy;
    public  IPaymentStrategy getPayment(String paymentType) {
        return switch (paymentType) {
            case "COD" -> codPaymentStrategy;
            case "MOMO_QR" -> momoQrPaymentStrategy;
            default -> null;
        };
    }
}

package com.ecom.service.impl;

import com.ecom.config.MomoConfig;
import com.ecom.dtos.responses.CreatePaymentResponse;
import com.ecom.dtos.responses.MomoCallbackParam;
import com.ecom.dtos.responses.MomoCreatePaymentResponse;
import com.ecom.service.IPaymentStrategy;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MomoQrPaymentStrategy implements IPaymentStrategy {
    public enum RequestType {
        QR_CODE("captureWallet"),
        PAY_WITH_ATM("payWithATM");

        private final String type;

        RequestType(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    private final MomoConfig momoConfig;

    @Override
    public CreatePaymentResponse createPayment(String orderId, Double amount, String paymentType, String currency) {
        return null;
    }

    public CreatePaymentResponse createPayment2(String paymentId, int amount, String orderInfo, RequestType requestType) throws Exception {

        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String secretKey = momoConfig.getSecretKey();
        var extraData = "Thanh toán đơn hàng";
        var orderId = UUID.randomUUID().toString();
        String sb = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + momoConfig.getIpnUrl() +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + momoConfig.getCallbackUrl() +
                "&requestId=" + paymentId +
                "&requestType=" + requestType.toString();
        var signature = signHmacSHA256(sb, secretKey);

        JSONObject body = new JSONObject();
        body.put("partnerCode", partnerCode);
        body.put("partnerName", "Ngô Hữu Hoàng");
        body.put("storeId", "CUAHANG_QUANAO");
        body.put("requestId", paymentId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", momoConfig.getCallbackUrl());
        body.put("ipnUrl", momoConfig.getIpnUrl());
        body.put("lang", "vi");
        body.put("extraData", extraData);
        body.put("requestType", requestType.toString());
        body.put("signature", signature);
        String url = "https://test-payment.momo.vn/v2/gateway/api/create";
        var restTemplate = new org.springframework.web.client.RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        HttpEntity<String> requestParams = new HttpEntity<>(body.toString(), requestHeaders);
        ResponseEntity<MomoCreatePaymentResponse> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.POST,
                requestParams,
                MomoCreatePaymentResponse.class
        );
        return CreatePaymentResponse.builder()
                .isRedirect(true)
                .redirectUrl(Objects.requireNonNull(response.getBody()).getPayUrl())
                .orderId(paymentId)
                .isSuccess(true)
                .build();
    }

    private String signHmacSHA256(String data, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(rawHmac);
    }

    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return sb.toString();
    }


    public JSONObject checkPaymentStatus(String paymentId, String orderId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String partnerCode = momoConfig.getPartnerCode();
        String accessKey = momoConfig.getAccessKey();
        String secretKey = momoConfig.getSecretKey();
        String sb = "accessKey=" + accessKey +
                "&orderId=" + orderId +
                "&partnerCode=" + partnerCode +
                "&requestId=" + paymentId;
        var signature = signHmacSHA256(sb, secretKey);

        JSONObject body = new JSONObject();
        body.put("partnerCode", partnerCode);
        body.put("requestId", paymentId);
        body.put("orderId", orderId);
        body.put("signature", signature);
        body.put("lang", "vi");
        String url = "https://test-payment.momo.vn/v2/gateway/api/query";
        var restTemplate = new org.springframework.web.client.RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        HttpEntity<String> requestParams = new HttpEntity<>(body.toString(), requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.POST,
                requestParams,
                String.class
        );
        return new JSONObject(response.getBody());
    }

    public Map<String, Object> handleCallback(MomoCallbackParam momoCallbackParam) {


        JSONObject jsonObject = null;
        try {
            jsonObject = this.checkPaymentStatus(momoCallbackParam.getRequestId(), momoCallbackParam.getOrderId());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Giao dịch không hợp lệ");
        }
        switch (jsonObject.getInt("resultCode")) {
            case 0 -> {
                return Map.of(
                        "status", "success",
                        "message", "Giao dịch thành công",
                        "orderId", momoCallbackParam.getOrderId()
                );
            }
            case 8000, 7000, 1000 -> {
                return Map.of(
                        "status", "pending",
                        "message", "Giao dịch đang chờ xử lý",
                        "orderId", momoCallbackParam.getOrderId()
                );
            }
            default -> {
                return Map.of(
                        "status", "failed",
                        "message", "Giao dịch thất bại",
                        "orderId", momoCallbackParam.getOrderId()
                );
            }
        }

    }


}

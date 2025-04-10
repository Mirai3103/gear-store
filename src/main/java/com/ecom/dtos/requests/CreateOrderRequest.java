package com.ecom.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private Integer userId;
    @NotBlank(message = "First name is required")
    private String fullName;
    @NotBlank(message = "Payment type is required")
    private String paymentType;
    private String note;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Phone is required")
    private String phone;
    @NotBlank(message = "Zip code is required")
    private String zip;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Country is required")
    private String country;
    private String discountCode;


    private String cardNumber;
    private String cardExpiryDate;
    private String cardCvv;


}

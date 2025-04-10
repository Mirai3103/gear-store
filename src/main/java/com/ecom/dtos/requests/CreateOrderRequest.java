package com.ecom.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    Integer userId;
    @NotBlank(message = "First name is required")
    String fullName;
    @NotBlank(message = "Payment type is required")
    String paymentType;
    String note;
    @NotBlank(message = "Address is required")
    String address;
    @NotBlank(message = "Email is required")
    String email;
    @NotBlank(message = "Phone is required")
    String phone;
    @NotBlank(message = "Zip code is required")
    String zip;
    @NotBlank(message = "City is required")
    String city;
    @NotBlank(message = "State is required")
    String state;
    @NotBlank(message = "Country is required")
    String country;
    String discountCode;
}

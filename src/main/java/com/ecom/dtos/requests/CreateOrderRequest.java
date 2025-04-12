package com.ecom.dtos.requests;

import com.ecom.model.Orders;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.core.annotation.Order;

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


    public Orders toEntity() {
        Orders order = new Orders();
        order.setOrderDate(java.time.LocalDate.now());
        order.setEmail(this.email);
        order.setPhoneNumber(this.phone);
        order.setAddress(this.address);
        order.setNote(this.note);
        order.setPaymentType(this.paymentType);
        order.setTotalMoney(0.0); // Set default value, you can change it later
        order.setStatus("IN_PROGRESS"); // Set default status
        order.setCardNumber(this.cardNumber);
        order.setCardExpiryDate(this.cardExpiryDate);
        order.setCardCvv(this.cardCvv);
        return order;
    }

}

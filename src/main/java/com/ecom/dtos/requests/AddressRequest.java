package com.ecom.dtos.requests;

import com.ecom.model.Address;
import com.ecom.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class AddressRequest {

    private int id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;


    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Phone number is required")
    @Size(max = 15, min = 3, message = "Mobile number must be at most 15 characters and at least 3 characters")
    private String phone;

    @NotBlank(message = "ZIP code is required")
    private String zip;
    private int userId;

    public Address toEntity() {
        Address address = new Address();
        address.setId(null);
        address.setFirstName(this.firstName);
        address.setLastName(this.lastName);
        address.setEmail(this.email);
        address.setAddress(this.address);
        address.setCountry(this.country);
        address.setPhone(this.phone);
        address.setPostalCode(this.zip);
        address.setUserId(this.userId);
        return address;
    }
}
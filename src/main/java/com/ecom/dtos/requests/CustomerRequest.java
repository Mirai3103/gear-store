package com.ecom.dtos.requests;

import lombok.Data;

@Data
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String note;
    private  String password;
    private  String uid;
}

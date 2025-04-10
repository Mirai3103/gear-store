package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
    private String country;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer userId;
    private String phone;
    private String email;
    private boolean isPrimary;
}

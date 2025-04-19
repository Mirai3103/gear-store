package com.ecom.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Orders")
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // user_id -> User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;  // đổi phone_number -> phoneNumber

    private String address;
    private String note;

    @OneToMany(mappedBy = "order", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderDetails> orderDetails = new ArrayList<>();

    @Column(name = "total_money")
    private Double totalMoney;

    // === Các trường thường dùng trong code ===

    private LocalDate orderDate;  // setOrderDate(...)

    private String paymentType;    // setPaymentType(...)

    private String status;         // setStatus(...), ví dụ "IN_PROGRESS", "PAID", v.v.


    //    === Thông tin thanh toán
    @JsonIgnore
    private String cardNumber;
    @JsonIgnore

    private String cardExpiryDate;
    @JsonIgnore

    private String cardCvv;
}

package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // user_id -> User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // product_id -> Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    /**
     * Nếu bạn muốn lưu tổng tiền (price * quantity) vào DB,
     * thì để field bình thường, KHÔNG @Transient.
     */
    @Transient
    private Double totalPrice;

    public  Double getFinalPrice() {
        return product.getFinalPrice() * quantity;
    }
}

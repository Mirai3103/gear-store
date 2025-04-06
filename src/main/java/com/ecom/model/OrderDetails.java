package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Order_Details") // Diagram
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Khóa ngoại order_id => map sang Orders
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    // Khóa ngoại product_id => map sang Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Column(name = "total_money")
    private Double totalMoney;
}

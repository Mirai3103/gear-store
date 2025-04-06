package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Gallery")
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // product_id -> Product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String thumbnail; // cột thumbnail (varchar(255)) theo diagram
}

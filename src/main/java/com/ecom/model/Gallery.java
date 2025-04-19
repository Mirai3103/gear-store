package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Gallery")
@Builder
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // product_id -> Product
    @ManyToOne()
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)

    private Product product;

    private String thumbnail; // cột thumbnail (varchar(255)) theo diagram
}

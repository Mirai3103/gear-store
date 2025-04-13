package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "Import_Details")
public class ImportDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // import_id -> Import
    @ManyToOne
    @JoinColumn(name = "import_id")
    private Import importRecord;

    // product_id -> Product
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "product_id")
    private Integer productId; // cột product_id (FK) theo diagram

    private Double cost;       // chi phí nhập 1 đơn vị
    private Integer quantity;
    private Double total_money;
}

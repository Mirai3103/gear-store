package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Import importRecord;

    // product_id -> Product
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(name = "product_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer productId; // cột product_id (FK) theo diagram

    private Double cost;       // chi phí nhập 1 đơn vị
    private Integer quantity;
    private Double total_money;
}

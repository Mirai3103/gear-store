package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter()
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "Product") // Tên bảng trong DB
@SQLRestriction("is_deleted = false")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * DB cột: name (varchar(255))
     */
    private String name;

    /**
     * DB cột: category_id (FK)
     * => map sang Entity Category (nếu có)
     */
    @ManyToOne(fetch = FetchType.EAGER) // Nhiều sản phẩm thuộc về 1 danh mục
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)

    private Category category;

    @Column(name = "price") // cột DB = price (varchar)
    private Float price;


    /**
     * DB cột: discount (int)
     */
    private Integer discount;

    /**
     * DB cột: image (varchar(255))
     */
    private String image;

    /**
     * DB cột: stock (int not null)
     */
    private Integer stock;

    /**
     * DB cột: created_at (datetime)
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * DB cột: updated_at (datetime)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * DB cột: description (nếu là longtext)
     * => dùng @Lob để JPA map cột TEXT / LONGTEXT
     */

    @Lob
    @Column(columnDefinition = "TEXT") // cột DB = description (TEXT)
    private String description;

    public Double getFinalPrice() {
        double finalPrice = price - (price * (discount * 1.0) / 100);
        return Math.round(finalPrice * 100.0) / 100.0;
    }

    @Column(name = "final_price", columnDefinition = "DECIMAL(10,2) dEFAULT 0.00")
    private Double finalPrice;

    @PrePersist
    @PreUpdate
    public void calculateFinalPrice() {
        finalPrice = getFinalPrice();
    }


    @Transient
    private List<Gallery> galleries;
    @Column(name = "is_deleted", columnDefinition = "BIT DEFAULT 0")

    private boolean isDeleted = false; // Trạng thái sản phẩm đã xóa hay chưa
}

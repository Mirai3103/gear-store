package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Product") // Tên bảng trong DB
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
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "price") // cột DB = price (varchar)
    private String price;
    

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
    private LocalDateTime createdAt;

    /**
     * DB cột: updated_at (datetime)
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * DB cột: description (nếu là longtext)
     * => dùng @Lob để JPA map cột TEXT / LONGTEXT
     */

    private String description;
}

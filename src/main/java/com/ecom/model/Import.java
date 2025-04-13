package com.ecom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Import")
public class Import {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "import_date")
    private LocalDateTime importDate;

    @OneToMany(mappedBy = "importRecord", fetch = FetchType.EAGER)
    private List<ImportDetails> details;

    private String note; // Lý do nhập
    private Double total_money;
}

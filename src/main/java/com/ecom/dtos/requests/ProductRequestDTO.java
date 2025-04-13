package com.ecom.dtos.requests;

import com.ecom.model.Category;
import com.ecom.model.Gallery;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ProductRequestDTO {
    private Integer id;
    private String name;
    private int categoryId;
    private Float price;
    private Integer discount;
    private Integer stock;
    private String description;
    private MultipartFile image;
    private List<MultipartFile> galleries;
}

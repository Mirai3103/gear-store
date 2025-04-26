package com.ecom.dtos.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
    private List<String> removedGalleryIds = List.of();
    private String removedGalleryIdsArrayStr;

    public void parseRemovedGalleryIds() {
        if (removedGalleryIdsArrayStr != null && !removedGalleryIdsArrayStr.isEmpty()) {
            removedGalleryIds = List.of(removedGalleryIdsArrayStr.split(","));
        }
    }
}

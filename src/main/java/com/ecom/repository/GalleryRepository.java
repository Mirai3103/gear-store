package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.Gallery;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {
    // Custom query methods can be defined here if needed
    // For example, you can add methods to find galleries by user or other criteria
    List<Gallery> findByProductId(Integer productId);
    
}

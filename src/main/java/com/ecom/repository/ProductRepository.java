package com.ecom.repository;

import com.ecom.model.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Tìm theo tên sản phẩm (cột `name`) ignoring case
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Tìm theo Category ID (nếu bạn muốn lọc theo category_id)
     */
    List<Product> findByCategory_Id(Integer catId);

    Page<Product> findByCategory_Id(Integer catId, Pageable pageable);

    /**
     * Tìm theo tên category ignoring case
     * (Category có field `name`)
     */
    List<Product> findByCategory_NameContainingIgnoreCase(String catName);

    Page<Product> findByCategory_NameContainingIgnoreCase(String catName, Pageable pageable);

    /**
     * Tìm theo name OR category.name, đều ignoring case
     */
    List<Product> findByNameContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(
            String name,
            String catName
    );

    Page<Product> findByNameContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(
            String name,
            String catName,
            Pageable pageable
    );


    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :stock WHERE p.id = :id")
    void increaseStockById(Integer id, Integer stock);

}

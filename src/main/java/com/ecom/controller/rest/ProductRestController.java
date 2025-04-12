package com.ecom.controller.rest;

import com.ecom.dtos.requests.ProductQuery;
import com.ecom.model.Product;
import com.ecom.service.ProductService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @PostMapping(value = "/xlsx", consumes = "multipart/form-data")
    public ResponseEntity<List<Product>> uploadFromXlsx(
            @RequestParam("file") MultipartFile file) {
        try {
            List<Product> products = productService.uploadProductsFromExcel(file);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProducts(ProductQuery query, HttpServletResponse response) {
        List<Product> products = productService.getAllProductsByQuery(query);
        var count = productService.countAllProductsByQuery(query);
        response.setHeader("X-Total-Count", String.valueOf(count));
        response.setContentType("application/json");
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

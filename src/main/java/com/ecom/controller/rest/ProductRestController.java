package com.ecom.controller.rest;

import com.ecom.dtos.requests.ProductQuery;
import com.ecom.dtos.requests.ProductRequestDTO;
import com.ecom.model.Product;
import com.ecom.repository.GalleryRepository;
import com.ecom.service.ProductService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> createProduct(
            @ModelAttribute ProductRequestDTO productRequestDTO,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "galleries", required = false) List<MultipartFile> galleries) throws IOException {

        productRequestDTO.setImage(image);
        productRequestDTO.setGalleries(galleries);
        Product product = productService.saveProduct(productRequestDTO);
        return ResponseEntity.ok(product);
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

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<Product> upload(
            @ModelAttribute ProductRequestDTO productRequestDTO,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "galleries", required = false) List<MultipartFile> galleries,
            @PathVariable Integer id) throws IOException {

        productRequestDTO.setId(id);
        productRequestDTO.setImage(image);
        productRequestDTO.setGalleries(galleries);
        Product product = productService.updateProduct(productRequestDTO);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

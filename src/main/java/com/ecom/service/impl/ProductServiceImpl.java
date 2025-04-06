package com.ecom.service.impl;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        // Lưu sản phẩm mới
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {
        Product dbProduct = getProductById(product.getId());
        if (dbProduct == null) return null;

        // Mapping lại các field
        dbProduct.setName(product.getName());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setStock(product.getStock());

        // Nếu product có `product.setCategory(...)` => ta cũng gán:
        // dbProduct.setCategory(product.getCategory());

        // Ảnh
        String imageName = (!image.isEmpty()) ? image.getOriginalFilename() : dbProduct.getImage();
        dbProduct.setImage(imageName);

        Product updated = productRepository.save(dbProduct);

        // Upload file ảnh
        if (updated != null && !image.isEmpty()) {
            try {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator
                                      + "product_img" + File.separator
                                      + image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return updated;
    }

    // -------------------- Các hàm “Active” / “Search” --------------------

    /**
     * Lấy danh sách Product, nếu category = rỗng => findAll,
     * ngược lại => tìm theo category.name (ignore case).
     */
    @Override
    public List<Product> getAllActiveProducts(String category) {
        if (org.springframework.util.ObjectUtils.isEmpty(category)) {
            // trả về tất cả
            return productRepository.findAll();
        } else {
            // tìm theo category.name ignoring case
            return productRepository.findByCategory_NameContainingIgnoreCase(category);
        }
    }

    /**
     * Tìm sản phẩm theo từ khoá `ch` (ignore case).
     */
    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByNameContainingIgnoreCase(ch);
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByNameContainingIgnoreCase(ch, pageable);
    }

    /**
     * Phân trang + lọc category => findByCategory_Name..., 
     * nếu category rỗng => findAll(pageable).
     */
    @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        if (org.springframework.util.ObjectUtils.isEmpty(category)) {
            return productRepository.findAll(pageable);
        } else {
            // category != null => tìm theo category.name ignoring case
            return productRepository.findByCategory_NameContainingIgnoreCase(category, pageable);
        }
    }

    /**
     * Tìm kiếm theo tên OR category.name (đều ignoring case),
     * + phân trang, tuỳ logic “category + ch”.
     */
    @Override
    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        if (!org.springframework.util.ObjectUtils.isEmpty(category)) {
            // Tìm name OR category.name ignoring case
            return productRepository.findByNameContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(
                    ch, category, pageable
            );
        } else {
            // chỉ tìm theo name
            return productRepository.findByNameContainingIgnoreCase(ch, pageable);
        }
    }
}

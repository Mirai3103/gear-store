package com.ecom.service.impl;

import com.ecom.dtos.requests.ProductQuery;
import com.ecom.model.Gallery;
import com.ecom.model.Product;
import com.ecom.repository.GalleryRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private GalleryRepository galleryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EntityManager entityManager;

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
        if (dbProduct == null)
            return null;

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
                    ch, category, pageable);
        } else {
            // chỉ tìm theo name
            return productRepository.findByNameContainingIgnoreCase(ch, pageable);
        }
    }

    @Override
    public List<Gallery> getAllProductsGallery(Integer id) {
        return galleryRepository.findByProductId(id);

    }

    @Override
    public List<Product> getAllProductsByQuery(ProductQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> productRoot = cq.from(Product.class);


        // Apply predicates (filters)
        Predicate[] predicateArray = createPredicates(cb, productRoot, query);
        cq.where(cb.and(predicateArray));

        if (query.getSortBy() != null) {
            switch (query.getSortBy()) {
                case FEATURED:
                    cq.orderBy(cb.desc(productRoot.get("featured"))); // Example field for sorting
                    break;
                case BEST_SELLING:
                    cq.orderBy(cb.desc(productRoot.get("sales"))); // Example field for best selling
                    break;
                case ALPHABETICALLY_A_Z:
                    cq.orderBy(cb.asc(productRoot.get("name")));
                    break;
                case ALPHABETICALLY_Z_A:
                    cq.orderBy(cb.desc(productRoot.get("name")));
                    break;
                case PRICE_LOW_TO_HIGH:
                    cq.orderBy(cb.asc(productRoot.get("price")));
                    break;
                case PRICE_HIGH_TO_LOW:
                    cq.orderBy(cb.desc(productRoot.get("price")));
                    break;
                case DATE_OLD_TO_NEW:
                    cq.orderBy(cb.asc(productRoot.get("createdAt")));
                    break;
                case DATE_NEW_TO_OLD:
                    cq.orderBy(cb.desc(productRoot.get("createdAt")));
                    break;
                default:
                    cq.orderBy(cb.desc(productRoot.get("id"))); // Default sorting by ID
                    break;
            }
        } else {
            cq.orderBy(cb.desc(productRoot.get("id"))); // Default sorting by ID
        }

        TypedQuery<Product> typedQuery = entityManager.createQuery(cq);
        int pageSize = query.getPageSize();
        int pageNumber = query.getPage()-1; // Assuming page starts from 1
        typedQuery.setFirstResult(pageNumber * pageSize);
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();
    }

    @Override
    public Long countAllProductsByQuery(ProductQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> productRoot = cq.from(Product.class);
        cq.select(cb.count(productRoot));
        Predicate[] predicateArray = createPredicates(cb, productRoot, query);
        cq.where(cb.and(predicateArray));
        TypedQuery<Long> typedQuery = entityManager.createQuery(cq);
        return typedQuery.getSingleResult();
    }


    private Predicate[] createPredicates(CriteriaBuilder cb, Root<Product> productRoot, ProductQuery query) {
        List<Predicate> predicates = new ArrayList<>();

        if (query.getSearch() != null && !query.getSearch().isEmpty()) {
            predicates.add(cb.like(cb.lower(productRoot.get("name")), "%" + query.getSearch().toLowerCase() + "%"));
        }
        if (query.getIsInStock() != null) {
            if (query.getIsInStock()) {
                predicates.add(cb.greaterThan(productRoot.get("stock"), 0));
            } else {
                predicates.add(cb.equal(productRoot.get("stock"), 0));
            }
        }

        if (query.getCategoryId() != null  &&!CollectionUtils.isEmpty(query.getCategoryId())) {
            predicates.add(cb.and(productRoot.get("category").get("id").in(query.getCategoryId())));
        }

        if (query.getPriceFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(productRoot.get("price"), query.getPriceFrom()));
        }

        if (query.getPriceTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(productRoot.get("price"), query.getPriceTo()));
        }

        return predicates.toArray(new Predicate[0]);
    }
}

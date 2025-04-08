package com.ecom;


import com.ecom.model.Category;
import com.ecom.model.Gallery;
import com.ecom.model.Product;
import com.ecom.repository.CategoryRepository;
import com.ecom.repository.GalleryRepository;
import com.ecom.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.util.List;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedData implements CommandLineRunner {

    private final ResourceLoader resourceLoader;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final GalleryRepository galleryRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // if seed data in args then seed the data
        if (!Arrays.asList(args).contains("seed")) {
            return;
        }
        log.info("Seeding data...");
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        galleryRepository.deleteAll();

        Resource resource = resourceLoader.getResource("classpath:static/file.json");
        if (!resource.exists()) {
            return;
        }

        // Read the JSON file as JsonNode
        JsonNode data = objectMapper.readTree(resource.getInputStream());
        Faker faker = new Faker();

        // Iterate through each node in the JSON array
        for (JsonNode item : data) {
            String categoryName = item.get("category").asText().trim();
            var existsCategory = categoryRepository.findByName(categoryName);
            if (existsCategory == null) {
                existsCategory = categoryRepository.save(Category.builder().name(categoryName).build());
            }

            // Get images from the JSON node
            JsonNode imagesNode = item.get("images");
            List<String> images = objectMapper.convertValue(imagesNode, List.class);

            var mn =item.get("price").asText().trim();
            if(StringUtils.isBlank(mn)){
                mn=faker.number().numberBetween(50,500)+"";
            }
            // Create the product entity
            var product = Product.builder()
                    .name(item.get("title").asText().trim())
                    .description(item.get("des").asText().trim())
                    .price(Float.parseFloat(mn))
                    .category(existsCategory)
                    .discount(0)
                    .createdAt(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                    .updatedAt(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                    .stock(faker.number().numberBetween(0, 300))
                    .image(images.stream().findFirst().orElse("https://placewaifu.com/image/300/400"))
                    .build();

            product = productRepository.save(product);

            // Save gallery images
            for (String image : images) {
                galleryRepository.save(Gallery.builder()
                        .product(product)
                        .thumbnail(image)
                        .build());
            }
        }
    }
}

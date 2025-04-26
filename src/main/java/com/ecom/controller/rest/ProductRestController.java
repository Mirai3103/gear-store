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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/form/product")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @PostMapping(value = "/xlsx", consumes = "multipart/form-data")
    public String uploadFromXlsx(
            @RequestParam("file") MultipartFile file) {
        try {
            List<Product> products = productService.uploadProductsFromExcel(file);
            return "redirect:/Admin/Profile";
        } catch (Exception e) {
            return "redirect:/Admin/Profile";
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public String createProduct(
            @ModelAttribute ProductRequestDTO productRequestDTO,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "galleries", required = false) List<MultipartFile> galleries) throws IOException {

        productRequestDTO.setImage(image);
        productRequestDTO.setGalleries(galleries);
        Product product = productService.saveProduct(productRequestDTO);
        return "redirect:/Admin/Profile";
    }


    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Integer id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return "redirect:/Admin/Profile";
        } else {
            return "redirect:/Admin/Profile";
        }
    }


    @PostMapping("/{id}/update")
    public String upload(
            @ModelAttribute ProductRequestDTO productRequestDTO,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "galleries", required = false) List<MultipartFile> galleries,
            @PathVariable Integer id) throws IOException {

        productRequestDTO.setId(id);
        productRequestDTO.parseRemovedGalleryIds();

        productRequestDTO.setImage(image);
        productRequestDTO.setGalleries(galleries);
        if (image.getBytes().length == 0) {
            productRequestDTO.setImage(null);
        }
        if (galleries.stream().allMatch(MultipartFile::isEmpty)) {
            productRequestDTO.setGalleries(null);
        }
        Product product = productService.updateProduct(productRequestDTO);
        if (product != null) {
            return "redirect:/Admin/Profile";
        } else {
            return "redirect:/Admin/Profile";
        }
    }

}

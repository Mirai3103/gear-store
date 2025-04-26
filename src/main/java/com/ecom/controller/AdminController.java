package com.ecom.controller;

import com.ecom.dtos.requests.ProductQuery;
import com.ecom.model.Category;
import com.ecom.model.Import;
import com.ecom.model.ImportDetails;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ImportService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.impl.ProductServiceImpl;
import com.ecom.service.impl.ReportService;
import com.ecom.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final UserServiceImpl userService;
    private final ImportService importService;
    private final ProductService productService;
    private final ReportService reportService;

    private ImportDetails briefImportDetails(ImportDetails details) {
        if (details.getProduct() != null) {
            details.getProduct()
                    .setDescription(null);
            details.getProduct()
                    .setGalleries(null);
        }

        details.setImportRecord(null);
        return details;
    }

    @GetMapping("/Admin/Profile")
    public String adminProfile(Model model) {


        List<Category> categories = categoryService.getAllCategory();

        model.addAttribute("categories", categories);
        var customersPaged = userService.getCustomers(0, 1000000, "");
        model.addAttribute("customers", customersPaged.getContent());
        List<Import> imports = importService.getAllImport(0, 100000, "").stream().peek(i -> i.setDetails(i.getDetails().stream().map(this::briefImportDetails).toList())).toList();
        model.addAttribute("imports", imports);
        var productQuery = new ProductQuery();
        productQuery.setPage(1);
        productQuery.setPageSize(100000);
        productQuery.setSortBy(ProductQuery.SortBy.DATE_NEW_TO_OLD);
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        var ordersPaged = orderService.getAllOrdersPagination(0, 1000000);
        model.addAttribute("orders", ordersPaged.getContent());
        var nowYear = Calendar.getInstance().get(Calendar.YEAR);
        model.addAttribute("reports", reportService.getReport(nowYear));
        return "admin/adminProfile";
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        log.info("categories length: {}", categories.size());
        model.addAttribute("categories", categories);

        // Add your logic to fetch product data and add it to the model
        return "admin/products";
    }
}


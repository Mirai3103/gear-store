package com.ecom.controller;

import com.ecom.dtos.requests.ProductQuery;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.ecom.service.CartService;
// import các service khác nếu cần

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class MappingController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    // ----------------------------------------------------
    // 1. All Products => "/All_Product"
    // ----------------------------------------------------
   
    
   
    @GetMapping("/All_Product")
    public String allProduct(ProductQuery query,Model m) {
        log.info("All Productll Product: {}", query);
        List<Product> allProducts = productService.getAllProducts();
        m.addAttribute("Products", allProducts);
        m.addAttribute("pageTitle", "All Products");
        
        return "shop"; 
    }

    // ----------------------------------------------------
    // 2. Contact => "/Contact"
    // ----------------------------------------------------
    @GetMapping("/Contact")
    public String contact() {
        // Trả về trang contact.html
        return "contact";
    }

    // ----------------------------------------------------
    // 3. Cart => "/Cart"
    //    (trong UserController đã có /user/cart)
    //    => có thể redirect để xài logic cũ
    // ----------------------------------------------------
    @GetMapping("/Cart")
    public String cart() {
        // Điều hướng sang /user/cart
        return "redirect:/user/cart";
    }

    // ----------------------------------------------------
    // 4. Profile => "/profile"
    //    (trong UserController đã có /user/profile)
    //    => có thể redirect để xài logic cũ
    // ----------------------------------------------------
    @GetMapping("/profile")
    public String profile() {
        // Điều hướng sang /user/profile
        return "redirect:/user/profile";
    }

    // ----------------------------------------------------
    // 5. Address => "/Address"
    // ----------------------------------------------------
    @GetMapping("/Address")
    public String address() {
        // Trả về trang address.html
        return "address";
    }

    // ----------------------------------------------------
    // 6. Checkout => "/checkout"
    //    (bạn có thể muốn logic thanh toán,
    //     hoặc redirect sang /user/orders)
    // ----------------------------------------------------
    @GetMapping("/checkout")
    public String checkout() {
        // Tuỳ logic, tạm thời redirect qua /user/orders
        return "redirect:/user/orders";
    }

    // ----------------------------------------------------
    // 7. Product-view => "/product-view/{id}"
    //    (front-end đang dùng link "/product-view/xxx")
    //    => Tạo mapping tương ứng
    // ----------------------------------------------------
    @GetMapping("/product-view/{id}")
    public String productView(@PathVariable int id, Model m) {
        Product product = productService.getProductById(id);
        if (ObjectUtils.isEmpty(product)) {
            // xử lý nếu không tìm thấy sản phẩm
            return "redirect:/All_Product";
        }
        m.addAttribute("galleries", productService.getAllProductsGallery(id));

        m.addAttribute("product", product);
        // Template "product-view.html"
        return "product-view";
    }

    // ----------------------------------------------------
    // 8. Latest, Console, Game, Accessory
    //    => Trong base.html có link nhưng bạn chưa mapping
    //    => Tạo các hàm tạm trả về "shop.html"
    // ----------------------------------------------------
    @GetMapping("/Latest")
    public String latest(Model m) {
        // Tuỳ ý load sản phẩm "mới" => tạm trả về shop.html
        // ...
        m.addAttribute("pageTitle", "Latest Products");
        return "shop";
    }

    @GetMapping("/Console")
    public String console(Model m) {
        // Tuỳ ý filter sản phẩm "Console" => tạm trả về shop.html
        m.addAttribute("pageTitle", "Console");
        return "shop";
    }

    @GetMapping("/Game")
    public String game(Model m) {
        // ...
        m.addAttribute("pageTitle", "Game");
        return "shop";
    }

    @GetMapping("/Accessory")
    public String accessory(Model m) {
        // ...
        m.addAttribute("pageTitle", "Accessory");
        return "shop";
    }

    // ----------------------------------------------------
    // 9. Các link con (Home Console, Handheld Console, ...)
    //    => Nếu bạn muốn hiển thị riêng, tạo mapping tương tự
    // ----------------------------------------------------
    @GetMapping("/Console/Home")
    public String consoleHome(Model m) {
        // ...
        return "shop";
    }

    @GetMapping("/Console/Handheld")
    public String consoleHandheld(Model m) {
        // ...
        return "shop";
    }

    // Tương tự cho "Limited Edition", "Mod Console", ...
    // Tương tự cho "CD-ROM", "Cartridge", "Host", ...
    // Tương tự cho "Controller", "Adapter", "Amiibo", "Case", ...
}

package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final CartService cartService;

    @GetMapping("/addCart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String,Object>> addToCart(@RequestParam Integer pid,
                                         @RequestParam Integer uid,
                                         @RequestParam Integer quantity,
                                         HttpSession session) {
        // Gọi service để thêm sản phẩm vào giỏ
        Cart saveCart = cartService.saveCart(pid, uid, quantity);
        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Product add to cart failed");
        } else {
            session.setAttribute("succMsg", "Product added to cart");
        }
        Integer countCart = cartService.getCountCart(uid);
        session.setAttribute("cartItemsLength", countCart);
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "Product added to cart successfully",
                "cartCount", countCart));
    }

}

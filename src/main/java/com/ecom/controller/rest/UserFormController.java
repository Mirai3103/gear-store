package com.ecom.controller.rest;

import com.ecom.dtos.requests.CustomerRequest;
import com.ecom.dtos.requests.RegisterRequest;
import com.ecom.model.Cart;
import com.ecom.model.User;
import com.ecom.service.CartService;
import com.ecom.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/form/user")
@RequiredArgsConstructor
public class UserFormController {

    private final CartService cartService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/addCart")
    @PreAuthorize("isAuthenticated()")
    public String addToCart(@RequestParam Integer pid,
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
        return "redirect:/user/cart";

    }


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String abc(@ModelAttribute @Valid CustomerRequest registerRequest,
                      HttpSession session, RedirectAttributes ra) {
        if (userService.existsEmail(registerRequest.getEmail())) {
            ra.addFlashAttribute("alert", "Email already exists");
            return "redirect:/Admin/Profile";
        }


        // Process valid registration
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setNote(registerRequest.getNote());

        userService.saveUser(user);
        return "redirect:/Admin/Profile";
    }

    @PostMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteUser(@RequestParam Integer uid) {
        // Gọi service để thêm sản phẩm vào giỏ
        userService.deleteUser(uid);
        return "redirect:/Admin/Profile";
    }

    @PostMapping("edit")
    @PreAuthorize("isAuthenticated()")
    public String updateUser(@RequestBody CustomerRequest registerRequest, RedirectAttributes ra
    ) {
        if (userService.existsEmail(registerRequest.getEmail())) {
            ra.addFlashAttribute("alert", "Email already exists");
            return "redirect:/Admin/Profile";
        }

        User user = new User();
        user.setId(Integer.valueOf(registerRequest.getUid()));
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setNote(registerRequest.getNote());
        if (!ObjectUtils.isEmpty(registerRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        }

        userService.updateUser(user);
        return "redirect:/Admin/Profile";
    }

    @PostMapping("/toggle-lock")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String toggleLockUser(@RequestParam Integer uid) {
        // Gọi service để thêm sản phẩm vào giỏ
        userService.toggleLockUser(uid);
        return "redirect:/Admin/Profile";
    }

    @PostMapping("/update-password")
    @PreAuthorize("isAuthenticated()")
    public String updatePassword(@RequestParam String password,
                                 @RequestParam Integer uid) {
        userService.updateUserPassword(uid, password);

        return "redirect:/Admin/Profile";
    }


}

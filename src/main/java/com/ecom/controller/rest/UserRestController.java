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
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final CartService cartService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/addCart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Integer pid,
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


    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody @Valid CustomerRequest registerRequest,
                                                         HttpSession session) {
        if (userService.existsEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error",
                            "message", "Email already exists"));
        }


        // Process valid registration
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setNote(registerRequest.getNote());

        userService.saveUser(user);
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "User registered successfully"));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestParam Integer uid) {
        // Gọi service để thêm sản phẩm vào giỏ
        userService.deleteUser(uid);
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "User deleted successfully"));
    }

    @PatchMapping("{uid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody CustomerRequest registerRequest,
                                                          @PathVariable Integer uid) {
        if (userService.existsEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error",
                            "message", "Email already exists"));
        }

        // Process valid registration
        User user = new User();
        user.setId(uid);
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhone());
        user.setNote(registerRequest.getNote());
        if (!ObjectUtils.isEmpty(registerRequest.getPassword())) {
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        }

        userService.updateUser(user);
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "User updated successfully"));
    }

    @PatchMapping("/toggle-lock")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleLockUser(@RequestParam Integer uid) {
        // Gọi service để thêm sản phẩm vào giỏ
        userService.toggleLockUser(uid);
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "User lock status updated successfully"));
    }

    @PatchMapping("{uid}/update-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, String> request,
                                                              @PathVariable Integer uid) {
        String newPassword = request.get("password");
        userService.updateUserPassword(uid, newPassword);

        return ResponseEntity.ok(Map.of("status", "success",
                "message", "Password updated successfully"));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) throws MessagingException {
        String email = request.get("email");
        userService.sendResetPassword(email);

        return ResponseEntity.ok(Map.of("status", "success",
                "message", "Reset password link sent successfully"));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error",
                            "message", "Invalid token"));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        return ResponseEntity.ok(Map.of("status", "success",
                "message", "Password reset successfully"));
    }

    @GetMapping("isEmailExist")
    public ResponseEntity<Map<String, Object>> isEmailExist(@RequestParam String email) {
        boolean exists = userService.existsEmail(email);
        return ResponseEntity.ok(Map.of("status", "success",
                "isExist", exists));
    }

}

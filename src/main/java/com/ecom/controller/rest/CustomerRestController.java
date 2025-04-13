package com.ecom.controller.rest;

import com.ecom.controller.UserController;
import com.ecom.model.User;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customer")
public class CustomerRestController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllCustomers(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "") String search,
                                                      @RequestParam(defaultValue = "30") int size, HttpServletResponse response) {
        var customersPaged = userService.getCustomers(page - 1, size, search);
        response.setHeader("X-Total-Count", String.valueOf(customersPaged.getTotalElements()));
        response.setContentType("application/json");
        return ResponseEntity.ok(customersPaged.getContent());
    }
}

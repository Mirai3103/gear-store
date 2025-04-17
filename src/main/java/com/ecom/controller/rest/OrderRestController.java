package com.ecom.controller.rest;

import com.ecom.model.Orders;
import com.ecom.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Integer id,
                                                    @RequestParam String status,
                                                    HttpServletRequest request) {
        Orders updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping
    public ResponseEntity<List<Orders>> getAllOrders(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "30") int size, HttpServletResponse response) {
        var ordersPaged = orderService.getAllOrdersPagination(page - 1, size);
        response.setHeader("X-Total-Count", String.valueOf(ordersPaged.getTotalElements()));
        response.setContentType("application/json");

        return ResponseEntity.ok(ordersPaged.getContent());
    }


}

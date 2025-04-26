package com.ecom.controller.rest;

import com.ecom.model.Orders;
import com.ecom.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/form/order")
@RequiredArgsConstructor
public class OrderFormController {
    private final OrderService orderService;

    @PostMapping("status")
    public String updateOrderStatus(@RequestParam Integer orderId,
                                    @RequestParam String status,
                                    HttpServletRequest request) {
        Orders updatedOrder = orderService.updateOrderStatus(orderId, status);
        return "redirect:/Admin/Profile";
    }


}

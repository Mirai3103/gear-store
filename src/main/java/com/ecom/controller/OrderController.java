package com.ecom.controller;

import com.ecom.config.CustomUser;
import com.ecom.dtos.requests.CreateOrderRequest;
import com.ecom.service.AddressService;
import com.ecom.service.CartService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@AllArgsConstructor
@Controller
@RequestMapping("/cart/order")
public class OrderController {
    private final UserService userService;
    private final CartService cartService;
    private final AddressService addressService;
    private final OrderService orderService;


    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public String order(Model model, Authentication authentication) {

        CustomUser user = (CustomUser) authentication.getPrincipal();
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setUserId(user.getUser().getId());
        createOrderRequest.setEmail(user.getUser().getEmail());
        createOrderRequest.setEmail(user.getUser().getPhoneNumber());
        createOrderRequest
                .setFullName(user.getUser().getName());
        model.addAttribute("createOrderRequest", createOrderRequest);
        var userAddress = addressService.getAddressByUserId(user.getUser().getId());
        if (userAddress != null) {
            createOrderRequest.setFullName(userAddress.getFirstName() + " " + userAddress.getLastName());
            createOrderRequest.setAddress(userAddress.getAddress());
            createOrderRequest.setCountry(userAddress.getCountry());
            createOrderRequest.setZip(userAddress.getPostalCode());
            createOrderRequest.setPhone(userAddress.getPhone());
            createOrderRequest.setEmail(userAddress.getEmail());
        }
        var totalPrice = cartService.getTotalPrice(user.getUser().getId());
        model.addAttribute("totalPrice", String.format("$%.2f", totalPrice));
        return "checkout";
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public String orderPost(Model model, Authentication authentication,
                            CreateOrderRequest createOrderRequest) throws Exception {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        createOrderRequest.setUserId(user.getUser().getId());
        boolean isOk = orderService.saveOrder(user.getUser().getId(), createOrderRequest);
        if (!isOk) {
            model.addAttribute("error", "Order failed");
            return "checkout-result";
        }
        return "checkout-result";
    }

    @GetMapping("test")
    public String test() throws Exception {

        return "checkout-result";
    }
}

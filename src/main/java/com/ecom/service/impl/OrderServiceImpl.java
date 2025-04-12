package com.ecom.service.impl;

import com.ecom.dtos.requests.CreateOrderRequest;
import com.ecom.exceptions.NotFoundException;
import com.ecom.model.Cart;
import com.ecom.model.Orders;
import com.ecom.model.OrderDetails;
import com.ecom.repository.CartRepository;
import com.ecom.repository.OrderDetailsRepository;
import com.ecom.repository.OrdersRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil; // Nếu bạn có gửi mail

    @Override
    public boolean saveOrder(Integer userId, CreateOrderRequest orderRequest) throws Exception {
        List<Cart> carts = cartRepository.findByUserId(userId);
        if (carts.isEmpty()) {
            // Không có gì để đặt
            return false;
        }

        // 1) Tính tổng tiền từ giỏ
        double totalMoney = 0.0;
        for (Cart cart : carts) {
            // Lấy cột price (String) => parseDouble
            String priceStr = cart.getProduct().getPrice().toString();
            double numericPrice = Double.parseDouble(priceStr);

            double realDiscountPrice = numericPrice
                    - (numericPrice * cart.getProduct().getDiscount() / 100.0);

            totalMoney += (realDiscountPrice * cart.getQuantity());
        }

        // 2) Tạo Orders
        Orders orders = orderRequest.toEntity();
        orders.setTotalMoney(totalMoney);
        orders.setUser(carts.get(0).getUser());
        // Nếu DB có cột orderDate, paymentType, status
        orders.setOrderDate(LocalDate.now());
        orders.setPaymentType(orderRequest.getPaymentType());
        orders.setStatus(OrderStatus.IN_PROGRESS.getName());

        Orders savedOrders = ordersRepository.save(orders);

        // 3) Tạo các OrderDetails
        for (Cart cart : carts) {
            OrderDetails od = new OrderDetails();
            od.setOrder(savedOrders);
            od.setProduct(cart.getProduct());
            od.setQuantity(cart.getQuantity());

            // Tính lại line total
            String priceStr = cart.getProduct().getPrice().toString();
            double numericPrice = Double.parseDouble(priceStr);
            double realDiscountPrice = numericPrice
                    - (numericPrice * cart.getProduct().getDiscount() / 100.0);

            double lineTotal = realDiscountPrice * cart.getQuantity();
            od.setTotalMoney(lineTotal);

            orderDetailsRepository.save(od);
        }

        // 4) Xoá toàn bộ cart
        cartRepository.deleteAll(carts);

        // 5) Nếu bạn có logic gửi mail
//        try {
//            // commonUtil.sendMailForOrders(savedOrders, "success");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }

    @Override
    public List<Orders> getOrdersByUser(Integer userId) {
        // Giả sử OrdersRepository có hàm findByUser_Id(userId)
        return ordersRepository.findByUser_Id(userId);
    }

    @Override
    public Orders updateOrderStatus(Integer id, String status) {
        Orders orders = ordersRepository.findById(id).orElse(null);
        if (orders != null) {
            orders.setStatus(status);
            return ordersRepository.save(orders);
        }
        return null;
    }

    @Override
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    @Override
    public Page<Orders> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return ordersRepository.findAll(pageable);
    }

    /**
     * Giữ nguyên hàm này, nhưng parse String -> Integer
     * vì ta đang dùng id (int) là PK cho Orders.
     */
    @Override
    public Orders getOrdersByOrderId(String orderId) {
        try {
            // parse chuỗi sang int
            int parsedId = Integer.parseInt(orderId);
            // tìm Orders theo khóa chính (id)
            return ordersRepository.findById(parsedId).orElse(null);
        } catch (NumberFormatException e) {
            // orderId không phải là số
            System.err.println("orderId không hợp lệ: " + orderId);
            return null;
            // hoặc throw new IllegalArgumentException("orderId must be numeric");
        }
    }

    @Override
    public Orders createOrder(CreateOrderRequest request) {

        List<Cart> cart = cartRepository.findByUserId(request.getUserId());
        if (cart.isEmpty()) {
            throw new NotFoundException("Cart is empty");
        }
        Double totalMoney = cart.stream()
                .mapToDouble(cartItem -> {
                    return cartItem.getProduct().getFinalPrice() * cartItem.getQuantity();
                })
                .sum();

        Orders orders = Orders.builder()
                .orderDate(LocalDate.now())
                .email(request.getEmail())
                .note(request.getNote())
                .paymentType(request.getPaymentType())
                .address(request.getAddress())
                .phoneNumber(request.getPhone())
                .user(cart.get(0).getUser())
                .status(OrderStatus.IN_PROGRESS.getName())
                .totalMoney(totalMoney)
                .build();
        Orders savedOrder = ordersRepository.save(orders);
        List<OrderDetails> orderDetails = cart.stream()
                .map(cartItem -> {
                    return OrderDetails.builder()
                            .order(savedOrder)
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .totalMoney(cartItem.getProduct().getFinalPrice() * cartItem.getQuantity())
                            .build();
                })
                .toList();
        orderDetailsRepository.saveAll(orderDetails);
        cartRepository.deleteAll(cart);
        return savedOrder;

    }
}

package com.ecom.service;

import java.util.List;

import com.ecom.dtos.requests.CreateOrderRequest;
import org.springframework.data.domain.Page;

// Đổi ProductOrder -> Orders
import com.ecom.model.Orders;

public interface OrderService {
    boolean saveOrder(Integer userId, CreateOrderRequest orderRequest) throws Exception;

    List<Orders> getOrdersByUser(Integer userId);

    Orders updateOrderStatus(Integer id, String status);

    List<Orders> getAllOrders();

    Page<Orders> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    // Giữ nguyên hàm này, nhưng giờ sẽ parse orderId (String) thành int
    Orders getOrdersByOrderId(String orderId);

    Orders createOrder(CreateOrderRequest request);
}

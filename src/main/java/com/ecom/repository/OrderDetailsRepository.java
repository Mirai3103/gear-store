package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.OrderDetails;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {

    /**
     * Tìm tất cả OrderDetails theo orderId (khoá ngoại).
     * Ở entity: private Orders order;
     * => findByOrder_Id(...) 
     */
    List<OrderDetails> findByOrder_Id(Integer orderId);
}

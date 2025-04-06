package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    /**
     * Tìm tất cả Orders bởi userId (theo cú pháp findBy<Field>).
     * 
     * Nếu Entity Orders có field: 
     *   private User user;
     * Thì cú pháp JPA để truyền userId là: findByUser_Id(...)
     */
    List<Orders> findByUser_Id(Integer userId);

    /**
     * Tìm 1 đơn hàng bằng "orderId" (chuỗi).
     * => Trong entity Orders, phải có: private String orderId;
     */

}

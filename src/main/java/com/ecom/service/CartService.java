package com.ecom.service;

import java.util.List;
import com.ecom.model.Cart;

public interface CartService {

    /**
     * Thêm sản phẩm vào giỏ (Cart).
     * @param productId ID sản phẩm
     * @param userId    ID user
     * @return Cart đã lưu
     */
    Cart saveCart(Integer productId, Integer userId, Integer quantity);

    /**
     * Lấy danh sách cart của 1 user.
     */
    List<Cart> getCartsByUser(Integer userId);

    /**
     * Đếm số sản phẩm trong cart của 1 user.
     */
    Integer getCountCart(Integer userId);

    /**
     * Tăng/giảm số lượng sản phẩm trong cart.
     * @param sy "de" => giảm 1, ngược lại => tăng 1
     * @param cid ID cart
     */
    void updateQuantity(String sy, Integer cid);

    void deleteCart(Integer pid, Integer uid);
}

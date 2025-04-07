package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.Orders; // Thay ProductOrder -> Orders
import com.ecom.model.User;   // Thay UserDtls -> User
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ======================== HOME ========================
    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    /**
     * Hàm này tự động được gọi trước mỗi request,
     * để lấy thông tin user, cart,... và đưa vào model.
     */
    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            // Đổi UserDtls -> User
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);

            Integer countCart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);
        }

        // Category
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categorys", categories);
    }

    // ======================== CART ========================
    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid,
                            @RequestParam Integer uid,
                            HttpSession session)
    {
        // Gọi service để thêm sản phẩm vào giỏ
        Cart saveCart = cartService.saveCart(pid, uid);
        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Product add to cart failed");
        } else {
            session.setAttribute("succMsg", "Product added to cart");
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        User user = getLoggedInUser(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);

        // Tính tổng tiền giỏ hàng, cột price là String => parseDouble
        double totalOrderPrice = 0.0;
        for (Cart c : carts) {
            // Lấy price dạng String
            String priceStr = c.getProduct().getPrice().toString();
            // Nếu DB lưu "299.99$" thì cắt bỏ ký tự $, ví dụ:
            // priceStr = priceStr.replace("$", "");

            // parse sang double
            double numericPrice = Double.parseDouble(priceStr);
            double lineTotal = numericPrice * c.getQuantity();
            totalOrderPrice += lineTotal;
        }
        m.addAttribute("totalOrderPrice", totalOrderPrice);

        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy,
                                     @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    // ======================== ORDER ========================
    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        User user = getLoggedInUser(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        m.addAttribute("carts", carts);

        // Tính tạm "orderPrice" = tổng giỏ
        double orderPrice = 0.0;
        for (Cart c : carts) {
            String priceStr = c.getProduct().getPrice().toString();
            double numericPrice = Double.parseDouble(priceStr);
            double lineTotal = numericPrice * c.getQuantity();
            orderPrice += lineTotal;
        }
        // Ví dụ cộng thêm phí ship 250 + 100
        double totalOrderPrice = orderPrice + 250 + 100;
        m.addAttribute("orderPrice", orderPrice);
        m.addAttribute("totalOrderPrice", totalOrderPrice);

        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request,
                            Principal p) throws Exception
    {
        // Tạo đơn hàng (Approach B: Orders + OrderDetails)
        User user = getLoggedInUser(p);
        orderService.saveOrder(user.getId(), request);

        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p) {
        User loginUser = getLoggedInUser(p);
        // Lấy danh sách Orders thay vì ProductOrder
        List<Orders> orders = orderService.getOrdersByUser(loginUser.getId());
        m.addAttribute("orders", orders);
        return "/user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id,
                                    @RequestParam Integer st,
                                    HttpSession session)
    {
        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }
        // Cập nhật Orders thay vì ProductOrder
        Orders updateOrder = orderService.updateOrderStatus(id, status);

        // Gửi mail (nếu có) => commonUtil.sendMailForOrders(...)

        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Status Updated");
        } else {
            session.setAttribute("errorMsg", "Status not updated");
        }
        return "redirect:/user/user-orders";
    }

    // ======================== PROFILE ========================
    @GetMapping("/profile")
    public String profile() {
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User user,
                                @RequestParam MultipartFile img,
                                HttpSession session) {
        // Code cập nhật user + upload ảnh
        User updatedProfile = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(updatedProfile)) {
            session.setAttribute("errorMsg", "Profile not updated");
        } else {
            session.setAttribute("succMsg", "Profile Updated");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,
                                 @RequestParam String currentPassword,
                                 Principal p,
                                 HttpSession session) {
        User loggedInUser = getLoggedInUser(p);

        boolean matches = passwordEncoder.matches(currentPassword, loggedInUser.getPassword());
        if (matches) {
            String encodePassword = passwordEncoder.encode(newPassword);
            loggedInUser.setPassword(encodePassword);
            User updatedUser = userService.updateUser(loggedInUser);
            if (ObjectUtils.isEmpty(updatedUser)) {
                session.setAttribute("errorMsg", "Password not updated! Error in server");
            } else {
                session.setAttribute("succMsg", "Password Updated successfully");
            }
        } else {
            session.setAttribute("errorMsg", "Current Password incorrect");
        }
        return "redirect:/user/profile";
    }

    // ======================== UTILS ========================
    private User getLoggedInUser(Principal p) {
        String email = p.getName();
        return userService.getUserByEmail(email);
    }

}

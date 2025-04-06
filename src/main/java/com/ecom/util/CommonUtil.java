package com.ecom.util;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.model.Orders;
import com.ecom.model.OrderDetails;
import com.ecom.model.User;
import com.ecom.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    /**
     * Gửi mail reset password
     */
    public Boolean sendMail(String url, String recipientEmail)
            throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("your_email@gmail.com", "Shopping Cart");
        helper.setTo(recipientEmail);

        String content = "<p>Hello,</p>" 
            + "<p>You have requested to reset your password.</p>"
            + "<p>Click the link below to change your password:</p>"
            + "<p><a href=\"" + url + "\">Change my password</a></p>";

        helper.setSubject("Password Reset");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    /**
     * Sinh ra URL gốc, bỏ phần servletPath
     */
    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    /**
     * Hàm mới thay cho sendMailForProductOrder(...).
     * Gửi mail cho 1 đơn hàng (Orders) + nhiều chi tiết (OrderDetails).
     */
    public Boolean sendMailForOrders(Orders orders, List<OrderDetails> details, String status) throws Exception {

        // Tạo nội dung mail
        StringBuilder msg = new StringBuilder();
        msg.append("<p>Hello <b>").append(orders.getUser().getName()).append("</b>,</p>");
        msg.append("<p>Thank you for your order. Your order status is: <b>")
           .append(status).append("</b>.</p>");

        // Hiển thị địa chỉ, phone, v.v. (nếu cột có sẵn)
        msg.append("<p><b>Delivery Info:</b><br>");
        msg.append("Email: ").append(orders.getEmail()).append("<br>");
        msg.append("Phone: ").append(orders.getPhoneNumber()).append("<br>");
        msg.append("Address: ").append(orders.getAddress()).append("</p>");

        // Thêm bảng sản phẩm
        msg.append("<table border='1' cellpadding='5' cellspacing='0'>");
        msg.append("<tr><th>Product</th><th>Quantity</th><th>Line Total</th></tr>");

        double grandTotal = 0.0;
        for (OrderDetails od : details) {
            String productName = od.getProduct().getName(); // cột 'name'
            int qty = od.getQuantity();
            double lineTotal = od.getTotalMoney(); // code set khi tạo OrderDetails

            msg.append("<tr>")
               .append("<td>").append(productName).append("</td>")
               .append("<td>").append(qty).append("</td>")
               .append("<td>").append(lineTotal).append("</td>")
               .append("</tr>");

            grandTotal += lineTotal;
        }
        msg.append("</table>");
        msg.append("<p><b>Total:</b> ").append(grandTotal).append("</p>");

        // Payment Type, note, v.v. (nếu cột Orders có)
        if (orders.getPaymentType() != null) {
            msg.append("<p>Payment Type: ").append(orders.getPaymentType()).append("</p>");
        }

        // Gửi mail
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("your_email@gmail.com", "Shopping Cart");
        helper.setTo(orders.getEmail()); // cột email trong Orders

        helper.setSubject("Order Status Update");
        helper.setText(msg.toString(), true);
        mailSender.send(message);

        return true;
    }

    /**
     * Đổi từ getLoggedInUserDetails(...) -> getLoggedInUser(...),
     * trả về entity User (thay vì UserDtls).
     */
    public User getLoggedInUser(Principal p) {
        if (p == null) return null;
        String email = p.getName();
        return userService.getUserByEmail(email); 
    }
}

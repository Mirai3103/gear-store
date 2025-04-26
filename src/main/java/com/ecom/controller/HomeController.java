package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import com.ecom.config.CustomUser;
import com.ecom.dtos.RequestPasswordRequest;
import com.ecom.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ecom.dtos.requests.RegisterRequest;
import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;



    @Autowired
    private AuthenticationManager authenticationManager;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userService.getUserByEmail(email); // Đổi UserDtls -> User
            m.addAttribute("user", user);
            Integer countCart = cartService.getCountCart(user.getId());
            m.addAttribute("countCart", countCart);
        }
        // Bỏ allActiveCategory => dùng getAllCategory()
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categorys", categories);
    }


    @GetMapping("Sell")
    public String sell() {
        return "sell";
    }

    @GetMapping("/")
    public String index(Model m, Authentication auth, HttpSession session) {
        // Bỏ logic allActiveCategory, allActiveProducts => tuỳ cột isActive
        // Thay = getAllCategory(), getAllProducts()
        List<Category> categories = categoryService.getAllCategory();
        List<Product> products = productService.getAllProducts();
        if (auth != null && auth.isAuthenticated()) {
            var user = (CustomUser) auth.getPrincipal();
            var totalCart = cartService.getCountCart(user.getUser().getId());
            session.setAttribute("cartItemsLength", totalCart);

        }
        m.addAttribute("category", categories);
        m.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/signin")
    public String getSignIn(Model m, Authentication auth) {
        m.addAttribute("registerRequest", new RegisterRequest());
        m.addAttribute("requestPasswordRequest", new RequestPasswordRequest());
        log.info("getSignIn() called with auth: {}", auth);
        if (auth != null) {
            log.info("User is already authenticated: {}", auth.getName());
            return "redirect:/"; // Chuyển hướng về trang chủ nếu đã đăng nhập
        }
        return "login-register";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "redirect:/signin?signup";
    }

    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute RegisterRequest registerRequest,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model,RedirectAttributes redirectAttributes) {

        // Manual validation
        model.addAttribute("requestPasswordRequest", new RequestPasswordRequest());
        model.addAttribute("page","register");
        model.addAttribute("registerRequest", registerRequest);
        if (userService.existsEmail(registerRequest.getEmail())) {
            model.addAttribute("isEmailExists", true);
            model.addAttribute("page", "Register");

//            bindingResult.rejectValue("email", "email.exists", "Email already exists");
            return "login-register";
        }
        registerRequest.setName(registerRequest.getEmail());

        // Return to form if errors exist
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerRequest", registerRequest); // Repopulate form
            model.addAttribute("page", "Register");
            return "login-register"; // Return the same view to show errors
        }

        // Process valid registration
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        userService.saveUser(user);
        model.addAttribute("notification", "Registration successful! Please log in.");
        model.addAttribute("msg", "Registration successful! Please log in.");
        redirectAttributes.addFlashAttribute("msg", "Registration successful! Please log in.");
        return "redirect:/signin";
    }

    @GetMapping("/products")
    public String products(Model m,
                           @RequestParam(value = "category", defaultValue = "") String category,
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,
                           @RequestParam(defaultValue = "") String ch) {

        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("paramValue", category);
        m.addAttribute("categories", categories);

        Page<Product> page = null;
        if (StringUtils.isEmpty(ch)) {
            // Bỏ isActive => code tuỳ cột DB
            page = productService.getAllProductsPagination(pageNo, pageSize);
        } else {
            page = productService.searchProductPagination(pageNo, pageSize, ch);
        }

        List<Product> products = page.getContent();
        m.addAttribute("products", products);
        m.addAttribute("productsSize", products.size());

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "product";
    }
    @PostMapping("/request-reset-password")
    public String resetPasswordRequest(RedirectAttributes redirectAttributes, @ModelAttribute RequestPasswordRequest requestPasswordRequest, HttpSession session, Model m, BindingResult bindingResult) throws MessagingException {
        String email = requestPasswordRequest.getEmail();
        boolean success= userService.sendResetPassword(email);
        redirectAttributes.addFlashAttribute("page", "forgotPassword");
        return "redirect:/signin?reset=true&error="+!success;
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model m) {
        Product productById = productService.getProductById(id);
        m.addAttribute("galleries", productService.getAllProductsGallery(id));
        m.addAttribute("product", productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam("img") MultipartFile file,
                           HttpSession session) throws IOException {

        Boolean existsEmail = userService.existsEmail(user.getEmail());
        if (existsEmail) {
            session.setAttribute("errorMsg", "Email already exist");
        } else {
            String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
            user.setProfileImage(imageName);
            User savedUser = userService.saveUser(user); // Đổi UserDtls -> User
            if (!ObjectUtils.isEmpty(savedUser)) {
                if (!file.isEmpty()) {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator
                            + "profile_img" + File.separator
                            + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }
                session.setAttribute("succMsg", "Register successfully");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server");
            }
        }
        return "redirect:/register?signup";
    }

    // ===================== Forgot Password Code =====================

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot_password.html";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        HttpSession session,
                                        HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {

        User userByEmail = userService.getUserByEmail(email);
        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email");
        } else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;
            Boolean sendMail = commonUtil.sendMail(url, email);

            if (sendMail) {
                session.setAttribute("succMsg", "Please check your email. Password Reset link sent");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server! Email not sent");
            }
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam(required = false) String token,
                                    Model m) {
        m.addAttribute("page","forgotPassword");
        m.addAttribute("registerRequest", new RegisterRequest());
        m.addAttribute("requestPasswordRequest", new RequestPasswordRequest());
        if (!org.apache.commons.lang3.StringUtils.isBlank(token)) {
            User userByToken = userService.getUserByToken(token);
            if (userByToken == null) {
                m.addAttribute("token", token);
                m.addAttribute("error", "Your link is invalid or expired !!");
                m.addAttribute("isTokenValid", false);

                return "login-register";
            }
            m.addAttribute("token", token);
            m.addAttribute("email", userByToken.getEmail());
            m.addAttribute("isTokenValid", true);
        }
        return "login-register";
    }

    @PostMapping("/reset-password")
        public String resetPassword(@RequestParam String token,
                                @RequestParam String password,
                                HttpSession session,
                                Model m) {
        User userByToken = userService.getUserByToken(token);
        m.addAttribute("registerRequest", new RegisterRequest());
        m.addAttribute("requestPasswordRequest", new RequestPasswordRequest());
        if (userByToken == null) {
            m.addAttribute("token", token);
            m.addAttribute("error", "Your link is invalid or expired !!");
            m.addAttribute("isTokenValid", false);
            return "login-register";
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            m.addAttribute("msg", "Password changed successfully");
            return "login-register";
        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m) {
        List<Product> searchProducts = productService.searchProduct(ch);
        m.addAttribute("products", searchProducts);

        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "product";
    }
}

package com.ecom.service.impl;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.crypto.SecretKey;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public User saveUser(User user) {
        // VD: set default role = ROLE_USER, encode password
        user.setRole("ROLE_USER");
        // user.setIsEnable(true); // nếu bạn có cột isEnable
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsers(String role) {
        // tuỳ logic: userRepository.findByRole(role);
        return userRepository.findAll(); // hoặc filter
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        // nếu DB có cột "isEnable" thì set isEnable = status
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            // user.setIsEnable(status); // nếu có cột isEnable
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(User user) {
        // nếu bạn không cần logic này, để trống hoặc bỏ hẳn
        // user.setFailedAttempt(user.getFailedAttempt() + 1);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(User user) {
        // nếu bạn không cần logic này, để trống hoặc bỏ hẳn
        // user.setAccountNonLocked(false);
        // user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(User user) {
        // nếu bạn không cần, để trống hoặc return false
        // tuỳ logic
        return false;
    }

    @Override
    public void resetAttempt(int userId) {
        // tuỳ logic
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        // nếu dùng chức năng forgot password
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // user.setResetToken(resetToken);
            userRepository.save(user);
        }
    }

    @Override
    public User getUserByToken(String token) {
        try {
            var userId = extractUidFromResetToken(token);
            if (userId != null) {
                return userRepository.findById(Integer.parseInt(userId)).orElse(null);
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public User updateUser(User user) {
        var existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            dbUser.setName(user.getName());
            dbUser.setEmail(user.getEmail());
            dbUser.setPhoneNumber(user.getPhoneNumber());
            dbUser.setNote(user.getNote());
            if (!StringUtils.isBlank(user.getPassword())) {
                dbUser.setPassword(user.getPassword());
            }

            userRepository.save(dbUser);
            return dbUser;
        }
        return null;
    }

    @Override
    public User updateUserProfile(User user, MultipartFile img) {
        // ví dụ upload ảnh profile
        User dbUser = userRepository.findById(user.getId()).orElse(null);
        if (dbUser != null) {
            // dbUser.setName(user.getName()); ...
            userRepository.save(dbUser);
        }
        return dbUser;
    }

    @Override
    public User saveAdmin(User user) {
        // tương tự saveUser, set role = ROLE_ADMIN
        user.setRole("ROLE_ADMIN");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Boolean existsEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<User> getCustomers(int pageNo, int pageSize, String search) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNo);
        var searchQuery = "%" + search + "%";
        var list = userRepository.searchCustomer(searchQuery, pageable);
        var count = userRepository.countSearchCustomer(searchQuery);
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    @Transactional
    public void deleteUser(Integer uid) {
        // tuỳ logic
        Optional<User> user = userRepository.findById(uid);
        user.ifPresent(value -> userRepository.delete(value));
    }

    @Override
    public void toggleLockUser(Integer uid) {
        Optional<User> user = userRepository.findById(uid);
        if (user.isPresent()) {
            User u = user.get();
            u.setEnable(u.getEnable() == null || !u.getEnable());
            userRepository.save(u);
        }
    }

    @Override
    public void updateUserPassword(Integer uid, String password) {
        var user = userRepository.findById(uid);
        if (user.isPresent()) {
            User u = user.get();
            u.setPassword(passwordEncoder.encode(password));
            userRepository.save(u);
        }
    }

    @Override
    public boolean sendResetPassword(String email) throws MessagingException {
        var user = userRepository.findByEmail(email);
        if (user != null) {
            String jwtToken = generateResetToken(user.getId().toString());
            Context context = new Context();
            context.setVariable("name", user.getName());
            context.setVariable("resetLink", "http://localhost:8080/reset-password?token=" + jwtToken);
            String htmlContent = templateEngine.process("reset-password-mail", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Đặt lại mật khẩu");
            helper.setFrom("no-reply@test-r83ql3ppr0xgzw1j.mlsender.net");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        }
        return false;
    }

    private final SecretKey RESET_SECRET = Keys.hmacShaKeyFor("cd620c9ffc4fb918e1ddfe73696a51c24f747f0ecffa69e3aa7793b002e76b317aaf500ad61fd4a61d5a1aab90b32ec3b41ffc3ecf3da2bff5f80c5a7f3db89604442318e962811d51a8b7b2cec41440299cc399c160d63ad67ffd84d64256f1a835e74abbfd61b22a36121152484b22b1e894f5463904f27d2dba8ba94d8d2f05866dd77f04349eb6b8f46feed6fa457b5730ee3c22a89ef24c0b54872caf9632d6741b85ef0b5c4d7332a4f74c405d020ba1f34a31077276f1e4e697674242ac059a83b3220a4a7c6894bd33ff2ef4b97244cbf677552402d594cb93871f2da23f4c6ffb817eb15fc6f2300147dcde3811b5d061dbbd0c7012d96ce0f05a50".getBytes());

    private String generateResetToken(String userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(30 * 60); // token sống 30 phút

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(RESET_SECRET)
                .compact();
    }

    private String extractUidFromResetToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(RESET_SECRET)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();

    }


}

package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        // return userRepository.findByResetToken(token);
        return null;
    }

    @Override
    public User updateUser(User user) {
        // tuỳ logic
        return userRepository.save(user);
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
        // tuỳ logic: userRepository.existsByEmail(email)
        return false;
    }

    @Override
    public Page<User> getCustomers(int pageNo, int pageSize, String search) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNo);
        var searchQuery = "%" + search + "%";
        var list = userRepository.searchCustomer(searchQuery, pageable);
        var count = userRepository.countSearchCustomer(searchQuery);
        return new PageImpl<>(list, pageable, count);
    }
}

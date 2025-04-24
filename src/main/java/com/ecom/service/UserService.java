package com.ecom.service;

import java.util.List;

import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.ecom.model.User;

public interface UserService {

    User saveUser(User user);

    User getUserByEmail(String email);

    List<User> getUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);

    void increaseFailedAttempt(User user);

    void userAccountLock(User user);

    boolean unlockAccountTimeExpired(User user);

    void resetAttempt(int userId);

    void updateUserResetToken(String email, String resetToken);

    User getUserByToken(String token);

    User updateUser(User user);

    User updateUserProfile(User user, MultipartFile img);

    User saveAdmin(User user);

    Boolean existsEmail(String email);


    Page<User> getCustomers(int pageNo, int pageSize, String search);

    void deleteUser(Integer uid);

    void toggleLockUser(Integer uid);

    void updateUserPassword(Integer uid, String password);

    void sendResetPassword(String email) throws MessagingException;
}

package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByEmail(String email);

    public List<User> findByRole(String role);

    public User findByResetToken(String token);

    public Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_USER' AND (u.name LIKE :search OR u.email LIKE :search)")
    List<User> searchCustomer(@Param("search") String search, Pageable pageable);
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_USER' AND (u.name LIKE :search OR u.email LIKE :search)")
    Long  countSearchCustomer(@Param("search") String search);
}
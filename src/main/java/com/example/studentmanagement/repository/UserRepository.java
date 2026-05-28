package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho bảng users (tài khoản đăng nhập).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo tên đăng nhập
    Optional<User> findByUsername(String username);
}
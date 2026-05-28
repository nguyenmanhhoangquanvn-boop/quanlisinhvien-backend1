package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.repository.UserRepository;
import com.example.studentmanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Xử lý đăng nhập.
 * POST /api/auth/login → trả về JWT token + role
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng nhập.
     * Body: { "username": "admin", "password": "admin123" }
     * Trả về: { "token": "...", "username": "admin", "role": "ADMIN" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String rawUsername = body.get("username");
        String rawPassword = body.get("password");

        String username = rawUsername != null ? rawUsername.trim() : null;
        String password = rawPassword != null ? rawPassword.trim() : null;

        System.out.println("Login attempt - Username: '" + rawUsername + "', Password: '" + rawPassword + "'");

        // Tìm user trong DB
        User user = userRepository.findByUsername(username).orElse(null);

        // Kiểm tra sai tài khoản hoặc mật khẩu
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Login failed - User: " + (user != null ? "found" : "not found") + ", Password matched: " + (user != null ? passwordEncoder.matches(password, user.getPassword()) : "N/A"));
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu!");
        }

        // Tạo JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "token",    token,
                "username", user.getUsername(),
                "role",     user.getRole()
        ));
    }
}
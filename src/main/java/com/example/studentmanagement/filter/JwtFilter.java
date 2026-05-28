package com.example.studentmanagement.filter;

import com.example.studentmanagement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter này chạy 1 lần cho mỗi HTTP request.
 * Nhiệm vụ: đọc JWT token từ header, xác thực, rồi gán quyền vào SecurityContext.
 *
 * Luồng hoạt động:
 *   Request → JwtFilter (đọc token) → SecurityContext (lưu quyền) → Controller
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Bỏ qua filter cho route đăng nhập (không cần token)
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Đọc header: Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // cắt bỏ "Bearer "

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                String role     = jwtUtil.extractRole(token);

                // Đảm bảo role có dạng "ROLE_ADMIN" hoặc "ROLE_USER"
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                // Gán thông tin xác thực vào SecurityContext
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Tiếp tục xử lý request
        filterChain.doFilter(request, response);
    }
}
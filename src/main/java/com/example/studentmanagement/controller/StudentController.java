package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller = cổng tiếp nhận các request HTTP từ React (frontend).
 *
 * Các API:
 *   GET    /api/students        → lấy danh sách (ADMIN thấy tất cả, USER chỉ thấy bản thân)
 *   GET    /api/students/{id}   → lấy 1 sinh viên (ADMIN + USER)
 *   POST   /api/students        → thêm mới (chỉ ADMIN)
 *   PUT    /api/students/{id}   → sửa (chỉ ADMIN)
 *   DELETE /api/students/{id}   → xóa (chỉ ADMIN)
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // ── GET ALL: ADMIN thấy tất cả, USER chỉ thấy chính mình ──
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Student> getAll(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        // USER chỉ được xem thông tin của bản thân (username = studentCode)
        if ("ROLE_USER".equals(role)) {
            String studentCode = authentication.getName();
            Student sv = studentService.getByStudentCode(studentCode);
            return sv != null ? List.of(sv) : List.of();
        }
        return studentService.getAll();
    }

    // ── CREATE: chỉ ADMIN ───────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được thêm mới
    public ResponseEntity<Student> create(@RequestBody Student student) {
        Student created = studentService.create(student);
        return ResponseEntity.ok(created);
    }

    // ── UPDATE: ADMIN và USER (USER chỉ được sửa một số trường) ──
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Student> update(@PathVariable Long id,
                                          @RequestBody Student student,
                                          Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        Student updated = studentService.update(id, student, role);
        return ResponseEntity.ok(updated);
    }

    // ── DELETE: chỉ ADMIN ───────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Chỉ ADMIN mới được xóa
    public ResponseEntity<String> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.ok("Đã xóa sinh viên id=" + id);
    }

    // Bắt lỗi (ví dụ trùng mã sinh viên) để trả về text dễ đọc
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // Nếu là lỗi báo trùng mã (từ Service ném ra) thì lấy đúng nội dung đó
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
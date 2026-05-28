package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service = lớp chứa logic nghiệp vụ (business logic).
 * Controller gọi Service, Service gọi Repository.
 * Luồng: Controller → Service → Repository → Database
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    // Spring tự inject (tiêm) StudentRepository vào đây
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── LẤY TOÀN BỘ DANH SÁCH ──────────────────────────────
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    // ── LẤY 1 SINH VIÊN THEO ID ────────────────────────────
    public Student getById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên id=" + id));
    }

    // ── LẤY 1 SINH VIÊN THEO MÃ SV (dùng cho tài khoản USER) ──
    public Student getByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode).orElse(null);
    }

    // ── THÊM MỚI ───────────────────────────────────────────
    @Transactional
    public Student create(Student student) {
        // Kiểm tra mã sinh viên đã tồn tại chưa
        if (studentRepository.existsByStudentCode(student.getStudentCode())) {
            throw new RuntimeException("Mã sinh viên " + student.getStudentCode() + " đã tồn tại!");
        }

        // Lưu sinh viên vào DB
        Student saved = studentRepository.save(student);

        // Tự động tạo tài khoản USER với username = password = mã sinh viên
        if (userRepository.findByUsername(student.getStudentCode()).isEmpty()) {
            User newUser = User.builder()
                    .username(student.getStudentCode())
                    .password(passwordEncoder.encode(student.getStudentCode()))
                    .role("USER")
                    .build();
            userRepository.save(newUser);
        }

        return saved;
    }

    // ── CẬP NHẬT ───────────────────────────────────────────
    public Student update(Long id, Student data, String userRole) {
        Student sv = getById(id); // lấy sinh viên cũ, nếu không có thì ném lỗi

        // USER chỉ được phép cập nhật số điện thoại và địa chỉ
        if ("ROLE_USER".equals(userRole) || "USER".equals(userRole)) {
            sv.setPhone(data.getPhone());
            sv.setAddress(data.getAddress());
        } else {
            // ADMIN cập nhật toàn bộ (ngoại trừ điểm - điểm quản lý riêng qua GradeController)
            sv.setFullName(data.getFullName());
            sv.setPhone(data.getPhone());
            sv.setAddress(data.getAddress());
            sv.setClassName(data.getClassName());
            sv.setDateOfBirth(data.getDateOfBirth());
            sv.setMajor(data.getMajor());
        }

        // Lưu ý: không cho đổi studentCode để tránh trùng

        return studentRepository.save(sv);
    }

    // ── XÓA ────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        Student sv = getById(id); // đảm bảo tồn tại trước khi xóa

        // Xóa tài khoản USER tương ứng (nếu có)
        userRepository.findByUsername(sv.getStudentCode()).ifPresent(userRepository::delete);

        studentRepository.deleteById(id);
    }
}
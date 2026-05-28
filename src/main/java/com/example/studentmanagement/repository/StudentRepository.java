package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository = lớp kết nối database cho bảng students.
 * Spring Boot tự cài đặt các method: findAll, findById, save, deleteById...
 * Ta chỉ cần khai báo interface, không cần viết SQL!
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Tự sinh SQL: SELECT * FROM students WHERE student_code = ?
    Optional<Student> findByStudentCode(String studentCode);

    // Kiểm tra mã sinh viên đã tồn tại chưa
    boolean existsByStudentCode(String studentCode);
}
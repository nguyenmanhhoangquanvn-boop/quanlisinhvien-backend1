package com.example.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity (bảng) sinh viên trong database.
 * @Entity   → Spring Boot biết đây là bảng DB
 * @Table    → tên bảng là "students"
 * @Data     → Lombok tự tạo getter/setter/toString
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tự tăng id
    private Long id;

    @Column(name = "student_code", unique = true, nullable = false)
    private String studentCode; // Mã sinh viên, ví dụ: SV001

    @Column(nullable = false)
    private String fullName;    // Họ và tên

    private String phone;       // Số điện thoại
    private String address;     // Địa chỉ
    private String className;   // Lớp học (ví dụ: "CNTT01")
    private LocalDate dateOfBirth; // Ngày sinh
    private String major;       // Ngành học (ví dụ: "Công nghệ thông tin")

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Grade> grades;
}
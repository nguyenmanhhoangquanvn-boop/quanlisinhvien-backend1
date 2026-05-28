package com.example.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subjectName; // Tên môn học

    @DecimalMin(value = "0.0", message = "Điểm không được nhỏ hơn 0")
    @DecimalMax(value = "10.0", message = "Điểm không được lớn hơn 10")
    private Double score; // Điểm môn học (0.0 - 10.0)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;
}

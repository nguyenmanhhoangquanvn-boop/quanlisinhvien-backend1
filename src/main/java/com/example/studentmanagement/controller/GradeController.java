package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Grade;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.GradeRepository;
import com.example.studentmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;

    // Kiểm tra điểm hợp lệ (0.0 - 10.0)
    private void validateScore(Double score) {
        if (score != null && (score < 0.0 || score > 10.0)) {
            throw new RuntimeException("Điểm không hợp lệ! Điểm phải nằm trong khoảng 0 đến 10.");
        }
    }

    // ADMIN thêm môn học cho sinh viên
    @PostMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addGrade(@PathVariable Long studentId, @RequestBody Grade gradeData) {
        validateScore(gradeData.getScore());

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        gradeData.setStudent(student);
        Grade savedGrade = gradeRepository.save(gradeData);
        return ResponseEntity.ok(savedGrade);
    }

    // ADMIN sửa điểm môn học
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @RequestBody Grade gradeData) {
        validateScore(gradeData.getScore());

        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));

        existingGrade.setSubjectName(gradeData.getSubjectName());
        existingGrade.setScore(gradeData.getScore());

        Grade updatedGrade = gradeRepository.save(existingGrade);
        return ResponseEntity.ok(updatedGrade);
    }

    // ADMIN xóa môn học
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        gradeRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa môn học");
    }

    // Bắt lỗi validation điểm
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

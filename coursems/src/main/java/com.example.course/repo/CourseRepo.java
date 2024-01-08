package com.example.course.repo;
import com.example.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course, Integer> {
    Optional<List<Course>> findByStudentId(Integer studentId);
}

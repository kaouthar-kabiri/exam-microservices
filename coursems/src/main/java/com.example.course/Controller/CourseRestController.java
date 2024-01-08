package com.example.course.Controller;

import com.example.course.model.Course;
import com.example.course.repo.CourseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseRestController.class);

    @Autowired
    private CourseRepo repo;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Course>> getAllCourses(@RequestParam(required = false) @Min(1) Integer studentId) {

        if (Objects.isNull(studentId)) {
            LOGGER.info("fetching all Courses");
            return ResponseEntity.ok(repo.findAll());
        }

        LOGGER.info("fetching all Courses for student: {}", studentId);
        Optional<List<Course>> studentCourses = repo.findByStudentId(studentId);
        if (studentCourses.isPresent() && studentCourses.get().size() > 0) {
            return ResponseEntity.ok(studentCourses.get());
        }

        LOGGER.error("No courses found for student: {}", studentId);
        return ResponseEntity.notFound().build();

    }

    @GetMapping(path = "/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> getSingleCourse(@PathVariable Integer courseId) {
        Optional<Course> course = repo.findById(courseId);
        if (course.isPresent()) {
            LOGGER.info("Course {}, found.", courseId);
            return ResponseEntity.ok(course.get());
        }

        LOGGER.error("Course {}, not found.", courseId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> createCourse(@RequestBody Course Course) throws URISyntaxException {
        Course newCourse = repo.save(Course);
        LOGGER.info("New Course {}, added.", newCourse.getId());
        return ResponseEntity.created(new URI(newCourse.getId().toString())).body(newCourse);
    }

    @DeleteMapping("/{CourseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer CourseId) {
        repo.deleteById(CourseId);
        LOGGER.info("Course {}, deleted.", CourseId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{CourseId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> updateCourse(@PathVariable Integer CourseId, @RequestBody Course Course) {
        boolean isIdFound = repo.existsById(CourseId);
        if (!isIdFound) {
            LOGGER.error("Course with id {}, not found.", Course.getId());
            return ResponseEntity.notFound().build();
        }

        Course.setId(CourseId);
        Course updatedCourse = repo.save(Course);
        LOGGER.info("Course {}, updated.", Course.getId());
        return ResponseEntity.ok(updatedCourse);
    }

}

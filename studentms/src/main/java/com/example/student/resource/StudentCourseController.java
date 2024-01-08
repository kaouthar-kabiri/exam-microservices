package com.example.student.resource;

import com.example.student.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/students/{studentId}/courses")
public class StudentCourseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCourseController.class);
    private final WebClient webClient;

    public StudentCourseController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public Mono<ResponseEntity<List<Course>>> getAllCoursesOfAStudent(@PathVariable @NotNull Integer studentId) {
        LOGGER.info("Getting Courses for student: {}", studentId);
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("studentId", studentId).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()

                .onStatus(status -> status.value() >= 400,
                        clientResponse -> Mono.empty()
                ).toEntity(new ParameterizedTypeReference<List<Course>>() {
                })
                .map(CourseResponseEntity -> {
                    if (CourseResponseEntity.getStatusCode().is2xxSuccessful()) {
                        LOGGER.info("Courses data found for student: {}", studentId);
                        return ResponseEntity.ok(CourseResponseEntity.getBody());
                    }
                    if (CourseResponseEntity.getStatusCodeValue() == HttpStatus.NOT_FOUND.value()) {
                        LOGGER.error("No Courses data found for student: {}", studentId);
                        return ResponseEntity.notFound().build();
                    }
                    if (CourseResponseEntity.getStatusCode().is4xxClientError()) {
                        LOGGER.error("Bad request for student: {}", studentId);
                        return ResponseEntity.badRequest().body(CourseResponseEntity.getBody());
                    }
                    LOGGER.error("Something went wrong for student: {}", studentId);
                    return ResponseEntity.internalServerError().body(CourseResponseEntity.getBody());
                });
    }

    @PostMapping
    public Mono<ResponseEntity<Course>> payCoursesOfAStudent(@PathVariable @NotNull Integer studentId,
                                                       @RequestBody Course Course) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(Course)
                .retrieve()
                .onStatus(status -> status.value() >= 400,
                        clientResponse -> Mono.empty()
                ).toEntity(new ParameterizedTypeReference<Course>() {
                })
                .map(CourseResponseEntity -> {
                    if (CourseResponseEntity.getStatusCode().is2xxSuccessful()) {
                        LOGGER.info("Courses data found for student: {}", studentId);
                        return CourseResponseEntity;
                    }
                    if (CourseResponseEntity.getStatusCodeValue() == HttpStatus.NOT_FOUND.value()) {
                        LOGGER.error("No Courses data found for student: {}", studentId);
                        return ResponseEntity.notFound().build();
                    }
                    if (CourseResponseEntity.getStatusCode().is4xxClientError()) {
                        LOGGER.error("Bad request for student: {}", studentId);
                        return ResponseEntity.badRequest().body(CourseResponseEntity.getBody());
                    }
                    LOGGER.error("Something went wrong for student: {}", studentId);
                    return ResponseEntity.internalServerError().body(CourseResponseEntity.getBody());
                });

    }
}

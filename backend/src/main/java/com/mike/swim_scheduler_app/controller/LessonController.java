package com.mike.swim_scheduler_app.controller;

import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @GetMapping
    public ResponseEntity<List<Lesson>> listLessons() {
        List<Lesson> lessons = lessonService.findAll();
        if (lessons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        return lessonService.findById(id)
                .map(lesson -> ResponseEntity.ok().body(lesson))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        Lesson savedLesson = lessonService.save(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLesson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        return lessonService.findById(id)
                .map(existingLesson -> {
                    lesson.setId(id);
                    Lesson updatedLesson = lessonService.save(lesson);
                    return ResponseEntity.ok().body(updatedLesson);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        return lessonService.findById(id)
                .map(lesson -> {
                    lessonService.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

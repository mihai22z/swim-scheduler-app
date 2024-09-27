package com.mike.swim_scheduler_app.controller;

import com.mike.swim_scheduler_app.model.Client;
import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.service.ClientLessonService;
import com.mike.swim_scheduler_app.service.LessonService;
import com.mike.swim_scheduler_app.service.WorkdayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ClientLessonService clientLessonService;
    @Autowired
    private WorkdayService workdayService;

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

    @GetMapping("/{lessonId}/clients")
    public ResponseEntity<List<Client>> getLessonClients(@PathVariable Long lessonId) {
        List<Client> clients = clientLessonService.findClientsForLesson(lessonId);
        if (clients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clients);
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        System.out.println("Received lesson: " + lesson);
        System.out.println("Client Lessons Size: " + lesson.getClientLessons().size());

        try {
            Lesson savedLesson = lessonService.save(lesson);
            return ResponseEntity.ok(savedLesson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        return lessonService.findById(id)
                .map(existingLesson -> {
                    Workday previousWorkday = existingLesson.getWorkday();

                    // Update the lesson and associate with the new workday
                    lesson.setId(id);
                    Lesson updatedLesson = lessonService.save(lesson);

                    // After saving, check if the lesson was moved to a different workday
                    if (!previousWorkday.getId().equals(lesson.getWorkday().getId())) {
                        // Remove the lesson from the old workday
                        previousWorkday.getLessons().remove(existingLesson);
                        workdayService.save(previousWorkday);

                        // If the previous workday has no more lessons, delete it
                        if (previousWorkday.getLessons().isEmpty()) {
                            System.out.println("I AM DELETING THE EMPTY WORKDAY");
                            workdayService.deleteById(previousWorkday.getId());
                        }
                        else {
                            System.out.println("SORRY BRO, EMPTY BUT NOT DELETED: " + previousWorkday.getLessons());
                        }
                    }
                    else {
                        System.out.println("WHAT DO YOU MEAN DUDE");
                    }

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

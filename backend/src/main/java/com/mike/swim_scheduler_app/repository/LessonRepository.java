package com.mike.swim_scheduler_app.repository;

import com.mike.swim_scheduler_app.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}

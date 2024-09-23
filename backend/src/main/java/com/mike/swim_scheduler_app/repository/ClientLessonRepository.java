package com.mike.swim_scheduler_app.repository;

import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.ClientLessonId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientLessonRepository extends JpaRepository<ClientLesson, ClientLessonId> {
    List<ClientLesson> findAllByLessonId(Long lessonId);
}

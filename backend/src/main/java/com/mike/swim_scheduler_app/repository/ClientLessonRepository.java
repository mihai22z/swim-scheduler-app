package com.mike.swim_scheduler_app.repository;

import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.ClientLessonId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientLessonRepository extends JpaRepository<ClientLesson, ClientLessonId> {
}

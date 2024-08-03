package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Client;
import com.mike.swim_scheduler_app.repository.ClientLessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientLessonService {
    @Autowired
    private ClientLessonRepository clientLessonRepository;

    public List<Client> findClientsForLesson(Long lessonId) {
        return clientLessonRepository.findAll().stream()
                .filter(clientLesson -> clientLesson.getLesson().getId().equals(lessonId))
                .map(clientLesson -> clientLesson.getClient())
                .toList();
    }
}

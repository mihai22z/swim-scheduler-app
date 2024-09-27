package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Client;
import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.ClientLessonId;
import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.repository.ClientLessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    public void saveClientLessons(Lesson lesson, Set<Client> clients) {
        for (Client client: clients) {
            ClientLesson clientLesson = new ClientLesson();
            clientLesson.setClient(client);
            clientLesson.setLesson(lesson);
            clientLessonRepository.save(clientLesson);
        }
    }

    public ClientLesson saveClientLesson(ClientLesson clientLesson) {
        return clientLessonRepository.save(clientLesson);
    }

    public void removeClientLessonsForLesson(Lesson lesson) {
        List<ClientLesson> clientLessons = clientLessonRepository.findAllByLessonId(lesson.getId());
        clientLessonRepository.deleteAll(clientLessons);
    }

    public void removeClientLessonById(ClientLessonId id) {
        clientLessonRepository.deleteById(id);
    }
}

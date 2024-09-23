package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Client;
import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.repository.LessonRepository;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private WorkdayRepository workdayRepository;
    @Autowired
    private ClientLessonService clientLessonService;
    @Autowired
    private WorkdayService workdayService;
    @Autowired
    private ClientService clientService;

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> findById(Long id) {
        return lessonRepository.findById(id);
    }

    public Lesson save(Lesson lesson) {
        if (lesson.getWorkday() == null) {
            throw new IllegalArgumentException("Lesson must be associated with a Workday.");
        }

        // Ensure Workday is saved or fetched
        Workday workday = lesson.getWorkday();
        if (workday.getId() == null) {
            workday = workdayService.save(workday);
        }
        else {
            workday = workdayService.findById(workday.getId()).orElseThrow(
                    () -> new IllegalArgumentException("Workday not found")
            );
        }
        lesson.setWorkday(workday);

        Set<ClientLesson> clientLessons = new HashSet<>(lesson.getClientLessons());
        lesson.getClientLessons().clear();

        // Save or update lesson
        Lesson savedLesson = lessonRepository.save(lesson);

        // Handle the ClientLesson associations
        if (!clientLessons.isEmpty()) {
            for (ClientLesson clientLesson : clientLessons) {
                clientLesson.setLesson(savedLesson);

                // Fetch existing client
                Client existingClient = clientService.findById(clientLesson.getId().getClientId())
                        .orElseThrow(() -> new IllegalArgumentException("Client not found."));
                clientLesson.setClient(existingClient);

                // Save ClientLesson and update associations
                clientLessonService.saveClientLesson(clientLesson);
            }
        }

        // Ensure the lesson is properly associated with its workday
        Workday associatedWorkday = savedLesson.getWorkday();
        if (associatedWorkday != null && !associatedWorkday.getLessons().contains(savedLesson)) {
            associatedWorkday.getLessons().add(savedLesson);
            workdayService.save(associatedWorkday);
        }

        // Update Workday times based on lessons
        workdayService.updateWorkdayTimes(savedLesson.getWorkday());

        return savedLesson;
    }

    public void deleteById(Long id) {
        lessonRepository.deleteById(id);
    }
}

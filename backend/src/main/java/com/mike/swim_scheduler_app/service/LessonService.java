package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Client;
import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.repository.LessonRepository;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        // Extract the date from the lesson's startTime
        LocalDate lessonDate = lesson.getStartTime().toLocalDate();

        // Check if a workday already exists for the date
        Workday workday = workdayService.findByDate(lessonDate);

        // If no existing workday is found, create a new one

        if (workday == null) {
            workday = new Workday();
            workday.setDate(lessonDate);
            workday.setStartTime(lesson.getStartTime());
            workday.setEndTime(lesson.getEndTime());
            workday = workdayService.save(workday);
        }

        lesson.setWorkday(workday); // Associate the lesson with the workday

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

        // Update Workday with the new lesson and update times if necessary
        workday.getLessons().add(savedLesson);
        workdayService.updateWorkdayTimes(savedLesson.getWorkday());

        return savedLesson;
    }

    public void deleteById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        // Handle ClientLesson associations (remove them from both sides)
        Set<ClientLesson> clientLessons = lesson.getClientLessons();
        if (clientLessons != null && !clientLessons.isEmpty()) {
            for (ClientLesson clientLesson : clientLessons) {
                Client client = clientLesson.getClient();
                if (client != null) {
                    client.getClientLessons().remove(clientLesson); // Remove from client
                    clientService.save(client);
                }
                clientLessonService.removeClientLessonById(clientLesson.getId()); // Delete ClientLesson from DB
            }
        }

        Workday workday = lesson.getWorkday();
        lessonRepository.deleteById(id);

        if (workday != null && workday.getLessons().isEmpty()) {
            workdayService.deleteById(workday.getId());
        }

        if (!workday.getLessons().isEmpty()) {
            workdayService.updateWorkdayTimes(workday);
        }
    }
}

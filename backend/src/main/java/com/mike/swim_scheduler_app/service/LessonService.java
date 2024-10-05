package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.*;
import com.mike.swim_scheduler_app.repository.LessonRepository;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public void createSubscription(List<Client> clients, List<DayTime> days, int totalWeeks, LocalDate startDate) {
        LocalDate today = LocalDate.now();
        int totalLessons = totalWeeks * days.size();

        int checkedLessons = 0;
        int week = 0;
        while (checkedLessons < totalLessons) {
            for (DayTime dayTime : days) {
                if (checkedLessons == totalLessons) {
                    break;
                }

                // Calculate the date for this day of the week in the given week
                LocalDate lessonDate = startDate.plusWeeks(week).with(dayTime.getDay());

                // Skip dates that are in the past
                if (lessonDate.isBefore(today)) {
                    continue;
                }
                else {
                    checkedLessons++;
                }

                // Check if there is a conflict for this date and time
                if (hasConflict(lessonDate, dayTime.getTime())) {
                    throw new IllegalArgumentException("Lesson conflict detected on " + lessonDate);
                }
            }
            week++;
        }

        // If no conflicts were found, proceed with updating the clients total and remaining lessons
        for (Client client : clients) {
            client.setSubscriptionTotalLessons(client.getSubscriptionTotalLessons() + totalLessons);
            client.setRemainingLessons(client.getRemainingLessons() + totalLessons);
            client.setSubscriptionStartDate(startDate);
            clientService.save(client);
        }

        // Create the lessons
        int addedLessons = 0;
        week = 0;
        while (addedLessons < totalLessons) {
            for (DayTime dayTime : days) {
                if (addedLessons == totalLessons) {
                    break;
                }
                LocalDate lessonDate = startDate.plusWeeks(week).with(dayTime.getDay());

                // Skip dates that are in the past
                if (lessonDate.isBefore(today)) {
                    continue;
                }
                else {
                    addedLessons++;
                }

                // Create a new lesson with the correct start and end times
                Lesson lesson = new Lesson();
                lesson.setStartTime(lessonDate.atTime(dayTime.getTime()));
                lesson.setEndTime(lesson.getStartTime().plusMinutes(45));

                // Create ClientLesson associations
                for (Client client : clients) {
                    ClientLessonId clientLessonId = new ClientLessonId();
                    clientLessonId.setClientId(client.getId());
                    clientLessonId.setLessonId(0L);

                    ClientLesson clientLesson = new ClientLesson();
                    clientLesson.setId(clientLessonId);
                    clientLesson.setClient(client);

                    lesson.getClientLessons().add(clientLesson);
                }

                save(lesson);
            }
            week++;
        }
    }

    public boolean hasConflict(LocalDate date, LocalTime time) {
        Workday workday = workdayService.findByDate(date);
        if (workday != null) {
            Set<Lesson> lessons = workday.getLessons();

            LocalDateTime newLessonStart = date.atTime(time);
            LocalDateTime newLessonEnd = newLessonStart.plusMinutes(45);

            return lessons.stream().anyMatch(lesson -> {
                LocalDateTime existingLessonStart = lesson.getStartTime();
                LocalDateTime existingLessonEnd = lesson.getEndTime();

                return (newLessonStart.isBefore(existingLessonEnd) && newLessonEnd.isAfter(existingLessonStart));
            });
        }
        return false;
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

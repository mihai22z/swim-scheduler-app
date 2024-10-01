package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.model.TimeSlot;
import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkdayService {
    @Autowired
    private WorkdayRepository workdayRepository;

    public List<Workday> findAll() {
        return workdayRepository.findAll();
    }

    public Optional<Workday> findById(Long id) {
        return workdayRepository.findById(id);
    }

    public Workday findByDate(LocalDate date) {
        return workdayRepository.findByDate(date).orElse(null);
    }

    public Workday save(Workday workday) {
        return workdayRepository.save(workday);
    }

    public void deleteById(Long id) {
        workdayRepository.deleteById(id);
    }

    public List<Workday> getWorkdaysForMonth(int year, int month) {
        return workdayRepository.findAll()
                .stream()
                .filter(workday -> {
                    LocalDate date = workday.getDate();
                    return date.getMonthValue() == month && date.getYear() == year;
                })
                .collect(Collectors.toList());
    }

    public void updateWorkdayTimes(Workday workday) {
        if (workday != null) {
            Set<Lesson> lessons = workday.getLessons();
            if (!lessons.isEmpty()) {
                workday.setStartTime(
                        lessons.stream().map(Lesson::getStartTime).min(LocalDateTime::compareTo).orElse(null)
                );
                workday.setEndTime(
                        lessons.stream().map(Lesson::getEndTime).max(LocalDateTime::compareTo).orElse(null)
                );
                save(workday);
            }
        }
    }

    public List<TimeSlot> getTimeSlotsForDay(Long workdayId) {
        Workday workday = workdayRepository.findById(workdayId).orElseThrow(() -> new RuntimeException("No Workday found!"));
        List<Lesson> workdayLessons = workday.getLessons().stream().toList();
        List<Lesson> lessons = new ArrayList<>(workdayLessons);
        lessons.sort(Comparator.comparing(Lesson::getStartTime));

        LocalDateTime dayStartTime = LocalDateTime.of(lessons.get(0).getStartTime().toLocalDate(), LocalTime.of(9, 0));
        List<TimeSlot> timeSlots = new ArrayList<>();
        for (Lesson lesson : lessons) {
            while (dayStartTime.isBefore(lesson.getStartTime())) {
                timeSlots.add(new TimeSlot(dayStartTime, false));
                dayStartTime = dayStartTime.plusMinutes(15);
            }
            while (!dayStartTime.isBefore(lesson.getStartTime()) && dayStartTime.isBefore(lesson.getEndTime())) {
                if (timeSlots.isEmpty() || !timeSlots.get(timeSlots.size() - 1).isOccupied()) {
                    timeSlots.add(new TimeSlot(dayStartTime, true));
                }
                dayStartTime = dayStartTime.plusMinutes(15);
            }
        }
        while (dayStartTime.getHour() < 21) {
            timeSlots.add(new TimeSlot(dayStartTime, false));
            dayStartTime = dayStartTime.plusMinutes(15);
        }

        return timeSlots;
    }
}

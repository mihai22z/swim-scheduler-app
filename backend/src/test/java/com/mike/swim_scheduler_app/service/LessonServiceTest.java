package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.repository.LessonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LessonServiceTest {

    AutoCloseable openMocks;

    @InjectMocks
    private LessonService lessonService;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private WorkdayService workdayService;

    @Mock ClientLessonService clientLessonService;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNewLesson() {
        Lesson lesson = new Lesson();
        Workday workday = new Workday();
        workday.setId(1L);
        lesson.setWorkday(workday);

        when(workdayService.findById(1L)).thenReturn(Optional.of(workday));
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        Lesson savedLesson = lessonService.save(lesson);

        assertNotNull(savedLesson);
        verify(lessonRepository, times(1)).save(lesson);
    }

    @Test
    void testUpdateExistingLesson() {
        Lesson existingLesson = new Lesson();
        existingLesson.setId(1L);

        Workday workday = new Workday();
        workday.setId(1L);
        existingLesson.setWorkday(workday);

        when(lessonRepository.findById(existingLesson.getId())).thenReturn(Optional.of(existingLesson));
        when(workdayService.findById(workday.getId())).thenReturn(Optional.of(workday));
        when(lessonRepository.save(existingLesson)).thenReturn(existingLesson);

        Lesson updatedLesson = lessonService.save(existingLesson);

        assertNotNull(updatedLesson);
        assertEquals(existingLesson, updatedLesson);
        verify(lessonRepository, times(1)).save(existingLesson);
    }

    @Test
    void testSaveLessonWithClients() {
        Lesson lesson = new Lesson();
        lesson.setId(1L);

        Workday workday = new Workday();
        workday.setId(1L);
        lesson.setWorkday(workday);

        when(workdayService.findById(1L)).thenReturn(Optional.of(workday));
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        Lesson savedLesson = lessonService.save(lesson);
        verify(clientLessonService, times(0)).saveClientLesson(any());

        assertEquals(1L, savedLesson.getId());
    }

    @Test
    void testFindLessonById() {
        Lesson lesson = new Lesson();
        lesson.setId(1L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Optional<Lesson> foundLesson = lessonService.findById(1L);
        assertTrue(foundLesson.isPresent());
        assertEquals(1L, foundLesson.get().getId());
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

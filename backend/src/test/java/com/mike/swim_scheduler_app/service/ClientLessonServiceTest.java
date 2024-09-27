package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.ClientLesson;
import com.mike.swim_scheduler_app.model.ClientLessonId;
import com.mike.swim_scheduler_app.repository.ClientLessonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientLessonServiceTest {

    AutoCloseable openMocks;

    @InjectMocks
    private ClientLessonService clientLessonService;

    @Mock
    private ClientLessonRepository clientLessonRepository;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveClientLesson() {
        ClientLesson clientLesson = new ClientLesson();

        when(clientLessonRepository.save(clientLesson)).thenReturn(clientLesson);

        ClientLesson savedClientLesson = clientLessonService.saveClientLesson(clientLesson);

        assertNotNull(savedClientLesson);
        verify(clientLessonRepository, times(1)).save(clientLesson);
    }

    @Test
    void testRemoveClientLessonsForLesson() {
        ClientLessonId id = new ClientLessonId(1L, 2L);

        doNothing().when(clientLessonRepository).deleteById(id);

        clientLessonService.removeClientLessonById(id);

        verify(clientLessonRepository, times(1)).deleteById(id);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

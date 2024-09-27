package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Lesson;
import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WorkdayServiceTest {

    AutoCloseable openMocks;

    @InjectMocks
    private WorkdayService workdayService;

    @Mock
    private WorkdayRepository workdayRepository;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveWorkday() {
        Workday workday = new Workday();
        when(workdayRepository.save(workday)).thenReturn(workday);

        Workday savedWorkday = workdayService.save(workday);

        assertNotNull(savedWorkday);
        verify(workdayRepository, times(1)).save(workday);
    }

    @Test
    void testUpdateWorkdayTimes() {
        Workday workday = new Workday();
        workday.setLessons(Set.of(
                new Lesson(null, LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 1, 1, 10, 0)),
                new Lesson(null, LocalDateTime.of(2023, 1, 1, 10, 30), LocalDateTime.of(2023, 1, 1, 11, 30))
        ));

        workdayService.updateWorkdayTimes(workday);

        assertEquals(LocalDateTime.of(2023, 1, 1, 9, 0), workday.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 11, 30), workday.getEndTime());
    }

    @Test
    void testFindWorkdayById() {
        Workday workday = new Workday();
        workday.setId(1L);

        when(workdayRepository.findById(1L)).thenReturn(Optional.of(workday));

        Optional<Workday> foundWorkday = workdayService.findById(1L);
        assertTrue(foundWorkday.isPresent());
        assertEquals(1L, foundWorkday.get().getId());
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

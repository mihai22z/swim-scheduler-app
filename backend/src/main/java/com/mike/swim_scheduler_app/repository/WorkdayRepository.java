package com.mike.swim_scheduler_app.repository;

import com.mike.swim_scheduler_app.model.Workday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkdayRepository extends JpaRepository<Workday, Long> {
    Optional<Workday> findByDate(LocalDate date);
}

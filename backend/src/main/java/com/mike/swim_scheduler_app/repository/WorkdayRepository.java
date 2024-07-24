package com.mike.swim_scheduler_app.repository;

import com.mike.swim_scheduler_app.model.Workday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkdayRepository extends JpaRepository<Workday, Long> {
}

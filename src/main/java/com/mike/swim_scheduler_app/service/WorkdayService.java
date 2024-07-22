package com.mike.swim_scheduler_app.service;

import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.repository.WorkdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Workday save(Workday workday) {
        return workdayRepository.save(workday);
    }

    public void deleteById(Long id) {
        workdayRepository.deleteById(id);
    }
}

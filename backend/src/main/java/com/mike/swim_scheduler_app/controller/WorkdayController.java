package com.mike.swim_scheduler_app.controller;

import com.mike.swim_scheduler_app.model.Workday;
import com.mike.swim_scheduler_app.service.WorkdayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workdays")
public class WorkdayController {
    @Autowired
    private WorkdayService workdayService;

    @GetMapping
    public ResponseEntity<List<Workday>> listWorkdays() {
        List<Workday> workdays = workdayService.findAll();
        if (workdays.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(workdays);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workday> getWorkdayById(@PathVariable Long id) {
        return workdayService.findById(id)
                .map(workday -> ResponseEntity.ok().body(workday))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Workday> createWorkday(@RequestBody Workday workday) {
        Workday savedWorkday = workdayService.save(workday);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWorkday);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workday> updateWorkday(@PathVariable Long id, @RequestBody Workday workday) {
        return workdayService.findById(id)
                .map(existingWorkday -> {
                    workday.setId(id);
                    Workday updatedWorkday = workdayService.save(workday);
                    return ResponseEntity.ok().body(updatedWorkday);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkday(@PathVariable Long id) {
        return workdayService.findById(id)
                .map(workday -> {
                    workdayService.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.mike.swim_scheduler_app.repository;

import com.mike.swim_scheduler_app.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}

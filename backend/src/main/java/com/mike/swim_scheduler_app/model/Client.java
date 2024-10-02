package com.mike.swim_scheduler_app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phone;
    private String notes;
    private int subscriptionTotalLessons;
    private int remainingLessons;
    private LocalDate subscriptionStartDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClientLesson> clientLessons = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<ClientLesson> getClientLessons() {
        return clientLessons;
    }

    public void setClientLessons(Set<ClientLesson> clientLessons) {
        this.clientLessons = clientLessons;
    }

    public int getSubscriptionTotalLessons() {
        return subscriptionTotalLessons;
    }

    public void setSubscriptionTotalLessons(int subscriptionTotalLessons) {
        this.subscriptionTotalLessons = subscriptionTotalLessons;
    }

    public int getRemainingLessons() {
        return remainingLessons;
    }

    public void setRemainingLessons(int remainingLessons) {
        this.remainingLessons = remainingLessons;
    }

    public LocalDate getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }
}

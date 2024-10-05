package com.mike.swim_scheduler_app.model;

import java.time.LocalDate;
import java.util.List;

public class SubscriptionRequest {
    private List<Client> clients;
    private List<DayTime> days;
    private int totalWeeks;
    private LocalDate startDate;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<DayTime> getDays() {
        return days;
    }

    public void setDays(List<DayTime> days) {
        this.days = days;
    }

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public void setTotalWeeks(int totalWeeks) {
        this.totalWeeks = totalWeeks;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}

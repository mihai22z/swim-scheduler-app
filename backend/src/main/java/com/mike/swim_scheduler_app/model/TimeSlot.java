package com.mike.swim_scheduler_app.model;

import java.time.LocalDateTime;

public class TimeSlot {
    public TimeSlot(LocalDateTime time, boolean occupied) {
        this.time = time;
        this.occupied = occupied;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    private LocalDateTime time;
    private boolean occupied;
}

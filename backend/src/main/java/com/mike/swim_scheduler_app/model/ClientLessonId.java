package com.mike.swim_scheduler_app.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ClientLessonId implements Serializable {

    private Long clientId;
    private Long lessonId;

    public ClientLessonId() {}

    public ClientLessonId(Long clientId, Long lessonId) {
        this.clientId = clientId;
        this.lessonId = lessonId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}

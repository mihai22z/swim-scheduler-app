package com.mike.swim_scheduler_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class ClientLesson implements Serializable {

    @EmbeddedId
    private ClientLessonId id;

    @ManyToOne
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    @ManyToOne
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id")
    @JsonIgnore
    private Lesson lesson;

    public ClientLesson() {}

    public ClientLesson(ClientLessonId id, Client client, Lesson lesson) {
        this.id = id;
        this.client = client;
        this.lesson = lesson;
    }

    public ClientLessonId getId() {
        return id;
    }

    public void setId(ClientLessonId id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}

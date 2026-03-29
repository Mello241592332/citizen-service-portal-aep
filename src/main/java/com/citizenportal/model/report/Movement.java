package com.citizenportal.model.report;

import java.time.LocalDateTime;

public class Movement {
    private String id;
    private String description;
    private LocalDateTime date;

    public Movement(String id, String description, LocalDateTime date) {
        this.id = id;
        this.description = description;
        this.date = date;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
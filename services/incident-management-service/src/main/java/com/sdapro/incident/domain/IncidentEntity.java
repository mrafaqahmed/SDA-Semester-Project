package com.sdapro.incident.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "incidents")
public class IncidentEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;

    public IncidentEntity() {
    }

    public IncidentEntity(UUID id, String title, String status, String priority, String details) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.details = details;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

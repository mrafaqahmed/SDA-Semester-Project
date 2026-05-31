package com.sdapro.ingestion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "alerts")
public class AlertEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    public AlertEntity() {
    }

    public AlertEntity(UUID id, String source, String severity, String status, String payload) {
        this.id = id;
        this.source = source;
        this.severity = severity;
        this.status = status;
        this.payload = payload;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}

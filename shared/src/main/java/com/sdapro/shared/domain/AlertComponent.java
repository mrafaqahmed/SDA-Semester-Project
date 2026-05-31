package com.sdapro.shared.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * PATTERN: Composite
 * Component interface for alert tree structure
 */
@Data
@AllArgsConstructor
public abstract class AlertComponent {
    protected UUID id;
    protected String severity;
    protected Long timestamp;

    public abstract void add(AlertComponent component);
    public abstract void remove(AlertComponent component);
    public abstract java.util.List<AlertComponent> getChildren();
    public abstract String getSeverity();
}

/**
 * PATTERN: Composite - Leaf node
 */
@Data
public class SingleAlert extends AlertComponent {
    private Alert alert;

    public SingleAlert(Alert alert) {
        super(alert.getId(), alert.getSeverity(), System.currentTimeMillis());
        this.alert = alert;
    }

    @Override
    public void add(AlertComponent component) {
        throw new UnsupportedOperationException("Cannot add to SingleAlert");
    }

    @Override
    public void remove(AlertComponent component) {
        throw new UnsupportedOperationException("Cannot remove from SingleAlert");
    }

    @Override
    public java.util.List<AlertComponent> getChildren() {
        return new java.util.ArrayList<>();
    }

    @Override
    public String getSeverity() {
        return this.alert.getSeverity();
    }
}

/**
 * PATTERN: Composite - Composite node
 */
@Data
public class AlertCampaign extends AlertComponent {
    private String campaignName;
    private String attackPattern;
    private java.util.List<AlertComponent> children = new java.util.ArrayList<>();

    public AlertCampaign(String campaignName, String attackPattern) {
        super(UUID.randomUUID(), "CRITICAL", System.currentTimeMillis());
        this.campaignName = campaignName;
        this.attackPattern = attackPattern;
    }

    @Override
    public void add(AlertComponent component) {
        this.children.add(component);
    }

    @Override
    public void remove(AlertComponent component) {
        this.children.remove(component);
    }

    @Override
    public java.util.List<AlertComponent> getChildren() {
        return this.children;
    }

    @Override
    public String getSeverity() {
        // Return max severity from children
        return this.children.stream()
            .map(AlertComponent::getSeverity)
            .max(String::compareTo)
            .orElse("LOW");
    }
}

/**
 * PATTERN: Composite - Another composite type
 */
@Data
public class IncidentCluster extends AlertComponent {
    private String clusterId;
    private String correlationRule;
    private java.util.List<AlertComponent> children = new java.util.ArrayList<>();

    public IncidentCluster(String correlationRule) {
        super(UUID.randomUUID(), "HIGH", System.currentTimeMillis());
        this.clusterId = UUID.randomUUID().toString();
        this.correlationRule = correlationRule;
    }

    @Override
    public void add(AlertComponent component) {
        this.children.add(component);
    }

    @Override
    public void remove(AlertComponent component) {
        this.children.remove(component);
    }

    @Override
    public java.util.List<AlertComponent> getChildren() {
        return this.children;
    }

    @Override
    public String getSeverity() {
        return this.children.stream()
            .map(AlertComponent::getSeverity)
            .max(String::compareTo)
            .orElse("MEDIUM");
    }
}

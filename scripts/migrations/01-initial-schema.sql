-- PostgreSQL Migrations for SDA-Pro

-- ============ INCIDENT MANAGEMENT ============
CREATE TABLE incidents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    assigned_analyst UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT severity_check CHECK (severity IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW'))
);

CREATE INDEX idx_incident_state ON incidents(state);
CREATE INDEX idx_incident_created ON incidents(created_at DESC);
CREATE INDEX idx_incident_severity ON incidents(severity);

CREATE TABLE incident_alerts (
    id SERIAL PRIMARY KEY,
    incident_id UUID NOT NULL REFERENCES incidents(id),
    alert_id UUID NOT NULL,
    CONSTRAINT unique_incident_alert UNIQUE (incident_id, alert_id)
);

-- ============ AUDIT LOGGING ============
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    event_data TEXT,
    actor VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT event_type_check CHECK (event_type IN (
        'AlertIngestedEvent', 'IncidentCreatedEvent', 'IncidentStateChangedEvent'
    ))
);

CREATE INDEX idx_audit_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_aggregate_id ON audit_logs(aggregate_id);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp DESC);
CREATE INDEX idx_audit_actor ON audit_logs(actor);

-- ============ IDENTITY & RBAC ============
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    last_login TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id),
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, roles),
    CONSTRAINT roles_check CHECK (roles IN ('ANALYST', 'INCIDENT_COMMANDER', 'ADMIN'))
);

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);

-- ============ SEED DATA ============
INSERT INTO users (id, username, password_hash, email, enabled, created_at, last_login) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'analyst1', 'hash1', 'analyst1@company.com', true, NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440002', 'incident_commander', 'hash2', 'commander@company.com', true, NOW(), NOW()),
    ('550e8400-e29b-41d4-a716-446655440003', 'admin', 'hash3', 'admin@company.com', true, NOW(), NOW());

INSERT INTO user_roles (user_id, roles) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'ANALYST'),
    ('550e8400-e29b-41d4-a716-446655440002', 'INCIDENT_COMMANDER'),
    ('550e8400-e29b-41d4-a716-446655440003', 'ADMIN');

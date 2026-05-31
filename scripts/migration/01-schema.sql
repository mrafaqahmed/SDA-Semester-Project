CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY,
    source VARCHAR(50),
    severity VARCHAR(20),
    raw_payload TEXT,
    normalized_json JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS incidents (
    id UUID PRIMARY KEY,
    state VARCHAR(50),
    severity VARCHAR(20),
    alert_ids JSONB,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_events (
    id SERIAL PRIMARY KEY,
    event_type VARCHAR(100),
    payload JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);
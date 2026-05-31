#!/bin/bash
set -e

echo "Waiting for services..."
for _ in $(seq 1 30); do
  if curl -fsS http://localhost:8081/actuator/health >/dev/null 2>&1 && curl -fsS http://localhost:8083/actuator/health >/dev/null 2>&1; then
    break
  fi
  sleep 2
done

echo "Sending test alert..."
curl -fsS -X POST http://localhost:8081/api/alerts -H "Content-Type: application/json" -d '{
  "source": "SPLUNK",
  "rawPayload": "{\"src_ip\":\"8.8.8.8\",\"dest_ip\":\"10.0.0.1\",\"message\":\"Port scan\"}"
}' >/dev/null

echo "Waiting for incident processing..."
for _ in $(seq 1 30); do
  INCIDENTS=$(curl -s http://localhost:8083/api/incidents || true)
  if echo "$INCIDENTS" | grep -q '"id"'; then
    echo "✅ Incident created."
    echo "✅ End-to-end test passed."
    exit 0
  fi
  sleep 2
done

echo "❌ No incident found."
exit 1
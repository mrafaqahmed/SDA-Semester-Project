/**
 * SOC Dashboard - MVC Architecture
 * PATTERN: MVC (Model-View-Controller)
 * Real-time updates via Server-Sent Events (SSE)
 * 
 * SDA-Pro: Security Incident Response & Threat Mitigation Platform
 * Team: Afaq Ahmed | Haseeb ur Rehman | Farhan ul Haq
 */

// ============ MODEL (Observer Pattern for data binding) ============
// PATTERN: Observer — Models notify Views when data changes

class AlertStreamModel {
  constructor() {
    this.alerts = [];
    this.observers = []; // PATTERN: Observer — subscriber list
  }

  addAlert(alert) {
    this.alerts.unshift(alert); // newest first
    this.notifyObservers();
  }

  subscribe(callback) {
    this.observers.push(callback);
  }

  notifyObservers() {
    this.observers.forEach(cb => cb(this.alerts));
  }

  getAlerts() {
    return this.alerts;
  }
}

class IncidentQueueModel {
  constructor() {
    this.incidents = [];
    this.observers = [];
  }

  addIncident(incident) {
    this.incidents.unshift(incident);
    this.notifyObservers();
  }

  updateIncidentState(incidentId, newState) {
    const incident = this.incidents.find(i => i.id === incidentId);
    if (incident) {
      incident.state = newState;
      this.notifyObservers();
    }
  }

  subscribe(callback) {
    this.observers.push(callback);
  }

  notifyObservers() {
    this.observers.forEach(cb => cb(this.incidents));
  }

  getIncidents() {
    return this.incidents;
  }
}

class DashboardMetricsModel {
  constructor() {
    this.metrics = {
      totalAlerts: 0,
      activeIncidents: 0,
      avgResponseTime: 0,
      threatsBlocked: 0
    };
    this.observers = [];
  }

  updateMetrics(data) {
    this.metrics = { ...this.metrics, ...data };
    this.notifyObservers();
  }

  subscribe(callback) {
    this.observers.push(callback);
  }

  notifyObservers() {
    this.observers.forEach(cb => cb(this.metrics));
  }

  getMetrics() {
    return this.metrics;
  }
}

// ============ VIEW (Renders UI from Model state) ============
// PATTERN: MVC — Views subscribe to Models and render DOM

class AlertStreamView {
  constructor(model) {
    this.model = model;
    this.model.subscribe(() => this.render());
  }

  render() {
    const alerts = this.model.getAlerts();

    const alertHtml = alerts.map(alert => `
      <div class="alert-item severity-${alert.severity}">
        <span class="timestamp">${alert.timestamp}</span>
        <span class="source">[${alert.source}]</span>
        <span class="title">${alert.title}</span>
      </div>
    `).join('');

    document.getElementById('alert-stream').innerHTML = alertHtml || '<p>Waiting for alerts...</p>';
  }
}

class IncidentQueueView {
  constructor(model) {
    this.model = model;
    this.model.subscribe(() => this.render());
  }

  render() {
    const incidents = this.model.getIncidents();

    const incidentHtml = incidents.map(incident => `
      <div class="incident-row state-${incident.state}">
        <span class="id">${incident.id}</span>
        <span class="title">${incident.title}</span>
        <span class="severity">${incident.severity}</span>
        <span class="state">${incident.state}</span>
        <button onclick="transitionIncident('${incident.id}')">Transition</button>
      </div>
    `).join('');

    document.getElementById('incident-queue').innerHTML = incidentHtml || '<p>No active incidents</p>';
  }
}

class MetricsDashboardView {
  constructor(model) {
    this.model = model;
    this.model.subscribe(() => this.render());
  }

  render() {
    const metrics = this.model.getMetrics();

    const metricsHtml = `
      <h2>📊 Key Metrics</h2>
      <div class="metrics-grid">
        <div class="metric-card">
          <h3>Total Alerts</h3>
          <p class="metric-value">${metrics.totalAlerts}</p>
        </div>
        <div class="metric-card">
          <h3>Active Incidents</h3>
          <p class="metric-value">${metrics.activeIncidents}</p>
        </div>
        <div class="metric-card">
          <h3>Avg Response Time</h3>
          <p class="metric-value">${metrics.avgResponseTime}s</p>
        </div>
        <div class="metric-card">
          <h3>Threats Blocked</h3>
          <p class="metric-value">${metrics.threatsBlocked}</p>
        </div>
      </div>
    `;

    document.getElementById('metrics-dashboard').innerHTML = metricsHtml;
  }
}

// ============ CONTROLLER (Coordinates Model ↔ View) ============
// PATTERN: MVC — Controller handles user actions and data flow

class DashboardController {
  constructor() {
    // Models
    this.alertModel = new AlertStreamModel();
    this.incidentModel = new IncidentQueueModel();
    this.metricsModel = new DashboardMetricsModel();

    // Views
    this.alertView = new AlertStreamView(this.alertModel);
    this.incidentView = new IncidentQueueView(this.incidentModel);
    this.metricsView = new MetricsDashboardView(this.metricsModel);

    // Load demo data for demonstration
    this.loadDemoData();

    // Attempt SSE connection (graceful fallback to demo simulation)
    this.connectSSE();
  }

  // ---- Demo data to populate dashboard for demonstration ----
  loadDemoData() {
    const now = new Date();
    const fmt = (d) => d.toISOString().replace('T', ' ').substring(0, 19);

    // Seed metrics
    this.metricsModel.updateMetrics({
      totalAlerts: 147,
      activeIncidents: 5,
      avgResponseTime: 12,
      threatsBlocked: 38
    });

    // Seed alerts (newest first)
    const demoAlerts = [
      { severity: 'CRITICAL', source: 'CrowdStrike', title: 'Ransomware payload detected on endpoint WS-0042', timestamp: fmt(new Date(now - 60000)) },
      { severity: 'HIGH', source: 'Splunk', title: 'Lateral movement detected: SMB brute-force from 10.0.3.15', timestamp: fmt(new Date(now - 120000)) },
      { severity: 'HIGH', source: 'Firewall', title: 'Outbound C2 beacon to 185.220.101.34:443 blocked', timestamp: fmt(new Date(now - 180000)) },
      { severity: 'MEDIUM', source: 'Splunk', title: 'Anomalous login from VPN — user jdoe, IP 91.132.89.7', timestamp: fmt(new Date(now - 300000)) },
      { severity: 'CRITICAL', source: 'CrowdStrike', title: 'Credential dump (mimikatz) on DC-01', timestamp: fmt(new Date(now - 420000)) },
      { severity: 'LOW', source: 'Firewall', title: 'Port scan from 203.0.113.45 — 1,200 ports in 10 s', timestamp: fmt(new Date(now - 600000)) },
      { severity: 'MEDIUM', source: 'Splunk', title: 'Failed SSH login x47 on bastion host from 45.33.32.156', timestamp: fmt(new Date(now - 900000)) },
      { severity: 'HIGH', source: 'CrowdStrike', title: 'Process injection detected: svchost → powershell.exe', timestamp: fmt(new Date(now - 1200000)) },
    ];
    demoAlerts.forEach(a => this.alertModel.addAlert(a));

    // Seed incidents (State Pattern demonstration)
    const demoIncidents = [
      { id: 'INC-2026-0042', title: 'Ransomware Campaign — Finance Dept', severity: 'CRITICAL', state: 'CONTAINMENT' },
      { id: 'INC-2026-0041', title: 'C2 Beacon — Endpoint WS-0015', severity: 'HIGH', state: 'UNDER_TRIAGE' },
      { id: 'INC-2026-0040', title: 'Credential Theft — Domain Controller', severity: 'CRITICAL', state: 'ERADICATION' },
      { id: 'INC-2026-0039', title: 'Brute-Force SSH — Bastion Host', severity: 'MEDIUM', state: 'NEW' },
      { id: 'INC-2026-0038', title: 'Data Exfiltration Attempt — HR DB', severity: 'HIGH', state: 'UNDER_TRIAGE' },
    ];
    demoIncidents.forEach(i => this.incidentModel.addIncident(i));

    // Seed response actions
    this.renderResponseActions();

    // Start live simulation — stream new alerts every few seconds
    this.startLiveSimulation();
  }

  renderResponseActions() {
    const html = `
      <div style="font-size:13px;">
        <div class="alert-item severity-HIGH" style="border-left-color:#4ade80; margin-bottom:6px;">
          <span class="timestamp" style="min-width:140px;">2026-05-31 01:15:03</span>
          <span class="source">[Auto]</span>
          <span>🔒 Isolated endpoint WS-0042 — Strategy: Aggressive</span>
        </div>
        <div class="alert-item severity-MEDIUM" style="border-left-color:#4ade80; margin-bottom:6px;">
          <span class="timestamp" style="min-width:140px;">2026-05-31 01:14:48</span>
          <span class="source">[Auto]</span>
          <span>🚫 Blocked IP 185.220.101.34 at perimeter firewall</span>
        </div>
        <div class="alert-item severity-MEDIUM" style="border-left-color:#fbbf24; margin-bottom:6px;">
          <span class="timestamp" style="min-width:140px;">2026-05-31 01:13:22</span>
          <span class="source">[Analyst]</span>
          <span>👤 Disabled account jdoe — pending investigation</span>
        </div>
        <div class="alert-item severity-LOW" style="border-left-color:#4ade80; margin-bottom:6px;">
          <span class="timestamp" style="min-width:140px;">2026-05-31 01:10:55</span>
          <span class="source">[Auto]</span>
          <span>📁 Quarantined file mimikatz.exe on DC-01</span>
        </div>
        <div class="alert-item severity-LOW" style="border-left-color:#60a5fa; margin-bottom:6px;">
          <span class="timestamp" style="min-width:140px;">2026-05-31 01:09:10</span>
          <span class="source">[System]</span>
          <span>📧 Escalation sent to SOC Manager via PagerDuty</span>
        </div>
      </div>
    `;
    document.getElementById('response-console').innerHTML = html;
  }

  startLiveSimulation() {
    const liveAlerts = [
      { severity: 'HIGH', source: 'Splunk', title: 'DNS tunnelling detected — exfiltration via TXT records' },
      { severity: 'MEDIUM', source: 'Firewall', title: 'Geo-anomaly: Login from North Korea for admin account' },
      { severity: 'CRITICAL', source: 'CrowdStrike', title: 'Cobalt Strike beacon loaded in memory on SRV-DB-02' },
      { severity: 'LOW', source: 'Splunk', title: 'Unusual after-hours VPN connection from user rsmith' },
      { severity: 'HIGH', source: 'CrowdStrike', title: 'Privilege escalation: local admin created on WS-0099' },
      { severity: 'MEDIUM', source: 'Firewall', title: 'TLS certificate mismatch on outbound HTTPS to 198.51.100.7' },
    ];

    let idx = 0;
    setInterval(() => {
      const base = liveAlerts[idx % liveAlerts.length];
      const alert = {
        ...base,
        timestamp: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      this.alertModel.addAlert(alert);

      // Update metrics
      const m = this.metricsModel.getMetrics();
      this.metricsModel.updateMetrics({
        totalAlerts: m.totalAlerts + 1,
        threatsBlocked: m.threatsBlocked + (Math.random() > 0.5 ? 1 : 0)
      });

      idx++;
    }, 6000); // new alert every 6 seconds
  }

  connectSSE() {
    try {
      const eventSource = new EventSource('/api/events/stream');

      eventSource.addEventListener('alert-ingested', (event) => {
        const alert = JSON.parse(event.data);
        this.alertModel.addAlert(alert);
        const m = this.metricsModel.getMetrics();
        this.metricsModel.updateMetrics({ totalAlerts: m.totalAlerts + 1 });
      });

      eventSource.addEventListener('incident-created', (event) => {
        const incident = JSON.parse(event.data);
        this.incidentModel.addIncident(incident);
        const m = this.metricsModel.getMetrics();
        this.metricsModel.updateMetrics({ activeIncidents: m.activeIncidents + 1 });
      });

      eventSource.addEventListener('incident-state-changed', (event) => {
        const data = JSON.parse(event.data);
        this.incidentModel.updateIncidentState(data.incidentId, data.newState);
      });

      eventSource.onerror = () => {
        console.log('[CONTROLLER] SSE unavailable — using demo simulation');
        eventSource.close();
      };
    } catch (e) {
      console.log('[CONTROLLER] SSE not available, demo mode active');
    }
  }

  transitionIncident(incidentId, action) {
    // PATTERN: State — transition incident through lifecycle states
    const stateOrder = ['NEW', 'UNDER_TRIAGE', 'CONTAINMENT', 'ERADICATION', 'RECOVERY', 'POST_INCIDENT_REVIEW', 'CLOSED'];
    const incident = this.incidentModel.getIncidents().find(i => i.id === incidentId);
    if (incident) {
      const curIdx = stateOrder.indexOf(incident.state);
      if (curIdx < stateOrder.length - 1) {
        const nextState = stateOrder[curIdx + 1];
        this.incidentModel.updateIncidentState(incidentId, nextState);
        console.log(`[STATE] ${incidentId}: ${incident.state} → ${nextState}`);
      }
    }
  }
}

// ============ INITIALIZATION ============
let dashboardController;

window.addEventListener('DOMContentLoaded', () => {
  console.log('[DASHBOARD] Initializing SDA-Pro MVC dashboard...');
  dashboardController = new DashboardController();

  // Update timestamp
  setInterval(() => {
    document.getElementById('last-update').textContent = new Date().toLocaleTimeString();
  }, 1000);

  console.log('[DASHBOARD] Dashboard initialized — MVC pattern active');
});

window.transitionIncident = (incidentId) => {
  dashboardController.transitionIncident(incidentId, 'next');
};

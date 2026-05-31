/**
 * SOC Dashboard - MVC Architecture
 * PATTERN: MVC (Model-View-Controller)
 * Real-time updates via Server-Sent Events (SSE)
 */

// ============ MODEL ============
class AlertStreamModel {
  constructor() {
    this.alerts = [];
    this.observers = [];
  }

  addAlert(alert) {
    this.alerts.push(alert);
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
    this.incidents.push(incident);
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

// ============ VIEW ============
class AlertStreamView {
  constructor(model) {
    this.model = model;
    this.model.subscribe(() => this.render());
  }

  render() {
    const alerts = this.model.getAlerts();
    console.log(`[VIEW] Rendering ${alerts.length} alerts`);

    const alertHtml = alerts.map(alert => `
      <div class="alert-item severity-${alert.severity}">
        <span class="timestamp">${alert.timestamp}</span>
        <span class="source">[${alert.source}]</span>
        <span class="title">${alert.title}</span>
        <span class="description">${alert.description}</span>
      </div>
    `).join('');

    document.getElementById('alert-stream').innerHTML = alertHtml || '<p>No alerts</p>';
  }
}

class IncidentQueueView {
  constructor(model) {
    this.model = model;
    this.model.subscribe(() => this.render());
  }

  render() {
    const incidents = this.model.getIncidents();
    console.log(`[VIEW] Rendering ${incidents.length} incidents`);

    const incidentHtml = incidents.map(incident => `
      <div class="incident-row state-${incident.state}">
        <span class="id">${incident.id}</span>
        <span class="title">${incident.title}</span>
        <span class="severity">${incident.severity}</span>
        <span class="state">${incident.state}</span>
        <button onclick="transitionIncident('${incident.id}')">Transition</button>
      </div>
    `).join('');

    document.getElementById('incident-queue').innerHTML = incidentHtml || '<p>No incidents</p>';
  }
}

class MetricsDashboardView {
  constructor(model) {
    this.model = model;
    this.model.subscribe(() => this.render());
  }

  render() {
    const metrics = this.model.getMetrics();
    console.log('[VIEW] Rendering metrics:', metrics);

    const metricsHtml = `
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

// ============ CONTROLLER ============
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

    // Initialize SSE connection
    this.connectSSE();
  }

  connectSSE() {
    const eventSource = new EventSource('/api/events/stream');

    eventSource.addEventListener('alert-ingested', (event) => {
      const alert = JSON.parse(event.data);
      console.log('[CONTROLLER] Received alert:', alert);
      this.alertModel.addAlert(alert);
      this.metricsModel.updateMetrics({
        totalAlerts: this.metricsModel.metrics.totalAlerts + 1
      });
    });

    eventSource.addEventListener('incident-created', (event) => {
      const incident = JSON.parse(event.data);
      console.log('[CONTROLLER] Received incident:', incident);
      this.incidentModel.addIncident(incident);
      this.metricsModel.updateMetrics({
        activeIncidents: this.metricsModel.metrics.activeIncidents + 1
      });
    });

    eventSource.addEventListener('incident-state-changed', (event) => {
      const data = JSON.parse(event.data);
      console.log('[CONTROLLER] Incident state changed:', data);
      this.incidentModel.updateIncidentState(data.incidentId, data.newState);
    });

    eventSource.onerror = (error) => {
      console.error('[CONTROLLER] SSE connection error:', error);
      eventSource.close();
      setTimeout(() => this.connectSSE(), 5000);
    };
  }

  transitionIncident(incidentId, action) {
    fetch(`/api/incidents/${incidentId}/${action}`, { method: 'PUT' })
      .then(r => r.json())
      .then(data => {
        console.log('[CONTROLLER] Transition response:', data);
        this.incidentModel.updateIncidentState(incidentId, data.incident.state);
      });
  }

  getAlerts() {
    return this.alertModel.getAlerts();
  }

  getIncidents() {
    return this.incidentModel.getIncidents();
  }

  getMetrics() {
    return this.metricsModel.getMetrics();
  }
}

// ============ INITIALIZATION ============
let dashboardController;

window.addEventListener('DOMContentLoaded', () => {
  console.log('[DASHBOARD] Initializing MVC dashboard...');
  dashboardController = new DashboardController();
  console.log('[DASHBOARD] Dashboard initialized');
});

window.transitionIncident = (incidentId) => {
  dashboardController.transitionIncident(incidentId, 'triage');
};

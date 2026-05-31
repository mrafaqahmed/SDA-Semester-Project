const healthSummary = document.getElementById('healthSummary');
const healthDetails = document.getElementById('healthDetails');
const healthDot = document.getElementById('healthDot');
const healthLabel = document.getElementById('healthLabel');
const openIncidents = document.getElementById('openIncidents');
const highPriority = document.getElementById('highPriority');
const lastUpdated = document.getElementById('lastUpdated');
const incidentsList = document.getElementById('incidentsList');
const operationsMessage = document.getElementById('operationsMessage');
const refreshButton = document.getElementById('refreshButton');

async function loadHealth() {
  try {
    const [alertResponse, incidentResponse] = await Promise.all([
      fetch('http://127.0.0.1:8081/api/health'),
      fetch('http://127.0.0.1:8083/api/health')
    ]);

    if (!alertResponse.ok || !incidentResponse.ok) {
      throw new Error('One or more backend health checks failed.');
    }

    const [alertText, incidentText] = await Promise.all([
      alertResponse.text(),
      incidentResponse.text()
    ]);

    const isHealthy = /healthy/i.test(alertText) && /healthy/i.test(incidentText);

    renderHealthState(
      isHealthy ? 'UP' : 'DOWN',
      isHealthy
        ? 'Alert ingestion and incident management are both responding normally.'
        : 'One or more backend services are not healthy right now.'
    );
    setOperationalMessage('Dashboard is using direct backend URLs and refreshing every 30 seconds.');
  } catch (error) {
    renderError('Unable to reach the backend services. Please check the stack and retry.');
  }
}


function formatTimestamp() {
  return new Date().toLocaleString();
}

function safeString(value) {
  if (typeof value === 'string') {
    return value;
  }

  if (value == null) {
    return 'No details provided.';
  }

  try {
    return JSON.stringify(value);
  } catch (error) {
    return 'No details provided.';
  }
}

function formatDetails(details) {
  const text = safeString(details).trim();

  if (!text) {
    return 'No additional details captured.';
  }

  return text
    .replace(/\{source=([A-Z_]+), rawPayload=(.+)\}/g, 'Source: $1 · Payload: $2')
    .replace(/\s+/g, ' ')
    .trim();
}

function badgeClass(priority, status) {
  const priorityClass = `priority-${String(priority || 'low').toLowerCase()}`;
  const statusClass = `status-${String(status || 'open').toLowerCase().replace(/\s+/g, '-')}`;
  return `${priorityClass} ${statusClass}`;
}

function renderIncidentCard(incident) {
  const priority = incident.priority || 'LOW';
  const status = incident.status || 'OPEN';
  const details = formatDetails(incident.details);
  const classes = badgeClass(priority, status).split(' ');

  return `
    <article class="incident-item">
      <div class="incident-header">
        <div>
          <h3>${incident.title || 'Untitled incident'}</h3>
          <p>${details}</p>
        </div>
        <div class="badge-row">
          <span class="badge ${classes[0]}">${priority}</span>
          <span class="badge ${classes[1]}">${status}</span>
        </div>
      </div>
      <div class="meta">ID: ${incident.id || 'n/a'}</div>
    </article>
  `;
}

function setOperationalMessage(message) {
  operationsMessage.textContent = message;
}

function renderHealthState(status, message) {
  const normalized = String(status || 'DOWN').toUpperCase();
  healthSummary.textContent = normalized === 'UP' ? 'Operational' : 'Degraded';
  healthDetails.textContent = message;
  healthLabel.textContent = normalized === 'UP' ? 'Backend healthy' : 'Backend unavailable';
  healthDot.className = `status-dot ${normalized === 'UP' ? 'up' : 'down'}`;
}

function renderIncidents(incidents) {
  if (!Array.isArray(incidents) || incidents.length === 0) {
    incidentsList.innerHTML = '<div class="empty-state">No incidents are currently available.</div>';
    openIncidents.textContent = '0';
    highPriority.textContent = '0';
    return;
  }

  incidentsList.innerHTML = incidents.map(renderIncidentCard).join('');
  openIncidents.textContent = incidents.filter((item) => String(item.status || '').toUpperCase() !== 'RESOLVED').length;
  highPriority.textContent = incidents.filter((item) => String(item.priority || '').toUpperCase() === 'HIGH').length;
}

function renderError(message) {
  healthSummary.textContent = 'Unavailable';
  healthDetails.textContent = message;
  healthLabel.textContent = 'Backend unavailable';
  healthDot.className = 'status-dot down';
  incidentsList.innerHTML = `<div class="error-state">${message}</div>`;
  openIncidents.textContent = '0';
  highPriority.textContent = '0';
  setOperationalMessage('The dashboard is in fallback mode until the backend responds again.');
}

async function loadIncidents() {
  try {
    const response = await fetch('http://127.0.0.1:8083/api/incidents');

    if (!response.ok) {
      throw new Error(`Incident API returned ${response.status}`);
    }

    const incidents = await response.json();
    renderIncidents(incidents);
  } catch (error) {
    renderError('Unable to load incidents from the backend. The dashboard is showing a safe fallback state.');
  }
}

async function refreshDashboard() {
  lastUpdated.textContent = formatTimestamp();
  await Promise.all([loadHealth(), loadIncidents()]);
}

refreshButton.addEventListener('click', () => {
  refreshDashboard();
});

refreshDashboard();
setInterval(refreshDashboard, 30000);

/**
 * AI Interview Preparation Platform - Dashboard Module
 * Visual performance charts, statistics, and interview history
 */

const Dashboard = {
  radarChartInstance: null,
  progressionChartInstance: null,

  async init() {
    if (!Api.isAuthenticated()) {
      App.navigateTo('landing');
      Auth.openModal('login');
      return;
    }

    await this.loadDashboardData();
  },

  async loadDashboardData() {
    try {
      let report;
      try {
        report = await Api.performance.getMe();
      } catch (e) {
        // Backend unreachable — show a real empty state for this user (not fake data)
        const user = Api.getCurrentUser();
        report = {
          totalInterviews: 0,
          completedInterviews: 0,
          averageScore: 0,
          highestScore: 0,
          improvementRate: 0,
          technicalAvg: 0,
          communicationAvg: 0,
          technologyProficiency: {},
          recentInterviews: [],
        };
      }

      this.renderMetrics(report);
      this.renderCharts(report);
      this.renderHistoryTable(report.recentInterviews || []);
    } catch (error) {
      console.error('Failed to load dashboard:', error);
      App.showToast('Could not load performance stats', 'error');
    }
  },

  renderMetrics(report) {
    const user = Api.getCurrentUser();
    const welcomeEl = document.getElementById('dash-welcome-name');
    if (welcomeEl && user) {
      welcomeEl.textContent = user.fullName.split(' ')[0];
    }

    const totalInterviews = report.totalInterviews || 0;
    const completedInterviews = report.completedInterviews || 0;
    const avgScore = parseFloat(report.averageScore) || 0;
    const impRate = parseFloat(report.improvementRate) || 0;

    document.getElementById('metric-total-interviews').textContent = totalInterviews;
    document.getElementById('metric-avg-score').textContent = totalInterviews > 0 ? `${avgScore.toFixed(1)}/10` : '—';
    document.getElementById('metric-completed').textContent = completedInterviews;

    const impEl = document.getElementById('metric-improvement');
    if (totalInterviews === 0) {
      impEl.textContent = '—';
    } else {
      impEl.textContent = impRate >= 0 ? `+${impRate.toFixed(1)}%` : `${impRate.toFixed(1)}%`;
    }
  },

  renderCharts(report) {
    const isLight = document.documentElement.getAttribute('data-theme') === 'light';
    const gridColor = isLight ? 'rgba(0, 0, 0, 0.07)' : 'rgba(255, 255, 255, 0.08)';
    const textColor = isLight ? '#334155' : '#94a3b8';
    const hasData = (report.totalInterviews || 0) > 0;

    // 1. Radar Chart: Domain Proficiency
    const radarCtx = document.getElementById('radar-proficiency-chart')?.getContext('2d');
    if (radarCtx) {
      if (this.radarChartInstance) this.radarChartInstance.destroy();

      const defaultDomains = ['Data Science', 'AI Developer', 'ML Eng', 'DevOps', 'Java', 'Python', 'SQL', 'DSA'];
      const profMap = report.technologyProficiency || {};

      // Only use actual data — never fake defaults. Show 0 for domains with no history.
      const radarValues = defaultDomains.map(d => {
        if (d === 'ML Eng') return parseFloat(profMap['Machine Learning']) || 0;
        return parseFloat(profMap[d]) || 0;
      });

      this.radarChartInstance = new Chart(radarCtx, {
        type: 'radar',
        data: {
          labels: defaultDomains,
          datasets: [{
            label: 'Skill Proficiency (/10)',
            data: radarValues,
            backgroundColor: isLight ? 'rgba(79, 70, 229, 0.18)' : 'rgba(99, 102, 241, 0.25)',
            borderColor: '#6366f1',
            pointBackgroundColor: '#06b6d4',
            pointBorderColor: '#ffffff',
            pointHoverBackgroundColor: '#ffffff',
            pointHoverBorderColor: '#6366f1',
            borderWidth: 2,
          }],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            r: {
              angleLines: { color: gridColor },
              grid: { color: gridColor },
              pointLabels: {
                color: textColor,
                font: { family: "'Plus Jakarta Sans', sans-serif", size: 10, weight: '600' },
              },
              ticks: {
                display: false,
                backdropColor: 'transparent',
                stepSize: 2,
              },
              suggestedMin: 0,
              suggestedMax: 10,
            },
          },
          plugins: {
            legend: { display: false },
          },
        },
      });
    }

    // 2. Progression Line Chart
    const progCtx = document.getElementById('line-progression-chart')?.getContext('2d');
    if (progCtx) {
      if (this.progressionChartInstance) this.progressionChartInstance.destroy();

      const recent = (report.recentInterviews || []).filter(r => r.status === 'COMPLETED').slice().reverse();

      // If no completed interviews, show a flat empty state chart
      const labels = recent.length > 0
        ? recent.map((r, i) => `#${i + 1} (${r.technology})`)
        : ['No sessions yet'];
      const scores = recent.length > 0
        ? recent.map(r => parseFloat(r.overallScore) || 0)
        : [0];

      this.progressionChartInstance = new Chart(progCtx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'Score Achieved (/10)',
            data: scores,
            borderColor: '#06b6d4',
            backgroundColor: isLight ? 'rgba(6, 182, 212, 0.08)' : 'rgba(6, 182, 212, 0.12)',
            fill: true,
            tension: 0.35,
            borderWidth: 3,
            pointRadius: recent.length > 0 ? 5 : 0,
            pointBackgroundColor: '#06b6d4',
            pointBorderColor: '#ffffff',
          }],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              grid: { color: gridColor },
              ticks: { color: textColor, font: { family: "'Plus Jakarta Sans', sans-serif" } },
            },
            y: {
              min: 0,
              max: 10,
              grid: { color: gridColor },
              ticks: { color: textColor, stepSize: 2 },
            },
          },
          plugins: {
            legend: { display: false },
          },
        },
      });
    }
  },

  renderHistoryTable(interviews) {
    const tbody = document.getElementById('history-table-body');
    if (!tbody) return;

    if (!interviews || interviews.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="6" style="text-align:center; padding: 2.5rem; color: var(--text-dim);">
            No interview sessions completed yet. Click <strong>"Start New Interview"</strong> to begin!
          </td>
        </tr>
      `;
      return;
    }

    tbody.innerHTML = interviews.map(item => {
      const dateStr = item.startedAt ? new Date(item.startedAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' }) : 'Recent';
      const diffClass = `badge-${(item.difficulty || 'medium').toLowerCase()}`;
      const score = item.overallScore ? `${Number(item.overallScore).toFixed(1)}/10` : 'In Progress';
      const statusClass = item.status === 'COMPLETED' ? 'badge-easy' : 'badge-medium';

      return `
        <tr>
          <td><span class="badge-pill badge-tech">${item.technology}</span></td>
          <td>${item.interviewType || 'Technical'}</td>
          <td><span class="badge-pill ${diffClass}">${item.difficulty}</span></td>
          <td><strong>${score}</strong></td>
          <td><span class="badge-pill ${statusClass}">${item.status}</span></td>
          <td>
            ${item.status === 'COMPLETED' 
              ? `<button class="btn btn-sm btn-secondary" onclick="Results.loadInterviewResult(${item.id})">View Report</button>`
              : `<button class="btn btn-sm btn-outline-glow" onclick="Interview.resumeInterview(${item.id})">Resume</button>`
            }
          </td>
        </tr>
      `;
    }).join('');
  },
};

window.Dashboard = Dashboard;

/**
 * AI Interview Preparation Platform - Main Application Router & Controller
 */

const App = {
  currentView: 'landing',

  init() {
    this.initTheme();
    this.bindNavigation();
    this.bindMobileMenu();
    this.handleRoute();

    // Initialize modules
    Auth.init();
    Interview.init();

    // Listen for hash changes
    window.addEventListener('hashchange', () => this.handleRoute());
  },

  initTheme() {
    const savedTheme = localStorage.getItem('interview_theme') || 'light';
    this.setTheme(savedTheme);

    const toggleBtn = document.getElementById('theme-toggle-btn');
    if (toggleBtn) {
      toggleBtn.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme') || 'dark';
        const next = current === 'light' ? 'dark' : 'light';
        this.setTheme(next);
        this.showToast(`Switched to ${next === 'light' ? 'White / Light' : 'Dark'} theme`, 'info', 2000);
      });
    }
  },

  setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('interview_theme', theme);
    const icon = document.getElementById('theme-toggle-icon');
    if (icon) {
      icon.textContent = theme === 'light' ? '☀️' : '🌙';
    }
    if (window.Dashboard && typeof window.Dashboard.initCharts === 'function') {
      window.Dashboard.init();
    }
  },

  bindNavigation() {
    // Nav links with data-target
    document.querySelectorAll('[data-target]').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const target = link.getAttribute('data-target');
        this.navigateTo(target);

        // Close mobile menu if open
        document.getElementById('nav-menu')?.classList.remove('open');
      });
    });
  },

  bindMobileMenu() {
    const toggle = document.getElementById('mobile-menu-toggle');
    const menu = document.getElementById('nav-menu');
    if (toggle && menu) {
      toggle.addEventListener('click', () => {
        menu.classList.toggle('open');
      });
    }
  },

  navigateTo(viewName) {
    window.location.hash = `#${viewName}`;
  },

  handleRoute() {
    const hash = window.location.hash.replace('#', '') || 'landing';
    this.switchView(hash);
  },

  switchView(viewName) {
    const validViews = [
      'landing',
      'dashboard',
      'interview-setup',
      'interview-session',
      'interview-results',
      'recommendations',
      'admin',
    ];

    const targetView = validViews.includes(viewName) ? viewName : 'landing';

    // Route Guards
    if ((targetView === 'dashboard' || targetView === 'interview-setup' || targetView === 'recommendations') && !Api.isAuthenticated()) {
      this.currentView = 'landing';
      this.renderView('landing');
      Auth.openModal('login');
      this.showToast('Please sign in to access your dashboard and interviews', 'info');
      return;
    }

    if (targetView === 'admin' && !Api.isAdmin()) {
      this.currentView = 'landing';
      this.renderView('landing');
      this.showToast('Administrator privileges required.', 'error');
      return;
    }

    this.currentView = targetView;
    this.renderView(targetView);

    // Initialize views
    if (targetView === 'dashboard') {
      Dashboard.init();
    } else if (targetView === 'recommendations') {
      Recommendations.init();
    } else if (targetView === 'admin') {
      Admin.init();
    }

    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
  },

  renderView(viewName) {
    document.querySelectorAll('.view-section').forEach(sec => {
      sec.classList.remove('active');
    });

    const activeSec = document.getElementById(`view-${viewName}`);
    if (activeSec) {
      activeSec.classList.add('active');
    }

    // Update active nav link
    document.querySelectorAll('.nav-link').forEach(link => {
      link.classList.remove('active');
      if (link.getAttribute('data-target') === viewName) {
        link.classList.add('active');
      }
    });
  },

  showToast(message, type = 'info', duration = 4000) {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    let icon = 'ℹ️';
    if (type === 'success') icon = '✓';
    else if (type === 'error') icon = '⚠';

    toast.innerHTML = `
      <span class="toast-icon" style="font-weight: 800;">${icon}</span>
      <span class="toast-message">${message}</span>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.animation = 'slideInRight 0.3s ease reverse forwards';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  },
};

document.addEventListener('DOMContentLoaded', () => {
  App.init();
});

window.App = App;

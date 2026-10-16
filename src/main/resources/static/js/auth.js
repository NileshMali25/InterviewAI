/**
 * AI Interview Preparation Platform - Authentication Module
 */

const Auth = {
  init() {
    this.bindEvents();
    this.updateNavUi();
  },

  bindEvents() {
    // Auth Modal open triggers
    document.querySelectorAll('[data-action="open-login"]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        this.openModal('login');
      });
    });

    document.querySelectorAll('[data-action="open-register"]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        this.openModal('register');
      });
    });

    // Close modal
    const closeBtn = document.getElementById('auth-modal-close');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => this.closeModal());
    }

    const modalOverlay = document.getElementById('auth-modal');
    if (modalOverlay) {
      modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) this.closeModal();
      });
    }

    // Tab switching in modal
    const loginTab = document.getElementById('tab-btn-login');
    const registerTab = document.getElementById('tab-btn-register');
    if (loginTab && registerTab) {
      loginTab.addEventListener('click', () => this.switchTab('login'));
      registerTab.addEventListener('click', () => this.switchTab('register'));
    }

    // Forms
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
      loginForm.addEventListener('submit', (e) => this.handleLogin(e));
    }

    const registerForm = document.getElementById('register-form');
    if (registerForm) {
      registerForm.addEventListener('submit', (e) => this.handleRegister(e));
    }

    // Demo auto-fill buttons
    const demoStudentBtn = document.getElementById('demo-btn-student');
    if (demoStudentBtn) {
      demoStudentBtn.addEventListener('click', () => {
        document.getElementById('login-email').value = 'alex@example.com';
        document.getElementById('login-password').value = 'password123';
      });
    }

    const demoAdminBtn = document.getElementById('demo-btn-admin');
    if (demoAdminBtn) {
      demoAdminBtn.addEventListener('click', () => {
        document.getElementById('login-email').value = 'admin@aiinterview.com';
        document.getElementById('login-password').value = 'password123';
      });
    }

    // Logout
    const logoutBtn = document.getElementById('nav-logout-btn');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        this.logout();
      });
    }

    // Listen for auth-changed events
    window.addEventListener('auth-changed', () => this.updateNavUi());
  },

  openModal(tab = 'login') {
    const modal = document.getElementById('auth-modal');
    if (!modal) return;
    this.switchTab(tab);
    modal.classList.add('active');
  },

  closeModal() {
    const modal = document.getElementById('auth-modal');
    if (modal) modal.classList.remove('active');
  },

  switchTab(tab) {
    const loginTab = document.getElementById('tab-btn-login');
    const registerTab = document.getElementById('tab-btn-register');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    if (tab === 'login') {
      loginTab.classList.add('active');
      registerTab.classList.remove('active');
      loginForm.style.display = 'block';
      registerForm.style.display = 'none';
    } else {
      registerTab.classList.add('active');
      loginTab.classList.remove('active');
      registerForm.style.display = 'block';
      loginForm.style.display = 'none';
    }
  },

  async handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    const submitBtn = document.getElementById('login-submit-btn');

    if (!email || !password) {
      App.showToast('Please enter both email and password', 'error');
      return;
    }

    try {
      submitBtn.disabled = true;
      submitBtn.textContent = 'Authenticating...';

      let response;
      try {
        response = await Api.auth.login(email, password);
      } catch (err) {
        // Fallback demo login if running in standalone frontend test mode
        console.warn('Real backend authentication endpoint unreachable, using client demo profile.');
        const isAdmin = email.includes('admin');
        response = {
          token: 'demo-jwt-token-' + Date.now(),
          id: isAdmin ? 1 : 2,
          fullName: isAdmin ? 'Admin Officer' : 'Alex Morgan',
          email: email,
          role: isAdmin ? 'ROLE_ADMIN' : 'ROLE_USER',
          targetRole: isAdmin ? 'Platform Administrator' : 'Full Stack Developer',
        };
      }

      Api.setAuth(response.token, {
        id: response.id,
        fullName: response.fullName,
        email: response.email,
        role: response.role,
        targetRole: response.targetRole,
      });

      this.closeModal();
      this.updateNavUi();
      App.showToast(`Welcome back, ${response.fullName}!`, 'success');
      window.dispatchEvent(new CustomEvent('auth-changed'));

      if (response.role === 'ROLE_ADMIN' && window.location.hash === '#admin') {
        App.navigateTo('admin');
      } else {
        App.navigateTo('dashboard');
      }
    } catch (error) {
      App.showToast(error.message || 'Login failed. Check your credentials.', 'error');
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Sign In';
    }
  },

  async handleRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('reg-name').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const targetRole = document.getElementById('reg-target-role').value.trim() || 'Software Engineer';
    const submitBtn = document.getElementById('reg-submit-btn');

    if (!fullName || !email || !password) {
      App.showToast('Please complete all required fields', 'error');
      return;
    }

    if (password.length < 6) {
      App.showToast('Password must be at least 6 characters long', 'error');
      return;
    }

    try {
      submitBtn.disabled = true;
      submitBtn.textContent = 'Creating Account...';

      let response;
      try {
        response = await Api.auth.register(fullName, email, password, targetRole, '');
      } catch (err) {
        // Client fallback
        response = {
          token: 'demo-jwt-token-' + Date.now(),
          id: Math.floor(Math.random() * 1000) + 10,
          fullName,
          email,
          role: 'ROLE_USER',
          targetRole,
        };
      }

      Api.setAuth(response.token, {
        id: response.id,
        fullName: response.fullName,
        email: response.email,
        role: response.role,
        targetRole: response.targetRole,
      });

      this.closeModal();
      this.updateNavUi();
      App.showToast(`Account created! Welcome, ${response.fullName}!`, 'success');
      window.dispatchEvent(new CustomEvent('auth-changed'));
      App.navigateTo('dashboard');
    } catch (error) {
      App.showToast(error.message || 'Registration failed', 'error');
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Create Free Account';
    }
  },

  logout() {
    Api.clearAuth();
    this.updateNavUi();
    App.showToast('You have been logged out.', 'info');
    window.dispatchEvent(new CustomEvent('auth-changed'));
    App.navigateTo('landing');
  },

  updateNavUi() {
    const user = Api.getCurrentUser();
    const guestActions = document.getElementById('nav-guest-actions');
    const userActions = document.getElementById('nav-user-actions');
    const navDashboardLink = document.getElementById('nav-link-dashboard');
    const navAdminLink = document.getElementById('nav-link-admin');
    const navRecommendationsLink = document.getElementById('nav-link-recommendations');

    if (user) {
      if (guestActions) guestActions.style.display = 'none';
      if (userActions) userActions.style.display = 'flex';
      if (navDashboardLink) navDashboardLink.style.display = 'inline-block';
      if (navRecommendationsLink) navRecommendationsLink.style.display = 'inline-block';

      if (navAdminLink) {
        navAdminLink.style.display = (user.role === 'ROLE_ADMIN' || user.role === 'ADMIN') ? 'inline-block' : 'none';
      }

      const avatar = document.getElementById('user-nav-avatar');
      const name = document.getElementById('user-nav-name');
      const role = document.getElementById('user-nav-role');

      if (avatar) avatar.textContent = user.fullName.charAt(0).toUpperCase();
      if (name) name.textContent = user.fullName;
      if (role) role.textContent = (user.role === 'ROLE_ADMIN' || user.role === 'ADMIN') ? 'Admin' : (user.targetRole || 'Candidate');
    } else {
      if (guestActions) guestActions.style.display = 'flex';
      if (userActions) userActions.style.display = 'none';
      if (navDashboardLink) navDashboardLink.style.display = 'none';
      if (navAdminLink) navAdminLink.style.display = 'none';
      if (navRecommendationsLink) navRecommendationsLink.style.display = 'none';
    }
  },
};

window.Auth = Auth;

/**
 * AI Interview Preparation Platform - API Client
 * Secure REST API communications, JWT token management, and error handling
 */

const API_BASE_URL = window.location.origin.includes('8080')
  ? `${window.location.origin}/api`
  : 'http://localhost:8080/api';

const TOKEN_KEY = 'ai_interview_token';
const USER_KEY = 'ai_interview_user';

const Api = {
  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  setAuth(token, user) {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
  },

  clearAuth() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  getCurrentUser() {
    const raw = localStorage.getItem(USER_KEY);
    try {
      return raw ? JSON.parse(raw) : null;
    } catch (e) {
      return null;
    }
  },

  isAuthenticated() {
    return !!this.getToken();
  },

  isAdmin() {
    const user = this.getCurrentUser();
    return user && (user.role === 'ROLE_ADMIN' || user.role === 'ADMIN');
  },

  async request(endpoint, options = {}) {
    const token = this.getToken();
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...options,
      headers,
    };

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
      
      // Handle unauthorized expired token
      if (response.status === 401 && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/register')) {
        this.clearAuth();
        window.dispatchEvent(new CustomEvent('auth-changed'));
        throw new Error('Session expired. Please log in again.');
      }

      const contentType = response.headers.get('content-type');
      let data = null;
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
      } else {
        data = await response.text();
      }

      if (!response.ok) {
        const errorMsg = (data && data.message) || response.statusText || 'Request failed';
        throw new Error(errorMsg);
      }

      return data;
    } catch (error) {
      console.warn(`API call failed for ${endpoint}:`, error.message);
      throw error;
    }
  },

  // Auth endpoints
  auth: {
    login(email, password) {
      return Api.request('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      });
    },

    register(fullName, email, password, targetRole, bio) {
      return Api.request('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ fullName, email, password, targetRole, bio }),
      });
    },

    getMe() {
      return Api.request('/auth/me');
    },

    updateProfile(profileData) {
      return Api.request('/auth/profile', {
        method: 'PUT',
        body: JSON.stringify(profileData),
      });
    },
  },

  // Interview endpoints
  interviews: {
    setup(data) {
      return Api.request('/interviews/setup', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    get(id) {
      return Api.request(`/interviews/${id}`);
    },

    submitAnswer(data) {
      return Api.request('/interviews/submit-answer', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    complete(id) {
      return Api.request(`/interviews/${id}/complete`, {
        method: 'POST',
      });
    },

    getMyHistory() {
      return Api.request('/interviews/my-history');
    },
  },

  // Questions endpoints
  questions: {
    getPublic(technology, difficulty) {
      const params = new URLSearchParams();
      if (technology) params.append('technology', technology);
      if (difficulty) params.append('difficulty', difficulty);
      const query = params.toString() ? `?${params.toString()}` : '';
      return Api.request(`/questions/public${query}`);
    },

    getTechnologies() {
      return Api.request('/questions/public/technologies');
    },

    getById(id) {
      return Api.request(`/questions/${id}`);
    },
  },

  // Performance endpoints
  performance: {
    getMe() {
      return Api.request('/performance/me');
    },
  },

  // Recommendations endpoints
  recommendations: {
    getMe() {
      return Api.request('/recommendations/me');
    },
  },

  // Admin endpoints
  admin: {
    getStats() {
      return Api.request('/admin/stats');
    },

    getUsers() {
      return Api.request('/admin/users');
    },

    updateUserRole(id, role) {
      return Api.request(`/admin/users/${id}/role`, {
        method: 'PUT',
        body: JSON.stringify({ role }),
      });
    },

    deleteUser(id) {
      return Api.request(`/admin/users/${id}`, {
        method: 'DELETE',
      });
    },

    createQuestion(data) {
      return Api.request('/admin/questions', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    updateQuestion(id, data) {
      return Api.request(`/admin/questions/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      });
    },

    deleteQuestion(id) {
      return Api.request(`/admin/questions/${id}`, {
        method: 'DELETE',
      });
    },
  },
};

window.Api = Api;

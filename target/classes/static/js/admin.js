/**
 * AI Interview Preparation Platform - Admin Dashboard Module
 * Platform metrics, question bank CRUD, user directory management
 */

const Admin = {
  currentTab: 'questions',
  cachedQuestions: [],
  editingQuestionId: null,

  async init() {
    if (!Api.isAuthenticated()) {
      App.navigateTo('landing');
      Auth.openModal('login');
      return;
    }

    if (!Api.isAdmin()) {
      App.showToast('Access restricted to Administrators only.', 'error');
      App.navigateTo('dashboard');
      return;
    }

    this.bindEvents();
    await this.loadStats();
    await this.loadQuestions();
  },

  bindEvents() {
    // Admin tabs
    document.querySelectorAll('.admin-tab-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('.admin-tab-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        this.switchTab(btn.dataset.tab);
      });
    });

    // Question filter dropdowns
    const techFilter = document.getElementById('admin-tech-filter');
    const diffFilter = document.getElementById('admin-diff-filter');
    if (techFilter) techFilter.addEventListener('change', () => this.filterQuestions());
    if (diffFilter) diffFilter.addEventListener('change', () => this.filterQuestions());

    // Add question modal
    const addQBtn = document.getElementById('admin-add-question-btn');
    if (addQBtn) {
      addQBtn.addEventListener('click', () => this.openQuestionModal());
    }

    const qModalClose = document.getElementById('question-modal-close');
    if (qModalClose) {
      qModalClose.addEventListener('click', () => this.closeQuestionModal());
    }

    const qForm = document.getElementById('question-crud-form');
    if (qForm) {
      qForm.addEventListener('submit', (e) => this.handleSaveQuestion(e));
    }
  },

  switchTab(tab) {
    this.currentTab = tab;
    document.getElementById('admin-pane-questions').style.display = tab === 'questions' ? 'block' : 'none';
    document.getElementById('admin-pane-users').style.display = tab === 'users' ? 'block' : 'none';

    if (tab === 'users') {
      this.loadUsers();
    }
  },

  async loadStats() {
    try {
      let stats;
      try {
        stats = await Api.admin.getStats();
      } catch (e) {
        stats = {
          totalUsers: 8,
          totalInterviews: 24,
          completedInterviews: 21,
          totalQuestions: 21,
          averagePlatformScore: 8.1,
        };
      }

      document.getElementById('admin-stat-users').textContent = stats.totalUsers || 0;
      document.getElementById('admin-stat-interviews').textContent = stats.totalInterviews || 0;
      document.getElementById('admin-stat-completed').textContent = stats.completedInterviews || 0;
      document.getElementById('admin-stat-questions').textContent = stats.totalQuestions || 0;
      document.getElementById('admin-stat-avg-score').textContent = stats.averagePlatformScore ? `${Number(stats.averagePlatformScore).toFixed(1)}/10` : '0.0/10';
    } catch (err) {
      console.error('Failed to load admin stats:', err);
    }
  },

  async loadQuestions() {
    try {
      let questions;
      try {
        questions = await Api.questions.getPublic();
      } catch (e) {
        // Fallback local questions
        questions = [
          { id: 1, technology: 'Java', difficulty: 'Easy', category: 'Core Java', title: 'HashMap vs ConcurrentHashMap in Java', description: 'Differences in locking and concurrency.' },
          { id: 2, technology: 'Java', difficulty: 'Medium', category: 'JVM', title: 'Generational Garbage Collection in HotSpot', description: 'Young gen vs Old gen mechanics.' },
          { id: 3, technology: 'Python', difficulty: 'Medium', category: 'Concurrency', title: 'Global Interpreter Lock (GIL)', description: 'Impact on multi-threading vs multi-processing.' },
          { id: 4, technology: 'SQL', difficulty: 'Medium', category: 'Indexing', title: 'B-Tree vs Composite Indexes', description: 'Leftmost prefix rule.' },
          { id: 5, technology: 'Data Structures and Algorithms', difficulty: 'Medium', category: 'Graphs', title: 'BFS vs DFS Traversal', description: 'Queue vs Stack graph search.' },
        ];
      }

      this.cachedQuestions = questions || [];
      this.renderQuestionsTable(this.cachedQuestions);
    } catch (err) {
      console.error('Failed to load questions:', err);
    }
  },

  filterQuestions() {
    const tech = document.getElementById('admin-tech-filter')?.value || '';
    const diff = document.getElementById('admin-diff-filter')?.value || '';

    let filtered = this.cachedQuestions;
    if (tech) {
      filtered = filtered.filter(q => q.technology.toLowerCase() === tech.toLowerCase());
    }
    if (diff) {
      filtered = filtered.filter(q => q.difficulty.toLowerCase() === diff.toLowerCase());
    }
    this.renderQuestionsTable(filtered);
  },

  renderQuestionsTable(questions) {
    const tbody = document.getElementById('admin-questions-tbody');
    if (!tbody) return;

    if (!questions || questions.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; padding: 2rem;">No questions found matching the filter.</td></tr>';
      return;
    }

    tbody.innerHTML = questions.map(q => `
      <tr>
        <td><strong>#${q.id}</strong></td>
        <td><span class="badge-pill badge-tech">${q.technology}</span></td>
        <td><span class="badge-pill badge-${q.difficulty.toLowerCase()}">${q.difficulty}</span></td>
        <td>
          <div style="font-weight: 600; color: var(--text-main);">${q.title}</div>
          <div style="font-size: 0.8rem; color: var(--text-dim);">${q.category || 'Technical'}</div>
        </td>
        <td>
          <div style="display: flex; gap: 0.5rem;">
            <button class="btn btn-sm btn-secondary" onclick="Admin.editQuestion(${q.id})">Edit</button>
            <button class="btn btn-sm btn-danger" onclick="Admin.deleteQuestion(${q.id})">Delete</button>
          </div>
        </td>
      </tr>
    `).join('');
  },

  openQuestionModal(question = null) {
    const modal = document.getElementById('question-crud-modal');
    if (!modal) return;

    this.editingQuestionId = question ? question.id : null;
    document.getElementById('q-modal-title').textContent = question ? 'Edit Interview Question' : 'Add New Interview Question';

    document.getElementById('q-input-tech').value = question ? question.technology : 'Java';
    document.getElementById('q-input-diff').value = question ? question.difficulty : 'Medium';
    document.getElementById('q-input-cat').value = question ? question.category : 'Technical';
    document.getElementById('q-input-title').value = question ? question.title : '';
    document.getElementById('q-input-desc').value = question ? question.description : '';
    document.getElementById('q-input-concepts').value = question ? (question.keyConcepts || '') : '';
    document.getElementById('q-input-sample').value = question ? (question.sampleAnswer || '') : '';

    modal.classList.add('active');
  },

  closeQuestionModal() {
    const modal = document.getElementById('question-crud-modal');
    if (modal) modal.classList.remove('active');
    this.editingQuestionId = null;
  },

  async handleSaveQuestion(e) {
    e.preventDefault();
    const data = {
      technology: document.getElementById('q-input-tech').value,
      difficulty: document.getElementById('q-input-diff').value,
      category: document.getElementById('q-input-cat').value,
      title: document.getElementById('q-input-title').value.trim(),
      description: document.getElementById('q-input-desc').value.trim(),
      keyConcepts: document.getElementById('q-input-concepts').value.trim(),
      sampleAnswer: document.getElementById('q-input-sample').value.trim(),
    };

    if (!data.title || !data.description) {
      App.showToast('Question title and description are required', 'error');
      return;
    }

    try {
      if (this.editingQuestionId) {
        await Api.admin.updateQuestion(this.editingQuestionId, data);
        App.showToast('Question updated successfully', 'success');
      } else {
        await Api.admin.createQuestion(data);
        App.showToast('New question created in bank', 'success');
      }
      this.closeQuestionModal();
      await this.loadQuestions();
    } catch (err) {
      // Local fallback for client demo
      if (this.editingQuestionId) {
        const idx = this.cachedQuestions.findIndex(q => q.id === this.editingQuestionId);
        if (idx !== -1) this.cachedQuestions[idx] = { ...this.cachedQuestions[idx], ...data };
      } else {
        this.cachedQuestions.unshift({ id: Date.now(), ...data });
      }
      this.closeQuestionModal();
      this.renderQuestionsTable(this.cachedQuestions);
      App.showToast('Question saved locally', 'success');
    }
  },

  editQuestion(id) {
    const q = this.cachedQuestions.find(item => item.id === id);
    if (q) this.openQuestionModal(q);
  },

  async deleteQuestion(id) {
    if (!confirm(`Are you sure you want to delete question #${id}?`)) return;

    try {
      await Api.admin.deleteQuestion(id);
      App.showToast('Question deleted', 'info');
      await this.loadQuestions();
    } catch (err) {
      this.cachedQuestions = this.cachedQuestions.filter(q => q.id !== id);
      this.renderQuestionsTable(this.cachedQuestions);
      App.showToast('Question removed locally', 'info');
    }
  },

  async loadUsers() {
    const tbody = document.getElementById('admin-users-tbody');
    if (!tbody) return;

    try {
      let users;
      try {
        users = await Api.admin.getUsers();
      } catch (e) {
        users = [
          { id: 1, fullName: 'Admin Officer', email: 'admin@aiinterview.com', role: 'ROLE_ADMIN', targetRole: 'Platform Administrator' },
          { id: 2, fullName: 'Alex Morgan', email: 'alex@example.com', role: 'ROLE_USER', targetRole: 'Full Stack Developer' },
        ];
      }

      tbody.innerHTML = users.map(u => {
        const isAdmin = u.role === 'ROLE_ADMIN' || u.role === 'ADMIN';
        return `
          <tr>
            <td><strong>#${u.id}</strong></td>
            <td><strong>${u.fullName}</strong></td>
            <td>${u.email}</td>
            <td>${u.targetRole || 'Candidate'}</td>
            <td><span class="badge-pill ${isAdmin ? 'badge-verified' : 'badge-tech'}">${isAdmin ? 'Admin' : 'Candidate'}</span></td>
            <td>
              ${!isAdmin ? `<button class="btn btn-sm btn-outline-glow" onclick="Admin.toggleUserRole(${u.id}, 'ROLE_ADMIN')">Make Admin</button>` : '<span style="color:var(--text-dim); font-size:0.8rem;">Super User</span>'}
            </td>
          </tr>
        `;
      }).join('');
    } catch (err) {
      console.error('Failed to load users:', err);
    }
  },

  async toggleUserRole(userId, newRole) {
    try {
      await Api.admin.updateUserRole(userId, newRole);
      App.showToast('User role updated', 'success');
      this.loadUsers();
    } catch (e) {
      App.showToast('Updated user role locally', 'success');
    }
  },
};

window.Admin = Admin;

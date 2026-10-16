/**
 * AI Interview Preparation Platform - Skills & Recommendations Module
 */

const Recommendations = {
  async init() {
    if (!Api.isAuthenticated()) {
      App.navigateTo('landing');
      Auth.openModal('login');
      return;
    }
    await this.loadRecommendations();
  },

  async loadRecommendations() {
    const container = document.getElementById('recommendations-container');
    if (!container) return;

    try {
      let list;
      try {
        list = await Api.recommendations.getMe();
      } catch (e) {
        list = [
          {
            id: 1,
            weakArea: 'Java Concurrency & Memory Model',
            recommendedTopic: 'Atomic Variables, CAS, and Thread Pools',
            suggestedDifficulty: 'Medium',
            isVerifiedResource: true,
            resourceUrl: 'https://docs.oracle.com/javase/tutorial/essential/concurrency/atomicvars.html',
            reason: 'Identified minor ambiguity between volatile visibility and atomic operations during recent mock session.',
          },
          {
            id: 2,
            weakArea: 'SQL Transactions & Locking',
            recommendedTopic: 'MVCC and Transaction Isolation Levels',
            suggestedDifficulty: 'Hard',
            isVerifiedResource: true,
            resourceUrl: 'https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html',
            reason: 'Strengthen explanation of Phantom Reads and InnoDB Next-Key locking mechanisms for backend engineering roles.',
          },
          {
            id: 3,
            weakArea: 'Data Structures & Algorithms',
            recommendedTopic: 'Dynamic Programming - 1D vs 2D State Space',
            suggestedDifficulty: 'Hard',
            isVerifiedResource: false,
            resourceUrl: null,
            reason: 'AI personalized recommendation: practice space-optimized knapsack patterns.',
          },
        ];
      }

      this.renderList(list);
    } catch (err) {
      console.error('Failed to load recommendations:', err);
      App.showToast('Could not load personalized recommendations', 'error');
    }
  },

  renderList(list) {
    const container = document.getElementById('recommendations-container');
    if (!container) return;

    if (!list || list.length === 0) {
      container.innerHTML = `
        <div class="glass-card" style="grid-column: 1 / -1; text-align: center; padding: 3rem;">
          <h3>No Weak Areas Flagged Yet</h3>
          <p style="margin-top: 0.5rem;">Complete mock interviews to allow the AI to detect skills to strengthen!</p>
          <button class="btn btn-primary" style="margin-top: 1.5rem;" onclick="App.navigateTo('interview-setup')">Start First Interview</button>
        </div>
      `;
      return;
    }

    container.innerHTML = list.map(item => {
      const isVerified = item.isVerifiedResource;
      const badgeHtml = isVerified
        ? '<span class="badge-pill badge-verified">✓ Verified Resource</span>'
        : '<span class="badge-pill badge-ai">✦ AI Generated</span>';

      const diffClass = `badge-${(item.suggestedDifficulty || 'medium').toLowerCase()}`;

      return `
        <div class="glass-card rec-card">
          <div>
            <div class="rec-header">
              <span class="badge-pill badge-tech">${item.weakArea}</span>
              ${badgeHtml}
            </div>
            <h3 class="rec-topic">${item.recommendedTopic}</h3>
            <p class="rec-reason">${item.reason}</p>
          </div>
          <div style="display: flex; align-items: center; justify-content: space-between; border-top: 1px solid var(--border-subtle); padding-top: 1rem; margin-top: 1rem;">
            <span class="badge-pill ${diffClass}">Difficulty: ${item.suggestedDifficulty || 'Medium'}</span>
            <div style="display: flex; gap: 0.5rem;">
              ${item.resourceUrl ? `<a href="${item.resourceUrl}" target="_blank" rel="noopener noreferrer" class="btn btn-sm btn-secondary">Documentation ↗</a>` : ''}
              <button class="btn btn-sm btn-outline-glow" onclick="Recommendations.launchTargetedPractice('${item.weakArea}', '${item.suggestedDifficulty}')">Practice ⚡</button>
            </div>
          </div>
        </div>
      `;
    }).join('');
  },

  launchTargetedPractice(weakArea, difficulty) {
    let tech = 'Java';
    const lower = weakArea.toLowerCase();
    if (lower.includes('data science')) tech = 'Data Science';
    else if (lower.includes('ai') || lower.includes('artificial intelligence') || lower.includes('llm')) tech = 'AI Developer';
    else if (lower.includes('machine learning') || lower.includes('ml')) tech = 'Machine Learning';
    else if (lower.includes('devops') || lower.includes('kubernetes') || lower.includes('cloud')) tech = 'DevOps';
    else if (lower.includes('sql')) tech = 'SQL';
    else if (lower.includes('python')) tech = 'Python';
    else if (lower.includes('algorithm') || lower.includes('dsa')) tech = 'Data Structures and Algorithms';
    else if (lower.includes('web')) tech = 'Web Development';
    else if (lower.includes('c++')) tech = 'C++';
    else if (lower.includes('javascript')) tech = 'JavaScript';

    Interview.setupConfig.technology = tech;
    Interview.setupConfig.difficulty = difficulty || 'Medium';
    App.navigateTo('interview-setup');
    App.showToast(`Prepared practice session for ${tech}`, 'info');
  },
};

window.Recommendations = Recommendations;

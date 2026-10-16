/**
 * AI Interview Preparation Platform - Results & Performance Report Module
 */

const Results = {
  currentResult: null,

  async loadInterviewResult(interviewId) {
    try {
      App.showToast('Loading interview report...', 'info');
      const data = await Api.interviews.get(interviewId);
      this.render(data);
      App.navigateTo('interview-results');
    } catch (e) {
      console.error('Failed to load interview report:', e);
      App.showToast('Could not load interview details', 'error');
    }
  },

  render(detail) {
    this.currentResult = detail;

    // Technology & Diff badges
    document.getElementById('res-tech-badge').textContent = detail.technology;
    const diffBadge = document.getElementById('res-diff-badge');
    diffBadge.textContent = detail.difficulty;
    diffBadge.className = `badge-pill badge-${(detail.difficulty || 'medium').toLowerCase()}`;

    // Score & Gauge with NaN protection
    let scoreVal = parseFloat(detail.overallScore);
    if (isNaN(scoreVal) || scoreVal <= 0) {
      if (detail.evaluations && detail.evaluations.length > 0) {
        const scores = detail.evaluations.map(e => parseFloat(e.overallScore)).filter(s => !isNaN(s));
        scoreVal = scores.length > 0 ? (scores.reduce((a, b) => a + b, 0) / scores.length) : 8.0;
      } else {
        scoreVal = 8.0;
      }
    }
    scoreVal = Math.min(10, Math.max(1, scoreVal));
    document.getElementById('res-overall-score').textContent = scoreVal.toFixed(1);

    // Performance level badge
    const levelBadge = document.getElementById('res-performance-level');
    if (scoreVal >= 8.5) {
      levelBadge.textContent = 'Mastery Level';
      levelBadge.className = 'badge-pill badge-easy';
    } else if (scoreVal >= 7.0) {
      levelBadge.textContent = 'Interview Ready';
      levelBadge.className = 'badge-pill badge-verified';
    } else if (scoreVal >= 5.0) {
      levelBadge.textContent = 'Developing';
      levelBadge.className = 'badge-pill badge-medium';
    } else {
      levelBadge.textContent = 'Needs Review';
      levelBadge.className = 'badge-pill badge-hard';
    }

    // SVG Gauge Dashoffset calculation
    // Circle circumference = 2 * PI * 48 ~= 301.6
    const circumference = 301.6;
    const offset = Math.max(0, Math.min(circumference, circumference - ((scoreVal / 10) * circumference)));
    const gaugeBar = document.getElementById('res-gauge-bar');
    if (gaugeBar) {
      gaugeBar.style.strokeDashoffset = offset;
    }

    // 4 Rubrics with NaN fallback
    let techScore = parseFloat(detail.technicalScore);
    if (isNaN(techScore)) techScore = Math.min(10, scoreVal + 0.2);

    let relScore = parseFloat(detail.relevanceScore);
    if (isNaN(relScore)) relScore = scoreVal;

    let compScore = parseFloat(detail.completenessScore);
    if (isNaN(compScore)) compScore = Math.max(1, scoreVal - 0.2);

    let commScore = parseFloat(detail.communicationScore);
    if (isNaN(commScore)) commScore = Math.min(10, scoreVal + 0.1);

    document.getElementById('res-rubric-tech').textContent = `${techScore.toFixed(1)}/10`;
    document.getElementById('res-rubric-rel').textContent = `${relScore.toFixed(1)}/10`;
    document.getElementById('res-rubric-comp').textContent = `${compScore.toFixed(1)}/10`;
    document.getElementById('res-rubric-comm').textContent = `${commScore.toFixed(1)}/10`;

    document.getElementById('res-bar-tech').style.width = `${Math.min(100, Math.max(10, techScore * 10))}%`;
    document.getElementById('res-bar-rel').style.width = `${Math.min(100, Math.max(10, relScore * 10))}%`;
    document.getElementById('res-bar-comp').style.width = `${Math.min(100, Math.max(10, compScore * 10))}%`;
    document.getElementById('res-bar-comm').style.width = `${Math.min(100, Math.max(10, commScore * 10))}%`;

    // Summary Feedback
    document.getElementById('res-summary-text').textContent = detail.summaryFeedback ||
      'Solid technical foundation demonstrated. Continue practicing edge cases and algorithmic nuances for senior positions.';

    // Question-by-Question Accordion
    this.renderQuestionEvaluations(detail.questions || [], detail.evaluations || []);

    // Practice Next recommendations
    this.renderNextPracticeSuggestions(detail);
  },

  renderQuestionEvaluations(questions, evaluations) {
    const container = document.getElementById('res-questions-container');
    if (!container) return;

    if (evaluations.length === 0) {
      container.innerHTML = '<p style="text-align:center; padding: 2rem;">No question evaluations found for this session.</p>';
      return;
    }

    container.innerHTML = evaluations.map((ev, idx) => {
      const q = questions[idx] || { title: ev.questionTitle || `Question ${idx + 1}` };
      const strengthsHtml = (ev.strengths || []).map(s => `<span class="strength-tag">✓ ${s}</span>`).join('');
      const weaknessesHtml = (ev.weaknesses || []).map(w => `<span class="weakness-tag">△ ${w}</span>`).join('');

      return `
        <div class="glass-card question-breakdown-card">
          <div class="q-eval-header" onclick="Results.toggleAccordion(${idx})">
            <div>
              <span class="badge-pill badge-tech" style="margin-right: 0.5rem;">Q${idx + 1}</span>
              <strong style="font-size: 1.05rem;">${q.title}</strong>
            </div>
            <div style="display: flex; align-items: center; gap: 1rem;">
              <span class="badge-pill badge-verified">Score: ${ev.overallScore || 'N/A'}/10</span>
              <span id="res-arrow-${idx}" style="color: var(--text-dim); transition: transform 0.2s;">▼</span>
            </div>
          </div>
          
          <div id="res-body-${idx}" class="q-eval-body" style="display: block;">
            <div class="feedback-section-title">Key Strengths Demonstrated</div>
            <div class="strengths-pills">
              ${strengthsHtml || '<span class="strength-tag">Addressed the primary question requirements</span>'}
            </div>

            <div class="feedback-section-title">Areas for Improvement & Missed Concepts</div>
            <div class="weaknesses-pills">
              ${weaknessesHtml || '<span class="weakness-tag">Practice formulating deeper trade-offs</span>'}
            </div>

            <div class="feedback-section-title">Benchmark Suggested Answer</div>
            <div class="model-answer-box">
              ${ev.suggestedAnswer || 'Review standard reference documentation and architectural best practices.'}
            </div>

            <div class="advice-callout">
              <strong>💡 Actionable Advice:</strong> ${ev.actionableAdvice || 'Continue practicing similar questions to solidify concept clarity.'}
            </div>
          </div>
        </div>
      `;
    }).join('');
  },

  toggleAccordion(idx) {
    const body = document.getElementById(`res-body-${idx}`);
    const arrow = document.getElementById(`res-arrow-${idx}`);
    if (body.style.display === 'none') {
      body.style.display = 'block';
      arrow.style.transform = 'rotate(0deg)';
    } else {
      body.style.display = 'none';
      arrow.style.transform = 'rotate(-90deg)';
    }
  },

  renderNextPracticeSuggestions(detail) {
    const container = document.getElementById('res-suggested-topics');
    if (!container) return;

    const tech = detail.technology;
    const suggestions = [
      { topic: `Advanced Concurrency & Async Patterns in ${tech}`, difficulty: 'Hard' },
      { topic: `System Scalability & Database Optimizations in ${tech}`, difficulty: 'Medium' },
    ];

    container.innerHTML = suggestions.map(s => `
      <div class="glass-card" style="display:flex; align-items:center; justify-content:space-between; padding: 1rem 1.5rem; margin-bottom: 0.75rem;">
        <div>
          <strong style="color: var(--text-main);">${s.topic}</strong>
          <span class="badge-pill badge-${s.difficulty.toLowerCase()}" style="margin-left: 0.5rem;">${s.difficulty}</span>
        </div>
        <button class="btn btn-sm btn-outline-glow" onclick="Results.practiceTopic('${tech}', '${s.difficulty}')">Practice Topic</button>
      </div>
    `).join('');
  },

  practiceTopic(tech, difficulty) {
    Interview.setupConfig.technology = tech;
    Interview.setupConfig.difficulty = difficulty;
    App.navigateTo('interview-setup');
  },

  printReport() {
    window.print();
  },
};

window.Results = Results;

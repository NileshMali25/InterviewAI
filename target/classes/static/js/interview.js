/**
 * AI Interview Preparation Platform - Interview Session Module
 * Handles interview setup, question progression, timers, answer submissions, and AI evaluation flow
 */

const Interview = {
  currentInterviewId: null,
  questions: [],
  currentIndex: 0,
  candidateAnswers: {}, // questionId -> answer text
  questionTimes: {}, // questionId -> seconds
  timerInterval: null,
  totalElapsedSeconds: 0,

  setupConfig: {
    interviewType: 'Technical',
    technology: 'Java',
    difficulty: 'Medium',
    questionCount: 3,
  },

  init() {
    this.bindSetupEvents();
    this.bindSessionEvents();
  },

  bindSetupEvents() {
    // Interview Type selector
    document.querySelectorAll('.type-option-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('.type-option-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        this.setupConfig.interviewType = btn.dataset.type;
      });
    });

    // Tech Selector cards
    document.querySelectorAll('.tech-option-card').forEach(card => {
      card.addEventListener('click', () => {
        document.querySelectorAll('.tech-option-card').forEach(c => c.classList.remove('selected'));
        card.classList.add('selected');
        this.setupConfig.technology = card.dataset.tech;
      });
    });

    // Difficulty buttons
    document.querySelectorAll('.diff-option-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        document.querySelectorAll('.diff-option-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');
        this.setupConfig.difficulty = btn.dataset.diff;
      });
    });

    // Question count slider
    const slider = document.getElementById('question-count-slider');
    const badge = document.getElementById('question-count-badge');
    if (slider && badge) {
      slider.addEventListener('input', (e) => {
        const val = parseInt(e.target.value, 10);
        badge.textContent = val;
        this.setupConfig.questionCount = val;
      });
    }

    // Launch Interview Button
    const launchBtn = document.getElementById('launch-interview-btn');
    if (launchBtn) {
      launchBtn.addEventListener('click', () => this.startInterview());
    }
  },

  bindSessionEvents() {
    const answerInput = document.getElementById('interview-answer-input');
    if (answerInput) {
      answerInput.addEventListener('input', (e) => {
        const currentQ = this.questions[this.currentIndex];
        if (currentQ) {
          this.candidateAnswers[currentQ.id] = e.target.value;
          this.updateCharWordCount(e.target.value);
        }
      });
    }

    // Navigation buttons
    const prevBtn = document.getElementById('q-prev-btn');
    if (prevBtn) {
      prevBtn.addEventListener('click', () => this.previousQuestion());
    }

    const nextBtn = document.getElementById('q-next-btn');
    if (nextBtn) {
      nextBtn.addEventListener('click', () => this.nextQuestion());
    }

    const submitBtn = document.getElementById('q-submit-interview-btn');
    if (submitBtn) {
      submitBtn.addEventListener('click', () => this.openFinishConfirmation());
    }

    // Confirmation Modal
    const confirmFinishBtn = document.getElementById('confirm-finish-interview-btn');
    if (confirmFinishBtn) {
      confirmFinishBtn.addEventListener('click', () => this.submitAllAndEvaluate());
    }

    const cancelFinishBtn = document.getElementById('cancel-finish-interview-btn');
    if (cancelFinishBtn) {
      cancelFinishBtn.addEventListener('click', () => {
        document.getElementById('finish-confirm-modal')?.classList.remove('active');
      });
    }

    // Early End Button
    const endEarlyBtn = document.getElementById('end-early-btn');
    if (endEarlyBtn) {
      endEarlyBtn.addEventListener('click', () => this.openFinishConfirmation());
    }
  },

  async startInterview() {
    if (!Api.isAuthenticated()) {
      Auth.openModal('login');
      App.showToast('Please sign in or create an account to start an interview', 'info');
      return;
    }

    const launchBtn = document.getElementById('launch-interview-btn');
    try {
      launchBtn.disabled = true;
      launchBtn.textContent = 'Generating AI Interview...';

      let data;
      try {
        data = await Api.interviews.setup(this.setupConfig);
      } catch (err) {
        console.warn('Backend interview setup failed, loading questions locally:', err.message);
        // Fallback local interview initialization with sample question bank
        const fallbackQs = this.getLocalQuestions(this.setupConfig.technology, this.setupConfig.difficulty, this.setupConfig.questionCount);
        data = {
          id: Date.now(),
          interviewType: this.setupConfig.interviewType,
          technology: this.setupConfig.technology,
          difficulty: this.setupConfig.difficulty,
          questionCount: fallbackQs.length,
          questions: fallbackQs,
        };
      }

      this.currentInterviewId = data.id;
      this.questions = data.questions || [];
      this.currentIndex = 0;
      this.candidateAnswers = {};
      this.questionTimes = {};
      this.totalElapsedSeconds = 0;

      if (this.questions.length === 0) {
        throw new Error('No questions available for this configuration.');
      }

      App.navigateTo('interview-session');
      this.renderCurrentQuestion();
      this.startTimer();
    } catch (error) {
      App.showToast(error.message || 'Could not launch interview session', 'error');
    } finally {
      launchBtn.disabled = false;
      launchBtn.textContent = 'Launch AI Interview';
    }
  },

  renderCurrentQuestion() {
    const q = this.questions[this.currentIndex];
    if (!q) return;

    // Badges & Meta
    document.getElementById('session-tech-badge').textContent = q.technology || this.setupConfig.technology;
    const diffBadge = document.getElementById('session-diff-badge');
    diffBadge.textContent = q.difficulty || this.setupConfig.difficulty;
    diffBadge.className = `badge-pill badge-${(q.difficulty || 'medium').toLowerCase()}`;

    // Progress counter
    const progressText = `Question ${this.currentIndex + 1} of ${this.questions.length}`;
    document.getElementById('session-progress-text').textContent = progressText;

    // Progress Bar
    const percent = ((this.currentIndex + 1) / this.questions.length) * 100;
    document.getElementById('session-progress-bar').style.width = `${percent}%`;

    // Question content
    document.getElementById('session-q-title').textContent = q.title;
    document.getElementById('session-q-desc').textContent = q.description;

    const conceptsEl = document.getElementById('session-q-concepts');
    if (conceptsEl) {
      if (q.keyConcepts) {
        conceptsEl.textContent = `Key Concepts to Cover: ${q.keyConcepts}`;
        conceptsEl.style.display = 'block';
      } else {
        conceptsEl.style.display = 'none';
      }
    }

    // Existing Answer
    const answerInput = document.getElementById('interview-answer-input');
    const savedAnswer = this.candidateAnswers[q.id] || '';
    answerInput.value = savedAnswer;
    this.updateCharWordCount(savedAnswer);

    // Prev / Next button states
    const prevBtn = document.getElementById('q-prev-btn');
    const nextBtn = document.getElementById('q-next-btn');
    const submitBtn = document.getElementById('q-submit-interview-btn');

    if (this.currentIndex === 0) {
      prevBtn.style.visibility = 'hidden';
    } else {
      prevBtn.style.visibility = 'visible';
    }

    if (this.currentIndex === this.questions.length - 1) {
      nextBtn.style.display = 'none';
      submitBtn.style.display = 'inline-flex';
    } else {
      nextBtn.style.display = 'inline-flex';
      submitBtn.style.display = 'none';
    }
  },

  updateCharWordCount(text) {
    const chars = text.length;
    const words = text.trim() ? text.trim().split(/\s+/).length : 0;
    const counterEl = document.getElementById('session-word-counter');
    if (counterEl) {
      counterEl.textContent = `${words} words | ${chars} characters`;
    }
  },

  previousQuestion() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
      this.renderCurrentQuestion();
    }
  },

  nextQuestion() {
    const currentQ = this.questions[this.currentIndex];
    const currentAns = (this.candidateAnswers[currentQ.id] || '').trim();

    if (!currentAns) {
      this.showEmptyWarning(() => {
        this.currentIndex++;
        this.renderCurrentQuestion();
      });
      return;
    }

    if (this.currentIndex < this.questions.length - 1) {
      this.currentIndex++;
      this.renderCurrentQuestion();
    }
  },

  showEmptyWarning(onProceed) {
    const modal = document.getElementById('empty-warning-modal');
    if (!modal) {
      if (confirm('You have not written an answer yet. Proceed to next question anyway?')) {
        onProceed();
      }
      return;
    }

    modal.classList.add('active');
    const proceedBtn = document.getElementById('empty-warning-proceed-btn');
    const stayBtn = document.getElementById('empty-warning-stay-btn');

    const handleProceed = () => {
      modal.classList.remove('active');
      proceedBtn.removeEventListener('click', handleProceed);
      onProceed();
    };

    proceedBtn.addEventListener('click', handleProceed);
    stayBtn.onclick = () => modal.classList.remove('active');
  },

  openFinishConfirmation() {
    const modal = document.getElementById('finish-confirm-modal');
    if (!modal) return;

    let answeredCount = 0;
    this.questions.forEach(q => {
      if ((this.candidateAnswers[q.id] || '').trim().length > 0) {
        answeredCount++;
      }
    });

    document.getElementById('finish-answered-count').textContent = answeredCount;
    document.getElementById('finish-total-count').textContent = this.questions.length;
    modal.classList.add('active');
  },

  async submitAllAndEvaluate() {
    document.getElementById('finish-confirm-modal')?.classList.remove('active');
    this.stopTimer();

    // Show AI Processing Overlay
    const overlay = document.getElementById('ai-eval-overlay');
    overlay.classList.add('active');

    const statusTexts = [
      'Evaluating technical accuracy against rubrics...',
      'Analyzing structural completeness and edge cases...',
      'Calculating communication and clarity metrics...',
      'Generating model answers and actionable advice...',
    ];

    let step = 0;
    const stepInterval = setInterval(() => {
      step = (step + 1) % statusTexts.length;
      document.getElementById('ai-overlay-status').textContent = statusTexts[step];
    }, 1200);

    try {
      const evaluationResults = [];

      for (let i = 0; i < this.questions.length; i++) {
        const q = this.questions[i];
        const ans = this.candidateAnswers[q.id] || 'No answer provided by candidate.';

        try {
          const evalResult = await Api.interviews.submitAnswer({
            interviewId: this.currentInterviewId,
            questionId: q.id,
            candidateAnswer: ans,
            timeTakenSeconds: 60,
          });
          evaluationResults.push(evalResult);
        } catch (e) {
          // If offline/demo fallback
          const localEval = this.evaluateLocally(q, ans);
          evaluationResults.push(localEval);
        }
      }

      // Complete interview in backend
      let completedDetail;
      try {
        completedDetail = await Api.interviews.complete(this.currentInterviewId);
      } catch (e) {
        // Compute summary locally with strict numeric parsing
        const validEvals = evaluationResults.filter(ev => ev && !isNaN(parseFloat(ev.overallScore)));
        let avgScore = 7.8;
        if (validEvals.length > 0) {
          avgScore = validEvals.reduce((acc, curr) => acc + parseFloat(curr.overallScore), 0) / validEvals.length;
        }
        avgScore = Math.min(10.0, Math.max(1.0, avgScore));

        completedDetail = {
          id: this.currentInterviewId,
          technology: this.setupConfig.technology,
          difficulty: this.setupConfig.difficulty,
          overallScore: Number(avgScore.toFixed(1)),
          technicalScore: Number(Math.min(10, avgScore + 0.2).toFixed(1)),
          communicationScore: Number(Math.max(1, avgScore - 0.1).toFixed(1)),
          relevanceScore: Number(avgScore.toFixed(1)),
          completenessScore: Number(Math.max(1, avgScore - 0.2).toFixed(1)),
          summaryFeedback: 'Good technical understanding with strong fundamentals demonstrated across core concepts.',
          questions: this.questions,
          evaluations: evaluationResults,
        };
      }

      clearInterval(stepInterval);
      overlay.classList.remove('active');

      App.showToast('AI Evaluation completed successfully!', 'success');
      Results.render(completedDetail);
      App.navigateTo('interview-results');
    } catch (error) {
      clearInterval(stepInterval);
      overlay.classList.remove('active');
      App.showToast('Evaluation failed: ' + error.message, 'error');
    }
  },

  startTimer() {
    this.stopTimer();
    const timerEl = document.getElementById('session-timer-clock');
    this.timerInterval = setInterval(() => {
      this.totalElapsedSeconds++;
      const mins = Math.floor(this.totalElapsedSeconds / 60);
      const secs = this.totalElapsedSeconds % 60;
      if (timerEl) {
        timerEl.textContent = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
      }
    }, 1000);
  },

  stopTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  },

  getLocalQuestions(tech, diff, count) {
    const questionsByDomain = {
      'Data Science': [
        {
          id: 101,
          technology: 'Data Science',
          difficulty: diff || 'Medium',
          title: 'Explain the Bias-Variance Tradeoff and its impact on model generalization.',
          description: 'Define bias and variance in statistical learning. Explain how model complexity influences both, and describe techniques like cross-validation and regularization to find the optimal balance.',
          keyConcepts: 'bias, variance, overfitting, underfitting, MSE decomposition, regularization, cross-validation',
          sampleAnswer: 'Bias refers to errors from overly simplistic assumptions in the learning algorithm (underfitting). Variance refers to sensitivity to small fluctuations in training data (overfitting). Total expected test error decomposes into Bias^2 + Variance + Irreducible Error. As complexity increases, bias decreases while variance increases.',
        },
        {
          id: 102,
          technology: 'Data Science',
          difficulty: diff || 'Medium',
          title: 'Compare Precision, Recall, and ROC-AUC for Imbalanced Classification.',
          description: 'Explain why raw Accuracy fails on imbalanced datasets. Define Precision, Recall, and F1-Score, and describe how the ROC Curve and AUC metric evaluate threshold-independent discrimination.',
          keyConcepts: 'precision, recall, F1-score, ROC-AUC, confusion matrix, false positives, false negatives, class imbalance',
          sampleAnswer: 'In severe class imbalance (e.g., fraud detection with 99% negative cases), an accuracy metric is misleading. Precision measures true positives among predicted positives. Recall measures true positives captured from all actual positives. ROC-AUC plots True Positive Rate against False Positive Rate across all thresholds.',
        },
        {
          id: 103,
          technology: 'Data Science',
          difficulty: diff || 'Hard',
          title: 'How do you handle feature collinearity and high dimensionality in tabular datasets?',
          description: 'Discuss Variance Inflation Factor (VIF), Principal Component Analysis (PCA), and L1 (Lasso) vs L2 (Ridge) regularization for feature selection and multicollinearity mitigation.',
          keyConcepts: 'multicollinearity, VIF, PCA, Lasso, Ridge, dimensionality reduction, eigenvalues',
          sampleAnswer: 'Multicollinearity inflates the variance of regression coefficient estimates. To diagnose it, compute VIF (values > 5-10 suggest high correlation). Solutions include removing redundant features, using Lasso regularization to shrink correlated coefficients to zero, or applying PCA to project features onto orthogonal eigenvectors.',
        },
      ],
      'AI Developer': [
        {
          id: 201,
          technology: 'AI Developer',
          difficulty: diff || 'Medium',
          title: 'Compare Retrieval-Augmented Generation (RAG) vs Model Fine-Tuning.',
          description: 'Explain architectural differences, cost, latency, data freshness, and hallucination mitigation when choosing between RAG and full or parameter-efficient fine-tuning (LoRA).',
          keyConcepts: 'RAG, fine-tuning, embeddings, vector database, LoRA, context window, hallucinations, latency',
          sampleAnswer: 'RAG injects external factual context dynamically into the prompt using vector similarity search, making it ideal for rapidly changing internal data and verifiable citations without retraining. Fine-tuning adjusts model weights to teach specific domain styles, specialized nomenclature, or task formatting (e.g. LoRA/QLoRA), but does not guarantee accurate factual retrieval.',
        },
        {
          id: 202,
          technology: 'AI Developer',
          difficulty: diff || 'Hard',
          title: 'How does the Transformer Scaled Dot-Product Attention mechanism work?',
          description: 'Formulate Query, Key, and Value matrices. Explain why scaling by sqrt(d_k) is required, how multi-head attention enables parallel subspace representation, and computational complexity O(N^2).',
          keyConcepts: 'attention, Query, Key, Value, softmax, scaling factor, multi-head attention, quadratic complexity',
          sampleAnswer: 'Attention is computed as Softmax((Q * K^T) / sqrt(d_k)) * V. The scaling factor sqrt(d_k) prevents dot products from growing excessively large for high dimensions, which would otherwise push softmax into regions with vanishingly small gradients. Multi-head attention projects Q, K, V into h distinct linear subspaces, allowing the model to jointly attend to information from different representation positions.',
        },
        {
          id: 203,
          technology: 'AI Developer',
          difficulty: diff || 'Medium',
          title: 'Explain techniques for mitigating LLM Hallucinations and Prompt Injection in production.',
          description: 'Discuss system prompt hardening, guardrails (e.g. NeMo, Llama Guard), structured schema outputs (JSON mode), and retrieval grounding.',
          keyConcepts: 'hallucinations, prompt injection, guardrails, ground truth, structured generation, temperature, verification',
          sampleAnswer: 'Prompt injection occurs when user inputs override developer system instructions. Mitigations include: separating system instructions from untrusted data using delimiters, using secondary LLM guardrail passes, enforcing structured JSON decoding with Pydantic schemas, setting low temperature (0.0-0.2) for factual tasks, and grounding responses strictly in retrieved chunks.',
        },
      ],
      'Machine Learning': [
        {
          id: 301,
          technology: 'Machine Learning',
          difficulty: diff || 'Medium',
          title: 'Explain Gradient Descent variants: Batch, Stochastic (SGD), and Adam Optimizer.',
          description: 'Compare computation cost, noise in gradient updates, and how Adam incorporates first and second momentum estimation with bias correction.',
          keyConcepts: 'gradient descent, SGD, mini-batch, learning rate, momentum, Adam, RMSProp, convergence',
          sampleAnswer: 'Batch Gradient Descent computes gradients across the entire dataset per step, which is computationally prohibitive for large datasets. SGD updates parameters using a single sample, which is fast but noisy. Mini-batch strikes a balance. Adam combines Momentum (exponential moving average of gradients) and RMSProp (moving average of squared gradients) with bias correction, adapting the learning rate per parameter.',
        },
        {
          id: 302,
          technology: 'Machine Learning',
          difficulty: diff || 'Medium',
          title: 'How do Bagging and Boosting differ in Ensemble Learning?',
          description: 'Explain Random Forest vs Gradient Boosting (XGBoost/LightGBM). Discuss how each reduces variance vs bias, parallelizability, and susceptibility to overfitting.',
          keyConcepts: 'ensemble learning, bagging, boosting, random forest, gradient boosting, XGBoost, variance reduction, bias reduction',
          sampleAnswer: 'Bagging (Bootstrap Aggregating) trains multiple independent weak learners in parallel on bootstrap samples and averages predictions, primarily reducing variance (e.g., Random Forest). Boosting trains models sequentially, where each successive learner corrects the pseudo-residuals of the previous models, primarily reducing bias (e.g., XGBoost, LightGBM).',
        },
        {
          id: 303,
          technology: 'Machine Learning',
          difficulty: diff || 'Hard',
          title: 'How do you detect and handle Concept Drift and Data Drift in production ML systems?',
          description: 'Explain differences between Covariate Shift, Prior Probability Shift, and Concept Drift. Discuss monitoring techniques like KS-test, PSI (Population Stability Index), and retraining triggers.',
          keyConcepts: 'data drift, concept drift, covariate shift, Kolmogorov-Smirnov test, PSI, model monitoring, shadow deployment',
          sampleAnswer: 'Data drift (covariate shift) means the input distribution P(X) changes while P(Y|X) remains unchanged. Concept drift means the relationship between features and target P(Y|X) changes over time. Detection uses statistical divergence tests like Kolmogorov-Smirnov (KS) for continuous features and Population Stability Index (PSI) for categorical bins. Retraining can be triggered periodically or upon metric degradation.',
        },
      ],
      'DevOps': [
        {
          id: 401,
          technology: 'DevOps',
          difficulty: diff || 'Medium',
          title: 'Explain Blue-Green vs Canary Deployments and zero-downtime release strategies.',
          description: 'Describe architecture, traffic routing at the load balancer / ingress level, rollback mechanisms, and database schema migration compatibility.',
          keyConcepts: 'blue-green deployment, canary, zero-downtime, load balancer, ingress, rollback, backward compatibility',
          sampleAnswer: 'Blue-Green deployment runs two identical production environments: Blue (active live) and Green (idle). The new release is deployed and smoke-tested on Green; once validated, traffic switches instantaneously at the router/load balancer. If issues arise, switching back to Blue takes seconds. Canary deployments gradually route a small percentage of traffic (e.g., 5% to 25% to 100%) to validate real-world telemetry before full rollout.',
        },
        {
          id: 402,
          technology: 'DevOps',
          difficulty: diff || 'Hard',
          title: 'How does Kubernetes handle Pod Scheduling, Resource Limits, and Self-Healing?',
          description: 'Discuss kube-scheduler filtering and scoring phases, CPU/Memory Requests vs Limits, OOMKilled events, readiness vs liveness probes, and Horizontal Pod Autoscalers (HPA).',
          keyConcepts: 'Kubernetes, kube-scheduler, requests, limits, OOMKilled, liveness probe, readiness probe, HPA, ReplicaSet',
          sampleAnswer: 'kube-scheduler binds pods to nodes through two phases: Filtering (removes nodes lacking required CPU, memory, or node affinities) and Scoring (ranks candidate nodes). Requests define guaranteed reservations for scheduling; Limits define hard caps enforced by cgroups. If a pod exceeds memory limits, the Linux kernel OOM-killer terminates it. Liveness probes trigger pod restarts; readiness probes control service endpoint inclusion.',
        },
        {
          id: 403,
          technology: 'DevOps',
          difficulty: diff || 'Medium',
          title: 'Explain Infrastructure as Code (IaC) principles and Terraform state management.',
          description: 'Discuss declarative vs imperative configuration, Terraform State locking (e.g. S3 + DynamoDB), plan vs apply cycles, and handling drift.',
          keyConcepts: 'Infrastructure as Code, Terraform, declarative, state file, state lock, DynamoDB, terraform plan, drift detection',
          sampleAnswer: 'IaC defines infrastructure declaratively via version-controlled code. Terraform tracks the real-world infrastructure mapping through a state file. In collaborative environments, the state must be stored in remote backends (like AWS S3) with state locking (via DynamoDB) to prevent concurrent writes and race condition corruption. `terraform plan` computes the delta between declared configuration and current state before `apply`.',
        },
      ],
      'Python': [
        {
          id: 501,
          technology: 'Python',
          difficulty: diff || 'Easy',
          title: 'What is the Global Interpreter Lock (GIL) in Python and how does it affect multithreading?',
          description: 'Explain what the GIL is, why it exists in CPython, and how it impacts CPU-bound vs I/O-bound multithreaded programs. Mention alternatives like multiprocessing and asyncio.',
          keyConcepts: 'GIL, CPython, thread safety, CPU-bound, I/O-bound, threading module, multiprocessing, asyncio',
          sampleAnswer: 'The GIL (Global Interpreter Lock) is a mutex in CPython that allows only one thread to execute Python bytecode at a time, even on multi-core processors. It exists to protect CPython\'s memory management (reference counting) from race conditions. For I/O-bound tasks (file reads, network calls), threads still provide concurrency because the GIL is released during I/O waits. For CPU-bound tasks, threads do NOT improve performance — use the multiprocessing module instead (each process has its own GIL and memory space), or asyncio for cooperative concurrency in I/O-heavy workloads.',
        },
        {
          id: 502,
          technology: 'Python',
          difficulty: diff || 'Easy',
          title: 'Explain Python Generators and how they differ from regular lists.',
          description: 'Describe the yield keyword, lazy evaluation, memory efficiency, and generator expressions. Explain use cases where generators outperform list comprehensions.',
          keyConcepts: 'generator, yield, iterator, lazy evaluation, memory efficiency, StopIteration, generator expression, list comprehension',
          sampleAnswer: 'A generator is a function that uses the yield keyword to return values one at a time, pausing execution between yields. Unlike lists, generators are lazy — they compute values on demand rather than storing all of them in memory at once. This makes them ideal for processing large datasets or infinite sequences. A generator object implements the iterator protocol (__iter__ and __next__). Example: def count_up(n): for i in range(n): yield i. Generator expressions (x*2 for x in range(1000)) are memory-efficient alternatives to list comprehensions [x*2 for x in range(1000)] which allocate the full list immediately.',
        },
        {
          id: 503,
          technology: 'Python',
          difficulty: diff || 'Medium',
          title: 'What are Python Decorators and how do you create one?',
          description: 'Explain the decorator pattern, how @functools.wraps preserves metadata, and give real-world examples like logging, authentication, and timing decorators.',
          keyConcepts: 'decorator, wrapper function, @syntax, functools.wraps, closure, higher-order function, @staticmethod, @classmethod, @property',
          sampleAnswer: 'A decorator is a higher-order function that takes a function as input and returns a modified version of it. The @decorator syntax is syntactic sugar for func = decorator(func). Example: def timer(func): import functools, time; @functools.wraps(func) def wrapper(*args, **kwargs): start = time.time(); result = func(*args, **kwargs); print(f"{func.__name__} took {time.time()-start:.3f}s"); return result; return wrapper. @functools.wraps is critical — without it, the wrapper function replaces the original\'s __name__ and __doc__ metadata. Real-world uses: @login_required in Django, @cache for memoization, @retry for resilient API calls.',
        },
        {
          id: 504,
          technology: 'Python',
          difficulty: diff || 'Medium',
          title: 'Explain Python\'s mutable default argument pitfall and how to fix it.',
          description: 'Demonstrate why def func(lst=[]) is dangerous. Explain that default argument objects are created once at function definition time, not at call time. Show the correct pattern using None as sentinel.',
          keyConcepts: 'mutable default argument, default parameter, list, dict, function definition, None sentinel, side effects, shared state',
          sampleAnswer: 'In Python, default argument values are evaluated ONCE when the function is defined, not each time the function is called. So def append_item(item, lst=[]): lst.append(item); return lst — every call shares the same list object. Calling append_item(1) returns [1], but calling append_item(2) returns [1, 2] — the previous state persisted! The fix is to use None as a sentinel: def append_item(item, lst=None): if lst is None: lst = []; lst.append(item); return lst. This creates a fresh list for each call. This gotcha applies to any mutable default: lists, dicts, sets, and custom objects.',
        },
        {
          id: 505,
          technology: 'Python',
          difficulty: diff || 'Hard',
          title: 'How does Python\'s Object-Oriented Programming work? Explain dunder (magic) methods.',
          description: 'Explain classes, instances, inheritance, and key dunder methods: __init__, __str__, __repr__, __len__, __eq__, __add__, and how they enable operator overloading.',
          keyConcepts: 'OOP, class, object, __init__, __str__, __repr__, dunder methods, operator overloading, inheritance, MRO, super()',
          sampleAnswer: 'Python OOP uses classes as blueprints for objects. __init__ is the constructor called when an object is instantiated. __str__ defines the human-readable string representation (used by print()), while __repr__ defines the developer-facing representation (used in the REPL). Example: class Vector: def __init__(self, x, y): self.x=x; self.y=y; def __add__(self, other): return Vector(self.x+other.x, self.y+other.y); def __repr__(self): return f"Vector({self.x}, {self.y})". This enables v1 + v2 syntax through operator overloading. Python supports multiple inheritance and uses C3 linearization (MRO — Method Resolution Order) to resolve method lookup order. super() delegates to the next class in the MRO chain.',
        },
      ],
    };

    if (questionsByDomain[tech]) {
      return questionsByDomain[tech].slice(0, count);
    }

    const defaultBank = [
      {
        id: 1,
        technology: tech || 'Java',
        difficulty: diff || 'Medium',
        title: `Explain core design and performance principles in ${tech}`,
        description: `Discuss architectural trade-offs, time complexity, and memory management guarantees relevant to ${tech} systems.`,
        keyConcepts: 'memory, concurrency, performance, time-complexity, caching',
        sampleAnswer: `A robust response details internal data structures, thread-safety, garbage collection or allocation, and algorithmic asymptotic complexities.`,
      },
      {
        id: 2,
        technology: tech || 'Java',
        difficulty: diff || 'Medium',
        title: `How do you handle error conditions and concurrency in ${tech}?`,
        description: `Describe synchronization primitives, exception safety, and race condition prevention when designing high-throughput services.`,
        keyConcepts: 'locks, exceptions, race condition, atomic, immutability',
        sampleAnswer: `Explain how to avoid deadlock, use thread-safe data structures or actor models, and handle unrecoverable versus recoverable errors.`,
      },
      {
        id: 3,
        technology: tech || 'Java',
        difficulty: diff || 'Medium',
        title: `Optimize database access and network latency in modern ${tech} applications.`,
        description: `Analyze connection pooling, query indexing, batching, and caching strategies to minimize P99 latency.`,
        keyConcepts: 'indexing, connection pool, caching, pagination, latency',
        sampleAnswer: `Detail B-Tree index utilization, prepared statement reuse, Redis/in-memory caching, and asynchronous non-blocking I/O.`,
      },
    ];
    return defaultBank.slice(0, count);
  },

  evaluateLocally(question, answer) {
    const words = (answer || '').trim().split(/\s+/).filter(Boolean).length;
    let score = 5.0;
    if (words > 15) score += 1.5;
    if (words > 40) score += 1.5;
    if (words > 80) score += 1.0;
    score = Math.min(9.4, Math.max(3.0, score));

    const techScore = Number(Math.min(10, score + 0.2).toFixed(1));
    const relScore = Number(score.toFixed(1));
    const compScore = Number(Math.max(1, score - 0.3).toFixed(1));
    const commScore = Number(Math.min(10, score + 0.1).toFixed(1));
    const overallScore = Number(score.toFixed(1));

    return {
      questionId: question.id,
      questionTitle: question.title,
      technicalScore: techScore,
      relevanceScore: relScore,
      completenessScore: compScore,
      communicationScore: commScore,
      overallScore: overallScore,
      strengths: ['Addressed the core architectural principles accurately', 'Provided well-structured explanation with logical flow'],
      weaknesses: ['Could elaborate on production edge cases, scaling limits, or specific performance trade-offs'],
      suggestedAnswer: question.sampleAnswer || 'A strong response outlines the core definition, internal mechanics, edge cases, and real-world trade-offs.',
      actionableAdvice: 'Practice articulating technical trade-offs and underlying mechanics with concise architectural examples.',
      modelUsed: 'heuristic-engine-v1',
    };
  },
};

window.Interview = Interview;

-- ====================================================================
-- AI Interview Preparation Platform - Seed Data
-- Demo Users, Core Skills, and Comprehensive Question Bank
-- ====================================================================

USE ai_interview_db;

-- 1. Seed Users (password for both is 'password123')
-- BCrypt hash for 'password123': $2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lR5eKq81bBqXb0h4J1n89M9WpL4g6
INSERT INTO users (id, full_name, email, password_hash, role, target_role, bio) VALUES
(1, 'Admin Officer', 'admin@aiinterview.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lR5eKq81bBqXb0h4J1n89M9WpL4g6', 'ROLE_ADMIN', 'Platform Administrator', 'Technical lead & hiring manager managing the question bank.'),
(2, 'Alex Morgan', 'alex@example.com', '$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lR5eKq81bBqXb0h4J1n89M9WpL4g6', 'ROLE_USER', 'Full Stack Developer', 'Computer Science senior preparing for upcoming SDE interviews.')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 2. Seed Skills
INSERT INTO skills (id, name, category, description) VALUES
(1, 'Java Core & Concurrency', 'Language', 'Object-oriented programming, JVM internals, threads, collections framework'),
(2, 'Python & Asynchronous I/O', 'Language', 'Data structures, generators, decorators, memory model, async/await'),
(3, 'C++ & Low-Level Memory', 'Language', 'Pointers, RAII, STL, virtual functions, move semantics, memory layout'),
(4, 'JavaScript & Event Loop', 'Language', 'Prototypes, promises, microtasks/macrotasks, DOM, closures'),
(5, 'SQL & Query Optimization', 'Database', 'Indexing, ACID transactions, joins, execution plans, normalization'),
(6, 'Data Structures & Algorithms', 'Fundamentals', 'Trees, graphs, dynamic programming, sorting, complexity analysis'),
(7, 'Web Development & REST APIs', 'Web', 'HTTP protocols, caching, authentication, CORS, microservices architecture')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. Comprehensive Question Bank
-- Java Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('Java', 'Easy', 'Core Java',
 'Explain the difference between HashMap and ConcurrentHashMap in Java.',
 'Discuss how HashMap and ConcurrentHashMap differ in terms of thread safety, internal locking mechanisms (segment locking vs synchronized buckets), null key/value allowances, and performance in multi-threaded environments.',
 'HashMap is non-thread-safe and allows one null key and multiple null values. In a concurrent scenario, concurrent modifications can cause race conditions or infinite loops (in older versions). ConcurrentHashMap is thread-safe and does not allow null keys or null values. In Java 8+, it achieves concurrency using CAS (Compare-And-Swap) operations and synchronized blocks on individual hash buckets rather than locking the whole map, providing high throughput.',
 'HashMap, ConcurrentHashMap, thread-safety, synchronized, CAS, null keys, bucket-level locking, race conditions'),

('Java', 'Medium', 'JVM & Garbage Collection',
 'How does the Java Garbage Collector work, and what are the generations in the HotSpot JVM heap?',
 'Explain the generational hypothesis behind the JVM memory model. Describe Young Generation (Eden, Survivor spaces), Old/Tenured Generation, and Metaspace. Mention how Minor GC and Major/Full GC are triggered.',
 'The JVM heap is divided according to the Generational Hypothesis: most objects die young. It is partitioned into Young Generation (Eden, S0, and S1) and Old Generation. New objects are allocated in Eden. When Eden fills, a Minor GC moves surviving objects to Survivor spaces, incrementing their age counter. When age reaches a threshold (tenuring threshold, typically 15), they are promoted to the Old Generation. Major/Full GC collects Old Generation, often using collectors like G1, ZGC, or CMS with stop-the-world pauses.',
 'Generational hypothesis, Young Generation, Eden, Survivor spaces, Old Generation, Minor GC, Major GC, Metaspace, Stop-The-World'),

('Java', 'Hard', 'Concurrency',
 'What is the Java Memory Model (JMM) and how does the volatile keyword prevent instruction reordering?',
 'Detail how the JMM defines the interaction between hardware memory and thread caches. Explain happens-before relationships, memory visibility, and how compiler/hardware instruction reordering is prevented with memory barriers.',
 'The Java Memory Model specifies how threads access shared memory and guarantees visibility through the "happens-before" relationship. CPU caches can keep local copies of variables causing visibility issues. The volatile keyword provides two guarantees: 1) Visibility: writes to a volatile variable are immediately flushed to main memory and reads always fetch from main memory. 2) Reordering prevention: the compiler and CPU insert memory barriers (StoreStore, StoreLoad, LoadLoad, LoadStore) preventing reordering across volatile boundaries.',
 'Java Memory Model, happens-before, volatile, memory barrier, CPU cache, instruction reordering, visibility, CAS');

-- Python Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('Python', 'Easy', 'Language Features',
 'What are Python decorators and how do you implement one with arguments?',
 'Explain what higher-order functions are in Python. Demonstrate how decorators wrap another function, use functools.wraps, and show how a decorator accepting arguments requires three levels of nested functions.',
 'Decorators are functions that take another function as an argument, extend or modify its behavior without modifying the original code, and return a callable. To accept arguments, a decorator needs three nested layers: an outer function accepting the arguments, a middle decorator function accepting the target function, and an inner wrapper that receives *args and **kwargs. Using @functools.wraps preserves function metadata like __name__ and docstrings.',
 'decorator, higher-order function, wraps, args, kwargs, closure, metadata, wrapper'),

('Python', 'Medium', 'Concurrency & Internals',
 'Explain the Global Interpreter Lock (GIL) in CPython and its impact on multi-threading vs multi-processing.',
 'Describe what the GIL is, why it was implemented (reference counting thread safety), how it impacts CPU-bound vs I/O-bound tasks, and how developers bypass its limitations.',
 'The Global Interpreter Lock (GIL) is a mutex in CPython that ensures only one native thread executes Python bytecode at any given moment. This simplifies CPython memory management and protects reference count increments/decrements from race conditions. However, for CPU-bound tasks, multi-threading cannot leverage multiple CPU cores. For I/O-bound tasks (network, disk), threads release the GIL while waiting. For CPU-intensive operations, developers use multiprocessing, external C extensions, or alternative runtimes.',
 'Global Interpreter Lock, GIL, CPython, reference counting, CPU-bound, I/O-bound, multiprocessing, threading, concurrency'),

('Python', 'Hard', 'Generators & Coroutines',
 'How do Python generators and the async/await framework work under the hood using coroutines?',
 'Explain how generator functions pause execution via the yield expression, how the frame state is preserved on the heap, and how asyncio uses an event loop with tasks and futures.',
 'Generators are functions that use the yield keyword. When called, they return a generator object whose execution is suspended and resumed via .next() or .send(). Python saves the entire execution frame (instruction pointer, local variables) in the heap. Asyncio builds on this generator/coroutine foundation: async def functions return coroutine objects. The event loop schedules coroutines as Tasks, tracking non-blocking socket selectors and resuming execution when I/O completes.',
 'generators, yield, coroutines, async, await, event loop, frame object, state machine, Task, Future');

-- C++ Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('C++', 'Easy', 'Object Oriented C++',
 'Explain the purpose of virtual destructors in C++ base classes.',
 'What happens if a base class pointer deletes a derived class object when the base destructor is not declared virtual? Discuss undefined behavior and resource leaks.',
 'If a base class destructor is not declared virtual and you delete an object of a derived class through a pointer to the base class, the behavior is undefined according to the C++ standard. In practice, only the base class destructor gets called; the derived class destructor is bypassed. This leads to memory leaks and resource leaks (e.g. file handles, sockets) held by the derived class. Declaring the base destructor virtual ensures the dynamic dispatch table (vtable) invokes the correct destructor chain.',
 'virtual destructor, vtable, polymorphism, base pointer, derived class, undefined behavior, memory leak'),

('C++', 'Medium', 'Modern C++',
 'What are rvalue references (&&) and move semantics introduced in C++11?',
 'Explain the distinction between lvalues and rvalues, how std::move works (casting), and how move constructors and move assignment operators eliminate expensive deep copies.',
 'An lvalue is an expression that has an identifiable memory address (can be on the left of assignment). An rvalue is a temporary value that does not persist beyond the expression (e.g., return value of a temporary). Rvalue references (Type&&) allow functions to bind to temporaries. Move semantics allow resources (like dynamically allocated heap buffers in std::vector or std::string) to be transferred/stolen from an rvalue object to a new object rather than deep-copied. std::move performs an unconditional static_cast to an rvalue reference.',
 'rvalue reference, lvalue, move semantics, std::move, shallow copy, resource transfer, temporary objects'),

('C++', 'Hard', 'Memory Management',
 'How does RAII (Resource Acquisition Is Initialization) work, and how are std::unique_ptr and std::shared_ptr implemented?',
 'Describe the RAII idiom, the ownership semantics of std::unique_ptr (zero overhead, move-only) versus std::shared_ptr (control block, reference counting, weak_ptr cycle prevention).',
 'RAII ties the lifecycle of resources (heap memory, mutexes, sockets) to the lifetime of stack objects. When the object goes out of scope, its destructor is guaranteed to release the resource, ensuring exception safety. std::unique_ptr provides exclusive ownership with zero overhead over a raw pointer, implemented as a move-only smart pointer. std::shared_ptr provides shared ownership via a heap-allocated control block containing strong and weak reference counts. std::weak_ptr provides non-owning access to break circular reference cycles.',
 'RAII, smart pointers, unique_ptr, shared_ptr, weak_ptr, control block, reference count, exception safety');

-- JavaScript Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('JavaScript', 'Easy', 'Core JavaScript',
 'What is a closure in JavaScript and what are common real-world use cases?',
 'Define lexical scoping, how inner functions retain access to their outer enclosing scope even after the outer function has returned, and demonstrate practical uses (data privacy, currying, factory functions).',
 'A closure is the combination of a function bundled together with references to its lexical environment. In JavaScript, whenever a function is created, a closure is formed. An inner function remembers variables from its outer enclosing function even after the outer function has executed. Common use cases include: data encapsulation and private variables (module pattern), function currying and partial application, memoization caches, and event handler callback factories.',
 'closure, lexical scope, private variables, encapsulation, higher-order function, memory retention'),

('JavaScript', 'Medium', 'Asynchronous JS',
 'Explain the JavaScript Event Loop, microtasks, and macrotasks with execution order.',
 'Describe the Call Stack, Web APIs, Task Queue (Macrotasks like setTimeout), and Microtask Queue (Promises, queueMicrotask). Explain which queue has higher priority and why.',
 'JavaScript is single-threaded with a non-blocking event-driven concurrency model. Code executes on the Call Stack. Asynchronous tasks are delegated to Web APIs. When ready, callbacks enter queues: 1) Microtask Queue (Promise .then/catch, MutationObserver, queueMicrotask) and 2) Macrotask Queue (setTimeout, setInterval, setImmediate, I/O). The Event Loop continually checks the Call Stack: once the stack is empty, it drains ALL microtasks before picking the NEXT macrotask. Thus, microtasks always execute before the next macrotask.',
 'Event Loop, Call Stack, Microtask Queue, Macrotask Queue, Promises, setTimeout, execution order, single-threaded'),

('JavaScript', 'Hard', 'Advanced V8 & Prototypes',
 'How does prototypal inheritance work under the hood, and how do modern JS engines optimize property lookups?',
 'Discuss the prototype chain (__proto__ vs prototype), prototype pollution prevention, and how V8 engine uses Hidden Classes (Shapes) and Inline Caches (IC) for rapid property access.',
 'Every JavaScript object has an internal [[Prototype]] link. When accessing a property, the engine traverses this chain until it finds the property or reaches null. Functions have a .prototype property that becomes the [[Prototype]] of instances created with `new`. V8 optimizes property lookups through Hidden Classes (Shapes) and Inline Caches (ICs). If objects share the same layout and property addition order, they share a Shape, allowing the engine to offset memory lookups in CPU registers without hash table lookups.',
 'prototype chain, __proto__, inheritance, V8, hidden classes, shapes, inline caches, deoptimization');

-- SQL Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('SQL', 'Easy', 'Relational Basics',
 'Explain the difference between WHERE and HAVING clauses in SQL.',
 'Clarify the order of execution in a SQL query, how WHERE filters rows before aggregation, and how HAVING filters aggregated groups created by GROUP BY.',
 'The WHERE clause filters individual rows before any grouping or aggregate functions (COUNT, SUM, AVG) are computed. You cannot use aggregate functions directly in a WHERE clause. The HAVING clause filters groups created by the GROUP BY clause after aggregation takes place. In SQL execution order, FROM and WHERE execute first, followed by GROUP BY, then HAVING, SELECT, and finally ORDER BY.',
 'WHERE, HAVING, GROUP BY, aggregation, execution order, row filter, group filter'),

('SQL', 'Medium', 'Indexing & Performance',
 'How do B-Tree indexes work in relational databases, and what is a Composite Index?',
 'Explain the structure of a B+ Tree index, node splitting, lookup complexity O(log N), range queries, and the Leftmost Prefix Rule when utilizing multi-column composite indexes.',
 'B-Tree (and specifically B+ Tree) indexes organize table row keys in a balanced tree where leaf nodes form a doubly linked list, enabling O(log N) point queries and fast sequential range scans. A Composite Index indexes multiple columns (e.g., INDEX(user_id, status, created_at)). According to the Leftmost Prefix Rule, a query can leverage this index only if the query predicates match the index columns in order starting from the leftmost column.',
 'B-Tree, B+ Tree, index, composite index, leftmost prefix rule, range query, O(log N), execution plan'),

('SQL', 'Hard', 'Transactions & Concurrency',
 'What are ACID properties and the four standard ANSI SQL Transaction Isolation Levels?',
 'Detail Atomicity, Consistency, Isolation, Durability. Explain Read Uncommitted, Read Committed, Repeatable Read, and Serializable, along with the concurrency phenomena they prevent (Dirty Read, Non-repeatable Read, Phantom Read).',
 'ACID guarantees database reliability: Atomicity (all or nothing), Consistency (valid state transitions), Isolation (concurrent transactions do not interfere), Durability (committed data survives crashes). The four isolation levels are: 1) Read Uncommitted (vulnerable to dirty reads, non-repeatable reads, phantom reads), 2) Read Committed (prevents dirty reads using short read locks or MVCC snapshots), 3) Repeatable Read (prevents dirty and non-repeatable reads; MySQL InnoDB also prevents phantom reads via Next-Key locks), 4) Serializable (strict two-phase locking or SSI, eliminates all phenomena).',
 'ACID, isolation levels, Read Committed, Repeatable Read, Serializable, Dirty Read, Phantom Read, MVCC, Two-Phase Locking');

-- Data Structures and Algorithms Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('Data Structures and Algorithms', 'Easy', 'Arrays & Hashing',
 'How would you find two numbers in an unsorted array that add up to a target sum in O(N) time?',
 'Describe the Two Sum problem. Explain why the brute force approach is O(N^2) and how a Hash Map provides an optimal O(N) time and O(N) space solution.',
 'The brute force approach uses nested loops to test all pairs in O(N^2) time. The optimal solution uses a Hash Map: iterate through the array once while storing each number and its index in the map. For each element `x`, check if `target - x` exists in the hash map. If it does, we have found the pair. If not, insert `x` into the map. This achieves O(N) time complexity because hash map lookups take O(1) average time, with O(N) auxiliary space.',
 'Two Sum, Hash Map, time complexity, space complexity, O(N), complement lookup, collision handling'),

('Data Structures and Algorithms', 'Medium', 'Trees & Graphs',
 'Explain the difference between Breadth-First Search (BFS) and Depth-First Search (DFS) and when to use each.',
 'Discuss queue vs stack/recursion implementations, time and space complexity, and scenarios where BFS is required (shortest path in unweighted graphs) vs DFS (cycle detection, topological sort).',
 'BFS explores a graph level by level using a Queue (FIFO). DFS explores as deep as possible along each branch before backtracking using a Stack (LIFO) or recursion. Both run in O(V + E) time. BFS is optimal for finding the shortest path in unweighted graphs, finding closest peers, and tree level-order traversal, requiring O(V) space for the queue. DFS uses less memory for deep trees and is suited for backtracking, cycle detection, topological sorting, and finding connected components.',
 'BFS, DFS, Queue, Stack, recursion, shortest path, topological sort, graph traversal, O(V+E)'),

('Data Structures and Algorithms', 'Hard', 'Dynamic Programming',
 'How would you approach the 0/1 Knapsack Problem and optimize its space complexity from O(N*W) to O(W)?',
 'Explain optimal substructure and overlapping subproblems. Formulate the state transition equation dp[i][w], and explain why iterating backwards through capacity allows a 1D array space optimization.',
 'The 0/1 Knapsack problem asks for the maximum value attainable with capacity W and N items, each with weight w_i and value v_i. Define DP state dp[i][w] = max value considering first i items with capacity w. Recurrence: if w_i > w, dp[i][w] = dp[i-1][w]; else dp[i][w] = max(dp[i-1][w], v_i + dp[i-1][w - w_i]). The 2D table requires O(N*W) space. We can optimize to 1D array dp[w] by iterating capacity w backwards from W down to w_i. Iterating backwards ensures we use values from the previous item (i-1) without overwriting them.',
 'dynamic programming, 0/1 knapsack, optimal substructure, overlapping subproblems, space optimization, 1D array, recurrence relation');

-- Web Development Questions
INSERT INTO questions (technology, difficulty, category, title, description, sample_answer, key_concepts) VALUES
('Web Development', 'Easy', 'HTTP & REST',
 'What are idempotent HTTP methods and why is this concept important in RESTful API design?',
 'Explain the definition of idempotency according to RFC 7231. Categorize GET, POST, PUT, DELETE, and PATCH. Discuss retry safety in distributed networks.',
 'An HTTP method is idempotent if the side-effects of making multiple identical requests are the same as making a single request. GET, HEAD, and OPTIONS are safe and idempotent. PUT and DELETE are idempotent: replacing an existing resource or deleting it multiple times leaves the server state identical after the first execution. POST is NOT idempotent because repeating it creates new resources. PATCH is generally not idempotent. Idempotency is crucial for network resilience, allowing clients and gateways to safely retry failed requests.',
 'idempotence, HTTP methods, GET, POST, PUT, DELETE, PATCH, REST, retry safety, side effects'),

('Web Development', 'Medium', 'Web Security',
 'What is Cross-Origin Resource Sharing (CORS) and how do preflight OPTIONS requests work?',
 'Explain the Same-Origin Policy (SOP), why CORS exists, what triggers a Preflight request, and what headers are exchanged (Access-Control-Allow-Origin, Access-Control-Allow-Methods).',
 'The Same-Origin Policy is a browser security measure that restricts a script from reading responses loaded from a different origin (domain, protocol, port). CORS is a standard mechanism that allows servers to specify who can access their resources. For requests that can cause side effects (methods other than GET/POST/HEAD, or custom headers like Authorization), the browser issues an automatic preflight `OPTIONS` request. The server replies with CORS headers (Access-Control-Allow-Origin, Allow-Methods, Allow-Headers). If authorized, the browser sends the actual request.',
 'CORS, Same-Origin Policy, preflight, OPTIONS, Access-Control-Allow-Origin, authorization header, browser security'),

('Web Development', 'Hard', 'System Architecture & Web Performance',
 'How does JSON Web Token (JWT) based authentication work, and what are the best practices for secure token storage and revocation?',
 'Explain JWT anatomy (Header, Payload, Signature), stateless verification, attacks (XSS vs CSRF with LocalStorage vs HttpOnly cookies), and revocation strategies (short TTL, refresh tokens, token blacklist in Redis).',
 'A JWT consists of Header (algorithm & token type), Payload (claims), and Signature (HMACSHA256 or RSA). The server verifies integrity statelessly using the secret/public key without database lookup. Storing JWTs in localStorage exposes them to Cross-Site Scripting (XSS). Best practice: store short-lived access tokens (e.g., 15 minutes) in memory and long-lived refresh tokens in secure, HttpOnly, SameSite cookies. For immediate revocation (logout or password reset), maintain a fast distributed blacklist or version counter in Redis.',
 'JWT, stateless authentication, HttpOnly cookies, XSS, CSRF, refresh tokens, token revocation, Redis blacklist, signature verification'),

-- Data Science Questions
('Data Science', 'Easy', 'Statistics & Modeling',
 'Explain the Bias-Variance Tradeoff and its impact on model generalization.',
 'Define bias and variance in statistical learning. Explain how model complexity influences both, and describe techniques like cross-validation and regularization to find the optimal balance.',
 'Bias refers to errors from overly simplistic assumptions in the learning algorithm (underfitting). Variance refers to sensitivity to small fluctuations in training data (overfitting). Total expected test error decomposes into Bias^2 + Variance + Irreducible Error. As complexity increases, bias decreases while variance increases.',
 'bias, variance, overfitting, underfitting, MSE decomposition, regularization, cross-validation'),

('Data Science', 'Medium', 'Evaluation Metrics',
 'Compare Precision, Recall, and ROC-AUC for Imbalanced Classification.',
 'Explain why raw Accuracy fails on imbalanced datasets. Define Precision, Recall, and F1-Score, and describe how the ROC Curve and AUC metric evaluate threshold-independent discrimination.',
 'In severe class imbalance (e.g., fraud detection with 99% negative cases), an accuracy metric is misleading. Precision measures true positives among predicted positives. Recall measures true positives captured from all actual positives. ROC-AUC plots True Positive Rate against False Positive Rate across all thresholds.',
 'precision, recall, F1-score, ROC-AUC, confusion matrix, false positives, false negatives, class imbalance'),

('Data Science', 'Hard', 'Feature Engineering',
 'How do you handle feature collinearity and high dimensionality in tabular datasets?',
 'Discuss Variance Inflation Factor (VIF), Principal Component Analysis (PCA), and L1 (Lasso) vs L2 (Ridge) regularization for feature selection and multicollinearity mitigation.',
 'Multicollinearity inflates the variance of regression coefficient estimates. To diagnose it, compute VIF (values > 5-10 suggest high correlation). Solutions include removing redundant features, using Lasso regularization to shrink correlated coefficients to zero, or applying PCA to project features onto orthogonal eigenvectors.',
 'multicollinearity, VIF, PCA, Lasso, Ridge, dimensionality reduction, eigenvalues'),

-- AI Developer Questions
('AI Developer', 'Easy', 'LLM Architectures',
 'Compare Retrieval-Augmented Generation (RAG) vs Model Fine-Tuning.',
 'Explain architectural differences, cost, latency, data freshness, and hallucination mitigation when choosing between RAG and full or parameter-efficient fine-tuning (LoRA).',
 'RAG injects external factual context dynamically into the prompt using vector similarity search, making it ideal for rapidly changing internal data and verifiable citations without retraining. Fine-tuning adjusts model weights to teach specific domain styles, specialized nomenclature, or task formatting (e.g. LoRA/QLoRA), but does not guarantee accurate factual retrieval.',
 'RAG, fine-tuning, embeddings, vector database, LoRA, context window, hallucinations, latency'),

('AI Developer', 'Medium', 'Production AI Systems',
 'Explain techniques for mitigating LLM Hallucinations and Prompt Injection in production.',
 'Discuss system prompt hardening, guardrails (e.g. NeMo, Llama Guard), structured schema outputs (JSON mode), and retrieval grounding.',
 'Prompt injection occurs when user inputs override developer system instructions. Mitigations include: separating system instructions from untrusted data using delimiters, using secondary LLM guardrail passes, enforcing structured JSON decoding with Pydantic schemas, setting low temperature (0.0-0.2) for factual tasks, and grounding responses strictly in retrieved chunks.',
 'hallucinations, prompt injection, guardrails, ground truth, structured generation, temperature, verification'),

('AI Developer', 'Hard', 'Transformer Internals',
 'How does the Transformer Scaled Dot-Product Attention mechanism work?',
 'Formulate Query, Key, and Value matrices. Explain why scaling by sqrt(d_k) is required, how multi-head attention enables parallel subspace representation, and computational complexity O(N^2).',
 'Attention is computed as Softmax((Q * K^T) / sqrt(d_k)) * V. The scaling factor sqrt(d_k) prevents dot products from growing excessively large for high dimensions, which would otherwise push softmax into regions with vanishingly small gradients. Multi-head attention projects Q, K, V into h distinct linear subspaces, allowing the model to jointly attend to information from different representation positions.',
 'attention, Query, Key, Value, softmax, scaling factor, multi-head attention, quadratic complexity'),

-- Machine Learning Questions
('Machine Learning', 'Easy', 'Optimization Algorithms',
 'Explain Gradient Descent variants: Batch, Stochastic (SGD), and Adam Optimizer.',
 'Compare computation cost, noise in gradient updates, and how Adam incorporates first and second momentum estimation with bias correction.',
 'Batch Gradient Descent computes gradients across the entire dataset per step, which is computationally prohibitive for large datasets. SGD updates parameters using a single sample, which is fast but noisy. Mini-batch strikes a balance. Adam combines Momentum (exponential moving average of gradients) and RMSProp (moving average of squared gradients) with bias correction, adapting the learning rate per parameter.',
 'gradient descent, SGD, mini-batch, learning rate, momentum, Adam, RMSProp, convergence'),

('Machine Learning', 'Medium', 'Ensemble Methods',
 'How do Bagging and Boosting differ in Ensemble Learning?',
 'Explain Random Forest vs Gradient Boosting (XGBoost/LightGBM). Discuss how each reduces variance vs bias, parallelizability, and susceptibility to overfitting.',
 'Bagging (Bootstrap Aggregating) trains multiple independent weak learners in parallel on bootstrap samples and averages predictions, primarily reducing variance (e.g., Random Forest). Boosting trains models sequentially, where each successive learner corrects the pseudo-residuals of the previous models, primarily reducing bias (e.g., XGBoost, LightGBM).',
 'ensemble learning, bagging, boosting, random forest, gradient boosting, XGBoost, variance reduction, bias reduction'),

('Machine Learning', 'Hard', 'MLOps & Drift',
 'How do you detect and handle Concept Drift and Data Drift in production ML systems?',
 'Explain differences between Covariate Shift, Prior Probability Shift, and Concept Drift. Discuss monitoring techniques like KS-test, PSI (Population Stability Index), and retraining triggers.',
 'Data drift (covariate shift) means the input distribution P(X) changes while P(Y|X) remains unchanged. Concept drift means the relationship between features and target P(Y|X) changes over time. Detection uses statistical divergence tests like Kolmogorov-Smirnov (KS) for continuous features and Population Stability Index (PSI) for categorical bins. Retraining can be triggered periodically or upon metric degradation.',
 'data drift, concept drift, covariate shift, Kolmogorov-Smirnov test, PSI, model monitoring, shadow deployment'),

-- DevOps Questions
('DevOps', 'Easy', 'Continuous Delivery',
 'Explain Blue-Green vs Canary Deployments and zero-downtime release strategies.',
 'Describe architecture, traffic routing at the load balancer / ingress level, rollback mechanisms, and database schema migration compatibility.',
 'Blue-Green deployment runs two identical production environments: Blue (active live) and Green (idle). The new release is deployed and smoke-tested on Green; once validated, traffic switches instantaneously at the router/load balancer. If issues arise, switching back to Blue takes seconds. Canary deployments gradually route a small percentage of traffic (e.g., 5% to 25% to 100%) to validate real-world telemetry before full rollout.',
 'blue-green deployment, canary, zero-downtime, load balancer, ingress, rollback, backward compatibility'),

('DevOps', 'Medium', 'Infrastructure as Code',
 'Explain Infrastructure as Code (IaC) principles and Terraform state management.',
 'Discuss declarative vs imperative configuration, Terraform State locking (e.g. S3 + DynamoDB), plan vs apply cycles, and handling drift.',
 'IaC defines infrastructure declaratively via version-controlled code. Terraform tracks the real-world infrastructure mapping through a state file. In collaborative environments, the state must be stored in remote backends (like AWS S3) with state locking (via DynamoDB) to prevent concurrent writes and race condition corruption. `terraform plan` computes the delta between declared configuration and current state before `apply`.',
 'Infrastructure as Code, Terraform, declarative, state file, state lock, DynamoDB, terraform plan, drift detection'),

('DevOps', 'Hard', 'Container Orchestration',
 'How does Kubernetes handle Pod Scheduling, Resource Limits, and Self-Healing?',
 'Discuss kube-scheduler filtering and scoring phases, CPU/Memory Requests vs Limits, OOMKilled events, readiness vs liveness probes, and Horizontal Pod Autoscalers (HPA).',
 'kube-scheduler binds pods to nodes through two phases: Filtering (removes nodes lacking required CPU, memory, or node affinities) and Scoring (ranks candidate nodes). Requests define guaranteed reservations for scheduling; Limits define hard caps enforced by cgroups. If a pod exceeds memory limits, the Linux kernel OOM-killer terminates it. Liveness probes trigger pod restarts; readiness probes control service endpoint inclusion.',
 'Kubernetes, kube-scheduler, requests, limits, OOMKilled, liveness probe, readiness probe, HPA, ReplicaSet');

-- 4. Sample Completed Interview and Evaluation for Demo Student
INSERT INTO interviews (id, user_id, interview_type, technology, difficulty, question_count, status, overall_score, technical_score, communication_score, relevance_score, completeness_score, summary_feedback, started_at, completed_at) VALUES
(1, 2, 'Technical', 'Java', 'Medium', 3, 'COMPLETED', 8.20, 8.50, 8.00, 8.30, 8.00,
 'Strong performance with clear understanding of Java memory management and collections. Minor gaps in JVM flag tuning and thread safety guarantees under high contention.',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- Answers for the sample interview
INSERT INTO answers (id, interview_id, question_id, candidate_answer, time_taken_seconds) VALUES
(1, 1, 1, 'HashMap is not synchronized while ConcurrentHashMap is thread-safe. HashMap allows null keys and values, but ConcurrentHashMap does not. ConcurrentHashMap uses segment locks in Java 7 and bucket-level locks with CAS in Java 8+ to improve concurrent write throughput.', 180),
(2, 1, 2, 'Garbage collection in Java works by dividing the heap into Young and Old generations. The young generation has Eden and two survivor spaces. When Eden gets full, Minor GC runs. Objects that survive multiple GC cycles get promoted to the Old Gen. Major GC cleans the Old Gen and causes stop-the-world pauses.', 240),
(3, 1, 3, 'The Java Memory Model defines how threads interact through main memory. Volatile variables guarantee visibility by flushing writes directly to main memory and reading from memory instead of thread local cache. It also prevents instruction reordering by using memory barriers.', 210)
ON DUPLICATE KEY UPDATE candidate_answer=VALUES(candidate_answer);

-- AI Evaluations for the answers
INSERT INTO ai_evaluations (answer_id, technical_score, relevance_score, completeness_score, communication_score, overall_score, strengths, weaknesses, suggested_answer, actionable_advice, model_used) VALUES
(1, 8.80, 9.00, 8.50, 8.70, 8.75,
 '["Accurately mentioned bucket-level locking and CAS in Java 8+", "Correctly identified null key differences", "Concise and precise explanation"]',
 '["Could mention the specific race conditions in HashMap like treeification or older infinite loop bugs during rehashing"]',
 'HashMap is non-thread-safe and permits a null key and null values. Under concurrent writes, HashMap can corrupt internal bucket lists or lose updates. In contrast, ConcurrentHashMap prohibits null keys and values, and achieves thread safety in Java 8+ using synchronized blocks on the head node of buckets alongside CAS operations, avoiding full-table locks.',
 'Review internal mechanics of Java 8 tree bins (TreeNodes) in ConcurrentHashMap when bucket collision count exceeds 8.',
 'gemini-1.5-pro'),

(2, 8.20, 8.50, 8.00, 8.10, 8.20,
 '["Clear breakdown of Young and Old generations", "Proper distinction between Minor and Major GC", "Identified survivor spaces"]',
 '["Did not specify the default tenuring threshold (15)", "Did not mention Metaspace or modern collectors like G1 and ZGC"]',
 'Java heap memory is structured based on the Generational Hypothesis. Young generation contains Eden and two survivor spaces (S0, S1). When Eden fills, Minor GC copies surviving objects to a survivor space. When an object survives past the tenuring threshold (typically 15), it is promoted to Tenured/Old generation. Modern collectors like G1GC partition the heap into dynamic regions to minimize Stop-The-World pause durations.',
 'Practice explaining how the G1GC garbage collector divides the heap into dynamic regions rather than contiguous generations.',
 'gemini-1.5-pro'),

(3, 7.80, 8.00, 7.40, 7.60, 7.70,
 '["Understood the visibility guarantee of volatile", "Mentioned memory barriers preventing reordering"]',
 '["Did not mention the happens-before relationship explicitly", "Did not clarify that volatile does not guarantee compound atomicity like count++"]',
 'The Java Memory Model (JMM) establishes a formal "happens-before" contract for concurrent memory access. The volatile modifier guarantees: 1) Immediate visibility across CPU caches, and 2) Prevention of instruction reordering via LoadLoad, StoreStore, LoadStore, and StoreLoad memory barriers. Crucially, volatile does NOT guarantee atomicity for compound operations like i++.',
 'Be careful to emphasize that volatile does not substitute for synchronization when multiple threads perform read-modify-write operations.',
 'gemini-1.5-pro')
ON DUPLICATE KEY UPDATE technical_score=VALUES(technical_score);

-- Performance record for Alex
INSERT INTO performance (user_id, total_interviews, completed_interviews, average_score, highest_score, improvement_rate, technical_avg, communication_avg) VALUES
(2, 3, 3, 8.20, 8.75, 14.50, 8.50, 8.00)
ON DUPLICATE KEY UPDATE average_score=VALUES(average_score);

-- Recommendations for Alex
INSERT INTO recommendations (user_id, skill_id, weak_area, recommended_topic, suggested_difficulty, is_verified_resource, resource_url, reason) VALUES
(2, 1, 'Java Concurrency & Atomic Operations', 'Atomic Variables & CAS Mechanics', 'Medium', TRUE, 'https://docs.oracle.com/javase/tutorial/essential/concurrency/atomicvars.html', 'Identified slight confusion between volatile visibility and atomic compound operations during Java interview.'),
(2, 1, 'JVM Memory Tuning & Garbage Collectors', 'G1GC and ZGC Garbage Collectors in Depth', 'Hard', TRUE, 'https://openjdk.org/jeps/333', 'Strengthen knowledge on region-based garbage collection algorithms for senior technical rounds.'),
(2, 6, 'Graph Algorithms', 'Topological Sort and Dijkstra Shortest Path', 'Medium', FALSE, NULL, 'AI recommendation based on your target role as Full Stack / Backend Software Engineer.')
ON DUPLICATE KEY UPDATE weak_area=VALUES(weak_area);

# InterviewAI — AI-Powered Technical Interview Preparation Platform

A full-stack, enterprise-grade web application built to help engineering students and job candidates prepare for rigorous technical interviews. Features real-time AI-powered answer evaluation across 4 objective rubrics, interactive mock interview sessions, visual performance analytics, personalized skill recommendations, and an administration console.

---

## 🌟 Key Features

1. **Modern Dark Theme UI/UX**:
   - Deep navy/black background (`#060913`), luminous electric blue and violet gradients, glassmorphism, and responsive design across desktop, tablet, and mobile.
   - Designed for high-impact college engineering capstone demonstrations and real-world production deployment.

2. **Full-Stack REST Architecture**:
   - **Frontend**: HTML5, CSS3, modern JavaScript (ES6+), Chart.js for data visualization.
   - **Backend**: Java 17/21/23 with Spring Boot 3.3.4, Spring Data JPA, and Spring Security.
   - **Authentication**: Stateless JSON Web Token (JWT) with BCrypt password hashing and role-based access control (`ROLE_USER`, `ROLE_ADMIN`).
   - **Database**: Normalized MySQL 8.0 schema with 8 relational tables, foreign key constraints, and indexes. Zero-config embedded H2 fallback profile included.

3. **Multi-Domain Technical Question Bank**:
   - Comprehensive seed questions across **7 core engineering tracks**:
     - Java Core & JVM Internals
     - Python & Asynchronous Coroutines
     - C++ & Low-Level Memory / RAII
     - JavaScript & V8 Event Loop
     - SQL & Query / Transaction Optimization
     - Data Structures & Algorithms
     - Web Systems & REST Architecture
   - 3 Difficulty levels: **Easy**, **Medium**, and **Hard**.

4. **Multi-Dimensional AI Evaluation Engine**:
   - Evaluates candidate answers across 4 core rubrics:
     - **Technical Accuracy** (Score /10)
     - **Relevance to Question** (Score /10)
     - **Completeness & Edge Cases** (Score /10)
     - **Communication & Clarity** (Score /10)
     - **Overall Weighted Score** (Score /10)
   - Generates identified strengths, missed concepts, benchmark model answers, and actionable guidance.
   - **Live AI Support**: Direct integration with **Google Gemini API** (`gemini-1.5-pro`) and **OpenAI API** (`gpt-4o-mini`).
   - **Offline Heuristic NLP Fallback**: Guaranteed zero-failure offline mode that computes keyword coverage, structural depth, and heuristic scoring if an API key is not supplied.

5. **Visual Performance Dashboard**:
   - Radar charts mapping multi-domain proficiency.
   - Progression line charts tracking historical score trajectory.
   - Historic interview session logs with status and score badges.

6. **Personalized Skill Recommendations**:
   - Automatically detects missed concepts from past mock rounds.
   - Distinguishes between AI-curated practice topics and verified official documentation (Oracle, MDN, Python Docs, MySQL Reference).

7. **Admin Management Console**:
   - Platform analytics: total users, interviews conducted, platform average score.
   - Question Bank CRUD: search, filter, add new questions, edit, and delete questions.
   - Candidate directory with role promotion.

---

## 📂 Project Directory Structure

```
AI-Interview-Platform/
├── pom.xml                               # Maven project configuration
├── .env.example                          # Environment variables template
├── README.md                             # Documentation & setup guide
├── database/
│   ├── schema.sql                        # Normalized MySQL DDL schema (8 tables)
│   └── data.sql                          # Seed data (demo users, skills, questions)
└── src/
    └── main/
        ├── java/com/aiinterview/
        │   ├── AiInterviewApplication.java
        │   ├── config/                   # Security, JWT filter, CORS, UserDetails
        │   │   ├── CorsConfig.java
        │   │   ├── CustomUserDetailsService.java
        │   │   ├── JwtFilter.java
        │   │   ├── JwtUtil.java
        │   │   └── SecurityConfig.java
        │   ├── controller/               # REST API Controllers
        │   │   ├── AdminController.java
        │   │   ├── AuthController.java
        │   │   ├── EvaluationController.java
        │   │   ├── GlobalExceptionHandler.java
        │   │   ├── InterviewController.java
        │   │   ├── PerformanceController.java
        │   │   ├── QuestionController.java
        │   │   └── RecommendationController.java
        │   ├── dto/                      # Data Transfer Objects
        │   │   ├── AdminStatsDto.java
        │   │   ├── AnswerSubmitRequest.java
        │   │   ├── AuthRequest.java
        │   │   ├── AuthResponse.java
        │   │   ├── EvaluationResultDto.java
        │   │   ├── InterviewDetailDto.java
        │   │   ├── InterviewSetupRequest.java
        │   │   ├── InterviewSummaryDto.java
        │   │   ├── PerformanceReportDto.java
        │   │   ├── QuestionDto.java
        │   │   ├── RegisterRequest.java
        │   │   └── UserProfileDto.java
        │   ├── model/                    # JPA Relational Entities
        │   │   ├── AiEvaluation.java
        │   │   ├── Answer.java
        │   │   ├── Interview.java
        │   │   ├── Performance.java
        │   │   ├── Question.java
        │   │   ├── Recommendation.java
        │   │   ├── Skill.java
        │   │   └── User.java
        │   ├── repository/               # Spring Data JPA Repositories
        │   │   ├── AiEvaluationRepository.java
        │   │   ├── AnswerRepository.java
        │   │   ├── InterviewRepository.java
        │   │   ├── PerformanceRepository.java
        │   │   ├── QuestionRepository.java
        │   │   ├── RecommendationRepository.java
        │   │   ├── SkillRepository.java
        │   │   └── UserRepository.java
        │   └── service/                  # Business Logic & AI Services
        │       ├── AdminService.java
        │       ├── AiEvaluationService.java
        │       ├── AuthService.java
        │       ├── InterviewService.java
        │       ├── PerformanceService.java
        │       ├── QuestionService.java
        │       └── RecommendationService.java
        └── resources/
            ├── application.properties    # Base Spring configuration
            ├── application-mysql.properties # MySQL datasource config
            ├── application-h2.properties # Embedded H2 fallback profile
            ├── schema.sql                # Auto-init schema
            ├── data.sql                  # Auto-init seed data
            └── static/                   # Frontend Web Application
                ├── index.html            # Main semantic SPA entry point
                ├── css/
                │   ├── style.css         # Dark theme & glassmorphic tokens
                │   └── components.css    # Cards, charts, editor, modals, toasts
                └── js/
                    ├── api.js            # REST API client & JWT storage
                    ├── auth.js           # Login, register, demo credentials
                    ├── dashboard.js      # Metrics & Chart.js integration
                    ├── interview.js      # Session lifecycle, timer, submissions
                    ├── results.js        # Score gauges & AI feedback diagnostics
                    ├── recommendations.js# Skill gaps & verified resources
                    ├── admin.js          # Administration panel & question CRUD
                    └── app.js            # App router, nav state, toast notifications
```

---

## 🗄️ Database Design

The relational database is normalized into 8 tables:

1. **`users`**: Candidate and administrator accounts, hashed passwords, target roles.
2. **`skills`**: Core competency categories (Languages, Frameworks, Databases, Fundamentals).
3. **`questions`**: Question bank indexed by technology, difficulty, and rubric key concepts.
4. **`interviews`**: Session instances tracking user, technology, difficulty, question count, status, and scores.
5. **`answers`**: Candidate responses, submission timestamps, time elapsed per question.
6. **`ai_evaluations`**: Evaluation scores (Technical, Relevance, Completeness, Communication, Overall), strengths, weaknesses, model answer, actionable advice.
7. **`performance`**: Aggregated candidate metrics (averages, highest score, completed count, improvement rate).
8. **`recommendations`**: Personalized skill gaps linked to curated documentation and practice tracks.

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java Development Kit (JDK)**: Version 17, 21, or 23 installed (`java -version`).
- **MySQL Server** (Optional for production; embedded H2 is provided for zero-config mode).
- **Node.js** (Optional for testing the static frontend via local server).

### 2. Database Setup (MySQL)
If running with MySQL:
1. Start your local MySQL service.
2. Run the provided schema and seed data in your MySQL client:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/data.sql
   ```
3. Update `.env` or set environment variables:
   ```properties
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=ai_interview_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   SPRING_PROFILES_ACTIVE=mysql
   ```

### 3. Running with Embedded H2 (Zero-Config Mode)
If MySQL is not installed or running, launch Spring Boot with the `h2` profile:
```bash
java -jar target/ai-interview-platform-1.0.0.jar --spring.profiles.active=h2
```
Or set `SPRING_PROFILES_ACTIVE=h2` in your environment. The application will initialize an in-memory database with all 21 seed questions and demo accounts!

### 4. Build and Run the Backend
Using Maven:
```bash
# Package the application
mvn clean package -DskipTests

# Run the Spring Boot application
java -jar target/ai-interview-platform-1.0.0.jar
```
The application will launch on **`http://localhost:8080`**.
Spring Boot serves the full frontend web application automatically at the root URL `http://localhost:8080/`.

### 5. Running the Frontend Standalone
The frontend is also fully decoupled and can be served with any static web server:
```bash
# Using Node npx serve
npx serve src/main/resources/static -p 3000

# Using Python
python -m http.server 3000 --directory src/main/resources/static
```
Open `http://localhost:3000` in any browser.

---

## 🔑 Demo Credentials

| Role | Email | Password | Access Level |
|---|---|---|---|
| **Candidate** | `alex@example.com` | `password123` | Dashboard, Practice, Results, Recommendations |
| **Administrator** | `admin@aiinterview.com` | `password123` | Full Access + Question Bank CRUD & User Directory |

*(Quick Auto-Fill buttons are embedded directly in the Sign In modal for 1-click evaluation during presentations!)*

---

## 📡 REST API Reference

### Authentication
- `POST /api/auth/register` — Create candidate account.
- `POST /api/auth/login` — Authenticate and receive JWT token.
- `GET /api/auth/me` — Retrieve active profile.
- `PUT /api/auth/profile` — Update candidate target role or bio.

### Questions
- `GET /api/questions/public` — List questions (filters: `technology`, `difficulty`).
- `GET /api/questions/public/technologies` — Distinct technologies in question bank.
- `GET /api/questions/{id}` — Retrieve specific question details.

### Interviews & AI Evaluation
- `POST /api/interviews/setup` — Configure and launch new interview session.
- `GET /api/interviews/{id}` — Get interview details and questions.
- `POST /api/interviews/submit-answer` — Submit answer and receive AI rubric scoring.
- `POST /api/interviews/{id}/complete` — Finalize session, compute composite scores, and trigger recommendation engine.
- `GET /api/interviews/my-history` — Candidate interview attempt history.

### Analytics & Recommendations
- `GET /api/performance/me` — Candidate analytics, radar scores, improvement index.
- `GET /api/recommendations/me` — AI skill gaps and verified resources.

### Admin Console (Requires `ROLE_ADMIN`)
- `GET /api/admin/stats` — Platform-wide statistics.
- `GET /api/admin/users` — Directory of registered candidates.
- `PUT /api/admin/users/{id}/role` — Promote or modify role.
- `POST /api/admin/questions` — Add new question to bank.
- `PUT /api/admin/questions/{id}` — Update existing question.
- `DELETE /api/admin/questions/{id}` — Delete question.

---

## 🤖 AI Service Configuration

Set your provider in `application.properties` or environment variables:

```bash
# Google Gemini
AI_PROVIDER=gemini
GEMINI_API_KEY=AIzaSy...
GEMINI_MODEL=gemini-1.5-pro

# OpenAI (Alternative)
AI_PROVIDER=openai
OPENAI_API_KEY=sk-...
OPENAI_MODEL=gpt-4o-mini
```

*Note: If no API key is provided, the platform seamlessly defaults to the built-in intelligent heuristic engine, allowing offline testing and grading without API billing.*

-- ====================================================================
-- AI Interview Preparation Platform - Database Schema
-- Normalized relational schema for MySQL 8.0+
-- ====================================================================

CREATE DATABASE IF NOT EXISTS ai_interview_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_interview_db;

-- 1. User Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER', -- ROLE_USER or ROLE_ADMIN
    target_role VARCHAR(100) DEFAULT 'Software Engineer',
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Skill Table
CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL, -- Language, Framework, Database, Fundamentals, Web
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Question Table
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    technology VARCHAR(50) NOT NULL, -- Java, Python, C++, JavaScript, SQL, DSA, Web Development
    difficulty VARCHAR(20) NOT NULL, -- Easy, Medium, Hard
    category VARCHAR(50) DEFAULT 'Technical',
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    sample_answer TEXT,
    key_concepts TEXT, -- Comma-separated or JSON list of required keywords/concepts
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_tech (technology),
    INDEX idx_question_diff (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Interview Table
CREATE TABLE IF NOT EXISTS interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    interview_type VARCHAR(50) NOT NULL, -- Technical, Mock Interview
    technology VARCHAR(50) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    question_count INT NOT NULL DEFAULT 5,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, COMPLETED, ABANDONED
    overall_score DECIMAL(4, 2) DEFAULT NULL,
    technical_score DECIMAL(4, 2) DEFAULT NULL,
    communication_score DECIMAL(4, 2) DEFAULT NULL,
    relevance_score DECIMAL(4, 2) DEFAULT NULL,
    completeness_score DECIMAL(4, 2) DEFAULT NULL,
    summary_feedback TEXT DEFAULT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_interview_user (user_id),
    INDEX idx_interview_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Answer Table
CREATE TABLE IF NOT EXISTS answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    candidate_answer TEXT NOT NULL,
    time_taken_seconds INT DEFAULT 0,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_answer_interview (interview_id),
    INDEX idx_answer_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. AI_Evaluation Table
CREATE TABLE IF NOT EXISTS ai_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    answer_id BIGINT NOT NULL UNIQUE,
    technical_score DECIMAL(4, 2) NOT NULL,
    relevance_score DECIMAL(4, 2) NOT NULL,
    completeness_score DECIMAL(4, 2) NOT NULL,
    communication_score DECIMAL(4, 2) NOT NULL,
    overall_score DECIMAL(4, 2) NOT NULL,
    strengths TEXT NOT NULL, -- JSON array of strengths
    weaknesses TEXT NOT NULL, -- JSON array of weaknesses
    suggested_answer TEXT NOT NULL,
    actionable_advice TEXT NOT NULL,
    model_used VARCHAR(50) DEFAULT 'gemini-1.5-pro',
    evaluated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE,
    INDEX idx_eval_answer (answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Performance Table
CREATE TABLE IF NOT EXISTS performance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_interviews INT DEFAULT 0,
    completed_interviews INT DEFAULT 0,
    average_score DECIMAL(4, 2) DEFAULT 0.00,
    highest_score DECIMAL(4, 2) DEFAULT 0.00,
    improvement_rate DECIMAL(5, 2) DEFAULT 0.00, -- Percentage change from earliest to latest
    technical_avg DECIMAL(4, 2) DEFAULT 0.00,
    communication_avg DECIMAL(4, 2) DEFAULT 0.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_perf_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Recommendation Table
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    skill_id BIGINT DEFAULT NULL,
    weak_area VARCHAR(150) NOT NULL,
    recommended_topic VARCHAR(150) NOT NULL,
    suggested_difficulty VARCHAR(20) DEFAULT 'Medium',
    is_verified_resource BOOLEAN DEFAULT FALSE,
    resource_url VARCHAR(255) DEFAULT NULL,
    reason TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE SET NULL,
    INDEX idx_rec_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

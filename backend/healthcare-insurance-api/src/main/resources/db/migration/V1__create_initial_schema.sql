CREATE TABLE users (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    full_name VARCHAR(100) NOT NULL,

    email VARCHAR(150) NOT NULL UNIQUE,

    phone VARCHAR(20),

    password_hash VARCHAR(255) NOT NULL,

    role VARCHAR(20) NOT NULL DEFAULT 'USER',

    date_of_birth DATE,

    gender VARCHAR(20),

    address VARCHAR(500),

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP

);


CREATE TABLE medical_history (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    medical_condition VARCHAR(150) NOT NULL,

    diagnosis_date DATE,

    treatment VARCHAR(500),

    doctor_name VARCHAR(100),

    hospital_name VARCHAR(150),

    status VARCHAR(30),

    notes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_medical_history_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE

);


CREATE TABLE policy_plans (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    plan_code VARCHAR(50) NOT NULL UNIQUE,

    name VARCHAR(150) NOT NULL,

    description TEXT,

    premium DECIMAL(12,2) NOT NULL,

    coverage_amount DECIMAL(15,2) NOT NULL,

    duration_years INT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP

);


CREATE TABLE policy_plan_benefits (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    policy_plan_id BIGINT NOT NULL,

    benefit VARCHAR(500) NOT NULL,

    CONSTRAINT fk_benefit_plan
        FOREIGN KEY (policy_plan_id)
        REFERENCES policy_plans(id)
        ON DELETE CASCADE

);


CREATE TABLE policy_plan_exclusions (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    policy_plan_id BIGINT NOT NULL,

    exclusion_text VARCHAR(500) NOT NULL,

    CONSTRAINT fk_exclusion_plan
        FOREIGN KEY (policy_plan_id)
        REFERENCES policy_plans(id)
        ON DELETE CASCADE

);


CREATE TABLE purchased_policies (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    policy_number VARCHAR(50) NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,

    policy_plan_id BIGINT NOT NULL,

    premium DECIMAL(12,2) NOT NULL,

    coverage_amount DECIMAL(15,2) NOT NULL,

    purchase_date DATE NOT NULL,

    expiry_date DATE NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_purchased_policy_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_purchased_policy_plan
        FOREIGN KEY (policy_plan_id)
        REFERENCES policy_plans(id)

);


CREATE TABLE payments (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    policy_plan_id BIGINT,

    purchased_policy_id BIGINT,

    amount DECIMAL(12,2) NOT NULL,

    payment_method VARCHAR(30) NOT NULL,

    transaction_id VARCHAR(100) NOT NULL UNIQUE,

    payment_status VARCHAR(20) NOT NULL,

    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_payment_plan
        FOREIGN KEY (policy_plan_id)
        REFERENCES policy_plans(id),

    CONSTRAINT fk_payment_policy
        FOREIGN KEY (purchased_policy_id)
        REFERENCES purchased_policies(id)

);


CREATE TABLE claims (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    claim_number VARCHAR(50) NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,

    purchased_policy_id BIGINT NOT NULL,

    amount DECIMAL(12,2) NOT NULL,

    claim_date DATE NOT NULL,

    description TEXT,

    hospital_name VARCHAR(150),

    treatment_details TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    rejection_reason TEXT,

    processed_date TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_claim_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_claim_policy
        FOREIGN KEY (purchased_policy_id)
        REFERENCES purchased_policies(id)

);


CREATE TABLE password_reset_tokens (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    token_hash VARCHAR(255) NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    used_at TIMESTAMP NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reset_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE

);


CREATE TABLE audit_logs (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    user_id BIGINT,

    action VARCHAR(100) NOT NULL,

    entity_type VARCHAR(50),

    entity_id BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL

);
CREATE TABLE salary_project (
     id SERIAL PRIMARY KEY,
     enterprise_id BIGINT NOT NULL,
     bank_id BIGINT NOT NULL,
     created_at TIMESTAMP NOT NULL,
     status VARCHAR(50) NOT NULL,
     CONSTRAINT fk_salary_projects_enterprise FOREIGN KEY (enterprise_id) REFERENCES enterprise(id) ON DELETE CASCADE,
     CONSTRAINT fk_salary_projects_bank FOREIGN KEY (bank_id) REFERENCES bank(id) ON DELETE CASCADE
);

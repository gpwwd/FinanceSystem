ALTER TABLE account DROP COLUMN IF EXISTS is_salary;

DROP TABLE IF EXISTS salary_account;

CREATE TABLE salary_account (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    salary_project_id BIGINT NOT NULL REFERENCES salary_project(id) ON DELETE CASCADE,
    CONSTRAINT unique_account UNIQUE (account_id)
);

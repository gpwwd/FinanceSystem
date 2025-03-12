ALTER TABLE enterprise
    ADD COLUMN payroll_account_id BIGINT REFERENCES account(id) ON DELETE SET NULL;

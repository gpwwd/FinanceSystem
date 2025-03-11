alter table salary_account add column pending_status VARCHAR(50);

UPDATE salary_account SET pending_status = 'PENDING' WHERE pending_status IS NULL;

ALTER TABLE salary_account ALTER COLUMN pending_status SET NOT NULL;
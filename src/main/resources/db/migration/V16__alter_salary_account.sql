alter table salary_account add column salary numeric(15, 2) default 0.00;

UPDATE salary_account SET salary = 0.00 WHERE salary IS NULL;

ALTER TABLE salary_account ALTER COLUMN salary SET NOT NULL;
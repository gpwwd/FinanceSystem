ALTER TABLE salary_project ADD COLUMN currency VARCHAR(3);

UPDATE salary_project SET currency = 'USD' WHERE currency IS NULL;

ALTER TABLE salary_project ALTER COLUMN currency SET NOT NULL;

ALTER TABLE account
ADD COLUMN enterprise_id BIGINT REFERENCES enterprise(id) ON DELETE CASCADE,
ADD CONSTRAINT chk_owner CHECK (
    (owner_id IS NOT NULL AND enterprise_id IS NULL) OR
    (enterprise_id IS NOT NULL AND owner_id IS NULL)
);

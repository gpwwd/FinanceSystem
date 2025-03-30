ALTER TABLE transaction
    ADD COLUMN revert_transaction_id BIGINT REFERENCES transaction(id);

ALTER TABLE transaction
    ADD CONSTRAINT revert_transaction_check CHECK (revert_transaction_id IS NULL OR revert_transaction_id <> id);

CREATE UNIQUE INDEX unique_reversal ON transaction (revert_transaction_id) WHERE revert_transaction_id IS NOT NULL;

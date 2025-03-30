CREATE TABLE transaction (
     id SERIAL PRIMARY KEY,
     from_entity_id BIGINT,
     from_type VARCHAR(50)
         CHECK (from_type IS NULL OR from_type IN ('ACCOUNT', 'DEPOSIT', 'LOAN', 'EXTERNAL')),
--  `                       null is temporary(because of the unfinished loan issue logic)
     to_entity_id BIGINT NOT NULL,
     to_type VARCHAR(50) NOT NULL CHECK (to_type IN ('ACCOUNT', 'DEPOSIT', 'LOAN', 'EXTERNAL')),
     amount DECIMAL(19,2) NOT NULL CHECK (amount > 0),
     timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
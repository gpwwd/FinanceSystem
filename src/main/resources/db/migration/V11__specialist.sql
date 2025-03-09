CREATE TABLE specialist (
        id SERIAL PRIMARY KEY,
        user_id INTEGER NOT NULL REFERENCES users(id),
        enterprise_id INTEGER NOT NULL REFERENCES enterprise(id)
);

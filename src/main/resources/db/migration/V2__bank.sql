create table bank
(
    id serial primary key,
    name varchar(255) not null,
    bik varchar(25) not null unique,
    address text
);

alter table account
    add column bank_id integer;

ALTER TABLE account
    ADD CONSTRAINT account_bank_id_fkey FOREIGN KEY (bank_id) REFERENCES bank(id);

alter table bank
    owner to postgres;


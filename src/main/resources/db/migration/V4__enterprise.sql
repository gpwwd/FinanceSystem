create table enterprise
(
    id            serial primary key,
    type          varchar(50)  not null,
    legal_name    varchar(255) not null,
    unp           varchar(20)  not null unique,
    bank_id       integer      not null references bank,
    legal_address text         not null,
    created_at    timestamp default CURRENT_TIMESTAMP
);

alter table enterprise
    owner to postgres;


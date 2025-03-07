create table loan
(
    id                serial primary key,
    account_id        integer not null references account on delete cascade,
    principal_amount  numeric(15, 2)                                  not null,
    remaining_balance numeric(15, 2)                                  not null,
    interest_rate     numeric(5, 2)                                   not null,
    term_months       integer                                         not null,
    created_at        timestamp                                       not null,
    status            varchar(20) default 'ACTIVE'::character varying not null
);

alter table loan
    owner to postgres;


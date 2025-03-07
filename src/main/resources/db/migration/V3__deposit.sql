create table deposit
(
    id                 serial primary key,
    account_id         integer not null references account on delete cascade,
    balance            numeric(15, 2) default 0.00                        not null,
    interest_rate      numeric(10, 5)                                     not null,
    created_at         timestamp      default now()                       not null,
    term_months        integer        default 0                           not null,
    last_interest_date timestamp      default now(),
    deposit_status     varchar(20)    default 'ACTIVE'::character varying not null,
    principal_balance  numeric(19, 2) default 0.00                        not null
);

alter table deposit
    owner to postgres;


create table account
(
    id serial primary key,
    balance numeric(15, 2) default 0.00,
    status varchar(20) default 'ACTIVE'::character varying not null,
    currency varchar(3) not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    is_salary boolean default false not null
);

alter table account
    owner to postgres;


create table pending_clients
(
    id                     serial primary key,
    full_name              varchar(255)                                     not null,
    passport_series_number varchar(20)                                      not null,
    identity_number        varchar(20)                                      not null,
    phone                  varchar(20)                                      not null,
    email                  varchar(255)                                     not null,
    role                   varchar(50)                                      not null,
    is_foreign             boolean                                          not null,
    created_at             timestamp   default CURRENT_TIMESTAMP,
    status                 varchar(20) default 'PENDING'::character varying not null,
    password               varchar(255)                                     not null
);

alter table pending_clients
    owner to postgres;


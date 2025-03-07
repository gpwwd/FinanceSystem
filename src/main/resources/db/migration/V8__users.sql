create table users
(
    id                     serial primary key ,
    full_name              varchar(255)                                         not null,
    passport_series_number varchar(20)                                          not null
        constraint client_passport_series_number_key
            unique,
    identity_number        varchar(20)                                          not null
        constraint client_identity_number_key
            unique,
    phone                  varchar(20)                                          not null,
    email                  varchar(255)                                         not null
        constraint client_email_key
            unique,
    is_foreign             boolean   default false,
    created_at             timestamp default CURRENT_TIMESTAMP,
    role                   varchar(50)                                          not null,
    password               varchar(255)                                         not null
);

alter table account
    add column owner_id integer;

ALTER TABLE account
    ADD CONSTRAINT account_user_id_fkey FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE;

alter table users
    owner to postgres;


create table pending_loan
(
    id                serial primary key ,
    account_id        integer                                          not null
            constraint pending_loans_account_id_fkey
            references account
            on delete cascade,
    principal_amount  numeric(15, 2)                                   not null,
    term_months       integer                                          not null,
    request_status    varchar(20) default 'PENDING'::character varying not null,
    is_fixed_interest boolean     default true                         not null
);

alter table pending_loan
    owner to postgres;



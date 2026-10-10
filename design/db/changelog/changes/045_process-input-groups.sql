--liquibase formatted sql


--changeset tfc:045-process-input-groups

alter table process_input
    add column input_group integer not null default 0;

alter table process_input
    drop constraint process_input_pkey;

alter table process_input
    add primary key (process_id, input_group, resource_id);
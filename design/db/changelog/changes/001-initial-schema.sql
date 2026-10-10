--liquibase formatted sql

--changeset tfc-skyblock:001-initial-schema

create table resource (
    id varchar(100) primary key,
    name varchar(200) not null,
    category varchar(50) not null,
    tier integer,
    is_renewable boolean not null default false,
    is_bootstrap boolean not null default false,
    notes text
);

create table source (
    id varchar(100) primary key,
    name varchar(200) not null,
    type varchar(50) not null,
    notes text
);

create table resource_source (
    resource_id varchar(100) not null references resource(id),
    source_id varchar(100) not null references source(id),
    is_initial boolean not null default false,
    is_renewable boolean not null default false,
    quantity numeric,
    conditions text,
    notes text,
    primary key (resource_id, source_id)
);

create table process (
    id varchar(100) primary key,
    name varchar(200) not null,
    type varchar(50) not null,
    description text,
    notes text
);

create table process_input (
    process_id varchar(100) not null references process(id),
    resource_id varchar(100) not null references resource(id),
    quantity numeric not null default 1,
    consumed boolean not null default true,
    notes text,
    primary key (process_id, resource_id)
);

create table process_output (
    process_id varchar(100) not null references process(id),
    resource_id varchar(100) not null references resource(id),
    quantity numeric not null default 1,
    chance numeric,
    notes text,
    primary key (process_id, resource_id)
);

create table technology (
    id varchar(100) primary key,
    name varchar(200) not null,
    tier integer,
    description text,
    notes text
);

create table technology_resource_requirement (
    technology_id varchar(100) not null references technology(id),
    resource_id varchar(100) not null references resource(id),
    quantity numeric,
    notes text,
    primary key (technology_id, resource_id)
);

create table technology_technology_requirement (
    technology_id varchar(100) not null references technology(id),
    required_technology_id varchar(100) not null references technology(id),
    notes text,
    primary key (technology_id, required_technology_id),
    check (technology_id <> required_technology_id)
);

create table technology_process_requirement (
    technology_id varchar(100) not null references technology(id),
    process_id varchar(100) not null references process(id),
    notes text,
    primary key (technology_id, process_id)
);

create table technology_resource_unlock (
    technology_id varchar(100) not null references technology(id),
    resource_id varchar(100) not null references resource(id),
    notes text,
    primary key (technology_id, resource_id)
);

create table technology_process_unlock (
    technology_id varchar(100) not null references technology(id),
    process_id varchar(100) not null references process(id),
    notes text,
    primary key (technology_id, process_id)
);

create table progression_goal (
    id varchar(100) primary key,
    name varchar(200) not null,
    stage integer not null,
    description text,
    notes text
);

create table goal_resource_requirement (
    goal_id varchar(100) not null references progression_goal(id),
    resource_id varchar(100) not null references resource(id),
    quantity numeric,
    notes text,
    primary key (goal_id, resource_id)
);

create table goal_technology_requirement (
    goal_id varchar(100) not null references progression_goal(id),
    technology_id varchar(100) not null references technology(id),
    notes text,
    primary key (goal_id, technology_id)
);

create table goal_process_requirement (
    goal_id varchar(100) not null references progression_goal(id),
    process_id varchar(100) not null references process(id),
    notes text,
    primary key (goal_id, process_id)
);

create table goal_goal_requirement (
    goal_id varchar(100) not null references progression_goal(id),
    required_goal_id varchar(100) not null references progression_goal(id),
    notes text,
    primary key (goal_id, required_goal_id),
    check (goal_id <> required_goal_id)
);

--rollback drop table goal_goal_requirement;
--rollback drop table goal_process_requirement;
--rollback drop table goal_technology_requirement;
--rollback drop table goal_resource_requirement;
--rollback drop table progression_goal;
--rollback drop table technology_process_unlock;
--rollback drop table technology_resource_unlock;
--rollback drop table technology_process_requirement;
--rollback drop table technology_technology_requirement;
--rollback drop table technology_resource_requirement;
--rollback drop table technology;
--rollback drop table process_output;
--rollback drop table process_input;
--rollback drop table process;
--rollback drop table resource_source;
--rollback drop table source;
--rollback drop table resource;

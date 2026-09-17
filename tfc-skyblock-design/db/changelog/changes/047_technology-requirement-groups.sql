--liquibase formatted sql

--changeset mcampoy:047-technology-requirement-groups

ALTER TABLE technology_resource_requirement
    ADD COLUMN requirement_group integer NOT NULL DEFAULT 0;

ALTER TABLE technology_resource_requirement
    DROP CONSTRAINT technology_resource_requirement_pkey;

ALTER TABLE technology_resource_requirement
    ADD CONSTRAINT technology_resource_requirement_pkey
        PRIMARY KEY (
            technology_id,
            requirement_group,
            resource_id
        );
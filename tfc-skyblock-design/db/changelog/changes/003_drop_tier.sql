--liquibase formatted sql

--changeset tfc:003-remove-tiers

ALTER TABLE resource
    DROP COLUMN tier;

ALTER TABLE technology
    DROP COLUMN tier;

--rollback ALTER TABLE technology ADD COLUMN tier INTEGER;
--rollback ALTER TABLE resource ADD COLUMN tier INTEGER;
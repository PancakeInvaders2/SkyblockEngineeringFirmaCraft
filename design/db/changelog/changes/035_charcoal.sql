--liquibase formatted sql
--changeset tfc:035-charcoal

INSERT INTO process (id, name, type)
VALUES
    ('make_charcoal', 'Make Charcoal', 'charcoal');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('make_charcoal', 'wooden_log'),
    ('make_charcoal', 'fire_starter');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('make_charcoal', 'charcoal');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('fire_starter', 'make_charcoal');
--liquibase formatted sql
--changeset tfc:037-water-lava-and-rock-generation

INSERT INTO resource (id, name, category)
VALUES
    ('water', 'Water', 'fluid'),
    ('lava', 'Lava', 'fluid');

INSERT INTO process (id, name, type)
VALUES
    ('generate_raw_rock', 'Generate Raw Rock', 'interaction');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('generate_raw_rock', 'water'),
    ('generate_raw_rock', 'lava');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('generate_raw_rock', 'raw_rock');

UPDATE process
SET technology_required = FALSE
WHERE id = 'generate_raw_rock';

INSERT INTO scenario_resource (scenario_id, resource_id)
VALUES
    ('vanilla_tfc', 'water'),
    ('vanilla_tfc', 'lava');
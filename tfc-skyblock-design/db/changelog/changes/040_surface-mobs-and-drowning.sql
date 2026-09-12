--liquibase formatted sql

--changeset tfc:040-surface-mobs-and-drowning

INSERT INTO resource (id, name, category)
VALUES
    ('skeleton', 'Skeleton', 'entity'),
    ('zombie', 'Zombie', 'entity'),
    ('creeper', 'Creeper', 'entity'),
    ('drowned', 'Drowned', 'entity');

INSERT INTO process (id, name, type)
VALUES
    ('drown_zombie', 'Drown Zombie', 'transformation');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('drown_zombie', 'zombie'),
    ('drown_zombie', 'water');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('drown_zombie', 'drowned');

UPDATE process
SET technology_required = FALSE
WHERE id = 'drown_zombie';

INSERT INTO scenario_resource (scenario_id, resource_id)
VALUES
    ('vanilla_tfc', 'skeleton'),
    ('vanilla_tfc', 'zombie'),
    ('vanilla_tfc', 'creeper');
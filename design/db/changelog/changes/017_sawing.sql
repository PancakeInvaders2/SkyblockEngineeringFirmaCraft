--liquibase formatted sql

--changeset tfc:017-sawing

INSERT INTO resource (id, name, category)
VALUES
    ('saw', 'Saw', 'tool');

INSERT INTO technology (id, name)
VALUES
    ('sawing', 'Sawing');

INSERT INTO process (id, name, type)
VALUES
    ('craft_saw', 'Craft Saw', 'crafting'),
    ('saw_wooden_log', 'Saw Wooden Log', 'sawing');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_saw', 'copper_ingot'),
    ('craft_saw', 'clay_mold'),
    ('craft_saw', 'wooden_log'),
    ('craft_saw', 'stick'),
    ('craft_saw', 'straw'),

    ('saw_wooden_log', 'wooden_log'),
    ('saw_wooden_log', 'saw');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_saw', 'saw'),
    ('saw_wooden_log', 'lumber');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('sawing', 'craft_saw'),
    ('sawing', 'saw_wooden_log');


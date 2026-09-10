--liquibase formatted sql

--changeset tfc:022-torches-and-ash

INSERT INTO resource (id, name, category)
VALUES
    ('torch', 'Torch', 'light');

INSERT INTO process (id, name, type)
VALUES
    ('craft_torch', 'Craft Torch', 'crafting');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_torch', 'stick'),
    ('craft_torch', 'wooden_log');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_torch', 'torch'),
    ('craft_torch', 'ash');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('firepit', 'craft_torch');


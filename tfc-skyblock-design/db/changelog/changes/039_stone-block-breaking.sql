--liquibase formatted sql

--changeset tfc:039-stone-block-breaking

INSERT INTO resource (id, name, category)
VALUES
    ('cobblestone', 'Cobblestone', 'stone');

INSERT INTO process (id, name, type)
VALUES
    ('break_raw_rock', 'Break Raw Rock', 'extraction'),
    ('break_cobblestone', 'Break Cobblestone', 'extraction'),
    ('craft_cobblestone', 'Craft Cobblestone', 'crafting');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('break_raw_rock', 'raw_rock'),
    ('break_raw_rock', 'pickaxe'),

    ('break_cobblestone', 'cobblestone'),
    ('break_cobblestone', 'pickaxe'),

    ('craft_cobblestone', 'loose_rock');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('break_raw_rock', 'loose_rock'),
    ('break_cobblestone', 'loose_rock'),
    ('craft_cobblestone', 'cobblestone');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('pickaxe', 'break_raw_rock'),
    ('pickaxe', 'break_cobblestone');

UPDATE process
SET technology_required = FALSE
WHERE id = 'craft_cobblestone';
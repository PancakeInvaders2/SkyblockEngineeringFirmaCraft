--liquibase formatted sql
--changeset tfc:033-chiseling-and-smooth-rock

INSERT INTO resource (id, name, category)
VALUES
    ('chisel', 'Chisel', 'tool');

INSERT INTO technology (id, name)
VALUES
    ('chiseling', 'Chiseling');

INSERT INTO process (id, name, type)
VALUES
    ('craft_chisel', 'Craft Chisel', 'crafting'),
    ('chisel_raw_rock', 'Chisel Raw Rock', 'chiseling');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_chisel', 'copper_ingot'),
    ('craft_chisel', 'clay_mold'),
    ('craft_chisel', 'wooden_log'),
    ('craft_chisel', 'stick'),
    ('craft_chisel', 'straw'),
    ('chisel_raw_rock', 'raw_rock'),
    ('chisel_raw_rock', 'chisel');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_chisel', 'chisel'),
    ('chisel_raw_rock', 'smooth_rock');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('chiseling', 'craft_chisel'),
    ('chiseling', 'chisel_raw_rock');
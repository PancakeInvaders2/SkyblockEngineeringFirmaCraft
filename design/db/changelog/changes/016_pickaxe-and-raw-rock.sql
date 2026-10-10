--liquibase formatted sql

--changeset tfc:016-pottery-pickaxe-and-raw-rock

INSERT INTO resource (id, name, category)
VALUES
    ('clay', 'Clay', 'mineral'),
    ('wooden_log', 'Wood', 'wood'),
    ('stick', 'Stick', 'wood'),
    ('straw', 'Straw', 'plants'),
    ('clay_mold', 'Clay Mold', 'pottery'),
    ('pickaxe', 'Pickaxe', 'tool');

INSERT INTO technology (id, name)
VALUES
    ('pottery', 'Pottery'),
    ('pickaxe', 'Pickaxe');

INSERT INTO process (id, name, type)
VALUES
    ('cook_raw_clay_mold', 'Cook Raw Clay Mold', 'firing'),
    ('craft_pickaxe', 'Craft Pickaxe', 'crafting'),
    ('raw_rock_extraction', 'Extract Raw Rock', 'extraction');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('cook_raw_clay_mold', 'clay'),
    ('cook_raw_clay_mold', 'wooden_log'),

    ('craft_pickaxe', 'copper_ingot'),
    ('craft_pickaxe', 'clay_mold'),
    ('craft_pickaxe', 'wooden_log'),
    ('craft_pickaxe', 'stick'),
    ('craft_pickaxe', 'straw'),

    ('raw_rock_extraction', 'pickaxe');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('cook_raw_clay_mold', 'clay_mold'),
    ('craft_pickaxe', 'pickaxe'),
    ('raw_rock_extraction', 'raw_rock');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('pottery', 'cook_raw_clay_mold'),
    ('pickaxe', 'craft_pickaxe'),
    ('pickaxe', 'raw_rock_extraction');


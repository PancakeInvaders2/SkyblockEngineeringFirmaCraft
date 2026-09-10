--liquibase formatted sql

--changeset tfc:029-bloomery

INSERT INTO resource (id, name, category)
VALUES
    ('bloomery', 'Bloomery', 'equipment'),
    ('iron_bloom', 'Iron Bloom', 'metal');

INSERT INTO technology (id, name)
VALUES
    ('bloomery', 'Bloomery');

INSERT INTO technology_resource_requirement (technology_id, resource_id)
VALUES
    ('bloomery', 'bronze_sheet');

INSERT INTO process (id, name, type)
VALUES
    ('bloom_iron_ore', 'Bloom Iron Ore', 'bloomery'),
    ('bloom_cast_iron', 'Bloom Cast Iron', 'bloomery'),
    ('forge_wrought_iron_ingot', 'Forge Wrought Iron Ingot', 'anvil');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('bloom_iron_ore', 'charcoal'),
    ('bloom_iron_ore', 'iron_ore'),
    ('bloom_cast_iron', 'charcoal'),
    ('bloom_cast_iron', 'cast_iron_ingot'),
    ('forge_wrought_iron_ingot', 'iron_bloom'),
    ('forge_wrought_iron_ingot', 'flux');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('bloom_iron_ore', 'iron_bloom'),
    ('bloom_cast_iron', 'iron_bloom'),
    ('forge_wrought_iron_ingot', 'wrought_iron_ingot');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('bloomery', 'bloom_iron_ore'),
    ('bloomery', 'bloom_cast_iron'),
    ('anvil', 'forge_wrought_iron_ingot');

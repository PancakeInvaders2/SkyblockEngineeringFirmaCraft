--liquibase formatted sql

--changeset tfc:028-bronze-and-wrought-iron-products

INSERT INTO resource (id, name, category)
VALUES
    ('bronze_sheet', 'Bronze Sheet', 'metal'),
    ('wrought_iron_ingot', 'Wrought Iron Ingot', 'metal');

INSERT INTO process (id, name, type)
VALUES
    ('forge_bronze_sheet', 'Forge Bronze Sheet', 'anvil'),
    ('forge_wrought_iron_sheet', 'Forge Wrought Iron Sheet', 'anvil'),
    ('forge_tuyere', 'Forge Tuyere', 'anvil');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('forge_bronze_sheet', 'bronze_ingot'),
    ('forge_bronze_sheet', 'flux'),
    ('forge_wrought_iron_sheet', 'wrought_iron_ingot'),
    ('forge_wrought_iron_sheet', 'flux'),
    ('forge_tuyere', 'wrought_iron_ingot'),
    ('forge_tuyere', 'flux');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('forge_bronze_sheet', 'bronze_sheet'),
    ('forge_wrought_iron_sheet', 'wrought_iron_sheet'),
    ('forge_tuyere', 'tuyere');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('anvil', 'forge_bronze_sheet'),
    ('anvil', 'forge_wrought_iron_sheet'),
    ('anvil', 'forge_tuyere');

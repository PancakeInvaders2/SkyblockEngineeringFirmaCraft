--liquibase formatted sql
--changeset tfc:032-quern-and-powders

INSERT INTO resource (id, name, category)
VALUES
    ('smooth_rock', 'Smooth Rock', 'stone'),
    ('quern', 'Quern', 'equipment'),
    ('kaolinite', 'Kaolinite', 'mineral'),
    ('graphite', 'Graphite', 'mineral');


INSERT INTO technology (id, name)
VALUES
    ('quern', 'Quern');

INSERT INTO process (id, name, type)
VALUES
    ('craft_quern', 'Craft Quern', 'crafting'),
    ('grind_kaolinite', 'Grind Kaolinite', 'grinding'),
    ('grind_graphite', 'Grind Graphite', 'grinding');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_quern', 'raw_rock'),
    ('craft_quern', 'smooth_rock'),
    ('grind_kaolinite', 'kaolinite'),
    ('grind_graphite', 'graphite');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_quern', 'quern'),
    ('grind_kaolinite', 'kaolinite_powder'),
    ('grind_graphite', 'graphite_powder');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('quern', 'craft_quern'),
    ('quern', 'grind_kaolinite'),
    ('quern', 'grind_graphite');
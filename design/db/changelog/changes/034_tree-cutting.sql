--liquibase formatted sql
--changeset tfc:034-tree-cutting

INSERT INTO resource (id, name, category)
VALUES
    ('tree', 'Tree', 'plants');

INSERT INTO technology (id, name)
VALUES
    ('tree_cutting', 'Tree Cutting');

INSERT INTO process (id, name, type)
VALUES
    ('cut_tree', 'Cut Tree', 'woodcutting');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('cut_tree', 'tree'),
    ('cut_tree', 'axe');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('cut_tree', 'wooden_log');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('tree_cutting', 'cut_tree');
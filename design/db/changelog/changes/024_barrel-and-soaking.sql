--liquibase formatted sql

--changeset tfc:024-barrel-and-soaking

INSERT INTO resource (id, name, category)
VALUES
    ('barrel', 'Barrel', 'equipment'),
    ('slaked_lime', 'Slaked Lime', 'material'),
    ('tannin', 'Tannin', 'material');

INSERT INTO technology (id, name)
VALUES
    ('barrel', 'Barrel');

INSERT INTO process (id, name, type)
VALUES
    ('craft_barrel', 'Craft Barrel', 'crafting'),
    ('soak_flux_to_slaked_lime', 'Soak Flux into Slaked Lime', 'soaking'),
    ('soak_wooden_log_to_tannin', 'Soak Wooden Log into Tannin', 'soaking');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('craft_barrel', 'lumber'),
    ('soak_flux_to_slaked_lime', 'flux'),
    ('soak_wooden_log_to_tannin', 'wooden_log');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('craft_barrel', 'barrel'),
    ('soak_flux_to_slaked_lime', 'slaked_lime'),
    ('soak_wooden_log_to_tannin', 'tannin');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('barrel', 'craft_barrel'),
    ('barrel', 'soak_flux_to_slaked_lime'),
    ('barrel', 'soak_wooden_log_to_tannin');

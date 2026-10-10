--liquibase formatted sql
--changeset tfc:038-water-container

INSERT INTO resource (id, name, category)
VALUES
    ('water_in_container', 'Water in Container', 'fluid');

INSERT INTO process (id, name, type)
VALUES
    ('fill_container_with_water', 'Fill Container with Water', 'filling');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('fill_container_with_water', 'water'),
    ('soak_flux_to_slaked_lime', 'water_in_container'),
    ('soak_wooden_log_to_tannin', 'water_in_container'),
    ('soak_raw_hide', 'water_in_container'),
    ('soak_scraped_hide', 'water_in_container'),
    ('soak_prepared_hide_in_tannin', 'water_in_container');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('fill_container_with_water', 'water_in_container');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('barrel', 'fill_container_with_water');
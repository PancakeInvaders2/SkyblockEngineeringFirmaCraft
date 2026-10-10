--liquibase formatted sql

--changeset tfc:027-leather-processing

INSERT INTO resource (id, name, category)
VALUES
    ('soaked_hide', 'Soaked Hide', 'animal_products'),
    ('scraped_hide', 'Scraped Hide', 'animal_products'),
    ('prepared_hide', 'Prepared Hide', 'animal_products');

INSERT INTO technology (id, name)
VALUES
    ('scraping', 'Scraping');

INSERT INTO technology_resource_requirement (technology_id, resource_id)
VALUES
    ('scraping', 'knife'),
    ('scraping', 'wooden_log');

INSERT INTO process (id, name, type)
VALUES
    ('soak_raw_hide', 'Soak Raw Hide', 'soaking'),
    ('scrape_soaked_hide', 'Scrape Soaked Hide', 'scraping'),
    ('soak_scraped_hide', 'Soak Scraped Hide', 'soaking'),
    ('soak_prepared_hide_in_tannin', 'Soak Prepared Hide in Tannin', 'soaking');

INSERT INTO process_input (process_id, resource_id)
VALUES
    ('soak_raw_hide', 'raw_hide'),
    ('soak_raw_hide', 'slaked_lime'),
    ('scrape_soaked_hide', 'soaked_hide'),
    ('soak_scraped_hide', 'scraped_hide'),
    ('soak_prepared_hide_in_tannin', 'prepared_hide'),
    ('soak_prepared_hide_in_tannin', 'tannin');

INSERT INTO process_output (process_id, resource_id)
VALUES
    ('soak_raw_hide', 'soaked_hide'),
    ('scrape_soaked_hide', 'scraped_hide'),
    ('soak_scraped_hide', 'prepared_hide'),
    ('soak_prepared_hide_in_tannin', 'leather');

INSERT INTO technology_process_unlock (technology_id, process_id)
VALUES
    ('barrel', 'soak_raw_hide'),
    ('scraping', 'scrape_soaked_hide'),
    ('barrel', 'soak_scraped_hide'),
    ('barrel', 'soak_prepared_hide_in_tannin');
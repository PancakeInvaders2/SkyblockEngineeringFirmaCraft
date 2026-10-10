--liquibase formatted sql

--changeset tfc:013-alloying-technology

INSERT INTO technology (
    id,
    name
) VALUES (
    'alloying',
    'Alloying'
);

INSERT INTO technology_process_unlock (
    technology_id,
    process_id
) VALUES
(
    'alloying',
    'copper_tin_to_bronze'
),
(
    'alloying',
    'copper_zinc_bismuth_to_bismuth_bronze'
),
(
    'alloying',
    'copper_silver_gold_to_black_bronze'
),
(
    'alloying',
    'copper_zinc_to_brass'
),
(
    'alloying',
    'copper_gold_to_rose_gold'
),
(
    'alloying',
    'copper_silver_to_sterling_silver'
);
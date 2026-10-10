--liquibase formatted sql

--changeset tfc:005-scenarios-and-dependency-cleanup

-- ============================================================================
-- Remove recipe quantities from the technology/dependency graph.
-- ============================================================================
-- Dependencies represent relationships, not crafting recipes.
-- A dependency simply means that the resource/technology/process is required.
--
-- Exact quantities belong to a future recipe/crafting model, if we ever need
-- one. They are deliberately not part of the technology progression graph.

ALTER TABLE technology_resource_requirement
    DROP COLUMN quantity;

-- ============================================================================
-- Remove bootstrap state from resources.
-- ============================================================================
-- Whether a resource is available at the beginning is a property of a
-- particular scenario, not of the resource itself.

ALTER TABLE resource
    DROP COLUMN is_bootstrap;

-- ============================================================================
-- Scenarios
-- ============================================================================
-- A scenario defines a particular progression environment built on top of the
-- underlying TFC technology/acquisition graph.
--
-- Examples might eventually include:
--   tfc
--   tfc_skyblock
--   tfc_skyblock_hard
--
-- The scenario itself does not alter the underlying resource or technology
-- definitions. It defines which things are available at the start and,
-- eventually, other scenario-specific progression rules.

CREATE TABLE scenario (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    notes TEXT
);

-- ============================================================================
-- Scenario starting resources
-- ============================================================================
-- Resources listed here are available to the player at the beginning of the
-- scenario.
--
-- This is intentionally separate from resource acquisition. A resource can
-- therefore be both obtainable through the normal progression graph and
-- provided initially by a particular scenario.

CREATE TABLE scenario_resource (
    scenario_id TEXT NOT NULL,
    resource_id TEXT NOT NULL,

    PRIMARY KEY (scenario_id, resource_id),

    FOREIGN KEY (scenario_id)
        REFERENCES scenario(id),

    FOREIGN KEY (resource_id)
        REFERENCES resource(id)
);

--rollback DROP TABLE scenario_resource;
--rollback DROP TABLE scenario;
--rollback ALTER TABLE resource ADD COLUMN is_bootstrap BOOLEAN NOT NULL DEFAULT FALSE;
--rollback ALTER TABLE technology_resource_requirement ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1;

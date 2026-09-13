--liquibase formatted sql


--changeset tfc:043-custom-processes-and-drop-old-functions


ALTER TABLE process
ADD COLUMN is_custom BOOLEAN NOT NULL DEFAULT FALSE;


DROP FUNCTION public.reachable_resources(text);
DROP FUNCTION public.infinite_reachable_resources(text);
DROP FUNCTION public.reachable_resources_from(_text);


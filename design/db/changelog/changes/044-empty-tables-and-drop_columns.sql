--liquibase formatted sql


--changeset tfc:044-empty-tables-and-drop_columns

delete from technology_process_unlock;
delete from technology_resource_requirement;
delete from technology;	


delete from process_output;
delete from process_input;
delete from process;	

delete from scenario_resource;
delete from resource;

	
ALTER TABLE resource
    DROP COLUMN name,
    DROP COLUMN category;
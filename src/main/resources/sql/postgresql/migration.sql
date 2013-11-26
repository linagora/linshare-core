-- Postgresql migration script : 1.1.0 to 1.2.0

BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

ALTER TABLE ldap_attribute ADD COLUMN completion bool DEFAULT true NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN auto_complete_command_on_first_and_last_name text DEFAULT '' NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN auto_complete_command_on_all_attributes text DEFAULT '' NOT NULL;

ALTER TABLE domain_pattern ADD COLUMN search_page_size int DEFAULT 500 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN search_size_limit int DEFAULT 500 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN completion_page_size int DEFAULT 20 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN completion_size_limit int DEFAULT 500 NOT NULL;
ALTER TABLE domain_pattern DROP COLUMN auto_complete_command;



-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.2.0');
COMMIT;

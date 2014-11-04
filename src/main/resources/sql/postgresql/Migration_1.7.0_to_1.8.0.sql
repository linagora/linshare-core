-- Postgresql migration script : 1.7.0 to 1.8.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

ALTER TABLE functionality_boolean DROP COLUMN IF EXISTS id ;
ALTER TABLE functionality_boolean DROP CONSTRAINT IF EXISTS functionality_boolean_pkey;
ALTER TABLE functionality_boolean ADD PRIMARY KEY(functionality_id);
ALTER TABLE functionality_boolean DROP CONSTRAINT IF EXISTS FKfunctional171577;
ALTER TABLE functionality_boolean ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);

ALTER TABLE document
	ADD COLUMN sha1sum varchar(255),
	ADD COLUMN sha256sum varchar(255);

ALTER TABLE entry
	ADD COLUMN meta_data text;

-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.8.0');


COMMIT;

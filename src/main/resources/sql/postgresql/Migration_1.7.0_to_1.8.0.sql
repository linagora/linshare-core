-- Postgresql migration script : 1.7.0 to 1.8.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

ALTER TABLE document
	ADD COLUMN sha1sum varchar(255),
	ADD COLUMN sha256sum varchar(255);

ALTER TABLE entry
	ADD COLUMN meta_data text;

-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.8.0');


COMMIT;

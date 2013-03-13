-- Postgresql migration script : 1.1.0 to 1.2.0

BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

ALTER TABLE allowed_mimetype ADD constraint allowed_mimetype_unique_mimetype UNIQUE(mimetype);
DELETE FROM allowed_mimetype ;

ALTER TABLE document ADD COLUMN check_mime_type bool  DEFAULT false NOT NULL;

-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.2.0');
COMMIT;

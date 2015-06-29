BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

UPDATE mail_content SET language = 1 where id = 80;

-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.10.0');

COMMIT;
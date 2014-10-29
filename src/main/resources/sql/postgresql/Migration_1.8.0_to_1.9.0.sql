-- Postgresql migration script : 1.8.0 to 1.9.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


update mail_content set body = replace(body, 'cliquez sur le lien ou copiez le', 'cliquez sur le lien ou copiez-le')
 WHERE body like '%cliquez sur le lien ou copiez le%';

-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.9.0');


COMMIT;

-- Postgresql migration script : 1.8.0 to 1.9.0

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

update mail_content set body = replace(body, 'cliquez sur le lien ou copiez le', 'cliquez sur le lien ou copiez-le')
 WHERE body like '%cliquez sur le lien ou copiez le%';

-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.9.0');


COMMIT;

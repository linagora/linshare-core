-- MySQL migration script : 1.8.0 to 1.9.0

SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0;
START TRANSACTION;

ALTER TABLE functionality_boolean DROP COLUMN IF EXISTS id ;
ALTER TABLE functionality_boolean DROP CONSTRAINT IF EXISTS functionality_boolean_pkey;
ALTER TABLE functionality_boolean ADD PRIMARY KEY(functionality_id);
ALTER TABLE functionality_boolean DROP CONSTRAINT IF EXISTS FKfunctional171577;
ALTER TABLE functionality_boolean ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);

update mail_content set body = replace(body, 'cliquez sur le lien ou copiez le', 'cliquez sur le lien ou copiez-le')
 WHERE body like '%cliquez sur le lien ou copiez le%';

-- LinShare version
INSERT INTO version (version) VALUES ('1.9.0');

COMMIT;
SET AUTOCOMMIT=1;

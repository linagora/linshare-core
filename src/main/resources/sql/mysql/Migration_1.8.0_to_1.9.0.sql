-- MySQL migration script : 1.8.0 to 1.9.0

SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0;
START TRANSACTION;

update mail_content set body = replace(body, 'cliquez sur le lien ou copiez le', 'cliquez sur le lien ou copiez-le')
 WHERE body like '%cliquez sur le lien ou copiez le%';

-- LinShare version
INSERT INTO version (version) VALUES ('1.9.0');

COMMIT;
SET AUTOCOMMIT=1;

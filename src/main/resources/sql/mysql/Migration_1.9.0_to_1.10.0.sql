SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0;
START TRANSACTION;

UPDATE mail_content SET language = 1 where id = 80;

-- LinShare version
INSERT INTO version (version) VALUES ('1.10.0');

COMMIT;
SET AUTOCOMMIT=1;
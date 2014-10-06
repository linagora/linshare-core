-- MySQL migration script : 1.7.0 to 1.8.0

SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0;
START TRANSACTION;

ALTER TABLE document
	ADD COLUMN sha1sum varchar(255),
	ADD COLUMN sha256sum varchar(255);

ALTER TABLE entry
	ADD COLUMN meta_data text;

-- LinShare version
INSERT INTO version (version) VALUES ('1.8.0');


COMMIT;
SET AUTOCOMMIT=1;

-- MySQL migration script : 1.7.0 to 1.8.0

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

ALTER TABLE document
	ADD COLUMN sha1sum varchar(255),
	ADD COLUMN sha256sum varchar(255);

ALTER TABLE entry
	ADD COLUMN meta_data text;

-- LinShare version
INSERT INTO version (version) VALUES ('1.8.0');

UPDATE mail_content set subject='L’invitation de dépôt: ${subject}, va expirer' WHERE id=71 OR id=72;

COMMIT;
SET AUTOCOMMIT=1;

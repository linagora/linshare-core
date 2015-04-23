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
ALTER TABLE mime_type DROP CONSTRAINT IF EXISTS unicity_type_and_policy;
-- If this command failed, you should delete all mime_type to apply this constraint.
ALTER TABLE mime_type ADD  CONSTRAINT unicity_type_and_policy  UNIQUE (mime_policy_id, mime_type);

-- system account for upload-request:
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id)
	SELECT 3, 7, 'system-account-uploadrequest', now(),now(), 3, 'en', 'en', true, false, 1 FROM account
	WHERE NOT EXISTS (SELECT id FROM account WHERE id=3) LIMIT 1;

-- system account for upload-proposition
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, password, destroyed, domain_id)
	SELECT 4, 4, '89877610-574a-4e79-aeef-5606b96bde35', now(),now(), 5, 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', false, 1 FROM account
	WHERE NOT EXISTS (SELECT id FROM account WHERE id=4) LIMIT 1;

INSERT INTO users(account_id, first_name, last_name, mail, can_upload, comment, restricted, can_create_guest)
	SELECT 4, null, 'Technical Account for upload proposition', 'linshare-noreply@linagora.com', false, '', false, false from users
	WHERE NOT EXISTS (SELECT account_id FROM users WHERE account_id=4) LIMIT 1;


UPDATE mail_content SET body = replace(body, 'cliquez sur le lien ou copiez le', 'cliquez sur le lien ou copiez-le')
 WHERE body LIKE '%cliquez sur le lien ou copiez le%';
UPDATE mail_content SET subject='L’invitation de dépôt: ${subject}, va expirer' WHERE id=71 OR id=72;

ALTER TABLE upload_request ALTER COLUMN expiry_date DROP NOT NULL;
ALTER TABLE upload_request ALTER COLUMN locale SET DEFAULT 'en';
ALTER TABLE upload_request ALTER COLUMN locale SET NOT NULL;
-- Upload request - notification language - Mandatory
UPDATE policy SET status = true, default_status = true, policy = 1, system = true where id=83;

-- schema upgrade - begin
-- step 1 : delete subclass functionality
DELETE FROM functionality_string WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE');

-- step 2 : save policy id in temp table
CREATE TEMP TABLE temptable_1_9 (id int8);
INSERT INTO temptable_1_9 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';
INSERT INTO temptable_1_9 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';
INSERT INTO temptable_1_9 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';

-- step 3 : delete subclass functionality
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';

-- step 4 : delete policies
DELETE FROM policy WHERE id in (SELECT id FROM temptable_1_9);

-- step 5 : create table
CREATE TABLE functionality_enum_lang (
  functionality_id int8 NOT NULL,
  lang_value            varchar(255),
  PRIMARY KEY (functionality_id));
-- step 6 : insert new functionality
-- Functionality : UPLOAD_REQUEST__NOTIFICATION_LANGUAGE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (83, true, true, 1, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (84, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (85, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(38, false, 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE', 83, 84, 85, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_enum_lang(functionality_id, lang_value) VALUES (38, 'en');

ALTER TABLE mime_type ALTER COLUMN extensions TYPE character varying(255);
ALTER TABLE mime_type ALTER COLUMN mime_type TYPE character varying(255);

-- Mail Content : Alternative Subject
ALTER TABLE mail_content ADD COLUMN alternative_subject TYPE character varying(255);
ALTER TABLE mail_content ADD COLUMN enable_as TYPE bool DEFAULT false NOT NULL;
UPDATE mail_content SET alternative_subject = '${actorSubject} from ${actorRepresentation}', enable_as = true, modification_date = now() WHERE language = 0 AND mail_content_type IN (8, 9, 10, 11);
UPDATE mail_content SET alternative_subject = '${actorSubject} de la part de ${actorRepresentation}', enable_as = true, modification_date = now() WHERE language = 1 AND mail_content_type IN (8, 9, 10, 11);

-- Mail Content : new template for UPLOAD_REQUEST_FILE_DELETED_BY_SENDER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (30, '88b90304-e9c9-11e4-b6b4-5404a6202d2c', 1, 0, 29, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request file deleted', E'A user ${actorRepresentation} has deleted a file for upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has deleted a file.<br/>File name: ${fileName}<br/>Deletion date: ${deleteDate}<br/>File size: ${fileSize}<br/><br/>');
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (81, '41ef3560-e9ca-11e4-b6b4-5404a6202d2c', 1, 1, 29, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Suppression de fichier après dépôt', E'${actorRepresentation} a supprimé un fichier suite à une invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a supprimé un fichier.<br/>Nom du fichier: ${fileName}<br/>Date de suppression: ${deleteDate}<br/>Taille du fichier: ${fileSize}<br/><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (30, 1, 0, 30, 29, 'ec270da7-e9cb-11e4-b6b4-5404a6202d2c');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (81, 1, 1, 81, 29, 'd6e18c3b-e9cb-11e4-b6b4-5404a6202d2c');

-- Domain Abstract
ALTER TABLE domain_abstract ADD COLUMN external_mail_locale varchar(255);
UPDATE domain_abstract SET external_mail_locale = 'en';
ALTER TABLE domain_abstract ALTER COLUMN external_mail_locale SET NOT NULL;

-- schema upgrade - end
CREATE TABLE upload_request_entry_url (
  id                 int8 NOT NULL,
  upload_request_entry_id int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  path              varchar(255) NOT NULL,
  password          varchar(255),
  creation_date     timestamp(6) NOT NULL,
  modification_date timestamp(6) NOT NULL,
  expiration_date   timestamp(6) NOT NULL,
  PRIMARY KEY (id));

ALTER TABLE mime_policy ADD COLUMN version INTEGER;
UPDATE mime_policy SET version = 0;
ALTER TABLE mime_policy ALTER COLUMN version SET NOT NULL;






-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.9.0');


COMMIT;

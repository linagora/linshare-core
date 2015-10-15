-- Postgresql migration script : 1.8.0 to 1.9.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.9.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '1.9.0';
	DECLARE version_from VARCHAR := '1.8.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your actual version is: %', version_history_from;
		RAISE NOTICE 'Your databse history is :';
		FOR row IN (SELECT * FROM version ORDER BY id DESC) LOOP
			RAISE INFO '%', row.version;
		END LOOP;
		RAISE NOTICE 'Your database system information is : %', database_info;
		IF (version_from <> version_history_from) THEN
			RAISE WARNING 'You must be in version : % to run this script. You are actually in version: %', version_from, version_history_from;
			IF EXISTS (SELECT * from version where version = version_to) THEN
				RAISE WARNING '%', error;
			END IF;
			RAISE WARNING 'We are about to abort the migration script, all the following instructions will be aborted and transaction will rollback.';
			RAISE INFO 'You should expect the following error : "query has no destination for result data".';
	--		DIRTY: did it to stop the process cause there is no clean way to do it.
	--		Expected error: query has no destination for result data.
			select error;
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_prechecks();

SET client_min_messages = warning;

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;

CREATE OR REPLACE FUNCTION set_func_policy_deleg(functionality_name varchar) RETURNS void AS $$
DECLARE
	row record;
	func_name ALIAS FOR $1;
BEGIN
	FOR row IN (SELECT id FROM functionality WHERE identifier = func_name) LOOP
		WITH ROWS AS (INSERT INTO policy (status, default_status, policy, system) VALUES (true, true, 1, false) returning id)
		UPDATE functionality SET policy_delegation_id = (SELECT id FROM ROWS) WHERE id = row.id;
	END LOOP;
END;
$$ LANGUAGE plpgsql;

-- TABLE FUNCTIONALITY_BOOLEAN
ALTER TABLE functionality_boolean
	DROP COLUMN IF EXISTS id,
	DROP CONSTRAINT IF EXISTS functionality_boolean_pkey,
	ADD PRIMARY KEY(functionality_id),
	DROP CONSTRAINT IF EXISTS FKfunctional171577,
	ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);

-- TABLE MIME_POLICY
ALTER TABLE mime_type DROP CONSTRAINT IF EXISTS unicity_type_and_policy;
ALTER TABLE mime_policy ADD COLUMN version integer;
UPDATE mime_policy SET version = 0;
ALTER TABLE mime_policy ALTER COLUMN version SET NOT NULL;
-- If this command failed, you should delete all mime_type to apply this constraint.
ALTER TABLE mime_type ADD  CONSTRAINT unicity_type_and_policy  UNIQUE (mime_policy_id, mime_type);

-- system account for upload-request:
DROP VIEW IF EXISTS alias_users_list_all, alias_users_list_active, alias_users_list_destroyed, alias_threads_list_all, alias_threads_list_active, alias_threads_list_destroyed;
ALTER TABLE account
	ALTER COLUMN creation_date TYPE timestamp,
	ALTER COLUMN modification_date TYPE timestamp,
	ADD COLUMN purge_step varchar(255) DEFAULT 'IN_USE' NOT NULL;
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
ALTER TABLE functionality_enum_lang ADD CONSTRAINT FKfunctional140416 FOREIGN KEY (functionality_id) REFERENCES functionality (id);

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
ALTER TABLE mail_content ADD COLUMN alternative_subject text;
ALTER TABLE mail_content ADD COLUMN enable_as bool DEFAULT false NOT NULL;
UPDATE mail_content SET alternative_subject = '${actorSubject} from ${actorRepresentation}', enable_as = true, modification_date = now() WHERE language = 0 AND mail_content_type IN (8, 9, 10, 11);
UPDATE mail_content SET alternative_subject = '${actorSubject} de la part de ${actorRepresentation}', enable_as = true, modification_date = now() WHERE language = 1 AND mail_content_type IN (8, 9, 10, 11);

-- Mail Content : new template for UPLOAD_REQUEST_FILE_DELETED_BY_SENDER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (30, '88b90304-e9c9-11e4-b6b4-5404a6202d2c', 1, 0, 29, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload request file deleted', E'A user ${actorRepresentation} has deleted a file for upload request: ${subject}', E'<strong>${firstName} ${lastName}</strong> has deleted a file.<br/>File name: ${fileName}<br/>Deletion date: ${deleteDate}<br/>File size: ${fileSize}<br/><br/>');
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (81, '41ef3560-e9ca-11e4-b6b4-5404a6202d2c', 1, 1, 29, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Suppression de fichier après dépôt', E'${actorRepresentation} a supprimé un fichier suite à une invitation de dépôt: ${subject}', E'<strong>${firstName} ${lastName}</strong> a supprimé un fichier.<br/>Nom du fichier: ${fileName}<br/>Date de suppression: ${deleteDate}<br/>Taille du fichier: ${fileSize}<br/><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (30, 1, 0, 30, 29, 'ec270da7-e9cb-11e4-b6b4-5404a6202d2c');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (81, 1, 1, 81, 29, 'd6e18c3b-e9cb-11e4-b6b4-5404a6202d2c');

-- Domain Abstract
ALTER TABLE domain_abstract ADD COLUMN default_mail_locale varchar(255);
UPDATE domain_abstract SET default_mail_locale = 'en';
ALTER TABLE domain_abstract
	DROP COLUMN messages_configuration_id,
	ADD COLUMN welcome_messages_id int8,
	ALTER COLUMN default_mail_locale SET NOT NULL,
	ALTER COLUMN default_locale SET NOT NULL;

-- UPLOAD REQUEST ENTRY URL
CREATE TABLE upload_request_entry_url (
  id                 int8 NOT NULL,
  upload_request_entry_id int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  path              varchar(255) NOT NULL,
  password          varchar(255),
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  expiry_date   timestamp NOT NULL,
  PRIMARY KEY (id));
ALTER TABLE upload_request_entry_url ADD CONSTRAINT FKupload_req784409 FOREIGN KEY (upload_request_entry_id) REFERENCES upload_request_entry (entry_id);

-- Functionality : UPLOAD_REQUEST_ENTRY_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (104, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (105, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param)
 VALUES(45, false, 'UPLOAD_REQUEST_ENTRY_URL', 104, 105, 1, false);

-- TABLE Functionality : add new functionality 'DOMAIN'
INSERT INTO policy(id, status, default_status, policy, system)
			VALUES (118, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
			VALUES (119, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id)
	VALUES(49, false, 'DOMAIN', 118, 119, 1);

-- Functionality GUESTS_EXPIRATION
UPDATE functionality
	SET identifier = 'GUESTS__EXPIRATION',
		parent_identifier = 'GUESTS',
		param = true
	WHERE id in (SELECT id FROM functionality WHERE identifier = 'ACCOUNT_EXPIRATION');
ALTER TABLE policy ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence');
SELECT set_func_policy_deleg('GUESTS__EXPIRATION');

-- Functionality GUESTS_RESTRICTED
UPDATE functionality
	SET identifier = 'GUESTS__RESTRICTED',
		parent_identifier = 'GUESTS',
		param = true
	WHERE id in (SELECT id FROM functionality WHERE identifier = 'RESTRICTED_GUEST');
SELECT set_func_policy_deleg('GUESTS__RESTRICTED');

UPDATE policy
	SET system = false,
		policy = 1
	WHERE id IN (SELECT policy_activation_id
		FROM functionality
		WHERE identifier = 'GUESTS__RESTRICTED');


-- Functionality : ANONYMOUS_URL (anonymous_url func has been changed from policy (id, status, default_status, policy, system) values (26, false, false, 2, true) to policy (id, status, default_status, policy, system) values (26, true, true, 1, false)
UPDATE policy
	SET status = true,
		default_status = true,
		policy = 1,
		system = false
	WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier = 'ANONYMOUS_URL');
UPDATE functionality
	SET system = false
	WHERE id IN (SELECT id FROM functionality WHERE identifier = 'ANONYMOUS_URL');
SELECT set_func_policy_deleg('ANONYMOUS_URL');
INSERT INTO functionality_boolean(functionality_id, boolean_value) SELECT id, true as bool_value FROM functionality WHERE identifier = 'ANONYMOUS_URL';

-- DROP SECURED_ANONYMOUS_URL FUNCTIONALITY
CREATE TEMP TABLE temp_secured (id int8);
INSERT INTO temp_secured SELECT policy_activation_id FROM functionality WHERE identifier = 'SECURED_ANONYMOUS_URL';
INSERT INTO temp_secured SELECT policy_configuration_id FROM functionality WHERE identifier = 'SECURED_ANONYMOUS_URL';
DELETE FROM functionality WHERE identifier = 'SECURED_ANONYMOUS_URL';
DELETE FROM policy WHERE id IN (SELECT id FROM temp_secured);

-- Functionality : INTERNAL_CAN_UPLOAD
UPDATE functionality
	SET identifier = 'INTERNAL_CAN_UPLOAD'
	WHERE id IN (SELECT id FROM functionality WHERE identifier = 'USER_CAN_UPLOAD');

-- Functionality : CUSTOM_LOGO__LINK
UPDATE functionality
	SET identifier = 'CUSTOM_LOGO__LINK',
		parent_identifier = 'CUSTOM_LOGO',
		param = true
	WHERE id IN (SELECT id FROM functionality WHERE identifier = 'LINK_LOGO');

-- Functionality : TAB_THREAD__CREATE_PERMISSION
UPDATE functionality
	SET identifier = 'TAB_THREAD__CREATE_PERMISSION',
		parent_identifier = 'TAB_THREAD',
		param = true
	WHERE id in (SELECT id FROM functionality WHERE identifier = 'CREATE_THREAD_PERMISSION');

-- Functionality : DOMAIN__NOTIFICATION_URL
UPDATE functionality
	SET identifier = 'DOMAIN__NOTIFICATION_URL',
		parent_identifier = 'DOMAIN',
		param = true
	WHERE id in (SELECT id FROM functionality WHERE identifier = 'NOTIFICATION_URL');
UPDATE policy SET system = true WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'DOMAIN__NOTIFICATION_URL');

-- Functionality : DOMAIN__MAIL
UPDATE functionality
	SET identifier = 'DOMAIN__MAIL',
		parent_identifier = 'DOMAIN',
		param = true
	WHERE id in (SELECT id FROM functionality WHERE identifier = 'DOMAIN_MAIL');

INSERT INTO functionality_boolean(functionality_id, boolean_value) SELECT id, true as bool_value FROM functionality WHERE identifier = 'GUESTS__RESTRICTED';

-- Functionality : add new functionality GUESTS__CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (113, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (114, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (115, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
			VALUES (48, false, 'GUESTS__CAN_UPLOAD', 113, 114, 115, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (48, true);

-- Functionality : SHARE_EXPIRATION
SELECT set_func_policy_deleg('SHARE_EXPIRATION');
INSERT INTO functionality_unit (SELECT functionality_id, integer_value, unit_id FROM functionality_unit_boolean);
DROP TABLE IF EXISTS functionality_unit_boolean;

-- Functionality : SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (120, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (121, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (50, false, 'SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION', 120, 121, 1, 'SHARE_EXPIRATION', true);
-- Notice : boolean_value= false, because we reset share_expiration functionality
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (50, false);

-- Functionality : GUEST__EXPIRATION_ALLOW_PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (123, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (124, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (125, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES(51, false, 'GUESTS__EXPIRATION_ALLOW_PROLONGATION', 123, 124, 125, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (51, true);

-- Functionality : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (126, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (127, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (128, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES(52, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 126, 127, 128, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (52, true);

-- Functionality : UPLOAD_REQUEST_ENTRY_URL__EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (106, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (107, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param)
 VALUES(46, false, 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION', 106, 107, 1, 'UPLOAD_REQUEST_ENTRY_URL', true);
-- time unit : day
INSERT INTO unit(id, unit_type, unit_value) VALUES (12, 0, 0);
-- time : 7 days
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (46, 7, 12);

-- Functionality : UPLOAD_REQUEST_ENTRY_URL__PASSWORD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (109, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param)
 VALUES(47, false, 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD', 109, 110, 1, 'UPLOAD_REQUEST_ENTRY_URL', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (47, false);

-- Functionality : UPLOAD_REQUEST_ENABLE_TEMPLATE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (129, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (130, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param)
 VALUES(53, false, 'UPLOAD_REQUEST_ENABLE_TEMPLATE', 129, 130, 1, false);

-- Mail content : new template SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (31, '01e0ac2e-f7ba-11e4-901b-08002722e7b1', 1, 0, 30, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Share creation acknowledgement', E'[SHARE ACKNOWLEDGEMENT] Shared on ${date}.', E'You just shared ${fileNumber} file(s), on the ${creationDate}, expiring the ${expirationDate}, with :<br/><ul>${recipientNames}</ul><br/>The list of your files is : <ul>${documentNames}</ul><br/>', false);
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (32, '2209b038-e1e7-11e4-8d2d-3b2a506425c0', 1, 0, 31, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Share creation acknowledgement', E'[SHARE ACKNOWLEDGEMENT] ${subject}. Shared on ${date}.', E'You just shared ${fileNumber} file(s), on the ${creationDate}, expiring the ${expirationDate}, with :<br/><ul>${recipientNames}</ul><br/>Your original message was:<br/><i>${message}</i><br/><br/>The list of your files is : <ul>${documentNames}</ul><br/>', false);
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (82, '5f705812-e351-11e4-b752-08002722e7b1', 1, 1, 30, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Accusé de réception de création de partage', E'[Accusé de Réception] Partagé le ${date}.', E'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationDate}, avec : <ul>${recipientNames}</ul><br/>Voici la liste des documents partagés : <ul>${documentNames}</ul>', false);
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (83, 'edd4eba0-f7b9-11e4-95cc-08002722e7b1', 1, 1, 31, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Accusé de réception de création de partage', E'[Accusé de Réception] ${subject}. Partagé le ${date}.', E'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationDate}, avec : <ul>${recipientNames}</ul>Votre message original est le suivant :<br/><i>${message}</i><br/><br/>Voici la liste des documents partagés :<br/><ul>${documentNames}</ul>', false);
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (31, 1, 0, 31, 30, '447217e4-e1ee-11e4-8a45-fb8c68777bdf');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (32, 1, 0, 32, 31, '1837a6f0-e8c7-11e4-b36a-08002722e7b1');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (82, 1, 1, 82, 30, '8f579a8a-e352-11e4-99b3-08002722e7b1');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (83, 1, 1, 83, 31, '2d3a0e80-e8c7-11e4-8349-08002722e7b1');

ALTER TABLE cookie ALTER COLUMN last_use TYPE timestamp;

ALTER TABLE document ALTER COLUMN creation_date TYPE timestamp;

ALTER TABLE document_entry
	ADD COLUMN type character varying(255),
	ADD COLUMN size int8,
	ADD COLUMN sha256sum character varying(255),
	ADD COLUMN has_thumbnail bool,
	ADD COLUMN shared int8;
UPDATE document_entry SET type = document.type FROM document WHERE document_entry.document_id = document.id;
UPDATE document_entry SET size = document.size FROM document WHERE document_entry.document_id = document.id;
UPDATE document_entry SET sha256sum = document.sha256sum FROM document WHERE document_entry.document_id = document.id;
UPDATE document_entry SET has_thumbnail = (SELECT document.thmb_uuid IS NOT NULL) FROM document WHERE document_entry.document_id = document.id;

-- function to count how many times a document has been shared
CREATE OR REPLACE FUNCTION count_share(bigint) RETURNS bigint AS $$
	DECLARE result_from_share bigint;
	DECLARE result_from_anonymous_share bigint;
	DECLARE final_result bigint;
	BEGIN
		SELECT COUNT(*) INTO result_from_share FROM share_entry WHERE document_entry_id = $1;
		SELECT COUNT(*) INTO result_from_anonymous_share FROM anonymous_share_entry WHERE document_entry_id = $1;
		RETURN result_from_share + result_from_anonymous_share;
	END;
$$ LANGUAGE plpgsql;
DO $$
DECLARE row record;
BEGIN
	FOR row IN (SELECT entry_id FROM document_entry) LOOP
		UPDATE document_entry
			SET shared = count_share(row.entry_id)
			WHERE entry_id = row.entry_id;
	END LOOP;
END;
$$;
ALTER TABLE document_entry
	ALTER COLUMN type SET NOT NULL,
	ALTER COLUMN size SET NOT NULL,
	ALTER COLUMN has_thumbnail SET NOT NULL,
	ALTER COLUMN shared SET NOT NULL;

DROP TABLE IF EXISTS mail_subjects, mail_templates, welcome_texts, messages_configuration;

ALTER TABLE entry
	ALTER COLUMN creation_date TYPE timestamp,
	ALTER COLUMN modification_date TYPE timestamp,
	ALTER COLUMN expiration_date TYPE timestamp;

ALTER TABLE functionality_unit
	ALTER COLUMN integer_value SET NOT NULL,
	ALTER COLUMN unit_id SET NOT NULL;

-- LDAP_CONNECTION RENAME TABLE AND ADD NEW COLUMNS
ALTER TABLE ldap_connection RENAME COLUMN ldap_connection_id TO id;
ALTER TABLE ldap_connection RENAME COLUMN identifier TO label;
ALTER TABLE ldap_connection
	ADD COLUMN uuid varchar(255) UNIQUE,
	ADD COLUMN creation_date timestamp,
	ADD COLUMN modification_date timestamp;
UPDATE ldap_connection
	SET creation_date = now(),
		modification_date = now();

-- TODO : function
DO $$
DECLARE row record;
BEGIN
	FOR row IN (SELECT id FROM ldap_connection) LOOP
		UPDATE ldap_connection
			SET uuid = (SELECT uuid_in(md5(random()::text || now()::text)::cstring))
			WHERE id = row.id;
	END LOOP;
END;
$$;
ALTER TABLE ldap_connection
	ALTER COLUMN uuid SET NOT NULL,
	ALTER COLUMN creation_date SET NOT NULL,
	ALTER COLUMN modification_date SET NOT NULL;

ALTER TABLE signature
	ALTER COLUMN creation_date TYPE timestamp,
	ALTER COLUMN modification_date TYPE timestamp,
	ALTER COLUMN cert_not_after TYPE timestamp;

ALTER TABLE ldap_attribute RENAME COLUMN domain_pattern_id TO ldap_pattern_id;
ALTER TABLE ldap_attribute ALTER COLUMN ldap_pattern_id SET NOT NULL;

ALTER TABLE thread_entry
	ADD COLUMN type character varying(255),
	ADD COLUMN size int8,
	ADD COLUMN sha256sum character varying(255),
	ADD COLUMN has_thumbnail boolean;
UPDATE thread_entry
	SET type = document.type FROM document WHERE thread_entry.document_id = document.id;
UPDATE thread_entry
	SET size = document.size FROM document WHERE thread_entry.document_id = document.id;
UPDATE thread_entry
	SET sha256sum = document.sha256sum FROM document WHERE thread_entry.document_id = document.id;
UPDATE thread_entry
	SET has_thumbnail = (SELECT document.thmb_uuid IS NOT NULL) FROM document WHERE thread_entry.document_id = document.id;
ALTER TABLE thread_entry
	ALTER COLUMN type SET NOT NULL,
	ALTER COLUMN size SET NOT NULL,
	ALTER COLUMN has_thumbnail SET NOT NULL;

ALTER TABLE thread_member
	ALTER COLUMN creation_date TYPE timestamp,
	ALTER COLUMN modification_date TYPE timestamp;

ALTER TABLE technical_account_permission
	ALTER COLUMN creation_date TYPE timestamp,
	ALTER COLUMN modification_date TYPE timestamp;

CREATE TABLE contact_provider (
	id                 int8 NOT NULL,
	uuid               varchar(255) NOT NULL UNIQUE,
	provider_type      varchar(255) NOT NULL,
	base_dn            varchar(255),
	creation_date      timestamp NOT NULL,
	modification_date  timestamp NOT NULL,
	domain_abstract_id int8 NOT NULL,
	ldap_pattern_id    int8 NOT NULL,
	ldap_connection_id int8 NOT NULL,
	PRIMARY KEY (id));

-- USER PROVIDER RENAME TABLE AND ADD NEW COLUMNS
ALTER TABLE user_provider_ldap RENAME TO user_provider;
ALTER TABLE user_provider RENAME COLUMN differential_key TO base_dn;
ALTER TABLE user_provider RENAME COLUMN domain_pattern_id TO ldap_pattern_id;
ALTER TABLE user_provider
	ADD COLUMN uuid varchar(255) UNIQUE,
	ADD COLUMN creation_date timestamp,
	ADD COLUMN modification_date timestamp,
	ADD COLUMN provider_type varchar(255),
	ALTER COLUMN base_dn DROP NOT NULL;
UPDATE user_provider
	SET creation_date = now(),
		modification_date = now(),
		provider_type = 'LDAP_PROVIDER';

-- TODO : function
DO $$
DECLARE row record;
BEGIN
	FOR row IN (SELECT id FROM user_provider) LOOP
		UPDATE user_provider
			SET uuid = (SELECT uuid_in(md5(random()::text || now()::text)::cstring))
			WHERE id = row.id;
	END LOOP;
END;
$$;
ALTER TABLE user_provider
	ALTER COLUMN uuid SET NOT NULL,
	ALTER COLUMN creation_date SET NOT NULL,
	ALTER COLUMN modification_date SET NOT NULL,
	ALTER COLUMN provider_type SET NOT NULL;

ALTER TABLE domain_pattern RENAME TO ldap_pattern;
ALTER TABLE ldap_pattern RENAME COLUMN domain_pattern_id TO id;
ALTER TABLE ldap_pattern RENAME COLUMN identifier TO label;
ALTER TABLE ldap_pattern
	ADD COLUMN pattern_type character varying(255),
	ADD COLUMN uuid character varying(255) UNIQUE,
	ALTER COLUMN auth_command DROP NOT NULL,
	ALTER COLUMN search_user_command DROP NOT NULL,
	ALTER COLUMN auth_command DROP NOT NULL,
	ALTER COLUMN auto_complete_command_on_first_and_last_name DROP NOT NULL,
	ALTER COLUMN auto_complete_command_on_all_attributes DROP NOT NULL,
	ALTER COLUMN search_page_size DROP NOT NULL,
	ALTER COLUMN search_size_limit DROP NOT NULL,
	ALTER COLUMN completion_page_size DROP NOT NULL,
	ALTER COLUMN completion_size_limit DROP NOT NULL,
	ADD COLUMN creation_date timestamp,
	ADD COLUMN modification_date timestamp;
UPDATE ldap_pattern
	SET pattern_type = 'USER_LDAP_PATTERN',
		creation_date = now(),
		modification_date = now();

-- TODO : function
DO $$
DECLARE row record;
BEGIN
	FOR row IN (SELECT id FROM ldap_pattern) LOOP
		UPDATE ldap_pattern
			SET uuid = (SELECT uuid_in(md5(random()::text || now()::text)::cstring))
			WHERE id = row.id;
	END LOOP;
END;
$$;

ALTER TABLE ldap_pattern
	ALTER COLUMN pattern_type SET NOT NULL,
	ALTER COLUMN uuid SET NOT NULL,
	ALTER COLUMN creation_date SET NOT NULL,
	ALTER COLUMN modification_date SET NOT NULL;

-- WELCOME MESSAGES CREATE TABLE AND INSERT MESSAGES
CREATE TABLE welcome_messages (
	id                int8 NOT NULL,
	uuid              varchar(255) NOT NULL UNIQUE,
	name              varchar(255) NOT NULL,
	description       text NOT NULL,
	creation_date     timestamp NOT NULL,
	modification_date timestamp NOT NULL,
	domain_id         int8 NOT NULL,
	PRIMARY KEY (id));
CREATE UNIQUE INDEX welcome_messages_uuid ON welcome_messages(uuid);
CREATE TABLE welcome_messages_entry (
	id          int8 NOT NULL,
	lang        varchar(255) NOT NULL,
	value       varchar(255) NOT NULL,
	welcome_messages_id  int8 NOT NULL,
	PRIMARY KEY (id));

-- drop constraints
ALTER TABLE domain_abstract DROP CONSTRAINT IF EXISTS fk449bc2ec4e302e7;
ALTER TABLE user_provider DROP CONSTRAINT IF EXISTS fk409cafb2372a0802;
ALTER TABLE user_provider DROP CONSTRAINT IF EXISTS fk409cafb23834018;
ALTER TABLE ldap_attribute DROP CONSTRAINT IF EXISTS fkldap_attri687153;

-- Adding constraint foreign key on tables
ALTER TABLE welcome_messages_entry ADD CONSTRAINT FKwelcome_me856948 FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD CONSTRAINT use_customisation FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs163989 FOREIGN KEY (user_provider_id) REFERENCES user_provider (id);
ALTER TABLE welcome_messages ADD CONSTRAINT own_welcome_messages FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr166740 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr806790 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr355176 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE ldap_attribute ADD CONSTRAINT FKldap_attri49928 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
-- ALTER TABLE functionality_boolean ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);
ALTER TABLE user_provider ADD CONSTRAINT FKuser_provi1640 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE user_provider ADD CONSTRAINT FKuser_provi813203 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);

-- Insert Welcome messages
INSERT INTO welcome_messages(id, uuid, name, description, creation_date, modification_date, domain_id)
 VALUES (1, '4bc57114-c8c9-11e4-a859-37b5db95d856', 'WelcomeName', 'a Welcome description', now(), now(), 1);

-- Insert welcome messages Entry
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (1, 'en', 'Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (2, 'fr', 'Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (3, 'mq', 'Bienvini an lè Linshare, an solusyon lib de partaj de fichié sékirisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (4, 'vi', 'Chào mừng bạn đến với Linshare, phần mềm nguồn mở chia sẻ file bảo mật.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (5, 'nl', 'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 1);
UPDATE domain_abstract SET welcome_messages_id = 1;

-- upload request entry url
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (29, 'aa74f9b1-471d-4588-9551-4fb985def2c7', 1, 0, 28, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Upload Request Entry Url', E'A user ${actorRepresentation} has uploaded a file you', E'<strong>${firstName} ${lastName}</strong> has uploaded a file &nbsp;:<ul>${documentNames}</ul>To download the file, follow this link &nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>The password to use is&nbsp;: <code>${password}</code><br/><br/>That link will not be available after ${expiryDate}<br/>');
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (80, '6f8096ec-36e7-4ec7-a82f-c37b2eac094e', 1, 0, 28, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Upload Request Entry Url', E'${actorRepresentation} vient de déposer un fichier', E'<strong>${firstName} ${lastName}</strong> a déposé un fichier à votre attention&nbsp;:<ul>${documentNames}</ul>Pour télécharger le fichier, cliquez sur le lien ou copiez-le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/><br/>Ce lien ne sera plus valide après le  ${expiryDate}<br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (29, 1, 0, 29, 28, 'b9c6779b-e8ef-4678-b81c-e37ed79e9ed7');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (80, 1, 1, 80, 28, 'cd65cae1-4946-4675-a356-addd722a5c6c');

-- new template for UPLOAD_REQUEST_ENTRY_URL
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 0, 29, 28, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 1, 80, 28, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);

-- new template UPLOAD REQUEST FILE DELETED
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 0, 30, 29, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 1, 81, 29, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);

-- new template for SHARE_CREATION_ACKNOWLEDGEMENT
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 0, 31, 30, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 1, 82, 30, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);

-- new template for SHARE_CREATION_ACKNOWLEDGEMENT WITH SPECIAL MESSAGE FOR OWNER
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 0, 32, 31, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 1, 83, 31, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);

-- LinShare version
SELECT ls_version();

-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = False;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = True;

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = False;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = True;
COMMIT;

-- MySQL migration script : 1.8.0 to 1.9.0

SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0;
START TRANSACTION;


DROP PROCEDURE IF EXISTS ls_drop_column_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_constraint_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_index_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_primarykey_if_exists;

delimiter '$$'
CREATE PROCEDURE ls_drop_column_if_exists(IN ls_table_name VARCHAR(255), IN ls_column_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE local_ls_column_name varchar(255) DEFAULT ls_column_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema = ls_database_name AND table_name = ls_table_name AND column_name = ls_column_name) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP COLUMN ', local_ls_column_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_constraint_if_exists(IN ls_table_name VARCHAR(255), IN ls_constraint_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE local_ls_constraint_name varchar(255) DEFAULT ls_constraint_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND constraint_name = ls_constraint_name AND constraint_type <> 'UNIQUE' ) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP FOREIGN KEY ', local_ls_constraint_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND constraint_name = ls_constraint_name AND constraint_type = 'UNIQUE' ) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP INDEX ', local_ls_constraint_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_index_if_exists(IN ls_table_name VARCHAR(255), IN ls_index_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE local_ls_index_name varchar(255) DEFAULT ls_index_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT * FROM information_schema.STATISTICS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND INDEX_NAME = ls_index_name) THEN
        SET @SQL := CONCAT('DROP INDEX ', local_ls_index_name, ' ON ', local_ls_table_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_primarykey_if_exists(IN ls_table_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT NULL FROM information_schema.table_constraints WHERE constraint_type = 'PRIMARY KEY' AND table_name = ls_table_name AND table_schema = ls_database_name) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP PRIMARY KEY ', ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE FUNCTION ls_return_last_insert() RETURNS bigint(8)
BEGIN
	DECLARE result bigint(8);
	INSERT INTO policy (status, default_status, policy, system) VALUES (true, true, 1, false);
	SET result = last_insert_id();
	return result;
END$$

delimiter ';'


-- "ALTER TABLE functionality_boolean DROP COLUMN IF EXISTS id" not supported by mysql.
call ls_drop_column_if_exists("functionality_boolean", "id");
call ls_drop_constraint_if_exists("functionality_boolean", "functionality_boolean_pkey");
call ls_drop_primarykey_if_exists("functionality_boolean");
ALTER TABLE functionality_boolean ADD PRIMARY KEY (functionality_id);

-- TABLE functionality_boolean : "DROP CONSTRAINT IF EXISTS FKfunctional171577" not supported by mysql
call ls_drop_constraint_if_exists("functionality_boolean", "FKfunctional171577");
ALTER TABLE functionality_boolean ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);


DROP TABLE IF EXISTS technical_account_permission_account;
DROP TABLE IF EXISTS statistic_event;

-- "drop index account_ls_uuid ON account IF EXISTS" not supported by mysql
call ls_drop_index_if_exists("account", "account_ls_uuid");

-- "drop index account_account_type ON account IF EXISTS" not supported by mysql
call ls_drop_index_if_exists("account", "account_account_type");

ALTER TABLE upload_request CHANGE locale locale varchar(255) NOT NULL DEFAULT "en";
ALTER TABLE upload_request CHANGE expiry_date expiry_date datetime;

ALTER TABLE upload_request_history CHANGE locale locale varchar(255) NOT NULL DEFAULT "en";
ALTER TABLE upload_request_history CHANGE expiry_date expiry_date datetime;

call ls_drop_constraint_if_exists("upload_request_template", "FKupload_req618325");
call ls_drop_index_if_exists("upload_request_template", "FKupload_req618325");
ALTER TABLE upload_request_template ADD INDEX FKupload_req618325 (account_id), ADD CONSTRAINT FKupload_req618325 FOREIGN KEY (account_id) REFERENCES account (id);


call ls_drop_constraint_if_exists("upload_proposition", "FKupload_pro226633");
call ls_drop_index_if_exists("upload_proposition", "FKupload_pro226633");
ALTER TABLE upload_proposition ADD INDEX FKupload_pro226633 (domain_abstract_id), ADD CONSTRAINT FKupload_pro226633 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);

-- TABLE MIME_TYPE
call ls_drop_constraint_if_exists("mime_type", "unicity_type_and_policy");
ALTER TABLE mime_type MODIFY mime_type varchar(255) NOT NULL;
ALTER TABLE mime_type MODIFY extensions varchar(255) NOT NULL;
ALTER TABLE mime_type ADD CONSTRAINT unicity_type_and_policy UNIQUE (mime_policy_id, mime_type);
-- If this command failed, you should delete all mime_type to apply this constraint.

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
UPDATE users SET mail ='linshare-noreply@linagora.com' WHERE account_id=4 and mail = 'bart.simpson@int1.linshare.dev';


UPDATE mail_content SET body = replace(body, 'cliquez sur le lien ou copiez le', 'cliquez sur le lien ou copiez-le')
 WHERE body LIKE '%cliquez sur le lien ou copiez le%';



ALTER TABLE upload_request MODIFY activation_date datetime NOT NULL;
ALTER TABLE upload_request MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_request MODIFY modification_date datetime NOT NULL;
ALTER TABLE upload_request MODIFY notification_date datetime;

ALTER TABLE upload_request_history MODIFY activation_date datetime NOT NULL;
ALTER TABLE upload_request_history MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_request_history MODIFY modification_date datetime NOT NULL;
ALTER TABLE upload_request_history MODIFY notification_date datetime;

ALTER TABLE upload_request_url MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_request_url MODIFY modification_date datetime NOT NULL;

ALTER TABLE upload_request_group MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_request_group MODIFY modification_date datetime NOT NULL;

ALTER TABLE upload_request_template MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_request_template MODIFY modification_date datetime NOT NULL;

ALTER TABLE upload_proposition MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_proposition MODIFY modification_date datetime NOT NULL;

ALTER TABLE mime_policy ADD COLUMN version integer;
UPDATE mime_policy SET version = 0;
ALTER TABLE mime_policy MODIFY version integer NOT NULL;
ALTER TABLE mime_policy MODIFY creation_date datetime NOT NULL;
ALTER TABLE mime_policy MODIFY modification_date datetime NOT NULL;

ALTER TABLE mime_type MODIFY creation_date datetime NOT NULL;
ALTER TABLE mime_type MODIFY modification_date datetime NOT NULL;

ALTER TABLE account MODIFY creation_date datetime NOT NULL;
ALTER TABLE account MODIFY modification_date datetime NOT NULL;
ALTER TABLE account ADD COLUMN purge_step varchar(255) DEFAULT 'IN_USE' NOT NULL;

ALTER TABLE document MODIFY creation_date datetime NOT NULL;

ALTER TABLE entry MODIFY creation_date datetime NOT NULL;
ALTER TABLE entry MODIFY modification_date datetime NOT NULL;
ALTER TABLE entry MODIFY expiration_date datetime;

ALTER TABLE log_entry MODIFY action_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE log_entry MODIFY expiration_date datetime;

ALTER TABLE signature MODIFY creation_date datetime NOT NULL;
ALTER TABLE signature MODIFY modification_date datetime NOT NULL;
ALTER TABLE signature MODIFY cert_not_after datetime  NULL;

ALTER TABLE thread_member MODIFY creation_date datetime NOT NULL;
ALTER TABLE thread_member MODIFY modification_date datetime NOT NULL;

ALTER TABLE users MODIFY not_after datetime NULL;
ALTER TABLE users MODIFY not_before datetime NULL;
ALTER TABLE users MODIFY expiration_date datetime NULL;

ALTER TABLE mail_notification MODIFY creation_date datetime NOT NULL;
ALTER TABLE mail_notification MODIFY modification_date datetime NOT NULL;

ALTER TABLE mail_config MODIFY creation_date datetime NOT NULL;
ALTER TABLE mail_config MODIFY modification_date datetime NOT NULL;

ALTER TABLE mail_footer MODIFY creation_date datetime NOT NULL;
ALTER TABLE mail_footer MODIFY modification_date datetime NOT NULL;

ALTER TABLE mail_content MODIFY creation_date datetime NOT NULL;
ALTER TABLE mail_content MODIFY modification_date datetime NOT NULL;

ALTER TABLE mail_layout MODIFY creation_date datetime NOT NULL;
ALTER TABLE mail_layout MODIFY modification_date datetime NOT NULL;

ALTER TABLE upload_proposition_filter MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_proposition_filter MODIFY modification_date datetime NOT NULL;

ALTER TABLE upload_proposition_action MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_proposition_action MODIFY modification_date datetime NOT NULL;

ALTER TABLE upload_proposition_rule MODIFY creation_date datetime NOT NULL;
ALTER TABLE upload_proposition_rule MODIFY modification_date datetime NOT NULL;

ALTER TABLE mailing_list MODIFY creation_date datetime NOT NULL;
ALTER TABLE mailing_list MODIFY modification_date datetime NOT NULL;

ALTER TABLE mailing_list_contact MODIFY creation_date datetime NOT NULL;
ALTER TABLE mailing_list_contact MODIFY modification_date datetime NOT NULL;


-- schema upgrade - begin
-- step 1 : delete subclass functionality
DELETE FROM functionality_string WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE');

-- step 2 : save policy id in temp table
CREATE TEMPORARY TABLE temptable_1_9 (id bigint(8));
INSERT INTO temptable_1_9 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';
INSERT INTO temptable_1_9 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';
INSERT INTO temptable_1_9 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';

-- step 3 : delete subclass functionality
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE';

-- step 4 : delete policies
DELETE FROM policy WHERE id in (SELECT id FROM temptable_1_9);

-- step 5 : create table
CREATE TABLE functionality_enum_lang (
  functionality_id bigint(8) NOT NULL,
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

-- Functionality : GUEST__EXPIRATION_ALLOW_PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (123, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (124, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (125, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(51, false, 'GUESTS__EXPIRATION_ALLOW_PROLONGATION', 123, 124, 125, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (51, true);

-- Functionality : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (126, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (127, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (128, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES(52, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 126, 127, 128, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (52, true);

-- Functionality : UPLOAD_REQUEST_ENABLE_TEMPLATE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (129, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (130, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param)
 VALUES(53, false, 'UPLOAD_REQUEST_ENABLE_TEMPLATE', 129, 130, 1, false);

ALTER TABLE mime_type CHANGE mime_type mime_type varchar(255) NOT NULL;
ALTER TABLE mime_type CHANGE extensions extensions varchar(255) NOT NULL;
-- schema upgrade - end

-- Mail Content : Alternative Subject
ALTER TABLE mail_content ADD COLUMN alternative_subject text;
ALTER TABLE mail_content ADD COLUMN enable_as bool DEFAULT false NOT NULL;
UPDATE mail_content SET alternative_subject = '${actorSubject} from ${actorRepresentation}', enable_as = true, modification_date = now() WHERE language = 0 AND mail_content_type IN (8, 9, 10, 11);
UPDATE mail_content SET alternative_subject = '${actorSubject} de la part de ${actorRepresentation}', enable_as = true, modification_date = now() WHERE language = 1 AND mail_content_type IN (8, 9, 10, 11);

-- Mail Content : new template for UPLOAD_REQUEST_FILE_DELETED_BY_SENDER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (30, '88b90304-e9c9-11e4-b6b4-5404a6202d2c', 1, 0, 29, true, false, now(), now(), 'Hello ${firstName} ${lastName},<br/><br/>', 'Upload request file deleted', 'A user ${actorRepresentation} has deleted a file for upload request: ${subject}', '<strong>${firstName} ${lastName}</strong> has deleted a file.<br/>File name: ${fileName}<br/>Deletion date: ${deleteDate}<br/>File size: ${fileSize}<br/><br/>');
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (81, '41ef3560-e9ca-11e4-b6b4-5404a6202d2c', 1, 1, 29, true, false, now(), now(), 'Bonjour ${firstName} ${lastName},<br/><br/>', 'Suppression de fichier après dépôt', '${actorRepresentation} a supprimé un fichier suite à une invitation de dépôt: ${subject}', '<strong>${firstName} ${lastName}</strong> a supprimé un fichier.<br/>Nom du fichier: ${fileName}<br/>Date de suppression: ${deleteDate}<br/>Taille du fichier: ${fileSize}<br/><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (30, 1, 0, 30, 29, 'ec270da7-e9cb-11e4-b6b4-5404a6202d2c');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (81, 1, 1, 81, 29, 'd6e18c3b-e9cb-11e4-b6b4-5404a6202d2c');

-- Mail content : new template SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (31, '01e0ac2e-f7ba-11e4-901b-08002722e7b1', 1, 0, 30, true, false, now(), now(), 'Hello ${firstName} ${lastName},<br/><br/>', 'Share creation acknowledgement', '[SHARE ACKNOWLEDGEMENT] Shared on ${date}.', 'You just shared ${fileNumber} file(s), on the ${creationDate}, expiring the ${expirationDate}, with :<br/><ul>${recipientNames}</ul><br/>The list of your files is : <ul>${documentNames}</ul><br/>', false);
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (32, '2209b038-e1e7-11e4-8d2d-3b2a506425c0', 1, 0, 31, true, false, now(), now(), 'Hello ${firstName} ${lastName},<br/><br/>', 'Share creation acknowledgement', '[SHARE ACKNOWLEDGEMENT] ${subject}. Shared on ${date}.', 'You just shared ${fileNumber} file(s), on the ${creationDate}, expiring the ${expirationDate}, with :<br/><ul>${recipientNames}</ul><br/>Your original message was:<br/><i>${message}</i><br/><br/>The list of your files is : <ul>${documentNames}</ul><br/>', false);
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (82, '5f705812-e351-11e4-b752-08002722e7b1', 1, 1, 30, true, false, now(), now(), 'Bonjour ${firstName} ${lastName},<br/><br/>', 'Accusé de réception de création de partag', '[Accusé de Réception]  Partagé le ${date}.', 'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationdate}, avec : <ul>${recipientNames}</ul><br/>Voici la liste des documents partagés : <ul>${documentNames}</ul>', false);
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body, enable_as) VALUES  (83, 'edd4eba0-f7b9-11e4-95cc-08002722e7b1', 1, 1, 31, true, false, now(), now(), 'Bonjour ${firstName} ${lastName},<br/><br/>', 'Accusé de réception de création de partag', '[Accusé de Réception] ${subject}. Partagé le ${date}.', 'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationdate}, avec : <ul>${recipientNames}</ul>Votre message original est le suivant :<br/><i>${message}</i><br/><br/>Voici la liste des documents partagés :<br/><ul>${documentNames}</ul>', false);
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (31, 1, 0, 31, 30, '447217e4-e1ee-11e4-8a45-fb8c68777bdf');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (32, 1, 0, 32, 31, '1837a6f0-e8c7-11e4-b36a-08002722e7b1');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (82, 1, 1, 82, 30, '8f579a8a-e352-11e4-99b3-08002722e7b1');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (83, 1, 1, 83, 31, '2d3a0e80-e8c7-11e4-8349-08002722e7b1');

-- Domain Abstract
ALTER TABLE domain_abstract ADD COLUMN default_mail_locale varchar(255);
UPDATE domain_abstract SET default_mail_locale = 'en';
ALTER TABLE domain_abstract MODIFY default_mail_locale varchar(255) NOT NULL;
call ls_drop_constraint_if_exists("domain_abstract", "fk449bc2ec126ff4f2");
call ls_drop_column_if_exists("domain_abstract", "messages_configuration_id");
-- ALTER TABLE domain_abstract DROP COLUMN messages_configuration_id;
ALTER TABLE domain_abstract ADD COLUMN welcome_messages_id bigint(8);
ALTER TABLE domain_abstract MODIFY default_locale varchar(255) NOT NULL;

-- UPLOAD REQUEST ENTRY URL
CREATE TABLE upload_request_entry_url (
  id                bigint(8) NOT NULL,
  upload_request_entry_id bigint(8) NOT NULL,
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
CREATE TEMPORARY TABLE temp_1000 (id bigint(8));
INSERT INTO temp_1000 SELECT id FROM functionality WHERE identifier = 'ACCOUNT_EXPIRATION';
UPDATE functionality
	SET identifier = 'GUESTS__EXPIRATION',
		parent_identifier = 'GUESTS',
		param = true
	WHERE id in (SELECT id FROM temp_1000);


-- Functionality GUESTS_RESTRICTED
CREATE TEMPORARY TABLE temp_1001 (id bigint(8));
INSERT INTO temp_1001 SELECT id FROM functionality WHERE identifier = 'RESTRICTED_GUEST';
UPDATE functionality
	SET identifier = 'GUESTS__RESTRICTED',
		parent_identifier = 'GUESTS',
		param = true
	WHERE id in (SELECT id FROM temp_1001);

UPDATE policy
    SET system = false,
        policy = 1
    WHERE id IN (SELECT policy_activation_id
        FROM functionality
        WHERE identifier = 'GUESTS__RESTRICTED');

-- Functionality : ANONYMOUS_URL
UPDATE policy
	SET status = true,
		default_status = true,
		policy = 1,
		system = false
	WHERE id = 26;
CREATE TEMPORARY TABLE temp_1002 (id bigint(8));
INSERT INTO temp_1002 SELECT id FROM functionality WHERE identifier = 'ANONYMOUS_URL';
UPDATE functionality
	SET system = false
	WHERE id IN (SELECT id FROM temp_1002);

INSERT INTO functionality_boolean(functionality_id, boolean_value) SELECT id, true as bool_value FROM functionality WHERE identifier = 'ANONYMOUS_URL';

-- DROP SECURED_ANONYMOUS_URL FUNCTIONALITY
CREATE TEMPORARY TABLE temp_1010 (id bigint(8));
INSERT INTO temp_1010 SELECT policy_activation_id FROM functionality WHERE identifier = 'SECURED_ANONYMOUS_URL';
INSERT INTO temp_1010 SELECT policy_configuration_id FROM functionality WHERE identifier = 'SECURED_ANONYMOUS_URL';
DELETE FROM functionality WHERE identifier = 'SECURED_ANONYMOUS_URL';
DELETE FROM policy WHERE id IN (SELECT id FROM temp_1010);

-- Functionality : INTERNAL_CAN_UPLOAD
CREATE TEMPORARY TABLE temp_1003 (id bigint(8));
INSERT INTO temp_1003 SELECT id FROM functionality WHERE identifier = 'USER_CAN_UPLOAD';
UPDATE functionality
	SET identifier = 'INTERNAL_CAN_UPLOAD'
	WHERE id IN (SELECT id FROM temp_1003);
-- Functionality : CUSTOM_LOGO__LINK
CREATE TEMPORARY TABLE temp_1004 (id bigint(8));
INSERT INTO temp_1004 SELECT id FROM functionality WHERE identifier = 'LINK_LOGO';
UPDATE functionality
	SET identifier = 'CUSTOM_LOGO__LINK',
		parent_identifier = 'CUSTOM_LOGO',
		param = true
	WHERE id IN (SELECT id FROM temp_1004);

-- Functionality : TAB_THREAD__CREATE_PERMISSION
CREATE TEMPORARY TABLE temp_1005 (id bigint(8));
INSERT INTO temp_1005 SELECT id FROM functionality WHERE identifier = 'CREATE_THREAD_PERMISSION';
UPDATE functionality
	SET identifier = 'TAB_THREAD__CREATE_PERMISSION',
		parent_identifier = 'TAB_THREAD',
		param = true
	WHERE id in (SELECT id FROM temp_1005);

-- Functionality : DOMAIN__NOTIFICATION_URL
CREATE TEMPORARY TABLE temp_1006 (id bigint(8));
INSERT INTO temp_1006 SELECT id FROM functionality WHERE identifier = 'NOTIFICATION_URL';
UPDATE functionality
	SET identifier = 'DOMAIN__NOTIFICATION_URL',
		parent_identifier = 'DOMAIN',
		param = true
	WHERE id in (SELECT id FROM temp_1006);
UPDATE policy SET system = true WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'DOMAIN__NOTIFICATION_URL');

-- Functionality : DOMAIN__MAIL
CREATE TEMPORARY TABLE temp_1007 (id bigint(8));
INSERT INTO temp_1007 SELECT id FROM functionality WHERE identifier = 'DOMAIN_MAIL';
UPDATE functionality
	SET identifier = 'DOMAIN__MAIL',
		parent_identifier = 'DOMAIN',
		param = true
	WHERE id in (SELECT id FROM temp_1007);

INSERT INTO functionality_boolean(functionality_id, boolean_value) SELECT id, true as bool_value FROM functionality WHERE identifier = 'GUESTS__RESTRICTED';

-- Functionality : add new functionality GUESTS__CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (113, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (114, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (115, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
			VALUES (48, false, 'GUESTS__CAN_UPLOAD', 113, 114, 115, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (48, true);

-- Functionality : SHARE_EXPIRATION
INSERT INTO functionality_unit (SELECT functionality_id, integer_value, unit_id FROM functionality_unit_boolean);

-- Functionality : SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (120, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (121, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (50, false, 'SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION', 120, 121, 1, 'SHARE_EXPIRATION', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (50, false);

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

-- insert into policy and update policy_delegation_id column in functionality table.
UPDATE functionality SET policy_delegation_id = (SELECT ls_return_last_insert()) WHERE identifier = 'GUESTS__EXPIRATION';
UPDATE functionality SET policy_delegation_id = (SELECT ls_return_last_insert()) WHERE identifier = 'GUESTS__RESTRICTED';
UPDATE functionality SET policy_delegation_id = (SELECT ls_return_last_insert()) WHERE identifier = 'ANONYMOUS_URL';
UPDATE functionality SET policy_delegation_id = (SELECT ls_return_last_insert()) WHERE identifier = 'SHARE_EXPIRATION';

ALTER TABLE cookie MODIFY last_use timestamp;

ALTER TABLE document MODIFY creation_date timestamp;

ALTER TABLE document_entry
	ADD COLUMN type varchar(255),
	ADD COLUMN size bigint(8),
	ADD COLUMN sha256sum varchar(255),
	ADD COLUMN has_thumbnail bool,
	ADD COLUMN shared bigint(8);
UPDATE document_entry, document SET document_entry.type = document.type WHERE document_entry.document_id = document.id;
UPDATE document_entry, document SET document_entry.size = document.size WHERE document_entry.document_id = document.id;
UPDATE document_entry, document SET document_entry.sha256sum = document.sha256sum WHERE document_entry.document_id = document.id;
UPDATE document_entry, document SET document_entry.has_thumbnail = (SELECT document.thmb_uuid IS NOT NULL) WHERE document_entry.document_id = document.id;
UPDATE document_entry
	SET shared = (SELECT COUNT(document_entry_id)
	FROM (SELECT entry_id, document_entry_id FROM share_entry UNION ALL SELECT entry_id, document_entry_id FROM anonymous_share_entry) as all_shared 
	WHERE all_shared.document_entry_id = document_entry.document_id);

ALTER TABLE document_entry
	MODIFY type varchar(255) NOT NULL,
	MODIFY size bigint(8) NOT NULL,
	MODIFY has_thumbnail bool NOT NULL,
	MODIFY shared bigint(8) NOT NULL;

DROP TABLE IF EXISTS mail_subjects, mail_templates, welcome_texts, messages_configuration, functionality_unit_boolean;

ALTER TABLE entry
	MODIFY creation_date timestamp,
	MODIFY modification_date timestamp,
	MODIFY expiration_date timestamp;

ALTER TABLE functionality_unit
	MODIFY integer_value int(4) NOT NULL,
	MODIFY unit_id bigint(8) NOT NULL;

-- LDAP_CONNECTION RENAME TABLE AND ADD NEW COLUMNS
call ls_drop_constraint_if_exists("user_provider_ldap", "fk409cafb23834018");
ALTER TABLE ldap_connection CHANGE ldap_connection_id id bigint(8);

ALTER TABLE ldap_connection CHANGE identifier label varchar(255);
ALTER TABLE ldap_connection
	ADD COLUMN uuid varchar(255) UNIQUE,
	ADD COLUMN creation_date datetime,
	ADD COLUMN modification_date datetime;
UPDATE ldap_connection
	SET uuid = UUID(),
		creation_date = now(),
		modification_date = now();
ALTER TABLE ldap_connection
	MODIFY uuid varchar(255) NOT NULL,
	MODIFY creation_date datetime NOT NULL,
	MODIFY modification_date datetime NOT NULL;

ALTER TABLE signature
	MODIFY creation_date timestamp,
	MODIFY modification_date timestamp,
	MODIFY cert_not_after timestamp;

call ls_drop_constraint_if_exists("ldap_attribute", "FKldap_attri687153");
ALTER TABLE ldap_attribute CHANGE domain_pattern_id ldap_pattern_id BIGINT(8) NOT NULL;

-- ALTER TABLE ldap_attribute ALTER COLUMN ldap_pattern_id SET NOT NULL;

ALTER TABLE thread_entry
	ADD COLUMN type varchar(255),
	ADD COLUMN size bigint(8),
	ADD COLUMN sha256sum varchar(255),
	ADD COLUMN has_thumbnail boolean;
UPDATE thread_entry, document
	SET thread_entry.type = document.type WHERE thread_entry.document_id = document.id;
UPDATE thread_entry, document
	SET thread_entry.size = document.size WHERE thread_entry.document_id = document.id;
UPDATE thread_entry, document
	SET thread_entry.sha256sum = document.sha256sum WHERE thread_entry.document_id = document.id;
UPDATE thread_entry, document
	SET thread_entry.has_thumbnail = (SELECT document.thmb_uuid IS NOT NULL) WHERE thread_entry.document_id = document.id;
ALTER TABLE thread_entry
	MODIFY type varchar(255) NOT NULL,
	MODIFY size bigint(8) NOT NULL,
	MODIFY has_thumbnail boolean NOT NULL;

ALTER TABLE thread_member
	MODIFY creation_date timestamp,
	MODIFY modification_date timestamp;

ALTER TABLE technical_account_permission
	MODIFY creation_date timestamp,
	MODIFY modification_date timestamp;

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
RENAME TABLE user_provider_ldap TO user_provider;
ALTER TABLE user_provider CHANGE differential_key base_dn varchar(255);
call ls_drop_constraint_if_exists("user_provider", "fk409cafb2372a0802");
ALTER TABLE user_provider CHANGE domain_pattern_id ldap_pattern_id bigint(8);

ALTER TABLE user_provider
	ADD COLUMN uuid varchar(255) UNIQUE,
	ADD COLUMN creation_date timestamp,
	ADD COLUMN modification_date timestamp,
	ADD COLUMN provider_type varchar(255),
	MODIFY base_dn varchar(255); -- DROP NOT NULL;
UPDATE user_provider
	SET uuid = UUID(),
		creation_date = now(),
		modification_date = now(),
		provider_type = 'LDAP_PROVIDER';
ALTER TABLE user_provider
	MODIFY uuid varchar(255) NOT NULL,
	MODIFY creation_date timestamp NOT NULL,
	MODIFY modification_date timestamp NOT NULL,
	MODIFY provider_type varchar(255) NOT NULL;

RENAME TABLE domain_pattern TO ldap_pattern;
ALTER TABLE ldap_pattern CHANGE domain_pattern_id id bigint(8);
ALTER TABLE ldap_pattern CHANGE identifier label varchar(255);
ALTER TABLE ldap_pattern
	ADD COLUMN pattern_type varchar(255),
	ADD COLUMN uuid varchar(255) UNIQUE,
	MODIFY auth_command text,
	MODIFY search_user_command text,
	MODIFY auto_complete_command_on_first_and_last_name text,
	MODIFY auto_complete_command_on_all_attributes text,
	MODIFY search_page_size int(4),
	MODIFY search_size_limit int(4),
	MODIFY completion_page_size int(4),
	MODIFY completion_size_limit int(4),
	ADD COLUMN creation_date timestamp,
	ADD COLUMN modification_date timestamp;
UPDATE ldap_pattern
	SET uuid = UUID(),
		pattern_type = 'USER_LDAP_PATTERN',
		creation_date = now(),
		modification_date = now();
ALTER TABLE ldap_pattern
	MODIFY pattern_type varchar(255) NOT NULL,
	MODIFY uuid varchar(255) NOT NULL,
	MODIFY creation_date timestamp NOT NULL,
	MODIFY modification_date timestamp NOT NULL;

-- WELCOME MESSAGES CREATE TABLE AND INSERT MESSAGES
CREATE TABLE welcome_messages (
	id                int8 NOT NULL,
	uuid              varchar(255) NOT NULL,
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

-- Drop constraint foreign key
call ls_drop_constraint_if_exists("domain_abstract", "fk449bc2ec4e302e7");
call ls_drop_constraint_if_exists("user_provider", "fk409cafb23834018");

  -- Adding constraint foreign key on tables
ALTER TABLE welcome_messages_entry ADD CONSTRAINT FKwelcome_me856948 FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD CONSTRAINT use_customisation FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs163989 FOREIGN KEY (user_provider_id) REFERENCES user_provider (id);
ALTER TABLE welcome_messages ADD CONSTRAINT own_welcome_messages FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr166740 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr806790 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr355176 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE ldap_attribute ADD CONSTRAINT FKldap_attri49928 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
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
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (29, 'aa74f9b1-471d-4588-9551-4fb985def2c7', 1, 0, 28, true, false, now(), now(), 'Hello ${firstName} ${lastName},<br/><br/>', 'Upload Request Entry Url', 'A user ${actorRepresentation} has uploaded a file you', '<strong>${firstName} ${lastName}</strong> has uploaded a file &nbsp;:<ul>${documentNames}</ul>To download the file, follow this link &nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>The password to use is&nbsp;: <code>${password}</code><br/><br/>That link will not be available after ${expiryDate}<br/>');
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (80, '6f8096ec-36e7-4ec7-a82f-c37b2eac094e', 1, 0, 28, true, false, now(), now(), 'Bonjour ${firstName} ${lastName},<br/><br/>', 'Upload Request Entry Url', '${actorRepresentation} vient de déposer un fichier', '<strong>${firstName} ${lastName}</strong> a déposé un fichier à votre attention&nbsp;:<ul>${documentNames}</ul>Pour télécharger le fichier, cliquez sur le lien ou copiez-le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/><br/>Ce lien ne sera plus valide après le  ${expiryDate}<br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (29, 1, 0, 29, 28, 'b9c6779b-e8ef-4678-b81c-e37ed79e9ed7');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (80, 1, 1, 80, 28, 'cd65cae1-4946-4675-a356-addd722a5c6c');

-- new template for UPLOAD REQUEST ENTRY URL
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 0, 29, 28, UUID() FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 1, 80, 28, UUID() FROM mail_config AS config WHERE id <> 1);
-- new template UPLOAD REQUEST FILE DELETED
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 0, 30, 29, UUID() FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 1, 81, 29, UUID() FROM mail_config AS config WHERE id <> 1);
-- new template for SHARE_CREATION_ACKNOWLEDGEMENT
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 0, 31, 30, UUID() FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 1, 82, 30, UUID() FROM mail_config AS config WHERE id <> 1);
-- new template for SHARE_CREATION_ACKNOWLEDGEMENT WITH SPECIAL MESSAGE FOR OWNER
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 0, 32, 31, UUID() FROM mail_config AS config WHERE id <> 1);
INSERT INTO mail_content_lang (mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT config.id, 1, 83, 31, UUID() FROM mail_config AS config WHERE id <> 1);

-- LinShare version
INSERT INTO version (version) VALUES ('1.9.0');

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
SET AUTOCOMMIT=1;

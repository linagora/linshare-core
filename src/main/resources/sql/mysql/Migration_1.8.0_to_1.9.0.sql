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
delimiter ';'


-- "ALTER TABLE functionality_boolean DROP COLUMN IF EXISTS id" not supported by mysql.
call ls_drop_column_if_exists("functionality_boolean", "id");
call ls_drop_constraint_if_exists("functionality_boolean", "functionality_boolean_pkey");
call ls_drop_primarykey_if_exists("functionality_boolean");
ALTER TABLE functionality_boolean ADD PRIMARY KEY (functionality_id);

-- "ALTER TABLE functionality_boolean DROP CONSTRAINT IF EXISTS FKfunctional171577" not supported by mysql
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

-- Upload request - notification language - Mandatory
UPDATE policy SET status = true, default_status = true, policy = 1, system = true where id=83;

call ls_drop_constraint_if_exists("upload_request_template", "FKupload_req618325");
call ls_drop_index_if_exists("upload_request_template", "FKupload_req618325");
ALTER TABLE upload_request_template ADD INDEX FKupload_req618325 (account_id), ADD CONSTRAINT FKupload_req618325 FOREIGN KEY (account_id) REFERENCES account (id);


call ls_drop_constraint_if_exists("upload_proposition", "FKupload_pro226633");
call ls_drop_index_if_exists("upload_proposition", "FKupload_pro226633");
ALTER TABLE upload_proposition ADD INDEX FKupload_pro226633 (domain_abstract_id), ADD CONSTRAINT FKupload_pro226633 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);

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
UPDATE users set mail ='linshare-noreply@linagora.com' where account_id=4 and mail = 'bart.simpson@int1.linshare.dev';


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

ALTER TABLE mime_policy MODIFY creation_date datetime NOT NULL;
ALTER TABLE mime_policy MODIFY modification_date datetime NOT NULL;

ALTER TABLE mime_type MODIFY creation_date datetime NOT NULL;
ALTER TABLE mime_type MODIFY modification_date datetime NOT NULL;

ALTER TABLE account MODIFY creation_date datetime NOT NULL;
ALTER TABLE account MODIFY modification_date datetime NOT NULL;

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

ALTER TABLE mime_type CHANGE mime_type mime_type varchar(255) NOT NULL;
ALTER TABLE mime_type CHANGE extensions extensions varchar(255) NOT NULL;
-- schema upgrade - end

-- Mail Content : Alternative Subject
ALTER TABLE mail_content ADD COLUMN alternative_subject varchar(255);
ALTER TABLE mail_content ADD COLUMN enable_as bool DEFAULT false NOT NULL;
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

-- LinShare version
INSERT INTO version (version) VALUES ('1.9.0');

COMMIT;
SET AUTOCOMMIT=1;

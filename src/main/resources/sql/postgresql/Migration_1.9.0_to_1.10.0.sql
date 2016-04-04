-- Postgresql migration script : 1.9.0 to 1.10.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.10.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '1.10.0';
	DECLARE version_from VARCHAR := '1.9.0';
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

CREATE OR REPLACE FUNCTION ls_check_user_connected() RETURNS void AS $$
BEGIN
	DECLARE database VARCHAR := (SELECT current_database());
	DECLARE user_connected VARCHAR = (SELECT usename FROM pg_stat_activity where datname = database);
	DECLARE error VARCHAR := ('You are actually connected with the user "postgres", you should be connected with your LinShare database user, we are about to stop the migration script.');
	BEGIN
		IF (user_connected = 'postgres') THEN
			RAISE WARNING '%', error;
		--	DIRTY: did it to stop the process cause there is no clean way to do it.
		--	Expected error: query has no destination for result data.
			SELECT '';
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_check_user_connected();
SELECT ls_prechecks();

SET client_min_messages = warning;

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;

UPDATE mail_content SET language = 1 where id = 80;

ALTER TABLE users ADD COLUMN inconsistent bool DEFAULT 'False';

ALTER TABLE account ADD COLUMN cmis_locale varchar(255);
UPDATE account SET cmis_locale = 'en';

DROP VIEW IF EXISTS alias_users_list_all, alias_users_list_active, alias_users_list_destroyed, alias_threads_list_all, alias_threads_list_active, alias_threads_list_destroyed;
ALTER TABLE account
	ALTER COLUMN cmis_locale SET NOT NULL,
	ALTER COLUMN creation_date TYPE timestamp(6),
	ALTER COLUMN modification_date TYPE timestamp(6);

ALTER TABLE entry ADD COLUMN cmis_sync bool DEFAULT 'false' NOT NULL;

UPDATE mail_content set body = 'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationDate}, avec : <ul>${recipientNames}</ul><br/>Voici la liste des documents partagés : <ul>${documentNames}</ul>' where id = 82;

UPDATE mail_content set body = 'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationDate}, avec : <ul>${recipientNames}</ul>Votre message original est le suivant :<br/><i>${message}</i><br/><br/>Voici la liste des documents partagés :<br/><ul>${documentNames}</ul>' where id = 83;


CREATE TABLE share_entry_group (
  id                int8 NOT NULL,
  account_id        int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  subject           text,
  notification_date timestamp,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  expiration_date   timestamp NULL,
  notified          bool DEFAULT 'false' NOT NULL,
  processed         bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mail_activation (
  id                      int8 NOT NULL,
  identifier              varchar(255) NOT NULL,
  system                  bool NOT NULL,
  policy_activation_id    int8 NOT NULL,
  policy_configuration_id int8 NOT NULL,
  policy_delegation_id    int8 NOT NULL,
  domain_id               int8 NOT NULL,
  enable                  bool NOT NULL,
  PRIMARY KEY (id));

ALTER TABLE anonymous_share_entry ADD COLUMN share_entry_group_id int8;
ALTER TABLE share_entry ADD COLUMN share_entry_group_id int8;

ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_708340 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr137514 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE share_entry_group ADD CONSTRAINT shareEntryGroup FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE mail_activation ADD CONSTRAINT FKmail_activ188698 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_activation ADD CONSTRAINT activation FOREIGN KEY (policy_activation_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD CONSTRAINT configuration FOREIGN KEY (policy_configuration_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD CONSTRAINT delegation FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);




-- ENGLISH
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (33, 'eb291876-53fc-419b-831b-53a480399f7c', 1, 0, 32, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Undownloaded shared documents alert', E'[Undownloaded shared documents alert] ${subject} Shared on ${date}.', E'Please find below the resume of the share you made on ${creationDate} with initial expiration date on ${expirationDate}.<br /> List of documents : <br /><table style="border-collapse: collapse;">${shareInfo}</table><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (33, 1, 0, 33, 32, 'bfcced12-7325-49df-bf84-65ed90ff7f59');
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 0, 33, 32, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);
-- FRENCH
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (84, 'f2cc5735-a3fe-43e8-ae9c-bace74195af0', 1, 1, 32, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Accusé de non téléchargement de fichiers', E'[Accusé de Non Téléchargement] ${subject} Partagé le ${date}.', E'Veuillez trouver ci-dessous le suivi du partage de documents réalisé le ${creationDate} avec pour date d’expiration initiale le ${expirationDate}.<br /> Liste des documents : <br /><table style="border-collapse: collapse;">${shareInfo}</table><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (84, 1, 1, 84, 32, 'fa7a23cb-f545-45b4-b9dc-c39586cb2398');
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 1, 84, 32, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);


-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
DO $$
DECLARE pol_act_id INT8;
DECLARE pol_conf_id INT8;
DECLARE pol_deleg_id INT8;
BEGIN
	pol_act_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_act_id, true, true, 1, false);
	pol_conf_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_conf_id, true, true, 1, false);
	pol_deleg_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_deleg_id, true, true, 1, false);
	INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) 
 	VALUES(54, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', pol_act_id, pol_conf_id, pol_deleg_id, 1);
	INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (54, true);
END;
$$;

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
DO $$
DECLARE pol_act_id INT8;
DECLARE pol_conf_id INT8;
DECLARE pol_deleg_id INT8;
BEGIN
	pol_act_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_act_id, true, true, 0, true);
	pol_conf_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_conf_id, true, true, 1, false);
	pol_deleg_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_deleg_id, true, true, 1, false);
	INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 	VALUES(55, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION', pol_act_id, pol_conf_id, pol_deleg_id, 1, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', true);
	INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (55, 3);
END;
$$;
-- Functionality : ANONYMOUS_URL__NOTIFICATION
DO $$
DECLARE pol_act_id INT8;
DECLARE pol_conf_id INT8;
DECLARE pol_deleg_id INT8;
BEGIN
	pol_act_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_act_id, true, true, 0, true);
	pol_conf_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_conf_id, true, true, 1, false);
	pol_deleg_id := (SELECT nextVal('hibernate_sequence'));
	INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_deleg_id, false, false, 2, true);
	INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
	 VALUES(56, false, 'ANONYMOUS_URL__NOTIFICATION', pol_act_id, pol_conf_id, pol_deleg_id, 1, 'ANONYMOUS_URL', true);
	 INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (56, true);
END;
$$;
-- MailActivation : BEGIN

CREATE OR REPLACE FUNCTION ls_mail_activation(mail_activation_name varchar) RETURNS void AS $$
BEGIN
	DECLARE pol_act_id INT8;
	DECLARE pol_conf_id INT8;
	DECLARE pol_deleg_id INT8;
	DECLARE mail_id INT8;
	mail_name ALIAS FOR $1;
	BEGIN
		pol_act_id := (SELECT nextVal('hibernate_sequence'));
		INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_act_id, true, true, 0, true);
		pol_conf_id := (SELECT nextVal('hibernate_sequence'));
		INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_conf_id, true, true, 1, false);
		pol_deleg_id := pol_conf_id + 1;
		INSERT INTO policy(id, status, default_status, policy, system) VALUES (pol_deleg_id, false, false, 2, true);
		mail_id := (SELECT nextVal('hibernate_sequence'));
		IF mail_id IS NULL THEN
		   mail_id := 1;
		END IF;
		INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) VALUES(mail_id, false, mail_name, pol_act_id, pol_conf_id, pol_deleg_id, 1, true);
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_mail_activation('NEW_GUEST');
SELECT ls_mail_activation('ANONYMOUS_DOWNLOAD');
SELECT ls_mail_activation('REGISTERED_DOWNLOAD');
SELECT ls_mail_activation('RESET_PASSWORD');
SELECT ls_mail_activation('SHARED_DOC_DELETED');
SELECT ls_mail_activation('SHARED_DOC_UPDATED');
SELECT ls_mail_activation('SHARED_DOC_UPCOMING_OUTDATED');
SELECT ls_mail_activation('DOC_UPCOMING_OUTDATED');
SELECT ls_mail_activation('NEW_SHARING');
SELECT ls_mail_activation('UPLOAD_PROPOSITION_CREATED');
SELECT ls_mail_activation('UPLOAD_PROPOSITION_REJECTED');
SELECT ls_mail_activation('UPLOAD_REQUEST_UPDATED');
SELECT ls_mail_activation('UPLOAD_REQUEST_ACTIVATED');
SELECT ls_mail_activation('UPLOAD_REQUEST_AUTO_FILTER');
SELECT ls_mail_activation('UPLOAD_REQUEST_CREATED');
SELECT ls_mail_activation('UPLOAD_REQUEST_ACKNOWLEDGEMENT');
SELECT ls_mail_activation('UPLOAD_REQUEST_REMINDER');
SELECT ls_mail_activation('UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY');
SELECT ls_mail_activation('UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY');
SELECT ls_mail_activation('UPLOAD_REQUEST_WARN_OWNER_EXPIRY');
SELECT ls_mail_activation('UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY');
SELECT ls_mail_activation('UPLOAD_REQUEST_CLOSED_BY_RECIPIENT');
SELECT ls_mail_activation('UPLOAD_REQUEST_CLOSED_BY_OWNER');
SELECT ls_mail_activation('UPLOAD_REQUEST_DELETED_BY_OWNER');
SELECT ls_mail_activation('UPLOAD_REQUEST_NO_SPACE_LEFT');
SELECT ls_mail_activation('UPLOAD_REQUEST_FILE_DELETED_BY_SENDER');
SELECT ls_mail_activation('SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER');
SELECT ls_mail_activation('UNDOWNLOADED_SHARED_DOCUMENTS_ALERT');
SELECT ls_mail_activation('ANONYMOUS_URL__NOTIFICATION');

-- MailActivation : END

-- UPDATE FUNCTIONALITIES THAT CONTAINS ACKNOWLEDGMENT INSTEAD OF ACKNOWLEDGMENT
UPDATE functionality SET identifier = 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER' WHERE identifier = 'SHARE_CREATION_ACKNOWLEDGMENT_FOR_OWNER';
UPDATE functionality SET identifier = 'UPLOAD_REQUEST_ACKNOWLEDGEMENT' WHERE identifier = 'UPLOAD_REQUEST_ACKNOWLEDGMENT';

-- step 1 : delete subclass functionality
CREATE TEMP TABLE temptable_1_10_unit (id int8);
INSERT INTO temptable_1_10_unit SELECT unit_id FROM functionality_unit as fu join functionality as f on f.id = fu.functionality_id WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';

CREATE TEMP TABLE temptable_1_10 (id int8);

INSERT INTO temptable_1_10 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';
INSERT INTO temptable_1_10 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';
INSERT INTO temptable_1_10 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';

INSERT INTO temptable_1_10 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';
INSERT INTO temptable_1_10 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';
INSERT INTO temptable_1_10 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';

INSERT INTO temptable_1_10 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';
INSERT INTO temptable_1_10 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';
INSERT INTO temptable_1_10 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';

-- step 2 : delete subclass functionality
DELETE FROM functionality_unit WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION');
DELETE FROM functionality_boolean WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD');

-- step 3 : delete unit
DELETE FROM unit WHERE id in (SELECT id FROM temptable_1_10_unit);

-- step 4 : delete subclass functionality
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';

-- step 5 : delete policies
DELETE FROM policy WHERE id in (SELECT id FROM temptable_1_10);

DROP TABLE upload_request_entry_url;
-- constraint was : FKupload_req784409


-- UPLOAD_REQUEST_ENTRY_URL = 28
DELETE FROM mail_content_lang WHERE mail_content_type = 28;
DELETE FROM mail_content WHERE mail_content_type = 28;


ALTER TABLE document RENAME COLUMN "size" to ls_size;
ALTER TABLE document_entry RENAME COLUMN "size" to ls_size;
ALTER TABLE domain_access_rule RENAME COLUMN "regexp" to ls_regexp;
ALTER TABLE signature RENAME COLUMN "size" to ls_size;
ALTER TABLE thread_entry RENAME COLUMN "size" to ls_size;
ALTER TABLE upload_request_entry RENAME COLUMN "size" to ls_size;
ALTER TABLE upload_proposition_filter RENAME COLUMN match to ls_match;


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

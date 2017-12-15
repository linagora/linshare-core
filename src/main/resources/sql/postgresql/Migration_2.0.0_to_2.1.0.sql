-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'2.1.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.1.0';
	DECLARE version_from VARCHAR := '2.0.0';
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
	DECLARE user_connected VARCHAR := (SELECT current_user);
	DECLARE error VARCHAR := ('You are actually connected with the user "postgres", you should be connected with your LinShare database user, we are about to stop the migration script.');
	BEGIN
		RAISE INFO 'Connected to "%" with user "%"', database, user_connected;
		IF (user_connected = 'postgres') THEN
			RAISE WARNING '%', error;
		--	DIRTY: did it to stop the process cause there is no clean way to do it.
		--	Expected error: query has no destination for result data.
			SELECT '';
		END IF;
	END;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION ls_fix_current_value_for_all_accounts() RETURNS void AS $$
BEGIN
	DECLARE myaccount record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	DECLARE op BIGINT;
	BEGIN
		FOR myaccount IN (SELECT id, mail FROM account WHERE account_type != 5) LOOP
			RAISE INFO 'account mail : % (account_id=%)', myaccount.mail, myaccount.id;
			i := (SELECT sum(ls_size) FROM account AS a join entry AS e on a.id = e.owner_id join document_entry AS de ON de.entry_id = e.id WHERE a.id = myaccount.id);
			IF i IS NULL THEN
				i := 0;
			END IF;
			j := (SELECT current_value FROM quota AS q WHERE account_id = myaccount.id);
			op := (SELECT - sum(operation_value) FROM operation_history AS q WHERE account_id = myaccount.id);
			IF op IS NULL THEN
				op := 0;
			END IF;
			RAISE INFO 'Value of current_value : %, sum(operation_value) : % (account=%)', j, op, myaccount.id;
			RAISE INFO 'Updating account with new value (sum(ls_size)) - sum(operation_value) : % - % = %', i, op, i - op;
			i := i - op;
			RAISE INFO 'Difference of current_value : % ', i - j;
			UPDATE quota SET current_value = i WHERE account_id = myaccount.id;
			RAISE INFO '----';
			RAISE INFO 'Delete OperationHistory for account : % ', myaccount.mail;
			DELETE FROM operation_history WHERE account_id = myaccount.id;
			RAISE INFO '----';
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION ls_fix_current_value_for_domains() RETURNS void AS $$
BEGIN
	DECLARE d record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	BEGIN
		FOR d IN (SELECT id, label FROM domain_abstract) LOOP
			RAISE INFO 'domain label : % (domain_id=%)', d.label, d.id;
			FOR q IN (SELECT id, container_type FROM quota WHERE quota_type = 'CONTAINER_QUOTA' and domain_id = d.id) LOOP
				i := (select sum(current_value) from quota where quota_container_id  = q.id);
				IF i IS NULL THEN
					i := 0;
				END IF;
				RAISE INFO 'Updating container % (id=%) with new value : % (domain_id=%)', q.container_type, q.id, i, d.id;
				UPDATE quota AS a SET current_value = i WHERE a.id = q.id;
			END LOOP;
			j := (SELECT sum(current_value) FROM quota WHERE quota_type = 'CONTAINER_QUOTA' and domain_id = d.id);
			IF j IS NULL THEN
				j := 0;
			END IF;
			RAISE INFO 'Updating domain with new value : % (domain_id=%)', j, d.id;
			UPDATE quota SET current_value = j WHERE quota_type = 'DOMAIN_QUOTA' and domain_id = d.id;
			RAISE INFO '----';
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_fix_current_value_for_subdomains(domain_type NUMERIC) RETURNS void AS $$
BEGIN
	DECLARE d record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	BEGIN
		RAISE INFO 'domain type : % ', domain_type;
		FOR d IN (SELECT id, label FROM domain_abstract where type = domain_type) LOOP
			RAISE INFO 'domain label : % (domain_id=%)', d.label, d.id;
			j := (SELECT sum(current_value + current_value_for_subdomains) FROM quota WHERE quota_type = 'DOMAIN_QUOTA' and domain_parent_id = d.id);
			IF j IS NULL THEN
				j := 0;
			END IF;
			RAISE INFO 'Updating domain column "current_value_for_subdomains" with new value : % (domain_id=%)', j, d.id;
			UPDATE quota SET current_value_for_subdomains = j WHERE quota_type = 'DOMAIN_QUOTA' and domain_id = d.id;
			RAISE INFO '----';
			RAISE INFO '';
		END LOOP;
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
-- Here your request

-- fix quota for account (copy issue).
SELECT ls_fix_current_value_for_all_accounts();
-- update quota containers.

-- updating current_value of all domains
SELECT ls_fix_current_value_for_domains();

-- updating current_value_for_subdomain of all top domains with the values of subdomains.
-- TOPDOMAIN(1)
SELECT ls_fix_current_value_for_subdomains(1);

-- updating current_value_for_subdomain of root domain with the values of subdomains.
-- ROOTDOMAIN(0)
SELECT ls_fix_current_value_for_subdomains(0);

TRUNCATE mail_content CASCADE;
TRUNCATE mail_footer CASCADE;
DELETE FROM mail_config;
DELETE FROM mail_layout;

ALTER TABLE document ALTER COLUMN sha256sum SET NOT NULL;
ALTER TABLE document ALTER COLUMN thmb_uuid DROP NOT NULL;
ALTER TABLE document ADD COLUMN has_thumbnail bool DEFAULT 'false' NOT NULL;
ALTER TABLE document ADD COLUMN compute_thumbnail bool DEFAULT 'false' NOT NULL;

ALTER TABLE domain_abstract ADD COLUMN purge_step varchar(255) DEFAULT 'IN_USE' NOT NULL;
ALTER TABLE domain_abstract ALTER COLUMN domain_policy_id DROP NOT NULL;

ALTER TABLE upload_request ALTER COLUMN modification_date SET NOT NULL;
ALTER TABLE upload_request ALTER COLUMN expiry_date SET NOT NULL;
ALTER TABLE upload_request ALTER COLUMN notification_date SET NOT NULL;

ALTER TABLE quota ADD COLUMN domain_shared bool DEFAULT 'false';
ALTER TABLE quota ADD COLUMN domain_shared_override bool;
ALTER TABLE quota ADD COLUMN default_domain_shared bool DEFAULT 'false';
ALTER TABLE quota ADD COLUMN default_domain_shared_override bool;

ALTER TABLE upgrade_task ALTER COLUMN creation_date TYPE timestamp(6);
ALTER TABLE upgrade_task ALTER COLUMN creation_date SET NOT NULL;
ALTER TABLE upgrade_task ALTER COLUMN modification_date TYPE timestamp(6);
ALTER TABLE upgrade_task ALTER COLUMN modification_date SET NOT NULL;
ALTER TABLE upgrade_task ALTER COLUMN priority SET NOT NULL;

CREATE TABLE thumbnail (
  id                      int8 NOT NULL,
  uuid                   varchar(255) NOT NULL UNIQUE,
  thumbnail_type         varchar(255) NOT NULL,
  creation_date          timestamp(6) NOT NULL,
  document_id            int8 NOT NULL,
  CONSTRAINT linshare_thumbnail_pkey
    PRIMARY KEY (id));
ALTER TABLE thumbnail ADD CONSTRAINT FKthumbnail35163 FOREIGN KEY (document_id) REFERENCES document (id);

-- Update document : Set compute_thumbnail TRUE.
UPDATE document SET compute_thumbnail = true ;
-- end update document

-- Begin Upgrade Tsk 2.1.0
  -- TASK: UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (13,
  'UNDEFINED',
  'UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR',
  'UPGRADE_2_1',
  null,
  null,
  13,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (14,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS',
  'UPGRADE_2_1',
  null,
  null,
  14,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_REMOVE_ALL_THREAD_ENTRIES
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (15,
  'UNDEFINED',
  'UPGRADE_2_1_REMOVE_ALL_THREAD_ENTRIES',
  'UPGRADE_2_1',
  null,
  null,
  15,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (16,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  'UPGRADE_2_1',
  null,
  'UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS',
  16,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA
INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  parent_uuid,
  parent_identifier,
  task_order,
  status,
  priority,
  creation_date,
  modification_date,
  extras)
VALUES
  (17,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA',
  'UPGRADE_2_1',
  null,
  'UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  17,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- End Upgrade Task 2.1.0

-- MailActivation : BEGIN
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (231, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (232, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (233, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(17, false, 'UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT', 231, 232, 233, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (234, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (235, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (236, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(18, false, 'UPLOAD_REQUEST_ACTIVATED_FOR_OWNER', 234, 235, 236, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (237, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (238, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (239, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(19, false, 'UPLOAD_REQUEST_REMINDER', 237, 238, 239, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (240, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (241, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (242, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(20, false, 'UPLOAD_REQUEST_PASSWORD_RENEWAL', 240, 241, 242, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (243, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (244, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (245, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(21, false, 'UPLOAD_REQUEST_CREATED', 243, 244, 245, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (246, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (247, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (248, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(22, false, 'UPLOAD_REQUEST_CLOSED_BY_OWNER', 246, 247, 248, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (249, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (250, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (251, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(23, false, 'UPLOAD_REQUEST_RECIPIENT_REMOVED', 249, 250, 251, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (252, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (253, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (254, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(24, false, 'UPLOAD_REQUEST_UPDATED_SETTINGS', 252, 253, 254, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (255, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (256, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (257, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(25, false, 'UPLOAD_REQUEST_FILE_DELETED_BY_OWNER', 255, 256, 257, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (258, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (259, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (260, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(26, false, 'GUEST_WARN_OWNER_ABOUT_GUEST_EXPIRATION', 258, 259, 260, 1, true);

-- MailActivation : SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (261, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (262, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (263, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(27, false, 'SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD', 261, 262, 263, 1, true);

-- MailActivation : SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (264, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (265, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (266, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(28, false, 'SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE', 264, 265, 266, 1, true);

-- MailActivation : WORKGROUP_WARN_NEW_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (267, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (268, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (269, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)  
 	VALUES(29, false, 'WORKGROUP_WARN_NEW_MEMBER', 267, 268, 269, 1, true);

-- MailActivation : WORKGROUP_WARN_UPDATED_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (270, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (271, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (272, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(30, false, 'WORKGROUP_WARN_UPDATED_MEMBER', 270, 271, 272, 1, true);

-- MailActivation : WORKGROUP_WARN_DELETED_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (273, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (274, true, true, 1, false);
	INSERT INTO policy(id, status, default_status, policy, system)
VALUES (275, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(31, false, 'WORKGROUP_WARN_DELETED_MEMBER', 273, 274, 275, 1, true);

-- MailActivation : GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (276, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (277, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (278, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(32, false, 'GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET', 276, 277, 278, 1, true);
-- MailActivation : END

-- Mail Layout 
--######   TODO
-- End mail layout

-- End of your requests

-- LinShare version
SELECT ls_version();

-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed != 0;

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed != 0;
COMMIT;
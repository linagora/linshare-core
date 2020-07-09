-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'2.2.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.2.0';
	DECLARE version_from VARCHAR := '2.1.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE nb_upgrade_tasks INT := (SELECT count(*)::int  FROM upgrade_task WHERE status != 'SUCCESS' AND status != 'SKIPPED');
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your actual version is: %', version_history_from;
		RAISE NOTICE 'Your database history is :';
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
		IF (nb_upgrade_tasks > 0) THEN
			RAISE WARNING 'Can not upgrade LinShare if all upgrade tasks are not completed with success !!!!';
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

CREATE OR REPLACE FUNCTION ls_update_one_upload_request_group(group_id BIGINT) RETURNS void AS $$
BEGIN
	DECLARE uploadrequest record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	DECLARE grp_created BIGINT;
	DECLARE grp_updated BIGINT;
	DECLARE count_url BIGINT;
	DECLARE is_restricted BOOLEAN;
	DECLARE ur_infos_cursor CURSOR IS
    				SELECT ur.*, urg.body, urg.subject FROM upload_request AS ur INNER JOIN upload_request_group AS urg ON ur.upload_request_group_id = urg.id
							WHERE ur.upload_request_group_id = group_id;
			ur_row RECORD;
			
	BEGIN
		i := 0;
		FOR ur_row IN ur_infos_cursor LOOP
			count_url := (SELECT COUNT(id) FROM upload_request_url WHERE upload_request_id = ur_row.id);
			is_restricted := (count_url = 1);
			IF i > 0 THEN
				j := (SELECT nextVal('hibernate_sequence'));
				INSERT INTO upload_request_group (uuid, id, subject, body, domain_abstract_id, account_id,
					status, creation_date, modification_date,
					secured, mail_message_id, activation_date, expiry_date, 
					notification_date, max_deposit_size, max_file, max_file_size,
					can_delete, can_close, can_edit_expiry_date, locale, restricted) 
					VALUES ((SELECT uuid_in(md5(random()::text || now()::text)::cstring)), j, ur_row.subject, ur_row.body, ur_row.domain_abstract_id, ur_row.account_id,
						ur_row.status, ur_row.creation_date, ur_row.modification_date,
						ur_row.secured, ur_row.mail_message_id, ur_row.activation_date, ur_row.expiry_date,
						ur_row.notification_date, ur_row.max_deposit_size, ur_row.max_file, ur_row.max_file_size,
						ur_row.can_delete, ur_row.can_close, ur_row.can_edit_expiry_date, ur_row.locale, is_restricted);
				UPDATE upload_request SET upload_request_group_id = j where id = ur_row.id;
			ELSE
				UPDATE upload_request_group SET (domain_abstract_id, account_id, status, secured, mail_message_id, activation_date, expiry_date, 
					notification_date, max_deposit_size, max_file, max_file_size,
					can_delete, can_close, can_edit_expiry_date, locale, restricted) = 
					(ur_row.domain_abstract_id, ur_row.account_id, ur_row.status, ur_row.secured, ur_row.mail_message_id, ur_row.activation_date, ur_row.expiry_date,
						ur_row.notification_date, ur_row.max_deposit_size, ur_row.max_file, ur_row.max_file_size,
						ur_row.can_delete, ur_row.can_close, ur_row.can_edit_expiry_date, ur_row.locale, is_restricted) 
					where id = ur_row.upload_request_group_id;
			END IF;
			i := i + 1;
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_update_upload_request_groups() RETURNS void AS $$
BEGIN
	DECLARE ur_group record;
	BEGIN
		FOR ur_group IN (SELECT id FROM upload_request_group) LOOP
			PERFORM ls_update_one_upload_request_group(ur_group.id) ;
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

--Update the upload request statuses
UPDATE upload_request_history set status = replace(status, 'STATUS_', '');
UPDATE upload_request set status = replace(status, 'STATUS_', '');

ALTER TABLE upload_request_group ADD COLUMN domain_abstract_id int8;
ALTER TABLE upload_request_group ADD COLUMN account_id int8;
ALTER TABLE upload_request_group ADD COLUMN secured bool;
ALTER TABLE upload_request_group ADD COLUMN mail_message_id varchar(255);
ALTER TABLE upload_request_group ADD COLUMN activation_date timestamp(6);
ALTER TABLE upload_request_group ADD COLUMN expiry_date timestamp(6);
ALTER TABLE upload_request_group ADD COLUMN notification_date timestamp(6);
ALTER TABLE upload_request_group ADD COLUMN max_deposit_size int8;
ALTER TABLE upload_request_group ADD COLUMN max_file int4;
ALTER TABLE upload_request_group ADD COLUMN max_file_size int8;
ALTER TABLE upload_request_group ADD COLUMN can_delete bool;
ALTER TABLE upload_request_group ADD COLUMN can_close bool;
ALTER TABLE upload_request_group ADD COLUMN can_edit_expiry_date bool;
ALTER TABLE upload_request_group ADD COLUMN locale varchar(255);
ALTER TABLE upload_request_group ADD COLUMN enable_notification bool DEFAULT TRUE NOT NULL;
ALTER TABLE upload_request_group ADD COLUMN restricted bool;
ALTER TABLE upload_request_group ADD COLUMN status varchar(255);

SELECT ls_update_upload_request_groups();

ALTER TABLE upload_request_group ALTER COLUMN domain_abstract_id SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN account_id SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN secured SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN activation_date SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN notification_date SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN can_delete SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN can_close SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN can_edit_expiry_date SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN locale SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN enable_notification SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN restricted SET NOT NULL;
ALTER TABLE upload_request_group ALTER COLUMN status SET NOT NULL;
ALTER TABLE upload_request_group ADD CONSTRAINT FKupload_req220337 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE upload_request_group ADD CONSTRAINT FKupload_req840249 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);

ALTER TABLE upload_request DROP COLUMN domain_abstract_id;
ALTER TABLE upload_request DROP COLUMN account_id;
ALTER TABLE upload_request ADD COLUMN dirty bool DEFAULT FALSE NOT NULL;
ALTER TABLE upload_request ADD COLUMN enable_notification bool DEFAULT TRUE NOT NULL;

ALTER TABLE upload_request_entry ADD COLUMN document_id int8;
ALTER TABLE upload_request_entry ADD COLUMN ls_type varchar(255);
ALTER TABLE upload_request_entry ADD COLUMN copied bool DEFAULT FALSE NOT NULL;
ALTER TABLE upload_request_entry ADD COLUMN ciphered bool DEFAULT FALSE NOT NULL;
ALTER TABLE upload_request_entry ADD COLUMN sha256sum varchar(255);
ALTER TABLE upload_request_entry ADD CONSTRAINT FKupload_req11782 FOREIGN KEY (document_id) REFERENCES document (id);
UPDATE upload_request_entry
	SET document_id = document_entry.document_id,
		ls_type = document_entry.type,
		ciphered = document_entry.ciphered,
		copied = TRUE,
		sha256sum = document_entry.sha256sum
	FROM document_entry
	WHERE document_entry.entry_id = upload_request_entry.document_entry_entry_id;
ALTER TABLE upload_request_entry ALTER COLUMN document_id SET NOT NULL;
ALTER TABLE upload_request_entry ALTER COLUMN ls_type SET NOT NULL;

-- Update ldap_pattern
ALTER TABLE ldap_pattern ADD COLUMN search_all_groups_query text;
ALTER TABLE ldap_pattern ADD COLUMN search_group_query text;
ALTER TABLE ldap_pattern ADD COLUMN group_prefix varchar(255);


-- Group ldap pattern
INSERT INTO ldap_pattern(
	id,
	uuid,
	pattern_type,
	label,
	system,
	description,
	auth_command,
	search_user_command,
	search_page_size,
	search_size_limit,
	auto_complete_command_on_first_and_last_name,
	auto_complete_command_on_all_attributes, completion_page_size,
	completion_size_limit,
	creation_date,
	modification_date,
	search_all_groups_query,
	search_group_query,
	group_prefix)
	VALUES(
	4,
	'dfaa3523-51b0-423f-bb6d-95d6ecbfcd4c',
	'GROUP_LDAP_PATTERN',
	'Ldap groups',
	true,
	'default-group-pattern',
	NULL,
	NULL,
	100,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NOW(),
	NOW(),
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-*))");',
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-" + pattern + "))");',
	'workgroup-');


-- ldap attributes
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(13, 'mail', 'member_mail', false, true, true, false, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(14, 'givenName', 'member_firstname', false, true, true, false, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(15, 'cn', 'group_name_attr', false, true, true, true, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(16, 'member', 'extended_group_member_attr', false, true, true, true, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(17, 'sn', 'member_lastname', false, true, true, false, 4);


-- Demo ldap pattern.
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    5,
    'a4620dfc-dc46-11e8-a098-2355f9d6585a',
    'USER_LDAP_PATTERN',
    'default-pattern-demo',
    'This is pattern the default pattern for the OpenLdap demo structure.',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (18, 'user_mail', 'mail', false, true, true, 5, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (19, 'user_firstname', 'givenName', false, true, true, 5, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (20, 'user_lastname', 'sn', false, true, true, 5, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (21, 'user_uid', 'uid', false, true, true, 5, false);


-- Update domain_abstract
ALTER TABLE domain_abstract ADD COLUMN group_provider_id int8;

-- New table  group_provider
CREATE TABLE group_provider (
  id                  int8 NOT NULL,
  uuid               varchar(255) NOT NULL UNIQUE,
  provider_type      varchar(255) NOT NULL,
  base_dn            varchar(255),
  creation_date      timestamp NOT NULL,
  modification_date  timestamp NOT NULL,
  ldap_connection_id int8 NOT NULL,
  ldap_pattern_id    int8 NOT NULL,
  search_in_other_domains bool DEFAULT 'true',
  PRIMARY KEY (id));

ALTER TABLE group_provider ADD CONSTRAINT FKgroup_provi815203 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE group_provider ADD CONSTRAINT FKgroup_provi1640 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs253989 FOREIGN KEY (group_provider_id) REFERENCES group_provider (id);

-- Upgrade Task

  -- TASK: UPGRADE_2_2_MIGRATE_HISTORY_TO_MONGO_AUDIT;
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
  (19,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_UPLOAD_REQUEST_HISTORY_TO_MONGO_AUDIT',
  'UPGRADE_2_2',
  null,
  null,
  19,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

  -- TASK: UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_FILTER_TO_MONGO_DATABASE
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
  (20,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_FILTER_TO_MONGO_DATABASE',
  'UPGRADE_2_2',
  null,
  null,
  20,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_TO_MONGO_DATABASE
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
  (21,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_TO_MONGO_DATABASE',
  'UPGRADE_2_2',
  null,
  null,
  21,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_2_MIGRATE_THREAD_TO_MONGO_DATABASE
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
  (22,
  'UNDEFINED',
  'UPGRADE_2_2_MIGRATE_THREAD_AND_THREAD_MEMBERS_TO_MONGO_DATABASE',
  'UPGRADE_2_2',
  null,
  null,
  22,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

  -- TASK: UPGRADE_2_2_GENERATE_BASIC_STATISTICS_FROM_AUDIT_LOG_ENTRIES
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
  (23,
  'UNDEFINED',
  'UPGRADE_2_2_GENERATE_BASIC_STATISTICS_FROM_AUDIT_LOG_ENTRIES',
  'UPGRADE_2_2',
  null,
  null,
  23,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_2_MIGRATE_WORKGROUP_AUDIT_TO_SHARED_SPACE_AUDIT
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
 (24,
 'UNDEFINED',
 'UPGRADE_2_2_MIGRATE_WORKGROUP_AUDIT_TO_SHARED_SPACE_AUDIT',
 'UPGRADE_2_2',
  null,
  null,
  24,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_2_MIGRATE_WORKGROUP_MEMBER_AUDIT_TO_SHARED_SPACE_MEMBER_AUDIT
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
 (25,
 'UNDEFINED',
 'UPGRADE_2_2_MIGRATE_WORKGROUP_MEMBER_AUDIT_TO_SHARED_SPACE_MEMBER_AUDIT',
 'UPGRADE_2_2',
  null,
  null,
  25,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);
--  END UPGRADE TASK
  
ALTER TABLE account ADD COLUMN first_name varchar(255);
ALTER TABLE account ADD COLUMN last_name varchar(255);
ALTER TABLE account ADD COLUMN encipherment_key_pass bytea;
ALTER TABLE account ADD COLUMN not_after timestamp(6);
ALTER TABLE account ADD COLUMN not_before timestamp(6);
ALTER TABLE account ADD COLUMN can_upload bool DEFAULT FALSE NOT NULL;
ALTER TABLE account ADD COLUMN comment text;
ALTER TABLE account ADD COLUMN restricted bool DEFAULT FALSE;
ALTER TABLE account ADD COLUMN expiration_date timestamp;
ALTER TABLE account ADD COLUMN ldap_uid varchar(255);
ALTER TABLE account ADD COLUMN can_create_guest bool DEFAULT FALSE NOT NULL;
ALTER TABLE account ADD COLUMN inconsistent bool DEFAULT FALSE;

UPDATE account SET 
	first_name = users.first_name,
	last_name = users.last_name,
	encipherment_key_pass = users.encipherment_key_pass,
	not_after = users.not_after,
	not_before = users.not_before,
	can_upload = users.can_upload,
	comment = users.comment,
	restricted = users.restricted,
	expiration_date = users.expiration_date,
	ldap_uid = users.ldap_uid,
	can_create_guest = users.can_create_guest,
	inconsistent  = users.inconsistent
	FROM users
	WHERE users.account_id = account.id;

ALTER TABLE recipient_favourite DROP CONSTRAINT FKrecipient_90791;
ALTER TABLE recipient_favourite ADD CONSTRAINT FKrecipient_90791 FOREIGN KEY (user_id) REFERENCES account (id);
ALTER TABLE allowed_contact DROP CONSTRAINT FKallowed_co409962;
ALTER TABLE allowed_contact ADD CONSTRAINT FKallowed_co409962 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE allowed_contact DROP CONSTRAINT FKallowed_co620678;
ALTER TABLE allowed_contact ADD CONSTRAINT FKallowed_co620678 FOREIGN KEY (contact_id) REFERENCES account (id);
ALTER TABLE thread_member DROP CONSTRAINT FKthread_mem565048;
ALTER TABLE thread_member ADD CONSTRAINT FKthread_mem565048 FOREIGN KEY (user_id) REFERENCES account (id);
ALTER TABLE mailing_list DROP CONSTRAINT fkmailing_li478123;
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES account (id);

CREATE OR REPLACE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from account as a ;
-- All active users
CREATE OR REPLACE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from account as a where a.destroyed = 0;
-- All destroyed users
CREATE OR REPLACE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from account as a where a.destroyed = 0;


DROP TABLE IF EXISTS users;

-- Add new fields : creation date and modification date
ALTER TABLE domain_abstract ADD COLUMN creation_date timestamp(6);
ALTER TABLE domain_abstract ADD COLUMN modification_date timestamp(6);
ALTER TABLE domain_access_policy ADD COLUMN creation_date timestamp(6);
ALTER TABLE domain_access_policy ADD COLUMN modification_date timestamp(6);
ALTER TABLE functionality ADD COLUMN creation_date timestamp(6);
ALTER TABLE functionality ADD COLUMN modification_date timestamp(6);
ALTER TABLE version ADD COLUMN creation_date timestamp(6);

UPDATE domain_abstract SET creation_date = CURRENT_TIMESTAMP;
UPDATE domain_abstract SET modification_date = CURRENT_TIMESTAMP;
UPDATE domain_access_policy SET creation_date = CURRENT_TIMESTAMP;
UPDATE domain_access_policy SET modification_date = CURRENT_TIMESTAMP;
UPDATE functionality SET creation_date = CURRENT_TIMESTAMP;
UPDATE functionality SET modification_date = CURRENT_TIMESTAMP;
UPDATE version SET creation_date = CURRENT_TIMESTAMP;

ALTER TABLE domain_abstract ALTER COLUMN creation_date SET NOT NULL;
ALTER TABLE domain_abstract ALTER COLUMN modification_date SET NOT NULL;
ALTER TABLE domain_access_policy ALTER COLUMN creation_date SET NOT NULL;
ALTER TABLE domain_access_policy ALTER COLUMN modification_date SET NOT NULL;
ALTER TABLE functionality ALTER COLUMN creation_date SET NOT NULL;
ALTER TABLE functionality ALTER COLUMN modification_date SET NOT NULL;
ALTER TABLE version ALTER COLUMN creation_date SET NOT NULL;

-- Mail policy
INSERT INTO policy(id, status, default_status, policy, system) VALUES (284, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (285, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (286, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (287, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (288, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (289, false, false, 2, true);

-- Mail Activation
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) VALUES(33, false, 'ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED', 284, 285, 286, 1, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) VALUES(34, false, 'ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_DELETED', 287, 288, 289, 1, true);
-- End MailActivation

-- Mail Layout

-- End mail layout

--Mail Content
INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,32,32,'','',NOW(),true,'','dbf022d8-8389-11e8-b804-d32666b16d41',true);
INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,33,33,'','',NOW(),true,'','dbf1b49a-8389-11e8-a006-77d9edee84a4',true);

INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (32, 0, 32, 1, 32, 'dbf0aaaa-8389-11e8-8743-9b6e3afe9f53', true);
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (132,1,1,32,32,true,'dbf12958-8389-11e8-964e-6b7eef81da86');
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (33, 0, 33, 1, 33, 'dbf1f8ba-8389-11e8-83c9-0b5ecc4849b0', true);
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (133,1,1,33,33,true,'dbf23f1e-8389-11e8-b430-a3d498f96a4f');

UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}">
                 Peter WILSON has created a new permanent authentication token for your account
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Création d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a créé un jeton d''''accès permanent pour votre compte.
tokenCreationDate = Date de création
tokenLabel = Nom
tokenDescription = Description',messages_english='subject = Creation of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a permanent authentication token for your account.
tokenCreationDate = Creation date
tokenLabel = Name
tokenDescription = Description' WHERE id=32;

UPDATE mail_content SET subject='[(#{subject})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${recipient.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}">
                 Peter WILSON has deleted a permanent access token for your account
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
           <th:block data-th-replace="layout :: infoStandardArea(#{tokenLabel},${label})"/>
           <th:block data-th-replace="layout :: infoDateArea(#{tokenCreationDate},${creationDate})"/>
           <div data-th-if="${!#strings.isEmpty(description)}">
             <th:block data-th-replace="layout :: infoStandardArea(#{tokenDescription},${description})"/>
           </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='subject = Suppression d''''un jeton d''''accès permanent
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé un jeton d''''accès permanent pour votre compte.
tokenCreationDate = Date de création
tokenLabel = Nom
tokenDescription = Description
tokenIdentifier = Identifiant',messages_english='subject = Deletion of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token for your account.
tokenCreationDate = Creation date
tokenLabel = Name
tokenDescription = Description' WHERE id=33;

UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(body)}">
      <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
        </span>
        <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection(~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div>
          <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
        </div>
          <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
                 <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                </span>
 <span th:with="df=#{customDate}" data-th-text="${#dates.format(request.activationDate,df)}">7th of November, 2018</span>
        </p>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <div data-th-if="${!#strings.isEmpty(request.activationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
       <div data-th-if="(${totalMaxDepotSize})">
               <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
         </div>
  <div data-th-if="(${isgrouped})">
         <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>', messages_french = 'activationDate = Ouverture du dépôt le
closureDate = Dépôt  disponible jusqu\''''au
customDate= d MMMM yyyy.
depotSize = Taille du dépôt
mainMsg = <b>{0} {1}</b> vous a créé une Invitation de Dépôt, qui sera ouverte le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires associés au dépôt
subject = {0} {1} vous a créé une Invitation de Dépot', messages_english = 'activationDate = Request activation date
closureDate = Request closing date
customDate= MMMM d, yyyy.
depotSize = Size of the upload repository
mainMsg = <b>{0} {1}</b> has created an Upload Request for you, set to open 
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients of the upload request
subject = {0} {1} has created an Upload Request repository for you.' WHERE id = 20;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="${body}" style="white-space: pre-line;">
                  Hi Amy,<br>
                  As agreed,  could you send me the report. Feel free to contact me if need be. <br/>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                   <th:block data-th-if="(${!request.wasPreviouslyCreated})">
                       <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                          Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                       </span>
                   </th:block>
                    <th:block data-th-if="(${request.wasPreviouslyCreated})">
                       <span data-th-text="#{msgAlt(${requestOwner.firstName} , ${requestOwner.lastName})}"> Peter Wilson\''s Upload Request depot is now activated..</span>
                     </th:block>
                     <br/>
                     <!--/* Check if the external user has a password protected file share */-->
                     <span data-th-if="(${!protected})">
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                     </span>
                     <span data-th-if="(${protected})">
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                     </span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="(${protected})">
               <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
           <div data-th-if="(${totalMaxDepotSize})">
                    <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', messages_french = 'buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers dans le dépôt : <b>{2}</b>.
msgAlt = L\''invitation de {0} {1} est désormais active.
msgFrom = Le message de
msgProtected = Vous pouvez déverrouiller le dépôt en suivant le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en suivant le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}', messages_english = 'buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> invited you to its upload request : <b>{2}</b>.
msgFrom = Message from
msgAlt = The repository from {0} {1} is now active.
msgProtected = Unlock it by following the link below and entering the password.
msgUnProtected = Access it by following the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients
subject = {0} {1} invited you to its upload request : {2}' WHERE id =16;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${request.subject})}">
                      Your Upload Request repository labeled $subject is now activated.
                     </span>
                     <span data-th-text="#{msgLink}">In order to access it click the link below.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
             <div data-th-if="(${totalMaxDepotSize})">
                   <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', messages_french = 'buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille autorisée
mainMsg = Votre dépôt intitulé <b>{0}</b> est désormais actif.
msgLink = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
recipientsOfDepot = Destinataires
subject = Votre invitation de dépôt {0} est désormais active', messages_english = 'buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = Your Upload Request labeled <b>{0}</b> is now active.
msgLink = Access it by following the link below.
recipientsOfDepot = Recipients
subject = Your Upload Request : {0}, is now active' WHERE id = 17;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has closed prematurely his Upload Request Depot labeled : subject.
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', messages_french = 'closureDate = Date de clôture
filesInURDepot = Fichiers
mainMsg = <b>{0} {1}</b> a fermé son invitation de dépôt : {2}.
recipientsOfDepot = Destinataires
subject = {0} {1} a fermé l\''invitation de dépôt : {2}', messages_english = 'closureDate = Closure date
filesInURDepot = Files
mainMsg = <b>{0} {1}</b> has closed the upload request labeled : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} has closed his upload request : {2}' WHERE id =21;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <section id="main-content">
    <!--/* Upper main-content*/-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="!(${isRestricted})" data-th-utext="#{groupedBeginningMainMsg(${requestRecipient.mail})}"></span>
          <span data-th-if="(${isRestricted})"
                data-th-utext="#{ungroupedBeginningMainMsg(${requestRecipient.mail})}"></span>
          <span data-th-if="(${documentsCount} == 1)" data-th-utext="#{endingMainMsgSingular}"></span>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="!(${isRestricted})">
       <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize}, ${totalSize})"/>
    <th:block data-th-if="(${request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{uploadedOverTotal(${request.uploadedFilesCount},${request.authorizedFiles})})"/>
    </th:block>
    <th:block data-th-if="(${!request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{totalUploaded(${request.uploadedFilesCount})})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', messages_french = 'endingMainMsgPlural =  Il y a <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = Il y a  <b>1 fichier </b> dans le dépôt.
filesInURDepot =  Fichiers déposés
fileSize =  Taille
groupedBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt.
invitationClosureDate = Date de clôture
invitationCreationDate = Date d\''activation
msgTitle = Message lié à l\''invitation :
numFilesInDepot = Nombre de fichiers déposés
recipientsURequest = Destinataires
subject = {0} a clôturé votre invitation de dépôt : {1}
ungroupedBeginningMainMsg = <b>{0}</b> a clôturé votre Invitation de Dépôt.
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} files', messages_english = 'endingMainMsgPlural = There are a total of <b> {0} files </b> in the depot.
endingMainMsgSingular =  There is a total <b>1 file </b> in the depot.
filesInURDepot = Files uploaded
fileSize =  Total filesize
groupedBeginningMainMsg = <b>{0}</b> has closed your grouped Upload Request depot.
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle =  Upload request\''s  attached message :
numFilesInDepot = Total uploaded files
recipientsURequest = Recipients
subject =  {0}  has closed  your Upload Request depot : {1}
ungroupedBeginningMainMsg  = <b>{0}</b> has closed your Upload Request depot.
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id =14;
UPDATE mail_content SET messages_french = 'deletionDate = Accès au dépôt retiré le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a retiré votre accès au dépôt de l\''invitation intitulée : {2}.
subject = {0} {1} a supprimé votre accès au dépôt : {2}
', messages_english = 'deletionDate = Deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has removed your access to the depot : {2}.
subject = {0} {1} has removed your access to the depot : {2}
' WHERE id =22;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a  customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName}, ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
                  Hi Amy,<br>
                  As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson reminds you that he still has not received the requested files. 
                     </span>
                     <span data-th-utext="#{mainMsgEnd}">
                     You can upload your files in the provided depot made available to you labeled  subject.
                     </span>
                     <!--/* Check if the external user has a password protected file share */-->
                     <br/>
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="(${totalMaxDepotSize})">
                 <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', messages_french = 'buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> aimerais vous rappeller de déposer vos fichiers.
mainMsgEnd =
msgFrom =  Le message de
msgUnProtected = Pour accéder au dépôt, suivez le lien ci-dessous.
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} attend toujours des fichiers de votre part', messages_english = 'buttonMsg = Access
closureDate = Closure date
depotSize = Size
mainMsg = <b>{0} {1}</b> kindly reminds you to upload your files.
mainMsgEnd =
msgFrom = Message from
msgUnProtected = In order to upload your files, please follow the link below.
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} is still awaiting your files' WHERE id =18;
UPDATE mail_content SET messages_french = 'invitationClosureDate = Date de clôture
invitationCreationDate = Date d\''activation
mainMsg =  <b>{0}</b>  n\''a pas pu déposer des fichiers dans le dépôt car il n\''y a plus d\''espace disponible dans votre Espace Personnel. Veuillez s\''il vous plait libérez de l\''espace.
mainMsgTitle = Vous n\''avez plus d\''espace disponible.
maxUploadDepotSize =  Taille total du dépôt
msgTitle = Message lié à l\''invitation de dépôt :
recipientsURequest = Destinataires
subject =  {0}  n\''a pu déposer un fichier car il n\''y a plus d\''espace disponible', messages_english = 'invitationClosureDate = Closure date
invitationCreationDate = Activation date
mainMsg =  <b>{0}</b> is not able to upload any file, since there is no more space available in your Personal Space. Please free up some space.
mainMsgTitle = No more space available.
maxUploadDepotSize = Maximum size of the depot
msgTitle = Upload Request\''s  attached message :
recipientsURequest = Recipients
subject =  {0} could not upload a file since there is no more space available' WHERE id =11;
UPDATE mail_content SET messages_french = 'activationDate = Date d\''activation
closureRight = Droits de dépôt
deletionRight = Droits de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
local = Langue
enableNotification = Activation des notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres liés au dépôt.
maxFileNum = Nombre de Fichiers
maxFileSize = Taille autorisée
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Les modifications sont listées ci-dessous.
subject = Modification des paramètres du dépôt : {0}', messages_english = 'activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Repository size
expiryDate = Closure date
local = Local
enableNotification = Enable notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings related to the Upload Request.
maxFileNum = File number
maxFileSize = File size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the depot
secondaryMsg = Updated settings are listed below.
subject = Updated Settings for Upload Request : {0}
' WHERE id =23;
UPDATE mail_content SET messages_french = 'endingMainMsg = dans votre Invitation de Dépôt.
fileSize =  Taille du fichier
fileUploadedThe= Fichier déposé le
invitationClosureDate = Date de clôture
invitationCreationDate = Date d\''activation
beginningMainMsg = <b> {0} </b> vous a déposé le fichier
numFilesInDepot = Nombre de fichiers déposés
subject =  {0}  vous a déposé {1}  dans votre Invitation de Dépôt
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} fichiers', messages_english = 'endingMainMsg = in your Upload Request
fileSize =  File size
fileUploadedThe = Upload date
invitationClosureDate = Closure date
invitationCreationDate = Activation date
beginningMainMsg =  <b> {0} </b> has uploaded the file
endingMainMsg = in your Upload Request.
numFilesInDepot = Total uploaded files
subject =  {0}  has uploaded {1}  in your Upload Request
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files' WHERE id =10;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})">
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="(${isRestricted})"   data-th-utext="#{beginningMainMsgUnGrouped(${remainingDays})}"></span>
            <span  data-th-if="!(${isRestricted})"   data-th-utext="#{beginningMainMsgGrouped(${remainingDays})}"></span>
            <span data-th-if="(${documentsCount} ==  1)"   data-th-utext="#{endingMainMsgSingular}" ></span>
            <span  data-th-if="(${documentsCount} >  1)"   data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for owner of the upload request */-->
        <!--/* upper message content for recipients of the upload request */-->
        <th:block data-th-if="(${!warnOwner})" >
          <!--/* Greetings */-->
          <th:block  data-th-replace="layout :: greetings(${requestRecipient.mail})" />
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"   data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
            <th:block   data-th-replace="layout :: actionButtonLink(#{uploadFileBtn},${requestUrl})"/>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
      <span id="message-title">
        <span data-th-text="#{msgTitle}">You have a message from</span>
      </span>
      <span id="message-content"  data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
         Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
      </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <!--/*Lower message content for the owner of the upload request */-->
    <th:block  data-th-if="(${warnOwner})">
      <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
      <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    </th:block>
    <!--/*Lower message content for the owner of the upload request */-->
    <!--/*Lower message content for recipients of the upload request */-->
    <th:block  data-th-if="(${!warnOwner})">
      <th:block  data-th-if="!(${isRestricted})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${isRestricted})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', messages_french = 'beginningMainMsgForRecipient =   L\''invitation dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> va expirer dans <b>{2} jours</b>
beginningMainMsgGrouped =   Votre invitation groupée sera clôturée dans  <b>{0} jours</b>.
beginningMainMsgUnGrouped =   Votre invitation au dépôt sera clôturée dans  <b>{0} jours</b>.
defaultSubject = : {0}
endingMainMsgPlural = et vous avez actuellement reçu <b>{0} fichiers</b>.
endingMainMsgPlural = Il y a un total de <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = et vous avez actuellement reçu <b>1 fichier</b>.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans le dépôt.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate =  Date d\''activation
invitationCreationDate = Date de clôture
msgTitle = Message lié à l\''invitation :
recipientsURequest = Destinataires
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturée
subjectForRecipient = L\'' invitation au dépôt de {0} {1} sera bientôt clôturée
uploadFileBtn = Déposer un fichier', messages_english = 'beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>\''s The Upload Request is about to reach it\''s end date in <b>{2} days</b>
beginningMainMsgGrouped = Your invitation will be closed in  <b>{0} days</b>.
beginningMainMsgUnGrouped =  Your invitation is about to be closed in <b>{0} days</b>.
endingMainMsgPlural =  and you currently have received<b>{0} files</b>.
endingMainMsgPlural = There are a total of <b> {0} files </b> in the depot.
endingMainMsgPluralForRecipient = and so far you have sent <b> {0} files </b> in the depot.
endingMainMsgSingular =   and you currently have received<b>1 file</b>.
endingMainMsgSingular = There is a total of <b>1 file </b> in the repository.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle =  Upload Request\''s  attached message :
recipientsURequest = Recipients
subjectForOwner =  Your invitation is about to be closed.
subjectForRecipient =  {0} {1}\''s  invitation is about to be closed
uploadFileBtn = Upload a file' WHERE id =12;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content container*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})" >
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="(${isRestricted})"   data-th-utext="#{beginningMainMsgUnGrouped}"></span>
            <span  data-th-if="!(${isRestricted})"   data-th-utext="#{beginningMainMsgGrouped}"></span>
            <span data-th-if="(${documentsCount} ==  1)"   data-th-utext="#{endingMainMsgSingular}" ></span>
            <span  data-th-if="(${documentsCount} >  1)"   data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for owner of the upload request */-->
        <!--/* upper message content for recipients of the upload request */-->
        <th:block data-th-if="(${!warnOwner})" >
          <!--/* Greetings */-->
          <th:block  data-th-replace="layout :: greetings(${requestRecipient.mail})" />
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"  data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of upper main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section><!--/* End of uppermain-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <!--/*Lower message content for the owner of the upload request */-->
    <th:block  data-th-if="(${warnOwner})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    </th:block>
    <!--/*Lower message content for the owner of the upload request */-->
    <!--/*Lower message content for recipients of the upload request */-->
    <th:block  data-th-if="(${!warnOwner})">
      <th:block  data-th-if="!(${isRestricted})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${isRestricted})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', messages_french = 'beginningMainMsgForRecipient = L\''invitation de Dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a expiré.
beginningMainMsgGrouped = Votre Invitation de Dépôt groupée a expiré.
beginningMainMsgUnGrouped = Votre Invitation de Dépôt a expiré.
endingMainMsgPlural = et vous avez reçu un total  de <b>{0} fichiers</b>.
endingMainMsgPluralForRecipient = et vous avez  envoyé  <b> {0} fichiers </b>.
endingMainMsgSingular = et vous avez  reçu au total <b>1 fichier</b>.
endingMainMsgSingularForRecipient = et vous avez  envoyé <b>1 fichier </b>.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationClosureDate = Date  de clôture
invitationCreationDate =  Date d\''activation
msgTitle = Message lié à l\''Invitation de Dépôt :
recipientsURequest = Destinataires
subjectForOwner = Votre Invitation de Dépôt est clôturée
subjectForRecipient = L\'' Invitation de Dépôt de {0} est clôturée', messages_english = 'beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>\''s Upload Request has expired
beginningMainMsgGrouped = Your grouped Upload Request has expired
beginningMainMsgUnGrouped = Your Upload Request has expired
endingMainMsgPlural = and you have received a total of <b>{0} files</b>.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b>.
endingMainMsgSingular = and you have received a total of <b>1 file</b>.
endingMainMsgSingularForRecipient = and you currently have uploaded <b>1 file </b> to the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationClosureDate = Closure date
invitationCreationDate = Activation date
msgTitle = Upload Request\''s  attached message :
recipientsURequest = Recipients
subjectForOwner = Your invitation {0} is now closed
subjectForRecipient =  {0} {1}\''s  invitation {2} is now closed' WHERE id = 13;
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(body)}">
      <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
        </span>
        <span id="message-content" data-th-text="*{body}" style="white-space: pre-line;">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection(~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div>
          <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
        </div>
          <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
                 <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                </span>
 <span th:with="df=#{customDate}" data-th-text="${#dates.format(request.activationDate,df)}">7th of November, 2018</span>
        </p>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <div data-th-if="${!#strings.isEmpty(request.activationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
       <div data-th-if="(${totalMaxDepotSize})">
               <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
         </div>
  <div data-th-if="!(${isRestricted})">
         <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>', messages_french = 'activationDate = Ouverture du dépôt le
closureDate = Date de clôture
customDate= d MMMM yyyy.
depotSize = Taille autorisée
mainMsg = <b>{0} {1}</b> a créé une Invitation de dépôt, qui sera ouverte le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires
subject = {0} {1} vous a créé une Invitation de Dépôt', messages_english = 'activationDate = Activation date
closureDate = Closure date
customDate= MMMM d, yyyy.
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> has invited you to access to his Upload Request, sets to open
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients
subject = {0} {1} has sent an invitation to access to his Upload Request.' WHERE id = 20;

UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
            <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
            <span>
              <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
               link
             </a>
            </span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${threadMember.admin})">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
    </th:block>
    <th:block data-th-if="(!${threadMember.admin})">
        <th:block data-th-if="(${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
        </th:block>
        <th:block data-th-if="(!${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
        </th:block>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', messages_french = 'workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au groupe de travail <br>
subject = Vous avez été ajouté au groupe de travail {0}
workGroupRight = Droit par défaut 
workGroupNameTitle = Nom du groupe de travail', messages_english = 'workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the workgroup <br>
subject = You have been added to the workgroup {0}
workGroupRight = Default right
workGroupNameTitle = Workgroup Name' WHERE id = 28;

UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg}"></span>
          <span>
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${workGroupName}" th:href="@{${workGroupLink}}" >
                link </a>
          </span>
          <span data-th-utext="#{mainMsgNes(${owner.firstName},${owner.lastName})}"></span>

             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${threadMember.admin})">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
    </th:block>
    <th:block data-th-if="(!${threadMember.admin})">
        <th:block data-th-if="(${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
        </th:block>
        <th:block data-th-if="(!${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
        </th:block>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupUpdatedDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', messages_french = 'workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le groupe de travail
mainMsgNext = ont été mis à jour par <b> {0} <span style="text-transform:uppercase">{1}</span> </b>.
subject =  Vos droits sur le groupe de travail {0} ont été mis à jour
workGroupRight =  Nouveau droit
workGroupNameTitle = Nom du groupe de travail', messages_english = 'workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the workgroup
mainMsgNext= have been updated by  <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the workgroup {0} was updated.
workGroupRight = Current right
workGroupNameTitle = Workgroup Name' WHERE id =29;

UPDATE mail_content SET messages_french = 'downloadBtn = Télécharger
downloadLink = Lien de téléchargement
helpMsgSingular =  pour visualiser le document partagé.
helpMsgPlural =pour visualiser tous les documents du partage.
helpPasswordMsgSingular = Cliquez sur le lien pour le télécharger et saisissez le mot de passe fourni ci.
helpPasswordMsgPlural = Cliquez sur le lien pour les télécharger et saisissez le mot de passe fourni.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a partagé {2} fichiers avec vous.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a partagé {2} fichier  avec vous.
msgFrom = Vous avez un message de
name = {0} {1}
password = Mot de passe
subjectCustomAlt =de {0} {1}
subjectPlural =  {0} {1} vous a partagé des fichiers
subjectSingular =  {0} {1} vous a partagé un fichier
click = Cliquez sur ce
link = lien' WHERE id = 2;

UPDATE mail_layout SET layout='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans'',arial,Helvetica,sans-serif;">
    <center>
      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
             style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"
             width="90%">
        <tbody>
        <tr>
          <td align="center" style="border-collapse:collapse" valign="top">
            <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px" width="90%">
              <tbody>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table bgcolor="transparent" border="0" cellpadding="0" cellspacing="0"
                         style="background-color:transparent;border-bottom:0;padding:0px">
                    <tbody>
                    <tr>
                      <td align="center" bgcolor="#ffffff"
                          style="border-collapse:collapse;color:#202020;background-color:#ffffff;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle">
                        <div align="center" style="text-align:center">
                          <a target="_blank"
                             style="border:0;line-height:100%;outline:none;text-decoration:none;width:233px;height:57px;padding:20px 0 20px 0"
                             data-th-href="@{${linshareURL}}">
                            <img src="cid:logo.linshare@linshare.org"
                              style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233" alt="Logo"
                              height="57"/>
                          </a>
                        </div>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table border="0" cellpadding="0" cellspacing="0" style="width:95%;max-width:500px" width="95%">
                    <tbody>
                    <tr>
                      <td
                        style="border-collapse:collapse;border-radius:3px;font-weight:300;border:1px solid #e1e1e1;background:white;border-top:none;"
                        valign="top">
                        <table border="0" cellpadding="20" cellspacing="0" width="100%">
                          <tbody>
                          <tr>
                            <td style="border-collapse:collapse;padding:0px" valign="top">
                              <div align="left"
                                   style="color:#505050;font-size:14px;line-height:150%;text-align:left">
                                <th:block data-th-replace="${upperMainContentArea}"/>
                              </div>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                     style="background-color: #f8f8f8;">
                                <tbody>
                                <tr>
                                  <td width="15" style="border-top:1px solid #c9cacc;">
                                  </td>
                                  <td width="20"><img src="cid:logo.arrow@linshare.org"
                                    width="20" height="9" border="0" style="display:block;" alt="down arrow"/></td>
                                  <td style="border-top:1px solid #c9cacc;"></td>
                                </tr>
                                </tbody>
                              </table>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                <tbody>
                                <tr>
                                  <td>
                                    <div align="left"
                                         style="font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">
                                      <div align="left"
                                           style="font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">
                                        <th:block data-th-replace="${bottomSecondaryContentArea}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                                </tbody>
                              </table>
                              <table width="100%"
                                     style="background:#f0f0f0;text-align:left;color:#a9a9a9;line-height:20px;border-top:1px solid #e1e1e1">
                                <tbody>
                                <tr data-th-insert="footer :: email_footer">
                                </tr>
                                </tbody>
                              </table>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table bgcolor="white" border="0" cellpadding="10" cellspacing="0"
                         style="background-color:white;border-top:0" width="400">
                    <tbody>
                    <tr>
                      <td style="border-collapse:collapse" valign="top">
                        <table border="0" cellpadding="10" cellspacing="0" width="100%">
                          <tbody>
                          <tr>
                            <td bgcolor="#ffffff" colspan="2"
                                style="border-collapse:collapse;background-color:#ffffff;border:0;padding: 0 8px;"
                                valign="middle">
                              <div align="center"
                                   style="color:#707070;font-size:12px;line-height:125%;text-align:center">
                                <!--/* Do not remove the copyright  ! */-->
                                <div data-th-insert="copyright :: copyright">
                                  <p
                                    style="line-height:15px;font-weight:300;margin-bottom:0;color:#b2b2b2;font-size:10px;margin-top:0">
                                    You are using the Open Source and free version of
                                    <a href="http://www.linshare.org/"
                                       style="text-decoration:none;color:#b2b2b2;"><strong>LinShare</strong>™</a>,
                                    powered by <a href="http://www.linshare.org/"
                                                  style="text-decoration:none;color:#b2b2b2;"><strong>Linagora</strong></a>
                                    ©&nbsp;2009–2020. Contribute to
                                    Linshare R&amp;D by subscribing to an Enterprise offer.
                                  </p>
                                </div>
                              </div>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              </tbody>
            </table>
          </td>
        </tr>
        </tbody>
      </table>
    </center>
  </div>
</div>
<!--/* End of common base layout template*/-->
 </body>
 </html>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoEditedItem(titleInfo,oldValue,newValue)">
     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
    <br/>
      <th:block th:replace="${oldValue}" />  -> <th:block th:replace="${newValue}" />
</div>
<!--/* Edited  date  display settings  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateArea(titleInfo,oldValue,newValue)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
    <br/>
 <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue,df)}"/> -> 
 <th:block  th:with="df=#{date.format}" data-th-text="${#dates.format(newValue,df)}"/>
</div>
<!--/* Common header template */-->
<head  data-th-fragment="header">
  <title data-th-text="${mailSubject}">Mail subject</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<!--/* Common greeting  template */-->
<div data-th-fragment="greetings(currentFirstName)">
  <p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px"
 data-th-text="#{welcomeMessage(${currentFirstName})}">
Hello Amy,</p>
</div>
<!--/* Common upper email section  template */-->
<div data-th-fragment="contentUpperSection(sectionContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;">
   <div align="left" style="padding:24px 17px 5px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;
border-top: 1px solid #e1e1e1;">
      <th:block th:replace="${sectionContent}" />
       </div>
</div>
<!--/* Common message section template */-->
<div data-th-fragment="contentMessageSection(messageTitle,messageContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;" >
          <div align="left" style="padding:24px 17px 15px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;">
<p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px">
<th:block th:replace="${messageTitle}" />
</p>
<p style="margin:0;color: #88a3b1;">
<th:block th:replace="${messageContent}" />
</p>
</div>
</div>
<!--/* Common link style */-->
<div data-th-fragment="infoActionLink(titleInfo,urlLink)"  style="margin-bottom:17px;" >
<span style="font-weight:bold;" data-th-text="${titleInfo}" >Download link title  </span>
  <br/>
<a target="_blank" style="color:#1294dc;text-decoration:none;"
                          data-th-text="${urlLink}"  th:href="@{${urlLink}}"   >Link </a>
</div>
<!--/* Common date display  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateArea(titleInfo,contentInfo)">
  <div data-th-if="${contentInfo != null}">
      <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
      <br/>
      <span  th:with="df=#{date.format}" data-th-text="${#dates.format(contentInfo,df)}">7th of November, 2018</span>
  </div>
</div>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoStandardArea(titleInfo,contentInfo)">
     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
    <br/>
      <th:block th:replace="${contentInfo}" />
</div>
<!--/* Common button action style */-->
<span   data-th-fragment="actionButtonLink(labelBtn,urlLink)">
<a
style="border-radius:3px;font-size:15px;color:white;text-decoration:none;padding: 10px 7px;width:auto;max-width:50%;display:block;background-color: #42abe0;text-align: center;margin-top: 17px;"  target="_blank"
data-th-text="${labelBtn}"  th:href="@{${urlLink}}">Button label</a>
</span>
<!--/* Common recipient listing for external and internal users */-->
<div  style="margin-bottom:17px;" data-th-fragment="infoRecipientListingArea(titleInfo,arrayRecipients)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Recipients</span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="recipientData: ${arrayRecipients}">
<div data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
         <span style="color:#787878;font-size:13px"  data-th-utext="${recipientData.mail}">
        my-file-name.pdf
         </span>
</div>
<div data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
         <span  style="color:#787878;font-size:13px">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
</div>
      </li>
   </ul>
</div>
<div data-th-if="(${!isAnonymous})">
         <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share   */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileLinksListingArea(titleInfo,arrayFileLinks,isAnonymous)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
<div data-th-if="(${!isAnonymous})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}" th:href="@{${shareLink.href}}">
        my-file-name.pdf
         </a>
</div>
<div data-th-if="(${isAnonymous})">
         <span style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
</div>
<!--/* Lists all file links in a share  and checks witch one are the recpient\s */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileListWithMyUploadRefs(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
<th:block  data-th-if="(${shareLink.mine})"> <span  data-th-text="#{common.byYou}">|  By You</span></th:block >
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share along with their download status   */-->
<div  data-th-fragment="infoFileListUploadState(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
<li style="color:#00b800;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${shareLink.downloaded})">
 <th:block data-th-if="(${shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px ;font-weight:bold"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
 <th:block data-th-if="(${!shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
      </li>
<li style="color:#787878;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${!shareLink.downloaded})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
      </li>
   </ul>
</div>
<!--/* Lists all recpients download states per file   */-->
<div   style="margin-bottom:17px;"  data-th-fragment="infoFileListRecipientUpload(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 0px; margin: 0;list-style-type:none;">
<li style="color:#787878;font-size:10px;margin-top:10px;"  th:each="shareLink : ${arrayFileLinks}" >
    <span style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
  <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
    <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">
test-file.jpg</span></a>
    <span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>
    <span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>
    </span>
    <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >
 <th:block  th:each="recipientData: ${shareLink.shares}">
   <th:block data-th-if="(${!recipientData.downloaded})" >
      <li style="color:#787878;font-size:15px"  >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
      <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"
           data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>
      </li>
   </th:block>
<th:block data-th-if="(${recipientData.downloaded})">
   <li style="color:#00b800;font-size:15px" >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span  style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
<th:block  data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"> able.cornell@linshare.com </span>
  </th:block>
      </li>
   </th:block>
</th:block>
    </ul>
</li>
   </ul>
</div>' WHERE id=1;

--End mail Content

-- Functionality : JWT_PERMANENT_TOKEN
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (290, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (291, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (60, true, 'JWT_PERMANENT_TOKEN', 290, 291, 1, now(), now());

-- Functionality : JWT_PERMANENT_TOKEN__USER_MANAGEMENT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (292, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (293, false, false, 1, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (294, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (61, false, 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT', 292, 293, 294, 1, 'JWT_PERMANENT_TOKEN', true, now(), now());

--Update purgeStep for deleted workGroups
UPDATE account SET purge_step = 'PURGED' where ((account_type = 5) AND (purge_step = 'IN_USE') AND (destroyed > 0));

-- End of your requests

-- LinShare version
SELECT ls_version();

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed != 0;
COMMIT;


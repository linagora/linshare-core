-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'2.4.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '2.4.0';
	DECLARE version_from VARCHAR := '2.3.0';
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

ALTER TABLE mail_attachment ALTER COLUMN language DROP NOT NULL;

--Cancel NOT NULL constraint for table statistic
ALTER TABLE statistic ALTER COLUMN domain_parent_id DROP NOT NULL;

--Enable WORKGROUP__FILE_EDITION functionality
UPDATE policy SET status = TRUE, default_status = TRUE WHERE id=303;
UPDATE policy SET policy = 1 WHERE id = 303;
UPDATE policy SET system = FALSE WHERE id = 303;

UPDATE policy SET status = TRUE, default_status = TRUE WHERE id = 304;
UPDATE policy SET policy = 1 WHERE id = 304;
UPDATE policy SET system = FALSE WHERE id = 304;

UPDATE policy SET status = TRUE, default_status = TRUE WHERE id = 305;
UPDATE policy SET policy = 1 WHERE id = 305;
UPDATE policy SET system = FALSE WHERE id = 305;


--Cancel NOT NULL constraint of document_id and ls_type for upload_request_entry
ALTER TABLE upload_request_entry ALTER COLUMN ls_type DROP NOT NULL;
ALTER TABLE upload_request_entry ALTER COLUMN document_id DROP NOT NULL;

-- Group ldap pattern
WITH ldap_pattern_already_exists AS ( UPDATE ldap_pattern SET id=id WHERE id=4 RETURNING *)
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
	SELECT 4,
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
	'workgroup-' WHERE NOT EXISTS ( SELECT * FROM ldap_pattern_already_exists);

-- ldap attributes
WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=13 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 13, 'mail', 'member_mail', false, true, true, false, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=14 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 14, 'givenName', 'member_firstname', false, true, true, false, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=15 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 15, 'cn', 'group_name_attr', false, true, true, true, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=16 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 16, 'member', 'extended_group_member_attr', false, true, true, true, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=17 RETURNING *)
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 17, 'sn', 'member_lastname', false, true, true, false, 4 WHERE NOT EXISTS (SELECT * FROM already_exists);

-- Demo ldap pattern.
WITH ldap_pattern_already_exists AS ( UPDATE ldap_pattern SET id=id WHERE id=5 RETURNING *)
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
	SELECT 5,
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
    now() WHERE NOT EXISTS ( SELECT * FROM ldap_pattern_already_exists);

-- ldap_attribute
WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=18 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 18, 'user_mail', 'mail', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=19 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 19, 'user_firstname', 'givenName', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=12 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 20, 'user_lastname', 'sn', false, true, true, 5, true WHERE NOT EXISTS (SELECT * FROM already_exists);

WITH already_exists AS (UPDATE ldap_attribute SET id=id WHERE id=21 RETURNING *)
INSERT INTO ldap_attribute
(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
SELECT 21, 'user_uid', 'uid', false, true, true, 5, false WHERE NOT EXISTS (SELECT * FROM already_exists);

--Update purgeStep for deleted workGroups
UPDATE account SET purge_step = 'PURGED' where ((account_type = 5) AND (purge_step = 'IN_USE') AND (destroyed > 0));

-- Upgrade Task
-- TASK: UPGRADE_2_4_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT
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
 (31,
 'UNDEFINED',
 'UPGRADE_2_4_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT',
 'UPGRADE_2_4',
  null,
  null,
  31,
 'NEW',
 'MANDATORY',
  now(),
  now(),
  null);
 
  -- End Upgrade Task

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

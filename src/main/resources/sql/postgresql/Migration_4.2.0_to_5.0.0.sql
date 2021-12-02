-- Migration script to upgrade from LinShare 4.2 to LinShare 4.3. 

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'5.0.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '5.0.0';
	DECLARE version_from VARCHAR := '4.2.0';
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

SELECT ls_prechecks();
SELECT ls_check_user_connected();

SET client_min_messages = warning;


---- Here your queries
ALTER TABLE user_provider ADD COLUMN domain_discriminator VARCHAR(255);
ALTER TABLE user_provider ADD COLUMN check_external_user_id  bool;
ALTER TABLE user_provider ADD COLUMN use_email_locale_claim  bool;
ALTER TABLE user_provider ADD COLUMN use_role_claim  bool;
ALTER TABLE user_provider ADD COLUMN move_between_domain_claim bool;
ALTER TABLE user_provider ADD COLUMN use_access_claim bool;

ALTER TABLE user_provider ALTER COLUMN ldap_connection_id DROP NOT NULL;
ALTER TABLE user_provider ALTER COLUMN ldap_pattern_id DROP NOT NULL;

ALTER TABLE user_provider ADD COLUMN twake_connection_id int8;
ALTER TABLE user_provider ADD COLUMN twake_company_id VARCHAR(255);
ALTER TABLE user_provider ADD CONSTRAINT FKuser_provi1641 FOREIGN KEY (twake_connection_id) REFERENCES remote_server (id);

CREATE INDEX account_mail
  ON account (mail);
CREATE INDEX account_first_name
  ON account (first_name);
CREATE INDEX account_last_name
  ON account (last_name);

-- Ldap Drive filter
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    system,
    description,
    search_page_size,
    creation_date,
    modification_date,
    search_all_groups_query,
    search_group_query,
    group_prefix)
    SELECT 6,
    'c59078f1-2366-4360-baa0-6c089202e9a6',
    'DRIVE_LDAP_PATTERN',
    'Default Ldap Drive filter',
    true,
    'Description of default LDAP Drive filter',
    100,
    NOW(),
    NOW(),
    'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=drive-*))");',
    'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=drive-" + pattern + "))");',
    'drive-' WHERE NOT EXISTS (SELECT id FROM ldap_pattern WHERE id = 6);

-- Update ldap drive filter
UPDATE ldap_pattern SET pattern_type = 'DRIVE_LDAP_PATTERN', description = 'Description of default LDAP Drive filter', label = 'Default Ldap Drive filter' WHERE uuid='c59078f1-2366-4360-baa0-6c089202e9a6';

-- ldap attributes
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 22, 'mail', 'member_mail', false, true, true, false, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 22);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 23, 'givenName', 'member_firstname', false, true, true, false, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 23);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 24, 'cn', 'group_name_attr', false, true, true, true, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 24);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 25, 'member', 'extended_group_member_attr', false, true, true, true, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 25);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
SELECT 26, 'sn', 'member_lastname', false, true, true, false, 6 WHERE NOT EXISTS (SELECT id FROM ldap_attribute WHERE id = 26);


-- UPGRADE_5_0_ADD_DOMAIN_TO_WORK_GROUP
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (45,
  'UNDEFINED',
  'UPGRADE_5_0_ADD_DOMAIN_TO_WORK_GROUP',
  'UPGRADE_5_0',
  45,
  'NEW',
  'REQUIRED',
  now(),
  now());
 
  -- UPGRADE_5_0_ADD_DOMAIN_TO_DRIVE
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (46,
  'UNDEFINED',
  'UPGRADE_5_0_ADD_DOMAIN_TO_DRIVE',
  'UPGRADE_5_0',
  46,
  'NEW',
  'REQUIRED',
  now(),
  now());

-- Remote server (previously ldap_connection)
ALTER TABLE ldap_connection ADD COLUMN server_type VARCHAR(255) DEFAULT 'LDAP' NOT NULL;
ALTER TABLE ldap_connection ALTER COLUMN server_type DROP DEFAULT;

ALTER TABLE ldap_connection RENAME TO remote_server;

ALTER TABLE remote_server ADD COLUMN client_id VARCHAR(255);
ALTER TABLE remote_server ADD COLUMN client_secret VARCHAR(255);

ALTER TABLE functionality_integer ADD COLUMN unlimited_value bool DEFAULT 'false' NOT NULL;
ALTER TABLE functionality_integer ADD COLUMN unlimited_value_used bool DEFAULT 'false' NOT NULL;
ALTER TABLE functionality_unit ADD COLUMN unlimited_value bool DEFAULT 'false' NOT NULL;
ALTER TABLE functionality_unit ADD COLUMN unlimited_value_used bool DEFAULT 'false' NOT NULL;

UPDATE functionality_integer SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'SHARE_EXPIRATION');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION');
UPDATE functionality_unit SET unlimited_value_used = TRUE WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION');


UPDATE functionality_unit SET integer_max_value = 900
	WHERE
		functionality_id IN (SELECT id FROM functionality WHERE identifier = 'WORK_GROUP__DOWNLOAD_ARCHIVE')
	AND
		integer_max_value = -1;
UPDATE functionality_unit SET unlimited_value = TRUE, integer_max_value = 0 WHERE integer_max_value = -1;


-- UPGRADE_5_0_DELETE_EVENT_NOTIFICATION_COLLECTION
 INSERT INTO upgrade_task
  (id,
  uuid,
  identifier,
  task_group,
  task_order,
  status,
  priority,
  creation_date,
  modification_date)
VALUES
  (47,
  'UNDEFINED',
  'UPGRADE_5_0_DELETE_EVENT_NOTIFICATION_COLLECTION',
  'UPGRADE_5_0',
  47,
  'NEW',
  'REQUIRED',
  now(),
  now());


-- Update INTERNAL_CAN_UPLOAD functionality's identifier to INTERNAL_CAN_UPLOAD
UPDATE functionality SET identifier = 'INTERNAL_ENABLE_PERSONAL_SPACE', system = false WHERE id in (SELECT id FROM functionality WHERE identifier = 'INTERNAL_CAN_UPLOAD');
-- Update activation policy to mandatory and system
UPDATE policy SET system = true, policy = 0 WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'INTERNAL_ENABLE_PERSONAL_SPACE');
-- Update configuration policy to allowed
UPDATE policy SET policy = 1, system = false, status = true, default_status = true WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier = 'INTERNAL_ENABLE_PERSONAL_SPACE');

ALTER TABLE recipient_favourite ADD COLUMN expiration_date       timestamp(6);

-- Functionality : COLLECTED_EMAILS_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (336, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (337, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, param, creation_date, modification_date)
	VALUES (70, false, 'COLLECTED_EMAILS_EXPIRATION', 336, 337, NULL, 1, false, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (22, 0, 0), (23, 0, 0);
INSERT INTO functionality_unit(functionality_id, integer_max_value, unit_id, max_unit_id, integer_default_value, default_value_used, max_value_used)
	VALUES (70, 0, 22, 23, 8, true, false);

UPDATE functionality SET identifier='SHARED_SPACE' WHERE identifier = 'DRIVE';
UPDATE functionality SET parent_identifier='SHARED_SPACE' WHERE parent_identifier = 'WORK_GROUP';
UPDATE functionality SET parent_identifier='SHARED_SPACE' WHERE parent_identifier = 'DRIVE';
ALTER TABLE policy ADD COLUMN delete_it BOOL DEFAULT 'FALSE';
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'WORK_GROUP');
UPDATE policy SET delete_it = true WHERE id IN (SELECT policy_configuration_id FROM functionality WHERE identifier = 'WORK_GROUP');
DELETE FROM functionality WHERE identifier = 'WORK_GROUP';
DELETE FROM policy WHERE delete_it = true;
ALTER TABLE policy DROP COLUMN delete_it;


---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;

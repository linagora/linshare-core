-- Migration script to upgrade from LinShare 4.2 to LinShare 4.3. 

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'4.3.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	-- TODO: CHANGE THE VERSIONS
	DECLARE version_to VARCHAR := '4.3.0';
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
VALUES(22, 'mail', 'member_mail', false, true, true, false, 6);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(23, 'givenName', 'member_firstname', false, true, true, false, 6);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(24, 'cn', 'group_name_attr', false, true, true, true, 6);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(25, 'member', 'extended_group_member_attr', false, true, true, true, 6);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(26, 'sn', 'member_lastname', false, true, true, false, 6);
---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;

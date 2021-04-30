-- Postgresql migration script from LinShare 4.1.0 to 4.2.0

BEGIN;
---- Prechecks 
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;


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

-- Fix LinShare DB version (4.1.0 instead of 2.4.0)
CREATE OR REPLACE FUNCTION check_to_fix_version_from_2_4_to_4_1_0() RETURNS void AS $$
BEGIN
	DECLARE current_version VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	BEGIN
		IF (current_version='2.4.0') THEN
			IF(EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='functionality_unit' AND column_name='integer_max_value')) then
				RAISE INFO 'Fixing LinShare DB version | Update from 2.4.0 to 4.1.0';
	        	UPDATE version SET version='4.1.0' WHERE version='2.4.0';
	        ELSE 
	        	RAISE WARNING 'Your DB schema did not match with LinShare DB 4.1.0';
	        END IF;
	    ELSE
	    	RAISE INFO 'No operation was performed. This script is applied only to fix LinShare DB version from 2.4.0 to 4.1.0';
	    END IF;
	END;
END
$$ LANGUAGE plpgsql;

-- Check if can apply the patch to the current LinShare version 4.1.0
CREATE OR REPLACE FUNCTION ls_prechecks_patch() RETURNS void AS $$
BEGIN
	DECLARE current_version VARCHAR := '4.1.0';
	DECLARE start VARCHAR := concat('You are about to apply a database patch to LinShare : ', current_version);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('This patch should only be applied to the version : ', current_version);
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your current LinShare database version is: %', version_history_from;
		RAISE NOTICE 'Your database history is :';
		FOR row IN (SELECT * FROM version ORDER BY id DESC) LOOP
			RAISE INFO '%', row.version;
		END LOOP;
		RAISE NOTICE 'Your database system information is : %', database_info;
		IF (current_version <> version_history_from) THEN
			RAISE WARNING 'You must be in version : % to run this script. You are actually in version: %', current_version, version_history_from;
			IF EXISTS (SELECT * from version where version = current_version) THEN
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

-- Upgrade LinShare version function 
CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'4.2.0', now());
END

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '4.2.0';
	DECLARE version_from VARCHAR := '4.1.0';
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

SELECT ls_check_user_connected();
SELECT check_to_fix_version_from_2_4_to_4_1_0();
SELECT ls_prechecks_patch();
SELECT ls_prechecks();

SET client_min_messages = warning;

-- Clean useless aliases
DROP VIEW IF EXISTS alias_func_list_all;
DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;

---- Here your queries

-- Avoid duplicate unit_id in functionality_unit table function declaration 
CREATE OR REPLACE FUNCTION ls_avoid_duplicate_unit_in_functionality_unit() RETURNS void AS $$
BEGIN
	DECLARE
		functionality_row record;
		functionality_unit_row record;
		count_occurences integer;
		new_unit_id integer;
	BEGIN
		RAISE INFO 'Removing duplicated unit_id in functionality_unit table...';
		FOR functionality_row IN SELECT * FROM functionality WHERE domain_id <> 1 AND id IN (SELECT functionality_id FROM functionality_unit) LOOP
			FOR functionality_unit_row IN SELECT * FROM functionality_unit where functionality_id = functionality_row.id LOOP
				SELECT COUNT(*) INTO count_occurences FROM functionality_unit where max_unit_id = functionality_unit_row.max_unit_id; 
				RAISE INFO 'Found functionality_unit with functionality_id "%" in sub domain', functionality_unit_row.functionality_id;
				IF (count_occurences > 1)
				THEN
					INSERT INTO unit(id, unit_type, unit_value)
						(  SELECT  nextVal('hibernate_sequence'), unit_type, unit_value FROM unit WHERE id = functionality_unit_row.unit_id )
					RETURNING id INTO new_unit_id;
					UPDATE functionality_unit SET max_unit_id = new_unit_id where functionality_id = functionality_unit_row.functionality_id;
					RAISE INFO 'Duplicate found : Updating max_unit_id of functionality_unit with functionality_id "%"', functionality_unit_row.functionality_id;
				END IF;
			END LOOP;
		END LOOP;
		RAISE INFO 'End removing duplicated unit_id in functionality_unit table';
	END;
END
$$ LANGUAGE plpgsql; 

-- Fix avoid duplicated unit_id in functionality_unit table
SELECT ls_avoid_duplicate_unit_in_functionality_unit();

-- Compute Workgroup daily statistics
UPDATE operation_history SET operation_value = - operation_value WHERE operation_type = 1 AND container_type = 'WORK_GROUP' AND operation_value > 0;
UPDATE statistic SET delete_operation_sum = -delete_operation_sum WHERE delete_operation_sum > 0 AND statistic_type = 'WORK_GROUP_DAILY_STAT';
UPDATE statistic SET diff_operation_sum = create_operation_sum + delete_operation_sum WHERE statistic_type = 'WORK_GROUP_DAILY_STAT';

CREATE OR REPLACE FUNCTION ls_compute_daily_workgroup_statistic() RETURNS void AS $$
BEGIN
	DECLARE current_actual_operation_sum BIGINT := 0;
	DECLARE stat_row record;
	DECLARE is_first_row boolean := TRUE;
	DECLARE work_group_row record;
	BEGIN
		RAISE NOTICE 'Computing all workgroup daily statistic';
		FOR work_group_row IN (SELECT DISTINCT(account_id) FROM statistic WHERE statistic_type = 'WORK_GROUP_DAILY_STAT') LOOP
			RAISE INFO 'Computing daily stats of workgroup with ID %', work_group_row.account_id;
			FOR stat_row IN (SELECT * FROM statistic WHERE statistic_type = 'WORK_GROUP_DAILY_STAT' AND account_id = work_group_row.account_id ORDER BY creation_date ASC) LOOP
				RAISE INFO 'ID STAT : %', stat_row.id;
				IF	 is_first_row THEN
					RAISE INFO 'First stat row skipped';
					current_actual_operation_sum = stat_row.actual_operation_sum;
					is_first_row = FALSE;
				ELSE
					RAISE INFO 'current_actual_operation_sum previous : %', current_actual_operation_sum;
					current_actual_operation_sum = current_actual_operation_sum + stat_row.diff_operation_sum;
					RAISE INFO 'diff_operation_sum : %', stat_row. diff_operation_sum;
					RAISE INFO 'current_actual_operation_sum next : %', current_actual_operation_sum;
					UPDATE statistic SET actual_operation_sum = current_actual_operation_sum WHERE id = stat_row.id;
				END IF;
			END LOOP;
		END LOOP;
		RAISE NOTICE 'END Computing all workgroup daily statistic';
	END;
END
$$ LANGUAGE plpgsql;
 
CREATE OR REPLACE FUNCTION ls_compute_weekly_workgroup_statistic() RETURNS void AS $$
BEGIN
	DECLARE current_actual_operation_sum BIGINT := 0;
	DECLARE current_create_operation_sum BIGINT := 0;
	DECLARE current_delete_operation_sum BIGINT := 0;
	DECLARE current_diff_operation_sum BIGINT := 0;
	DECLARE is_first_row boolean := TRUE;
	DECLARE weekly_stat_row record;
	DECLARE daily_stat_row record;
	DECLARE work_group_row record;
	BEGIN
		RAISE NOTICE 'Computing all workgroup weekly statistic';
		FOR work_group_row IN (SELECT DISTINCT(account_id) FROM statistic WHERE statistic_type = 'WORK_GROUP_WEEKLY_STAT') LOOP
			RAISE INFO 'Computing weekly stats of workgroup with ID %', work_group_row.account_id;
			FOR weekly_stat_row IN (SELECT * FROM statistic WHERE statistic_type = 'WORK_GROUP_WEEKLY_STAT' AND account_id = work_group_row.account_id ORDER BY creation_date ASC) LOOP
				current_create_operation_sum = 0;
				current_delete_operation_sum = 0;
				current_diff_operation_sum = 0;
				IF is_first_row THEN
					RAISE INFO 'First stat row skipped';
					current_actual_operation_sum = weekly_stat_row.actual_operation_sum;
					is_first_row = FALSE;
				ELSE
					select 
						sum(create_operation_sum), sum(delete_operation_sum), sum(diff_operation_sum) 
					INTO current_create_operation_sum, current_delete_operation_sum, current_diff_operation_sum
					FROM statistic 
					WHERE statistic_type = 'WORK_GROUP_DAILY_STAT' 
						AND DATE_PART('day', weekly_stat_row.statistic_date::timestamp - statistic_date::timestamp) <= 7 
						AND DATE_PART('day', weekly_stat_row.statistic_date::timestamp - statistic_date::timestamp) >= 0 
						AND account_id = work_group_row.account_id ;
					
					RAISE INFO 'WEEKLY STAT with ID "%" DATE : %', weekly_stat_row.id, weekly_stat_row.statistic_date;
					RAISE INFO 'current_actual_operation_sum previous : %', current_actual_operation_sum;
					current_actual_operation_sum = current_actual_operation_sum + current_diff_operation_sum;
					RAISE INFO 'current_actual_operation_sum next : %', current_actual_operation_sum;
					UPDATE statistic 
						SET 
							actual_operation_sum = current_actual_operation_sum,
							create_operation_sum = current_create_operation_sum,
							diff_operation_sum = current_diff_operation_sum,
							delete_operation_sum = current_delete_operation_sum
						WHERE id = weekly_stat_row.id;
				END IF;
			END LOOP;
		END LOOP;
		RAISE NOTICE 'END Computing all workgroup weekly statistic';
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_compute_monthly_workgroup_statistic() RETURNS void AS $$
BEGIN
	DECLARE current_actual_operation_sum BIGINT := 0;
	DECLARE current_create_operation_sum BIGINT := 0;
	DECLARE current_delete_operation_sum BIGINT := 0;
	DECLARE current_diff_operation_sum BIGINT := 0;
	DECLARE is_first_row boolean := TRUE;
	DECLARE monthly_stat_row record;
	DECLARE weekly_stat_row record;
	DECLARE work_group_row record;
	BEGIN
		RAISE NOTICE 'Computing all workgroup monthly statistic';
		FOR work_group_row IN (SELECT DISTINCT(account_id) FROM statistic WHERE statistic_type = 'WORK_GROUP_MONTHLY_STAT') LOOP
			RAISE INFO 'Computing monthly stats of workgroup with ID %', work_group_row.account_id;
			FOR monthly_stat_row IN (SELECT * FROM statistic WHERE statistic_type = 'WORK_GROUP_MONTHLY_STAT' AND account_id = work_group_row.account_id ORDER BY creation_date ASC) LOOP
				current_create_operation_sum = 0;
				current_delete_operation_sum = 0;
				current_diff_operation_sum = 0;
				IF is_first_row THEN
					RAISE INFO 'First stat row skipped';
					current_actual_operation_sum = monthly_stat_row.actual_operation_sum;
					is_first_row = FALSE;
				ELSE
					SELECT 
						sum(create_operation_sum), sum(delete_operation_sum), sum(diff_operation_sum) 
					INTO current_create_operation_sum, current_delete_operation_sum, current_diff_operation_sum
					FROM statistic 
					WHERE statistic_type = 'WORK_GROUP_WEEKLY_STAT' 
						AND DATE_PART('month', monthly_stat_row.statistic_date::TIMESTAMP) =  DATE_PART('month', statistic_date::TIMESTAMP) + 1
						AND account_id = work_group_row.account_id ;
					
					RAISE INFO 'MONTHLY STAT with ID "%" DATE : %', monthly_stat_row.id, monthly_stat_row.statistic_date;
					RAISE INFO 'current_actual_operation_sum previous : %', current_actual_operation_sum;
					current_actual_operation_sum = current_actual_operation_sum + current_diff_operation_sum;
					RAISE INFO 'current_actual_operation_sum next : %', current_actual_operation_sum;
					UPDATE statistic 
						SET 
							actual_operation_sum = current_actual_operation_sum,
							create_operation_sum = current_create_operation_sum,
							diff_operation_sum = current_diff_operation_sum,
							delete_operation_sum = current_delete_operation_sum
						WHERE id = monthly_stat_row.id;
				END IF;
			END LOOP;
		END LOOP;
		RAISE NOTICE 'End computing all workgroup monthly statistic';
	END;
END
$$ LANGUAGE plpgsql;

--Delete duplicated rows in statistic table with same statistic_type and statistic_date
DELETE FROM statistic 
	WHERE id IN (
	SELECT id 
	FROM (
		SELECT id, ROW_NUMBER() 
			OVER( PARTITION BY statistic_date, statistic_type, account_id, domain_id ORDER BY  id ) AS row_num
		FROM statistic ) partition_statistic
	WHERE partition_statistic.row_num > 1 );
	
--Delete recent statistic that will be computed by the batch
DELETE FROM statistic
	WHERE DATE_PART('day', NOW()::timestamp - statistic_date::timestamp) <= 7
		AND DATE_PART('day', NOW()::timestamp - statistic_date::timestamp) >= 0
		AND (statistic_type LIKE '%WEEKLY%' OR statistic_type LIKE '%MONTHLY%') ;
		

SELECT ls_compute_daily_workgroup_statistic();
SELECT ls_compute_weekly_workgroup_statistic();
SELECT ls_compute_monthly_workgroup_statistic();

  -- TASK: UPGRADE_4_2_COMPUTE_ALL_WORKGROUPS_QUOTA
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
 (36,
 'UNDEFINED',
 'UPGRADE_4_2_COMPUTE_ALL_WORKGROUPS_QUOTA',
 'UPGRADE_4_2',
  null,
  null,
  36,
 'NEW',
 'REQUIRED',
  now(),
  now(),
  null);

  -- TASK: UPGRADE_4_2_COMPUTE_CURRENT_VALUE_FOR_DOMAINS
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
  (37,
  'UNDEFINED',
  'UPGRADE_4_2_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  'UPGRADE_4_2',
  null,
  'UPGRADE_4_2_COMPUTE_ALL_WORKGROUPS_QUOTA',
  37,
  'NEW',
  'REQUIRED',
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
  (38,
  'UNDEFINED',
  'UPGRADE_4_2_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA',
  'UPGRADE_4_2',
  null,
  'UPGRADE_4_2_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  38,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- Update default values (integer_max_value integer_default_value) of UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION and UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION
UPDATE functionality_unit
SET    integer_max_value = 4
WHERE  functionality_id IN (SELECT id
                            FROM   functionality f
                            WHERE
              identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION')
       AND integer_max_value = 1
       AND integer_default_value = 3
       AND unit_id IN (SELECT id
                       FROM   unit
                       WHERE  unit_value = 2
                              AND unit_type = 0)
       AND max_unit_id IN (SELECT id
                       FROM   unit
                       WHERE  unit_value = 2
                              AND unit_type = 0);

UPDATE functionality_unit
SET    integer_max_value = 21,
       integer_default_value = 7
WHERE  functionality_id IN (SELECT id
                            FROM   functionality f
                            WHERE
              identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION')
       AND integer_max_value = 7
       AND integer_default_value = 20
       AND unit_id IN (SELECT id
                       FROM   unit
                       WHERE  unit_value = 0
                              AND unit_type = 0)
       AND max_unit_id IN (SELECT id
                       FROM   unit
                       WHERE  unit_value = 0
                              AND unit_type = 0);

 -- enable Upload Request mail activation
UPDATE mail_activation SET system = false, enable = true WHERE identifier LIKE 'UPLOAD_REQUEST_%';
-- -- activation policy update
UPDATE policy SET status = true , default_status = true, policy = 0 ,system = true WHERE id IN (SELECT policy_activation_id FROM mail_activation WHERE identifier LIKE 'UPLOAD_REQUEST_%');
-- -- configuration policy update
UPDATE policy SET status = true , default_status = true, policy = 1 ,system = false WHERE id IN (SELECT policy_configuration_id FROM mail_activation WHERE identifier LIKE 'UPLOAD_REQUEST_%');
-- -- delegation policy update
UPDATE policy SET status = false , default_status = false, policy = 2 ,system = true WHERE id IN (SELECT policy_delegation_id FROM mail_activation WHERE identifier LIKE 'UPLOAD_REQUEST_%'); 

 -- Remove mailing_list_contact_index from mailing_list_contact table
 ALTER TABLE mailing_list_contact DROP mailing_list_contact_index;
 
 -- Refactor change mailing to contact in contact list tables
 ALTER INDEX mailing_list_index RENAME TO contact_list_index;
 ALTER INDEX mailing_list_contact_index RENAME TO contact_list_contact_index;
 
 ALTER TABLE mailing_list RENAME TO contact_list;
 ALTER TABLE mailing_list_contact RENAME TO contact_list_contact;
 ALTER TABLE contact_list_contact RENAME COLUMN mailing_list_id TO contact_list_id;


-- Update domain_abstract by adding the column of drive provider
ALTER TABLE domain_abstract ADD COLUMN drive_provider_id int8;

-- Create the Drive provider
	CREATE TABLE drive_provider (
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

-- Add the foreign keys constraints related to drive provider
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs303989 FOREIGN KEY (drive_provider_id) REFERENCES group_provider (id);
ALTER TABLE drive_provider ADD CONSTRAINT FKdrive_provi820203 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE drive_provider ADD CONSTRAINT FKdrive_provi1670 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);

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
	6,
	'c59078f1-2366-4360-baa0-6c089202e9a6',
	'GROUP_LDAP_PATTERN',
	'Ldap drives',
	true,
	'default-drive-pattern',
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
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=drive-*))");',
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=drive-" + pattern + "))");',
	'drive-');


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

-- LinShare version
SELECT ls_version();

COMMIT;

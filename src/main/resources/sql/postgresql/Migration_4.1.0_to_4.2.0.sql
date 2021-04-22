-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

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

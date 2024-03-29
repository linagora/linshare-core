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
        --  DIRTY: did it to stop the process cause there is no clean way to do it.
        --  Expected error: query has no destination for result data.
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
    --      DIRTY: did it to stop the process cause there is no clean way to do it.
    --      Expected error: query has no destination for result data.
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
    --      DIRTY: did it to stop the process cause there is no clean way to do it.
    --      Expected error: query has no destination for result data.
            select error;
        END IF;
        IF (nb_upgrade_tasks > 0) THEN
            RAISE WARNING 'Can not upgrade LinShare if all upgrade tasks are not completed with success !!!!';
            RAISE WARNING 'We are about to abort the migration script, all the following instructions will be aborted and transaction will rollback.';
            RAISE INFO 'You should expect the following error : "query has no destination for result data".';
    --      DIRTY: did it to stop the process cause there is no clean way to do it.
    --      Expected error: query has no destination for result data.
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
            is_first_row = TRUE;
            FOR stat_row IN (SELECT * FROM statistic WHERE statistic_type = 'WORK_GROUP_DAILY_STAT' AND account_id = work_group_row.account_id ORDER BY creation_date ASC) LOOP
                RAISE INFO 'ID STAT : %', stat_row.id;
                IF   is_first_row THEN
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
            is_first_row = TRUE;
            FOR weekly_stat_row IN (SELECT * FROM statistic WHERE statistic_type = 'WORK_GROUP_WEEKLY_STAT' AND account_id = work_group_row.account_id ORDER BY creation_date ASC) LOOP
                current_create_operation_sum = 0;
                current_delete_operation_sum = 0;
                current_diff_operation_sum = 0;
                IF is_first_row THEN
                    RAISE INFO 'First stat row skipped';
                    current_actual_operation_sum = weekly_stat_row.actual_operation_sum;
                    is_first_row = FALSE;
                ELSE
                    SELECT
                        COALESCE(sum(create_operation_sum), 0),
                        COALESCE(sum(delete_operation_sum), 0),
                        COALESCE(sum(diff_operation_sum), 0)
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
            is_first_row := TRUE;
            current_actual_operation_sum = 0;
            RAISE INFO 'Computing monthly stats of workgroup with ID %', work_group_row.account_id;
            FOR monthly_stat_row IN (SELECT * FROM statistic WHERE statistic_type = 'WORK_GROUP_MONTHLY_STAT' AND account_id = work_group_row.account_id ORDER BY creation_date ASC) LOOP
                RAISE DEBUG 'is_first_row : %', is_first_row;
                current_create_operation_sum = 0;
                current_delete_operation_sum = 0;
                current_diff_operation_sum = 0;
                IF is_first_row THEN
                    RAISE INFO 'First stat row skipped';
                    current_actual_operation_sum = monthly_stat_row.actual_operation_sum;
                    is_first_row = FALSE;
                ELSE
                    SELECT
                        COALESCE(sum(create_operation_sum), 0),
                        COALESCE(sum(delete_operation_sum), 0),
                        COALESCE(sum(diff_operation_sum), 0)
                    INTO current_create_operation_sum, current_delete_operation_sum, current_diff_operation_sum
                    FROM statistic
                    WHERE statistic_type = 'WORK_GROUP_WEEKLY_STAT'
                        AND DATE_PART('month', monthly_stat_row.statistic_date::TIMESTAMP) =  DATE_PART('month', statistic_date::TIMESTAMP) + 1
                        AND account_id = work_group_row.account_id ;

                    RAISE DEBUG 'current_create_operation_sum: %', current_create_operation_sum;
                    RAISE DEBUG 'current_delete_operation_sum: %', current_delete_operation_sum;
                    RAISE DEBUG 'current_diff_operation_sum: %', current_diff_operation_sum;

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

-- TASK: UPGRADE_4_2_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA
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

-- TASK: UPGRADE_4_2_ADD_DETAILS_TO_SHARED_SPACE_NODES
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
  (39,
  'UNDEFINED',
  'UPGRADE_4_2_ADD_DETAILS_TO_SHARED_SPACE_NODES',
  'UPGRADE_4_2',
  null,
  null,
  39,
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
 ALTER INDEX IF EXISTS mailing_list_index RENAME TO contact_list_index;
 ALTER INDEX IF EXISTS mailing_list_contact_index RENAME TO contact_list_contact_index;

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
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs303989 FOREIGN KEY (drive_provider_id) REFERENCES drive_provider (id);
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

-- Enable Drive functionality by default

UPDATE policy SET status=true, default_status=true, system=false WHERE id IN (SELECT policy_activation_id FROM functionality WHERE identifier = 'DRIVE');
UPDATE functionality SET system=false WHERE identifier='DRIVE';


-- Update DRIVE_WARN_NEW_MEMBER.sql to get the right drive url

UPDATE mail_content SET subject='[( #{subject(${driveName})})]',body='<!DOCTYPE html>
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
            <span th:if="${owner.firstName} !=null AND ${owner.lastName} !=null" data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName})}"></span>
            <span th:if="${owner.firstName} ==null OR ${owner.lastName} ==null" data-th-utext="#{simpleMainMsg}"></span>
            <span>
              <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${driveName}" th:href="@{${driveLink}}" >
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
    <th:block th:switch="${driveMember.role.name}">
      <p th:case="''DRIVE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{driveRoleAdminTitle})"/></p>
      <p th:case="''DRIVE_WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{driveRoleWriteTitle})"/></p>
      <p th:case="''DRIVE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{driveRoleReadTitle})"/></p>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{driveNameTitle},${driveName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{driveMemberCreationDateTitle},${driveMember.creationDate})"/>
    <div th:if="${!childMembers.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="member : ${childMembers}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayDriveAndRole(${member.node.name},${member.role.name})}"/>
          </li>
      </ul>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='driveMemberCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au Drive: <br>
simpleMainMsg = Vous avez été ajouté au Drive
subject = Vous avez été ajouté au Drive {0}
driveRight = Droit par défaut
driveNameTitle = Nom du Drive
nestedWorkGroupsList=Vous avez automatiquement été ajouté aux groupes de travail suivants :
displayDriveAndRole ={0} avec un rôle <span style="text-transform:uppercase">{1}</span>',messages_english='driveMemberCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the Drive: <br>
simpleMainMsg = You have been added to the Drive
subject = You have been added to the Drive {0}
driveRight = Default right
driveNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role',messages_russian='driveMemberCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the Drive: <br>
simpleMainMsg = You have been added to the Drive
subject = You have been added to the Drive {0}
driveRight = Default right
driveNameTitle = Drive Name
nestedWorkGroupsList=You have been automatically added to the following workgroups:
displayDriveAndRole ={0} with a <span style="text-transform:uppercase">{1}</span> role' WHERE id=34;

-- Update DRIVE_WARN_UPDATED_MEMBER

UPDATE mail_content SET subject='[(#{subject(${driveName})})]',body='<!DOCTYPE html>
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
               <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${driveName}" th:href="@{${driveLink}}" >
                link </a>
          </span>
          <span data-th-utext="#{mainMsgNext}"></span>
          <span th:if="${owner.firstName} != null AND ${owner.firstName} != null" data-th-utext="#{mainMsgNextBy(${owner.firstName},${owner.lastName})}"></span>

             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{driveNameTitle},${driveName})"/>
    <th:block th:switch="${driveMember.role.name}">
      <p th:case="''DRIVE_ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{driveRoleAdminTitle})"/></p>
      <p th:case="''DRIVE_WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{driveRoleWriteTitle})"/></p>
      <p th:case="''DRIVE_READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{driveRight}, #{driveRoleReadTitle})"/></p>
    </th:block>
    <th:block th:switch="${driveMember.nestedRole.name}">
      <p th:case="''ADMIN''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/></p>
      <p th:case="''CONTRIBUTOR''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''WRITER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/></p>
      <p th:case="''READER''"> <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/></p>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{driveMemberUpdatedDateTitle},${driveMember.modificationDate})"/>
    <div th:if="${nbrWorkgroupsUpdated != 0}">
    <th:block data-th-replace="layout :: infoStandardArea(#{nbrWorkgoups},${nbrWorkgroupsUpdated})"/>
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul>
        <li  th:each="member : ${nestedMembers}">
              <th:block data-th-utext="${member.node.name}"/>
        </li>
        <span th:if="${nbrWorkgroupsUpdated > 3}">
             <li>...</li>
        </span>
      </ul>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='driveMemberUpdatedDateTitle = Date de la mise à jour
mainMsg = Vos droits sur le Drive
mainMsgNext = et dans ses WorkGroups contenus ont été mis à jour
mainMsgNextBy= par <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Vos droits sur le Drive {0} ont été mis à jour
driveRight = Droit sur le Drive
workGroupRight =  Droit sur le groupe de travail
driveNameTitle = Nom du Drive
nestedWorkGroupsList = Liste des workgoups
nbrWorkgoups = Nombre de groupe de travail mis à jours',messages_english='driveMemberUpdatedDateTitle = Updated date
mainMsg = Your roles on the Drive
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your roles on the Drive {0} was updated.
driveRight = Drive right
workGroupRight = Workgroup right
driveNameTitle = Drive Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups',messages_russian='driveMemberUpdatedDateTitle = Updated date
mainMsg = Your roles on the Drive
mainMsgNext= and workgroups inside it, have been updated
mainMsgNextBy= by <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your roles on the Drive {0} was updated.
driveRight = Drive right
workGroupRight = Workgroup right
driveNameTitle = Drive Name
nestedWorkGroupsList = Workgroups list
nbrWorkgoups = Number of updated workGroups' WHERE id=35;

-- Update mail layout

UPDATE mail_layout SET messages_french='common.availableUntil = Expire le
common.byYou= | Par vous
common.download= Télécharger
common.filesInShare=Fichiers joints
common.recipients = Destinataires
common.titleSharedThe= Partagé le
date.format=d MMMM, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administration
workGroupRightWirteTitle = Écriture
workGroupRightContributeTitle = Contribution
workGroupRightReadTitle = Lecture
workGroupRightContributorTitle = Contributeur
driveRoleAdminTitle = Drive: Administrateur
driveRoleWriteTitle = Drive: Auteur
driveRoleReadTitle = Drive: Lecteur
welcomeMessage = Bonjour {0},',messages_english='common.availableUntil = Expiry date
common.byYou= | By you
common.download= Download
common.filesInShare = Attached files
common.recipients = Recipients
common.titleSharedThe= Creation date
date.format= MMMM d, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrator
workGroupRightWirteTitle = Writer
workGroupRightContributeTitle = Contributor
workGroupRightReadTitle = Reader
driveRoleAdminTitle = Drive: Administrator
driveRoleWriteTitle = Drive: Writer
driveRoleReadTitle = Drive: Reader
welcomeMessage = Hello {0},',messages_russian='common.availableUntil = Срок действия
common.byYou= | Вами
common.download= Загрузить
common.filesInShare = Прикрепленные файлы
common.recipients = Получатели
common.titleSharedThe= Дата создания
date.format= d MMMM, yyyy
productCompagny= Linagora
productName=LinShare
workGroupRightAdminTitle = Администратор
workGroupRightWirteTitle = Автор
workGroupRightContributeTitle = Редактор
workGroupRightReadTitle = Читатель
driveRoleAdminTitle = Drive: Administrator
driveRoleWriteTitle = Drive: Writer
driveRoleReadTitle = Drive: Reader
welcomeMessage = Здравствуйте, {0},',layout='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div
    style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans'',arial,Helvetica,sans-serif;">
    <center>
      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
        style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px" width="90%">
        <tbody>
          <tr>
            <td align="center" style="border-collapse:collapse" valign="top">
              <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px"
                width="90%">
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
                                    style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233"
                                    alt="Logo" height="57" />
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
                                        <th:block data-th-replace="${upperMainContentArea}" />
                                      </div>
                                      <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                        style="background-color: #f8f8f8;">
                                        <tbody>
                                          <tr>
                                            <td width="15" style="border-top:1px solid #c9cacc;">
                                            </td>
                                            <td width="20"><img src="cid:logo.arrow@linshare.org" width="20" height="9"
                                                border="0" style="display:block;" alt="down arrow" /></td>
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
                                                  <th:block data-th-replace="${bottomSecondaryContentArea}" />
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
                                            ©&nbsp;2009–2022. Contribute to
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
    <span>
        <th:block th:if="${oldValue == null}">
            null
        </th:block>
        <th:block th:unless="${oldValue == null}">
            <th:block th:replace="${oldValue}" />
        </th:block>
        =>
        <th:block th:if="${newValue == null}">
            null
        </th:block>
        <th:block th:unless="${newValue == null}">
            <th:block th:replace="${newValue}" />
        </th:block>
    </span>
</div>

<!--/* Edited  date  display settings  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoEditedDateArea(titleInfo,oldValue,newValue)">
    <span style="font-weight:bold;" data-th-text="${titleInfo}"></span>
    <br />
    <th:block th:if="${oldValue == null}">
        null
    </th:block>
    <th:block th:unless="${oldValue == null}">
        <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(oldValue,df)}" />
    </th:block>
    =>
    <th:block th:if="${newValue == null}">
        null
    </th:block>
    <th:block th:unless="${newValue == null}">
        <th:block th:with="df=#{date.format}" data-th-text="${#dates.format(newValue,df)}" />
    </th:block>
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
     <div data-th-if="${contentInfo != null}">
       <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
       <br/>
       <th:block th:replace="${contentInfo}" />
    </div>
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
         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
   </li>
</ul>
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
        <th:block style="color; #787878; font-size:10px;margin-top:10px; display: inline-block;" th:each="shareLink : ${arrayFileLinks}" >
            <div style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
                <!--[if mso]>
                    &nbsp;&nbsp;
                <![endif]-->
                <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
                    <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">test-file.jpg</span>
                </a>
                <span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>
                <span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>
            </div>
            <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >
                <th:block  th:each="recipientData: ${shareLink.shares}">
                    <th:block data-th-if="(${!recipientData.downloaded})" >
                        <li style="color:#787878;font-size:15px;"  >
                            <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
                                <span style="color:#7f7f7f;font-size:13px;">
                                    <th:block  data-th-utext="${recipientData.firstName}"/>
                                    <th:block data-th-utext="${recipientData.lastName}"/>
                                </span>
                            </th:block>
                            <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>
                        </li>
                    </th:block>
                    <th:block data-th-if="(${recipientData.downloaded})">
                        <li style="color:#00b800;font-size:15px;" >
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
</th:block>
</div>' WHERE id=1;


-- System account for anonymous share
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id, purge_step, can_upload, restricted, can_create_guest, authentication_failure_count)
    VALUES (4,'system-anonymous-share-account', 7, 'system-anonymous-share-account', now(),now(), 8, 'en', 'en','en', true, 0, 1, 'IN_USE', false, false, false, 0);

-- TASK: UPGRADE_4_2_UPDATE_SYSTEM_TO_ANONYMOUS_ACCOUNT_ON_AUDIT_TRACES
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
  (40,
  'UNDEFINED',
  'UPGRADE_4_2_UPDATE_SYSTEM_TO_ANONYMOUS_ACCOUNT_ON_AUDIT_TRACES',
  'UPGRADE_4_2',
  null,
  null,
  40,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);
 -- UPGRADE_4_2_ADD_WORKGROUP_UUID_AS_RELATED_RESOURCE_IN_WORKGROUP_NODE_AUDIT_TRACES
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
  (41,
  'UNDEFINED',
  'UPGRADE_4_2_ADD_WORKGROUP_UUID_AS_RELATED_RESOURCE_IN_WORKGROUP_NODE_AUDIT_TRACES',
  'UPGRADE_4_2',
  null,
  null,
  41,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);
 -- UPGRADE_4_2_ADD_SEE_AS_NESTED_FIELD_TO_SHARED_SPACE_MEMBERS
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
  (42,
  'UNDEFINED',
  'UPGRADE_4_2_ADD_SEE_AS_NESTED_FIELD_TO_SHARED_SPACE_MEMBERS',
  'UPGRADE_4_2',
  null,
  null,
  42,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);
-- UPGRADE_4_2_UPDATE_SEE_AS_NESTED_FIELD_IN_NESTED_SHARED_SPACE_MEMBERS
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
  (43,
  'UNDEFINED',
  'UPGRADE_4_2_UPDATE_SEE_AS_NESTED_FIELD_IN_NESTED_SHARED_SPACE_MEMBERS',
  'UPGRADE_4_2',
  null,
  'UPGRADE_4_2_ADD_SEE_AS_NESTED_FIELD_TO_SHARED_SPACE_MEMBERS',
  43,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);
-- UPGRADE_4_2_ADD_MISSING_PROPERTIES_TO_WORK_GROUP_NODE
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
  (44,
  'UNDEFINED',
  'UPGRADE_4_2_ADD_MISSING_PROPERTIES_TO_WORK_GROUP_NODE',
  'UPGRADE_4_2',
  null,
  null,
  44,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- Mail content for WORKGROUP_WARN_DELETED_WORKGROUP
INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,39,39,'','','',NOW(),true,'','b8fd5482-c47f-11eb-8529-0242ac130003',true);
-- Mail content for DRIVE_WARN_DELETED_DRIVE
INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,40,40,'','','',NOW(),true,'','c34c8f84-c47f-11eb-8529-0242ac130003',true);

-- Mail content lang for WORKGROUP_WARN_DELETED_WORKGROUP and DRIVE_WARN_DELETED_DRIVE (EN)
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (39,0,1,39,39,true,'84738a92-c47f-11eb-8529-0242ac130003');
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (40,0,1,40,40,true,'92708d98-c47f-11eb-8529-0242ac130003');

-- Mail content lang for WORKGROUP_WARN_DELETED_WORKGROUP and DRIVE_WARN_DELETED_DRIVE (FR)
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (139,1,1,39,39,true,'84738a92-c47f-11eb-8529-0242ac130003');
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (140,1,1,40,40,true,'92708d98-c47f-11eb-8529-0242ac130003');

-- Mail content lang for WORKGROUP_WARN_DELETED_WORKGROUP and DRIVE_WARN_DELETED_DRIVE (RU)
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (239,2,1,39,39,true,'84738a92-c47f-11eb-8529-0242ac130003');
INSERT INTO mail_content_lang (id,language,mail_config_id,mail_content_id,mail_content_type,readonly,uuid) VALUES (240,2,1,40,40,true,'92708d98-c47f-11eb-8529-0242ac130003');

-- MailActivation : WORKGROUP_WARN_DELETED_WORKGROUP
INSERT INTO policy(id, status, default_status, policy, system)
    VALUES (330, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
    VALUES (331, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
    VALUES (332, false, false, 2, true);
-- --mail activation
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
    VALUES(40, false, 'WORKGROUP_WARN_DELETED_WORKGROUP', 330, 331, 332, 1, true);

-- MailActivation : DRIVE_WARN_DELETED_DRIVE
INSERT INTO policy(id, status, default_status, policy, system)
    VALUES (333, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
    VALUES (334, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
    VALUES (335, false, false, 2, true);
-- --mail activation
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
    VALUES(41, false, 'DRIVE_WARN_DELETED_DRIVE', 333, 334, 335, 1, true);

-- Mail content WORKGROUP_WARN_DELETED_WORKGROUP
UPDATE mail_content SET subject='[( #{subject(${workGroupName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <!--/* Upper main-content*/-->
    <section id="main-content">
        <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
            <div id="section-content">
                <!--/* Greetings */-->
                <th:block data-th-replace="layout :: greetings(${member.account.firstName})"/>
                <!--/* End of Greetings  */-->
                <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${workGroupName}, ${actor.firstName},${actor.lastName})}">
                     </span>
                  </p>
                </p> <!--/* End of Main email  message content*/-->
            </div><!--/* End of section-content*/-->
        </div><!--/* End of main-content container*/-->
    </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='subject = Le groupe de travail {0} a été supprimé.
mainMsg = Le groupe de travail {0} a été supprimé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Nom du groupe de travail',messages_english='subject = The workgroup {0} has been deleted.
mainMsg = The workgroup {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Workgroup Name',messages_russian='subject = The workgroup {0} has been deleted.
mainMsg = The workgroup {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
workGroupNameTitle = Workgroup Name' WHERE id=39;

-- Mail content DRIVE_WARN_DELETED_DRIVE
UPDATE mail_content SET subject='[( #{subject(${driveName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <!--/* Upper main-content*/-->
    <section id="main-content">
        <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
            <div id="section-content">
                <!--/* Greetings */-->
                <th:block data-th-replace="layout :: greetings(${member.account.firstName})"/>
                <!--/* End of Greetings  */-->
                <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${driveName}, ${actor.firstName},${actor.lastName})}">
                     </span>
                  </p>
                </p> <!--/* End of Main email  message content*/-->
            </div><!--/* End of section-content*/-->
        </div><!--/* End of main-content container*/-->
    </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
    <div th:if="${!nestedNodes.isEmpty()}">
      <th:block data-th-utext="#{nestedWorkGroupsList}"/>
      <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
        <li style="color:#787878;font-size:10px" th:each="node : ${nestedNodes}">
            <span style="color:#787878;font-size:13px">
              <th:block data-th-utext="#{displayNestedNodeName(${node.name})}"/>
          </li>
      </ul>
    </div>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='subject = Le Drive {0} a été supprimé.
mainMsg = Le Drive {0} a été supprimé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=Vous avez automatiquement été supprimé des groupes de travail suivants :
displayNestedNodeName:{0}',messages_english='subject = The Drive {0} has been deleted.
mainMsg = The Drive {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=You have been automatically removed from the following workgroups:
workGroupNameTitle = Workgroup Name
displayNestedNodeName:{0}',messages_russian='subject = The Drive {0} has been deleted.
mainMsg = The Drive {0} has been deleted by <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
nestedWorkGroupsList=You have been automatically removed from the following workgroups:
displayNestedNodeName:{0}' WHERE id=40;

-- Delete link to expired sharedFile SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE
UPDATE mail_content SET subject='[( #{subject(${share.name})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <b><span data-th-text="#{fileNameEndOfLine(${share.name})}"></span></b>
          <span data-th-utext="#{endingMainMsg(${shareOwner.firstName},${shareOwner.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b> a expiré et a été supprimé par le <b>système</b>.
subject = Le partage {0} a expiré
fileNameEndOfLine = {0}',messages_english='shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b> has expired and been deleted by the <b>system</b>.
subject = The fileshare {0} has expired
fileNameEndOfLine = {0}',messages_russian='shareFileTitle = Файл рассылки
shareCreationDateTitle = Дата создания
shareExpiryDateTitle = Дата срока истечения действия
activationLinkTitle = Ссылка активации
beginningMainMsg =  У файла рассылки
endingMainMsg = отправленного <b> {0} <span style="text-transform:uppercase">{1}</span></b> истек срок действия и он был удален <b>system</b>.
subject = Срок действия файла рассылки {0} истек
fileNameEndOfLine = {0}' WHERE id=27;

-- Update UPLOAD_REQUEST_UPDATED_SETTINGS by adding access link
UPDATE mail_content SET subject='[(#{subject(${subject.value})})]',body='<!DOCTYPE html>
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
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName}, ${subject.value})}">
                     </span>
                     <span data-th-utext="#{secondaryMsg}">
                     </span>
                  </p>
                  <!--/* If the sender has added a  customized message */-->
                  <th:block data-th-if="(${message.modified})">
                     <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                        <span id="message-title">
                        <span data-th-text="#{msgFrom}">You have a message from</span>
                        <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                        </span>
                        <span id="message-content" data-th-text="*{message.value}" style="white-space: pre-line;">
                        Hi Amy,<br>
                        As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
                        </span>
                     </div>
                  </th:block>
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
            <span data-th-if="(${expiryDate.modified})">
               <th:block data-th-replace="layout :: infoEditedDateArea(#{expiryDate},${expiryDate.oldValue},${expiryDate.value})"/>
            </span>
            <span data-th-if="(${activationDate.modified})">
               <th:block data-th-replace="layout :: infoEditedDateArea(#{activationDate},${activationDate.oldValue},${activationDate.value})"/>
            </span>
            <span data-th-if="(${closureRight.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{closureRight},${closureRight.oldValue},${closureRight.value})"/>
            </span>
            <span data-th-if="(${deletionRight.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{deletionRight},${deletionRight.oldValue},${deletionRight.value})"/>
            </span>
            <span data-th-if="(${maxFileSize.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{maxFileSize},${maxFileSize.oldValue},${maxFileSize.value})"/>
            </span>
            <span data-th-if="(${maxFileNum.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{maxFileNum},${maxFileNum.oldValue},${maxFileNum.value})"/>
            </span>
            <span data-th-if="(${totalMaxDepotSize.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{depotSize},${totalMaxDepotSize.oldValue},${totalMaxDepotSize.value})"/>
            </span>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='activationDate = Date d''activation
closureRight = Droits de dépôt
deletionRight = Droits de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
enableNotification = Activation des notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres liés à l''''Invitation de dépôt <b>{2}</b>.
maxFileNum = Nombre de Fichiers
maxFileSize = Taille autorisée
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Les modifications sont listées ci-dessous.
subject = Modification des paramètres du dépôt : {0}
buttonMsg = Accès',messages_english='activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Repository size
expiryDate = Closure date
enableNotification = Enable notifications
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings related to the Upload Request <b>{2}</b>.
maxFileNum = File number
maxFileSize = File size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the Upload Request
secondaryMsg = Updated settings are listed below.
subject = Updated Settings for Upload Request : {0}
buttonMsg = Access',messages_russian='activationDate = Дата активации
closureRight = Права закрытия
deletionRight = Права удаления
depotSize = Размер репозитория
expiryDate = Дата закрытия
enableNotification = Разрешить уведомления
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  обновил некоторые настройки запроса загрузки <b>{2}</b>.
maxFileNum = Номер файла
maxFileSize = Размер файла
msgFrom =  Новое сообщение от
name = {0} {1}
nameOfDepot: Название загрузки
secondaryMsg = Список обновленных настроек доступен ниже.
subject = Обновленные настройки для запроса загрузки {0}
buttonMsg = Access' WHERE id=23;


-- Update ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED
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
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${recipient.firstName} AND ${recipient.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                       <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} == ${recipient.firstName} AND ${recipient.lastName} == ${owner.lastName})"
                                      data-th-utext="#{mainMsgOwner(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                  </p>
                      <span data-th-utext="#{endMsg}"></span>
                      <span>
                             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{tokenLinkEndOfLine(${jwtTokenLink})}" th:href="@{${jwtTokenLink}}" >
                            </a>
                     </span>
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
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a créé un jeton d''''accès permanent: {2}, pour votre compte.
mainMsgOwner = Vous vous avez créé un jeton d''''accès permanent : {2},pour votre compte.
tokenCreationDate = Date de création
endMsg = Vous pouvez consulter les jetons d''''accès liés à votre compte
tokenLinkEndOfLine = ici
tokenLabel = Nom
tokenDescription = Description',messages_english='subject = Creation of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a permanent authentication token: {2}, for your account.
mainMsgOwner = You have created a permanent authentication token: {2}, for your account.
tokenCreationDate = Creation date
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Name
tokenDescription = Description',messages_russian='subject = Создание постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> создал постоянный токен аутентификации: {2},для вашей учетной записи.
mainMsgOwner = You have created a permanent authentication token: {2}, for your account.
tokenCreationDate = Дата создания
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Имя
tokenDescription = Описание' WHERE id=32;

-- Update ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_DELETED
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
                      <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} != ${recipient.firstName} AND ${recipient.lastName} != ${owner.lastName})"
                                      data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                       <span th:if="(${owner.firstName} !=null AND ${owner.lastName} !=null)
                       AND (${owner.firstName} == ${recipient.firstName} AND ${recipient.lastName} == ${owner.lastName})"
                                      data-th-utext="#{mainMsgOwner(${owner.firstName},${owner.lastName},${label})}">
                      </span>
                     </span>
                  </p>
                      <span data-th-utext="#{endMsg}"></span>
                      <span>
                             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="#{tokenLinkEndOfLine(${jwtTokenLink})}" th:href="@{${jwtTokenLink}}" >
                            </a>
                     </span>
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
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> a supprimé un jeton d''''accès permanent: {2}, pour votre compte.
mainMsgOwner = Vous avez supprimé un jeton d''''accès permanent: {2}, pour votre compte.
tokenCreationDate = Date de création
endMsg = Vous pouvez consulter les jetons d''''accès liés à votre compte
tokenLinkEndOfLine = ici
tokenLabel = Nom
tokenDescription = Description
tokenIdentifier = Identifiant',messages_english='subject = Deletion of a permanent authentication token
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token: {2}, for your account.
mainMsgOwner = You have deleted a permanent authentication token: {2}, for your account.
tokenCreationDate = Creation date
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Name
tokenDescription = Description',messages_russian='subject = Удаление постоянного токена аутентификации
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> has deleted a permanent authentication token: {2}, for your account.
mainMsgOwner = You have deleted a permanent authentication token: {2}, for your account.
tokenCreationDate = Дата создания
endMsg = You can review the active tokens tied to your account
tokenLinkEndOfLine = here
tokenLabel = Имя
tokenDescription = Описание' WHERE id=33;

-- Update mail UPLOAD_REQUEST_WARN_BEFORE_EXPIRY
UPDATE mail_content SET subject='[# th:if="${warnOwner}"] [( #{subjectForOwner})]
[/]
[# th:if="${!warnOwner}"]
[( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName})})]
[/]
[# th:if="${!#strings.isEmpty(mailSubject)}"]
[( #{formatMailSubject(${mailSubject})})]
[/]',body='<!DOCTYPE html>
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
            <span  data-th-if="!(${isCollective})"   data-th-utext="#{beginningMainMsgIndividual(${remainingDays})}"></span>
            <span  data-th-if="(${isCollective})"   data-th-utext="#{beginningMainMsgCollective(${remainingDays})}"></span>
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
      <th:block  data-th-if="(${isCollective})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="!(${isCollective})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationActivationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgForRecipient =   L''''invitation dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> sera clôturée dans <b>{2} jours</b>
beginningMainMsgCollective =   Votre Invitation sera clôturée dans  <b>{0} jours</b>.
beginningMainMsgIndividual =   Votre Invitation sera clôturée dans  <b>{0} jours</b>.
endingMainMsgPlural = Il y a un total de <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans le dépôt.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationActivationDate = Date d''''activation
invitationClosureDate =  Date de clôture
recipientsURequest = Destinataires
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturée
subjectForRecipient = L''''invitation au dépôt de {0} {1} sera bientôt clôturée
uploadFileBtn = Déposer un fichier',messages_english='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s The Upload Request is about to reach it''''s end date in <b>{2} days</b>
beginningMainMsgCollective = Your invitation will be closed in  <b>{0} days</b>.
beginningMainMsgIndividual =  Your invitation is about to be closed in <b>{0} days</b>.
endingMainMsgPlural = There are a total of <b> {0} files </b> in the Upload Request.
endingMainMsgPluralForRecipient = and so far you have sent <b> {0} files </b> in the Upload Request.
endingMainMsgSingular = There is a total of <b>1 file </b> in the repository.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationActivationDate = Activation date
invitationClosureDate = Closure date
recipientsURequest = Recipients
subjectForOwner =  Your invitation is about to be closed.
subjectForRecipient =  {0} {1}''''s  invitation is about to be closed
uploadFileBtn = Upload a file',messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s Действие запроса на загрузку закончится через <b>{2} дней</b>
beginningMainMsgCollective = Действие вашего приглашения закончится через <b>{0} дней</b>.
beginningMainMsgIndividual =  Действие вашего приглашения закончится через <b>{0} дней</b>.
endingMainMsgPlural = Всего загрузка содержит <b> {0} файлов </b>.
endingMainMsgPluralForRecipient = вы отправили <b> {0} файлов </b> в загрузку.
endingMainMsgSingular = Всего в репозитории of <b>1 файл </b>.
endingMainMsgSingularForRecipient = вы отправили <b>1 файл </b> в репозиторий.
filesInURDepot = Загруженные файлы
formatMailSubject = : {0}
invitationActivationDate = Дата активации
invitationClosureDate = Дата закрытия
recipientsURequest = Получатели
subjectForOwner =  Срок действия вашего приглашения заканчивается.
subjectForRecipient =  {0} {1}''''s срок действия вашего приглашения заканчивается.
uploadFileBtn = Загрузить файл' WHERE id=12;

-- Update UPLOAD_REQUEST_CLOSED_BY_OWNER
UPDATE mail_content SET subject='[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',body='<!DOCTYPE html>
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
               <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
            <div data-th-if="(${isCollective})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',messages_french='closureDate = Date de clôture
filesInURDepot = Fichiers
mainMsg = <b>{0} {1}</b> a fermé son invitation de dépôt : {2}.
recipientsOfDepot = Destinataires
subject = {0} {1} a fermé l''''invitation de dépôt : {2}',messages_english='closureDate = Closure date
filesInURDepot = Files
mainMsg = <b>{0} {1}</b> has closed the upload request labeled : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} has closed his upload request : {2}',messages_russian='closureDate = Дата закрытия
filesInURDepot = Файлы
mainMsg = <b>{0} {1}</b> закрыл запрос загрузки {2}.
recipientsOfDepot = Получатели
subject = {0} {1} закрыл запрос загрузки {2}' WHERE id=21;

-- Update of UPLOAD_REQUEST_WARN_BEFORE_EXPIRY
UPDATE mail_content SET subject='[# th:if="${warnOwner}"] [( #{subjectForOwner})]
[/]
[# th:if="${!warnOwner}"]
[( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName})})]
[/]
[# th:if="${!#strings.isEmpty(mailSubject)}"]
[( #{formatMailSubject(${mailSubject})})]
[/]',body='<!DOCTYPE html>
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
            <span  data-th-if="!(${isCollective})"   data-th-utext="#{beginningMainMsgIndividual(${subject},${remainingDays})}"></span>
            <span  data-th-if="(${isCollective})"   data-th-utext="#{beginningMainMsgCollective(${subject},${remainingDays})}"></span>
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
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${subject},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"   data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
            <th:block   data-th-replace="layout :: actionButtonLink(#{uploadFileBtn},${requestUrl})"/>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
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
      <th:block  data-th-if="(${isCollective})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="!(${isCollective})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationActivationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='beginningMainMsgForRecipient =   L''''invitation de dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b>: :  <b>{2}</b> sera clôturée dans <b>{3} jours</b>
beginningMainMsgCollective =   Votre Invitation de dépôt collective: {0}, sera clôturée dans  <b>{1} jours</b>.
beginningMainMsgIndividual =   Votre Invitation de dépôt individuelle: {0}, sera clôturée dans  <b>{0} jours</b>.
endingMainMsgPlural = Il y a un total de <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans l''''invitation de dépôt.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans l''''invitation de dépôt.
filesInURDepot = Fichiers déposés
formatMailSubject = : {0}
invitationActivationDate = Date d''''activation
invitationClosureDate =  Date de clôture
recipientsURequest = Destinataires
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturée
subjectForRecipient = L''''invitation au dépôt de {0} {1} sera bientôt clôturée
uploadFileBtn = Déposer un fichier',messages_english='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s upload Request:  <b>{2}</b> is about to reach it''''s end date in <b>{3} days</b>
beginningMainMsgCollective = Your collective upload request: {0}, is about to be closed in  <b>{1} days</b>.
beginningMainMsgIndividual =  Your individual upload request: {0}, is about to be closed in <b>{1} days</b>.
endingMainMsgPlural = There are a total of <b> {0} files </b> in the Upload Request.
endingMainMsgPluralForRecipient = and so far you have sent <b> {0} files </b> in the Upload Request.
endingMainMsgSingular = There is a total of <b>1 file </b> in the upload request.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the repository.
filesInURDepot = Files uploaded
formatMailSubject = : {0}
invitationActivationDate = Activation date
invitationClosureDate = Closure date
recipientsURequest = Recipients
subjectForOwner =  Your invitation is about to be closed.
subjectForRecipient =  {0} {1}''''s  invitation is about to be closed
uploadFileBtn = Upload a file',messages_russian='beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>''''s upload Request:  <b>{2}</b> is about to reach it''''s end date in <b>{3} days</b>
beginningMainMsgCollective = Your collective upload request: {0}, is about to be closed in  <b>{1} days</b>.
beginningMainMsgIndividual =  Your individual upload request: {0}, is about to be closed in <b>{1} days</b>.
endingMainMsgPlural = Всего загрузка содержит <b> {0} файлов </b>.
endingMainMsgPluralForRecipient = вы отправили <b> {0} файлов </b> в загрузку.
endingMainMsgSingular = Всего в репозитории of <b>1 файл </b>.
endingMainMsgSingularForRecipient = вы отправили <b>1 файл </b> в репозиторий.
filesInURDepot = Загруженные файлы
formatMailSubject = : {0}
invitationActivationDate = Дата активации
invitationClosureDate = Дата закрытия
recipientsURequest = Получатели
subjectForOwner =  Срок действия вашего приглашения заканчивается.
subjectForRecipient =  {0} {1}''''s срок действия вашего приглашения заканчивается.
uploadFileBtn = Загрузить файл' WHERE id=12;

-- Update root user's first and last name

UPDATE account SET first_name='Super', last_name='Administrator' where ls_uuid='root@localhost.localdomain';

-- enable Upload Request mail activation
UPDATE mail_activation SET system = false, enable = true WHERE identifier LIKE 'UPLOAD_REQUEST_%';
-- -- activation policy update
UPDATE policy SET status = true , default_status = true, policy = 0 ,system = true WHERE id IN (SELECT policy_activation_id FROM mail_activation WHERE identifier LIKE 'UPLOAD_REQUEST_%');
-- -- configuration policy update
UPDATE policy SET status = true , default_status = true, policy = 1 ,system = false WHERE id IN (SELECT policy_configuration_id FROM mail_activation WHERE identifier LIKE 'UPLOAD_REQUEST_%');
-- -- delegation policy update
UPDATE policy SET status = false , default_status = false, policy = 2 ,system = true WHERE id IN (SELECT policy_delegation_id FROM mail_activation WHERE identifier LIKE 'UPLOAD_REQUEST_%');

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
---- End of your queries

-- LinShare version
SELECT ls_version();

COMMIT;

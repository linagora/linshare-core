-- Postgresql migration script template

UPDATE version SET version = '4.0.0' WHERE version = '2.4.0';

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'4.1.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '4.1.0';
	DECLARE version_from VARCHAR := '4.0.0';
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

-- Activation of Upload_Request Functionality
UPDATE policy SET system = false, status = true, default_status = true WHERE id = 63;

-- Activation of Collective Upload_Request Functionality by default
UPDATE policy SET status = true, default_status = true WHERE id = 71;
UPDATE policy SET system = false, status = true, default_status = true WHERE id IN (63);

-- Add new field to Functionality_integer and Functionality_unit

ALTER TABLE functionality_unit ADD integer_max_value int4 NULL;
ALTER TABLE functionality_integer ADD integer_max_value int4 NULL;


ALTER TABLE functionality_unit RENAME COLUMN integer_value TO integer_default_value;
ALTER TABLE functionality_integer RENAME COLUMN integer_value TO integer_default_value;


-- Set the max value and max unit for the new field on functionality_unit and functionality_integer tables

UPDATE functionality_integer SET integer_default_value = 3, integer_max_value = 3 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'COMPLETION');   -- COMPLETION
UPDATE functionality_integer SET integer_default_value = 3, integer_max_value = 3 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION');   -- UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
UPDATE functionality_integer SET integer_default_value = 5, integer_max_value = 10 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT');  -- UPLOAD_REQUEST__MAXIMUM_FILE_COUNT
UPDATE functionality_unit SET integer_default_value = 3, integer_max_value = 4 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'GUESTS__EXPIRATION');      -- GUESTS__EXPIRATION
UPDATE functionality_unit SET integer_default_value = 3, integer_max_value = 4 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'DOCUMENT_EXPIRATION');      -- DOCUMENT_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 3, integer_max_value = 4 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'SHARE_EXPIRATION');      -- SHARE_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 0, integer_max_value = 900  WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'WORK_GROUP__DOWNLOAD_ARCHIVE'); -- WORK_GROUP__DOWNLOAD_ARCHIVE
UPDATE functionality_unit SET integer_default_value = 0, integer_max_value = -1 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION');     -- UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
UPDATE functionality_unit SET integer_default_value = 7, integer_max_value = 7 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION');      -- UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 10, integer_max_value = 20 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE');    -- UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
UPDATE functionality_unit SET integer_default_value = 50, integer_max_value = 100 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE');   -- UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
UPDATE functionality_unit SET integer_default_value = 7, integer_max_value = 7 WHERE functionality_id IN (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION');      -- UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION

-- Add new fields for default pwd and store original pwd of an URU
ALTER TABLE upload_request_url ADD COLUMN default_Password bool DEFAULT true NOT NULL;
ALTER TABLE upload_request_url ADD COLUMN original_password character varying(255)


--Drop upload proposition tables
DROP TABLE upload_proposition;
DROP TABLE upload_proposition_action;
DROP TABLE upload_proposition_rule;
DROP TABLE upload_proposition_filter;


--Delete UPLOAD_REQUEST__PROLONGATION functionality
DELETE FROM functionality_boolean WHERE functionality_id= 40;
DELETE FROM functionality WHERE identifier='UPLOAD_REQUEST__PROLONGATION';
DELETE FROM policy WHERE id= 89;
DELETE FROM policy WHERE id= 90;
DELETE FROM policy WHERE id= 91;


-- Delete UPLOAD_REQUEST__GROUPED_MODE functionality
DELETE FROM functionality_boolean WHERE functionality_id= 34;
DELETE FROM functionality WHERE identifier='UPLOAD_REQUEST__GROUPED_MODE';
DELETE FROM policy WHERE id= 71;
DELETE FROM policy WHERE id= 72;
DELETE FROM policy WHERE id= 73;


-- Drop not null constraint for notification date and expiry date for upload_request and upload_request_group to accept null value if func is disabled
alter table upload_request alter column notification_date drop not null;
alter table upload_request_group alter column notification_date drop not null;
alter table upload_request_group alter column expiry_date drop not null;
alter table upload_request alter column expiry_date drop not null;
-- End of your requests

-- LinShare version
SELECT ls_version();
COMMIT;

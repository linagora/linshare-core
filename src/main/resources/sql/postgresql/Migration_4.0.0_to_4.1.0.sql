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

ALTER TABLE functionality_unit ADD integer_default_value int4 NULL;
ALTER TABLE functionality_integer ADD integer_default_value int4 NULL;

ALTER TABLE functionality_unit ALTER COLUMN integer_value DROP NOT NULL;


-- Set the default value for the new field on functionality_unit and functionality_integer tables
UPDATE functionality_integer SET integer_default_value = 3, integer_value = 3 WHERE functionality_id = 16;   -- COMPLETION
UPDATE functionality_integer SET integer_default_value = 3, integer_value = 3 WHERE functionality_id = 55;   -- UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
UPDATE functionality_integer SET integer_default_value = 5, integer_value = 10 WHERE functionality_id = 35;  -- UPLOAD_REQUEST__MAXIMUM_FILE_COUNT

UPDATE functionality_unit SET integer_default_value = 3, integer_value = 4 WHERE functionality_id = 10;      -- GUESTS__EXPIRATION
UPDATE functionality_unit SET integer_default_value = 3, integer_value = 4 WHERE functionality_id = 11;      -- DOCUMENT_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 3, integer_value = 4 WHERE functionality_id = 12;      -- SHARE_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 900, integer_value = null WHERE functionality_id = 66; -- WORK_GROUP__DOWNLOAD_ARCHIVE
UPDATE functionality_unit SET integer_default_value = 0, integer_value = -1 WHERE functionality_id = 32;     -- UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
UPDATE functionality_unit SET integer_default_value = 7, integer_value = 7 WHERE functionality_id = 33;      -- UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
UPDATE functionality_unit SET integer_default_value = 10, integer_value = 20 WHERE functionality_id = 36;    -- UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
UPDATE functionality_unit SET integer_default_value = 50, integer_value = 100 WHERE functionality_id = 37;   -- UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
UPDATE functionality_unit SET integer_default_value = 7, integer_value = 7 WHERE functionality_id = 42;      -- UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION


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

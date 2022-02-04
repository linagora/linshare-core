-- This script should only be executed after LinShare 4.2.0 installation
-- This script is idempotent it has no additional effect if run more than once on the same database 
BEGIN;

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


-- Check if can apply the patch to the current LinShare version 4.2.0
CREATE OR REPLACE FUNCTION ls_prechecks_patch() RETURNS void AS $$
BEGIN
	DECLARE current_version VARCHAR := '4.2.0';
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


SELECT ls_check_user_connected();
SELECT ls_prechecks_patch();

-- Fix quota value for all accounts
CREATE OR REPLACE FUNCTION ls_fix_current_value_for_all_accounts() RETURNS void AS $$
BEGIN
	DECLARE myaccount record;
	DECLARE de_size BIGINT;
	DECLARE ure_size BIGINT;
	DECLARE account_quota BIGINT;
	DECLARE diff BIGINT;
	DECLARE new_account_quota BIGINT;
	BEGIN
		FOR myaccount IN (SELECT id, ls_uuid, mail, domain_id FROM account WHERE account_type IN (2,3,6)) LOOP
			RAISE INFO 'account mail : % (account_id=%)', myaccount.mail, myaccount.id;
			de_size := (SELECT sum(ls_size) FROM account AS a join entry AS e on a.id = e.owner_id join document_entry AS de ON de.entry_id = e.id WHERE a.id = myaccount.id);
			IF de_size IS NULL THEN
				de_size := 0;
			END IF;
			ure_size := (SELECT sum(ls_size) FROM account AS a join entry AS e on a.id = e.owner_id join upload_request_entry AS ure ON ure.entry_id = e.id WHERE a.id = myaccount.id);
			IF ure_size IS NULL THEN
				ure_size := 0;
			END IF;
			account_quota := (SELECT current_value FROM quota AS q WHERE account_id = myaccount.id);
			RAISE INFO 'Sum of de_size and ure_size  : % ', de_size + ure_size;
			new_account_quota = de_size + ure_size;
			RAISE INFO 'Difference between current value and sum of de_size and ure_size  : % ', new_account_quota - account_quota;
			diff = new_account_quota - account_quota;
			IF diff <> 0 THEN
			RAISE INFO 'If difference between current_value and sum of document entries size and upload request entries size different than 0, we insert a new entry into operation_history to recalculate account''s quota';
			INSERT INTO operation_history (id, uuid, operation_value, operation_type, container_type, creation_date, domain_id, account_id) VALUES ((SELECT nextVal('hibernate_sequence')), myaccount.ls_uuid, diff, 1, 'USER', NOW(), myaccount.domain_id, myaccount.id);
			END IF;
			RAISE INFO '----';
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;
SELECT ls_fix_current_value_for_all_accounts();
COMMIT;

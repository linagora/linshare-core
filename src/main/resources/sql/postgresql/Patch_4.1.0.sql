-- This script should only be executed after LinShare 4.1.0 installation
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

SELECT ls_check_user_connected();
SELECT check_to_fix_version_from_2_4_to_4_1_0();
SELECT ls_prechecks_patch();

-- Note: Insert fix scripts bellow.
-- All fixes should be in the same transaction
COMMIT;

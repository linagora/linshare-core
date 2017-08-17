BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;


CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.0.0';
	DECLARE version_from VARCHAR := '2.0.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your actual version is: %', version_history_from;
		RAISE NOTICE 'Your databse history is :';
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


CREATE OR REPLACE FUNCTION ls_fix_current_value_for_domains() RETURNS void AS $$
BEGIN
	DECLARE d record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	BEGIN
		FOR d IN (SELECT id, label FROM domain_abstract) LOOP
			RAISE INFO 'domain label : % (domain_id=%)', d.label, d.id;
			FOR q IN (SELECT id, container_type FROM quota WHERE quota_type = 'CONTAINER_QUOTA' and domain_id = d.id) LOOP
				RAISE INFO 'CONTAINER_QUOTA: % (id=%)', q.container_type, q.id;
				i := (select sum(current_value) from quota where quota_container_id  = q.id);
				RAISE INFO 'sum(current_value) : %', i;
				IF i > 0 THEN
					RAISE INFO 'Updating container current value : % (domain_id=%)', j, d.id;
					UPDATE quota AS a SET current_value = i WHERE a.id = q.id;
				END IF;
				RAISE INFO '-- ';
			END LOOP;
			j := (SELECT sum(current_value) FROM quota WHERE quota_type = 'CONTAINER_QUOTA' and domain_id = d.id);
			RAISE INFO 'Domain current value : % (domain_id=%)', j, d.id;
			IF j > 0 THEN
				RAISE INFO 'Updating domain current value : % (domain_id=%)', j, d.id;
				UPDATE quota SET current_value = j WHERE quota_type = 'DOMAIN_QUOTA' and domain_id = d.id;
			END IF;
			RAISE INFO '----';
			RAISE INFO '';
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_fix_current_value_for_subdomains(domain_type NUMERIC) RETURNS void AS $$
BEGIN
	DECLARE d record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	BEGIN
		RAISE INFO 'domain type : % ', domain_type;
		FOR d IN (SELECT id, label FROM domain_abstract where type = domain_type) LOOP
			RAISE INFO 'domain label : % (domain_id=%)', d.label, d.id;
			j := (SELECT sum(current_value + current_value_for_subdomains) FROM quota WHERE quota_type = 'DOMAIN_QUOTA' and domain_parent_id = d.id);
			RAISE INFO 'sum(current_value + current_value_for_subdomains) for all sub domains : % (domain_id=%)', j, d.id;
			IF j > 0 THEN
				RAISE INFO 'Updating domain with value : % (domain_id=%)', j, d.id;
				UPDATE quota SET current_value_for_subdomains = j WHERE quota_type = 'DOMAIN_QUOTA' and domain_id = d.id;
			END IF;
			RAISE INFO '----';
			RAISE INFO '';
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

COMMIT;

BEGIN;


SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

SELECT ls_check_user_connected();
SELECT ls_prechecks();

SELECT ls_fix_current_value_for_domains();
SELECT ls_fix_current_value_for_subdomains(1);
SELECT ls_fix_current_value_for_subdomains(0);

COMMIT;
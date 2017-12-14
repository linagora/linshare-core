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



CREATE OR REPLACE FUNCTION ls_fix_current_value_for_all_accounts() RETURNS void AS $$
BEGIN
	DECLARE myaccount record;
	DECLARE q record;
	DECLARE i BIGINT;
	DECLARE j BIGINT;
	DECLARE op BIGINT;
	BEGIN
		FOR myaccount IN (SELECT id, mail FROM account WHERE account_type != 5) LOOP
			RAISE INFO 'account mail : % (account_id=%)', myaccount.mail, myaccount.id;
			i := (SELECT sum(ls_size) FROM account AS a join entry AS e on a.id = e.owner_id join document_entry AS de ON de.entry_id = e.id WHERE a.id = myaccount.id);
			IF i IS NULL THEN
				i := 0;
			END IF;
			j := (SELECT current_value FROM quota AS q WHERE account_id = myaccount.id);
			op := (SELECT - sum(operation_value) FROM operation_history AS q WHERE account_id = myaccount.id);
			IF op IS NULL THEN
				op := 0;
			END IF;
			RAISE INFO 'Value of current_value : %, sum(operation_value) : % (account=%)', j, op, myaccount.id;
			RAISE INFO 'Updating account with new value (sum(ls_size)) - sum(operation_value) : % - % = %', i, op, i - op;
			i := i - op;
			RAISE INFO 'Difference of current_value : % ', i - j;
			UPDATE quota SET current_value = i WHERE account_id = myaccount.id;
			RAISE INFO '----';
			RAISE INFO 'Delete OperationHistory for account : % ', myaccount.mail;
			DELETE FROM operation_history WHERE account_id = myaccount.id;
			RAISE INFO '----';
		END LOOP;
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
				i := (select sum(current_value) from quota where quota_container_id  = q.id);
				IF i IS NULL THEN
					i := 0;
				END IF;
				RAISE INFO 'Updating container % (id=%) with new value : % (domain_id=%)', q.container_type, q.id, i, d.id;
				UPDATE quota AS a SET current_value = i WHERE a.id = q.id;
			END LOOP;
			j := (SELECT sum(current_value) FROM quota WHERE quota_type = 'CONTAINER_QUOTA' and domain_id = d.id);
			IF j IS NULL THEN
				j := 0;
			END IF;
			RAISE INFO 'Updating domain with new value : % (domain_id=%)', j, d.id;
			UPDATE quota SET current_value = j WHERE quota_type = 'DOMAIN_QUOTA' and domain_id = d.id;
			RAISE INFO '----';
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
			IF j IS NULL THEN
				j := 0;
			END IF;
			RAISE INFO 'Updating domain column "current_value_for_subdomains" with new value : % (domain_id=%)', j, d.id;
			UPDATE quota SET current_value_for_subdomains = j WHERE quota_type = 'DOMAIN_QUOTA' and domain_id = d.id;
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


-- fix quota for account (copy issue).
SELECT ls_fix_current_value_for_all_accounts();
-- update quota containers.

-- updating current_value of all domains
SELECT ls_fix_current_value_for_domains();

-- updating current_value_for_subdomain of all top domains with the values of subdomains.
-- TOPDOMAIN(1)
SELECT ls_fix_current_value_for_subdomains(1);

-- updating current_value_for_subdomain of root domain with the values of subdomains.
-- ROOTDOMAIN(0)
SELECT ls_fix_current_value_for_subdomains(0);

COMMIT;

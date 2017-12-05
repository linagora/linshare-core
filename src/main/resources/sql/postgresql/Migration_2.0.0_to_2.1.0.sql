-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'2.1.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.1.0';
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

ALTER TABLE document ALTER COLUMN sha256sum SET NOT NULL;
ALTER TABLE document ALTER COLUMN thmb_uuid DROP NOT NULL;
ALTER TABLE document ADD COLUMN has_thumbnail bool DEFAULT 'false' NOT NULL;
ALTER TABLE document ADD COLUMN compute_thumbnail bool DEFAULT 'false' NOT NULL;

ALTER TABLE upgrade_task ALTER COLUMN priority SET NOT NULL;

ALTER TABLE domain_abstract ADD COLUMN purge_step varchar(255) DEFAULT 'IN_USE' NOT NULL;
ALTER TABLE domain_abstract ALTER COLUMN domain_policy_id SET int8;

ALTER TABLE upgrade_task ALTER COLUMN creation_date TYPE timestamp(6);
ALTER TABLE upgrade_task ALTER COLUMN creation_date SET NOT null;
ALTER TABLE upgrade_task ALTER COLUMN modification_date TYPE timestamp(6);
ALTER TABLE upgrade_task ALTER COLUMN modification_date SET NOT null;

CREATE TABLE thumbnail (
  id                      int8 NOT NULL,
  uuid                   varchar(255) NOT NULL UNIQUE,
  thumbnail_type         varchar(255) NOT NULL,
  creation_date          timestamp(6) NOT NULL,
  document_id            int8 NOT NULL,
  CONSTRAINT linshare_thumbnail_pkey
    PRIMARY KEY (id));
ALTER TABLE thumbnail ADD CONSTRAINT FKthumbnail35163 FOREIGN KEY (document_id) REFERENCES document (id);

-- Update document : Set compute_thumbnail TRUE.
UPDATE document SET compute_thumbnail = true ;
-- end update document

-- Upgrade Task 2.1.0 UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR
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
  (13,
  'UNDEFINED',
  'UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR',
  'UPGRADE_2_1',
  null,
  null,
  13,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);
-- End Upgrade Task 2.1.0

-- End of your requests

-- LinShare version
SELECT ls_version();

-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed != 0;

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed != 0;
COMMIT;
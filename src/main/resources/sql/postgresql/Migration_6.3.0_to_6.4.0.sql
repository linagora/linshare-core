-- Postgresql migration script template

-- Migration script to upgrade from LinShare 6.3.0 to LinShare 6.4.0.

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'6.4.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '6.4.0';
	DECLARE version_from VARCHAR := '6.3.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE nb_upgrade_tasks INT := (SELECT count(*)::int  FROM upgrade_task WHERE status != 'SUCCESS' AND status != 'SKIPPED' AND priority != 'OPTIONAL');
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

SELECT ls_prechecks();
SELECT ls_check_user_connected();

SET client_min_messages = warning;


---- Here your queries
-- Create table Account_Contact_List
CREATE TABLE account_contact_lists
(
    account_id      int8  CONSTRAINT nonnull_account_contact_lists_account_id NOT NULL,
    contact_list_id int8  CONSTRAINT nonnull_account_contact_lists_contact_list_id NOT NULL,
    can_view_contact_list_members bool ,
    CONSTRAINT pk_account_contact_lists PRIMARY KEY (account_id, contact_list_id),
    CONSTRAINT fk_account_contact_lists_account_id FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT fk_account_contact_lists_contact_list_id FOREIGN KEY (contact_list_id) REFERENCES contact_list (id)
);

CREATE INDEX contact_list_identifier
    ON contact_list (identifier);

-- Functionality : GUESTS__CONTACT_LISTS
INSERT INTO policy(id, status, default_status, policy, system)
VALUES (359, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
VALUES (360, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
VALUES (361, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
VALUES (89, false, 'GUESTS__CONTACT_LISTS', 359, 360, 361, 1, 'GUESTS', true, now(), now());
INSERT INTO functionality_boolean(functionality_id, boolean_value)
VALUES (89, true);

-- update tables

ALTER TABLE account
    ADD COLUMN default_can_view_contact_list_members bool;


-- Upgrade LinShare version
SELECT ls_version();


COMMIT;
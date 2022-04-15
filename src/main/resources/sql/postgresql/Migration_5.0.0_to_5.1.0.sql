-- Postgresql migration script template

-- Migration script to upgrade from LinShare 5.0.0 to LinShare 5.1.0. 

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

---- Precheck functions 

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version, creation_date) VALUES ((SELECT nextVal('hibernate_sequence')),'5.1.0', now());
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '5.1.0';
	DECLARE version_from VARCHAR := '5.0.0';
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

SELECT ls_prechecks();
SELECT ls_check_user_connected();

SET client_min_messages = warning;


---- Here your queries

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


-- OpenLdap ldap filter to search users by group memberShip.
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    7,
    'd277f339-bc60-437d-8f66-515cba43df37',
    'USER_LDAP_PATTERN',
    'default-openldap-filtered-by-group-membership',
    'This is default openldap filtered by group membership.',
    'var group_dn = "cn=regular-users,ou=Groups,dc=linshare,dc=org";
    // initial query; looking for users
    var users = ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");
    logger.trace("users: {}", users);
    // second query to get all members (dn) of a group
    var dn_group_members = ldap.attribute(group_dn, "member");
    logger.trace("dn_group_members: {}", dn_group_members);
    // this array will contains all members without the baseDn
    var group_members = new java.util.ArrayList();
    for (var i = 0; i < dn_group_members.length; i++) {
        group_members.add(dn_group_members[i].replace("," + domain,""));
    };
    logger.trace("group_members: {}", group_members);
    // this array will contain the result of a left join between users and group_members
    var output =  new java.util.ArrayList();
    for (var i = 0; i < users.length; i++) {
        if (group_members.contains(users[i])) {
            output.add(users[i]);
        }
    }
    logger.debug("users (filtered): {}", output);
    // we must "return" the result.
    output;',
    'var group_dn = "cn=regular-users,ou=Groups,dc=linshare,dc=org";
    // initial query; looking for users
    var users = ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");
    logger.trace("users: {}", users);
    // second query to get all members (dn) of a group
    var dn_group_members = ldap.attribute(group_dn, "member");
    logger.trace("dn_group_members: {}", dn_group_members);
    // this array will contains all members without the baseDn
    var group_members = new java.util.ArrayList();
    for (var i = 0; i < dn_group_members.length; i++) {
        group_members.add(dn_group_members[i].replace("," + domain,""));
    };
    logger.trace("group_members: {}", group_members);
    // this array will contain the result of a left join between users and group_members
    var output =  new java.util.ArrayList();
    for (var i = 0; i < users.length; i++) {
        if (group_members.contains(users[i])) {
            output.add(users[i]);
        }
    }
    logger.debug("users (filtered): {}", output);
    // we must "return" the result.
    output;',
    true,
    'var group_dn = "cn=regular-users,ou=Groups,dc=linshare,dc=org";
    // initial query; looking for users
    var users = ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");
    logger.trace("users: {}", users);
    // second query to get all members (dn) of a group
    var dn_group_members = ldap.attribute(group_dn, "member");
    logger.trace("dn_group_members: {}", dn_group_members);
    // this array will contains all members without the baseDn
    var group_members = new java.util.ArrayList();
    for (var i = 0; i < dn_group_members.length; i++) {
        group_members.add(dn_group_members[i].replace("," + domain,""));
    };
    logger.trace("group_members: {}", group_members);
    // this array will contain the result of a left join between users and group_members
    var output =  new java.util.ArrayList();
    for (var i = 0; i < users.length; i++) {
        if (group_members.contains(users[i])) {
            output.add(users[i]);
        }
    }
    logger.debug("users (filtered): {}", output);
    // we must "return" the result.
    output;',
    'var group_dn = "cn=regular-users,ou=Groups,dc=linshare,dc=org";
    // initial query; looking for users
    var users = ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");
    logger.trace("users: {}", users);
    // second query to get all members (dn) of a group
    var dn_group_members = ldap.attribute(group_dn, "member");
    logger.trace("dn_group_members: {}", dn_group_members);
    // this array will contains all members without the baseDn
    var group_members = new java.util.ArrayList();
    for (var i = 0; i < dn_group_members.length; i++) {
        group_members.add(dn_group_members[i].replace("," + domain,""));
    };
    logger.trace("group_members: {}", group_members);
    // this array will contain the result of a left join between users and group_members
    var output =  new java.util.ArrayList();
    for (var i = 0; i < users.length; i++) {
        if (group_members.contains(users[i])) {
            output.add(users[i]);
        }
    }
    logger.debug("users (filtered): {}", output);
    // we must "return" the result.
    output;',
    100,
    100,
    10,
    10,
    now(),
    now()
);

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (27, 'user_mail', 'mail', false, true, true, 7, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (28, 'user_firstname', 'givenName', false, true, true, 7, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (29, 'user_lastname', 'sn', false, true, true, 7, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (30, 'user_uid', 'uid', false, true, true, 7, false);


-- default-openldap-filtered-by-memberOf
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    8,
    'a8914c53-4ad0-4b30-ae91-c2a2de8f8cc4',
    'USER_LDAP_PATTERN',
    'default-openldap-filtered-by-memberOf',
    'This is default openldap filtered by memberOf.',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(memberOf=cn=regular-users,ou=Groups,dc=linshare,dc=org)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(memberOf=cn=regular-users,ou=Groups,dc=linshare,dc=org)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(memberOf=cn=regular-users,ou=Groups,dc=linshare,dc=org)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(memberOf=cn=regular-users,ou=Groups,dc=linshare,dc=org)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (31, 'user_mail', 'mail', false, true, true, 8, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (32, 'user_firstname', 'givenName', false, true, true, 8, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (33, 'user_lastname', 'sn', false, true, true, 8, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (34, 'user_uid', 'uid', false, true, true, 8, false);

-- UPGRADE_5_1_ADD_INTERNAL_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER
INSERT INTO upgrade_task
(id,
 uuid,
 identifier,
 task_group,
 task_order,
 status,
 priority,
 creation_date,
 modification_date)
VALUES
    (53,
     'UNDEFINED',
     'UPGRADE_5_1_ADD_INTERNAL_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER',
     'UPGRADE_5_1',
     53,
     'NEW',
     'REQUIRED',
     now(),
     now());

-- UPGRADE_5_1_ADD_GUEST_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER
INSERT INTO upgrade_task
(id,
 uuid,
 identifier,
 task_group,
 parent_identifier,
 task_order,
 status,
 priority,
 creation_date,
 modification_date)
VALUES
    (54,
     'UNDEFINED',
     'UPGRADE_5_1_ADD_GUEST_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER',
     'UPGRADE_5_1',
     'UPGRADE_5_1_ADD_INTERNAL_ACCOUNT_TYPE_TO_SHARED_SPACE_MEMBER',
     54,
     'NEW',
     'REQUIRED',
     now(),
     now());

-- UPGRADE_5_1_RENAME_WORKGROUP_TO_WORK_GROUP
INSERT INTO upgrade_task
(id,
 uuid,
 identifier,
 task_group,
 task_order,
 status,
 priority,
 creation_date,
 modification_date)
VALUES
    (55,
     'UNDEFINED',
     'UPGRADE_5_1_RENAME_WORKGROUP_TO_WORK_GROUP',
     'UPGRADE_5_1',
     55,
     'NEW',
     'REQUIRED',
     now(),
     now());

-- Moderator table
CREATE TABLE moderator (
  id             int8 NOT NULL,
  uuid           varchar(255) NOT NULL UNIQUE,
  role           varchar(255) NOT NULL,
  creation_date     timestamp(6) NOT NULL,
  modification_date timestamp(6) NOT NULL,
  account_id     int8 NOT NULL,
  guest_id     int8 NOT NULL,
  PRIMARY KEY (id));

ALTER TABLE moderator ADD CONSTRAINT FKmoder87410 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE moderator ADD CONSTRAINT FKmoder87411 FOREIGN KEY (guest_id) REFERENCES account (id);

CREATE UNIQUE INDEX moderator_uuid_index ON moderator (uuid);
CREATE UNIQUE INDEX moderator_account_id_guest_id ON moderator (account_id, guest_id);


-- Transform guest owner to moderator
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE OR REPLACE FUNCTION ls_transform_guest_owner_to_moderator() RETURNS void AS $$
BEGIN
	DECLARE guest record;
	DECLARE o_id BIGINT;
	BEGIN
		FOR guest IN (SELECT id, creation_date, modification_date, owner_id FROM account WHERE account_type=3) loop
			SELECT id INTO o_id FROM account WHERE id = guest.owner_id;
			INSERT INTO moderator (id, uuid, role, creation_date, modification_date, account_id, guest_id) VALUES ((SELECT nextVal('hibernate_sequence')), uuid_generate_v4(), 'ADMIN', guest.creation_date, guest.modification_date, o_id, guest.id);
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;
SELECT ls_transform_guest_owner_to_moderator();

-- Account table, new field
ALTER TABLE account RENAME COLUMN mail_locale TO mail_locale;
ALTER TABLE account ADD COLUMN external_mail_locale VARCHAR(255);
UPDATE account SET external_mail_locale = 'en';
ALTER TABLE account ALTER COLUMN external_mail_locale SET NOT NULL;
ALTER TABLE account DROP COLUMN locale;

---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;

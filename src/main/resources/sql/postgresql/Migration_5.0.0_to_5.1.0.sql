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
ALTER TABLE account RENAME COLUMN external_mail_locale TO mail_locale;
ALTER TABLE account ADD COLUMN external_mail_locale VARCHAR(255);
UPDATE account SET external_mail_locale = 'en';
ALTER TABLE account ALTER COLUMN external_mail_locale SET NOT NULL;
ALTER TABLE account DROP COLUMN locale;

-- Share related tables, new field
ALTER TABLE share_entry_group ADD COLUMN external_mail_locale VARCHAR(255);
UPDATE share_entry_group SET external_mail_locale = 'en';
ALTER TABLE share_entry_group ALTER COLUMN external_mail_locale SET NOT NULL;

-- Add moderator mail_content and mail_content_lang
INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,41,41,'','','',NOW(),true,'','11650cc8-b73c-11ec-a84c-235f5362c454',true);

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (41,0,41,1,41,true,'1165b0b0-b73c-11ec-b20a-33728c1610a7');

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (141,1,41,1,41,true,'11663c88-b73c-11ec-8649-27fb71fc49cf');

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (241,2,41,1,41,true,'1166d990-b73c-11ec-b337-4b09e04976cd');

INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,42,42,'','','',NOW(),true,'','11679380-b73c-11ec-8bba-17ee00d3ad28',true);

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (42,0,42,1,42,true,'1167f136-b73c-11ec-947a-7f07dff5f89a');

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (142,1,42,1,42,true,'11685018-b73c-11ec-8a49-0b3657e2f901');

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (242,2,42,1,42,true,'1168ad92-b73c-11ec-b7d0-8bf4f18337f7');

INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,modification_date,readonly,subject,uuid,visible) VALUES ('',NOW(),'',1,43,43,'','','',NOW(),true,'','116957c4-b73c-11ec-80f2-2b24398412f7',true);

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (43,0,43,1,43,true,'1169c600-b73c-11ec-8f48-177d8ee3ef97');

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (143,1,43,1,43,true,'116a1cb8-b73c-11ec-9e9e-b7c7d1b3387a');

INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES (243,2,43,1,43,true,'116a9968-b73c-11ec-b40a-53616caa8660');

-- Mail activation: GUEST_MODERATOR_CREATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (338, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (339, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (340, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
	VALUES(42, false, 'GUEST_MODERATOR_CREATION', 338, 339, 340, 1, true);

-- Mail activation: GUEST_MODERATOR_UPDATE
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (341, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (342, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (343, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
	VALUES(43, false, 'GUEST_MODERATOR_UPDATE', 341, 342, 343, 1, true);

-- Mail activation: GUEST_MODERATOR_DELETION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (344, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (345, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (346, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
	VALUES(44, false, 'GUEST_MODERATOR_DELETION', 344, 345, 346, 1, true);

	-- SHARED_SPACE__WORKSPACE_LIMIT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (347, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (348, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (71, false, 'SHARED_SPACE__WORKSPACE_LIMIT', 347, 348, 1, 'SHARED_SPACE', true, now(), now());
INSERT INTO functionality_integer(functionality_id, integer_max_value, integer_default_value, default_value_used, max_value_used, unlimited_value, unlimited_value_used)
	VALUES (71, 5, 0, false, true, false, false);

-- SHARED_SPACE__NESTED_WORKGROUPS_LIMIT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (349, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (350, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (72, false, 'SHARED_SPACE__NESTED_WORKGROUPS_LIMIT', 349, 350, 1, 'SHARED_SPACE', true, now(), now());
INSERT INTO functionality_integer(functionality_id, integer_max_value, integer_default_value, default_value_used, max_value_used, unlimited_value, unlimited_value_used)
	VALUES (72, 5, 5, false, true, false, false);

-- UPLOAD_REQUEST__LIMIT
	INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (351, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (352, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (73, false, 'UPLOAD_REQUEST__LIMIT', 351, 352, 1, 'UPLOAD_REQUEST', true, now(), now());
INSERT INTO functionality_integer(functionality_id, integer_max_value, integer_default_value, default_value_used, max_value_used, unlimited_value, unlimited_value_used)
	VALUES (73, 5, 5, false, true, false, false);

ALTER TABLE account ADD COLUMN guest_source_domain_id int8;
ALTER TABLE account ADD CONSTRAINT guest_source_domain_id_fk FOREIGN KEY (guest_source_domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
CREATE INDEX guest_source_domain_id_index ON account (guest_source_domain_id);
UPDATE account AS g SET guest_source_domain_id = o.domain_id FROM account AS o WHERE g.owner_id = o.id AND g.account_type = 3;


-- GUEST_MODERATOR_CREATION
UPDATE mail_content SET subject='[( #{subject(${actor.firstName},${actor.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName},${role})}"></span>
          <!--/* Access button to guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${guestLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{guestNameTitle},${guest.mail})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a ajouté comme modérateur d''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a ajouté comme modérateur de <b>{2}</b> <b>{3}</b> avec <b>{4}</b> role.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} added you as guest moderator
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you as a guest moderator for <b>{2}</b> <b>{3}</b> with <b>{4}</b> role.
guestNameTitle = Guest',
messages_russian='accessToLinshareBTn = Access
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you as a guest moderator for <b>{2}</b> <b>{3}</b> with <b>{4}</b> role.
subject = {0} {1} added you as guest moderator
guestNameTitle = Guest' WHERE id=41;

-- GUEST_MODERATOR_UPDATE
UPDATE mail_content SET subject='[( #{subject(${actor.firstName},${actor.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName},${role})}"></span>
          <!--/* Access button to guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${guestLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
           <th:block data-th-replace="layout :: infoEditedItem(#{role}, ${role.oldValue}, ${role.value})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a modifié le modérateur role
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a modifié le modérateur role pour <b>{2}</b> <b>{3}</b>.
role = Role',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} updated your moderator role
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated your moderator role on the guest <b>{2}</b> <b>{3}</b>.
role = Role',
messages_russian='accessToLinshareBTn = Access
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> updated your moderator role on the guest <b>{2}</b> <b>{3}.
subject = {0} {1} updated your moderator role
role = Role' WHERE id=42;

-- GUEST_MODERATOR_DELETION
UPDATE mail_content SET subject='[( #{subject(${actor.firstName},${actor.lastName}, #{productName})})]',body='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${account.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${actor.firstName},${actor.lastName},${guest.firstName},${guest.lastName})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
        <th:block data-th-replace="layout :: infoStandardArea(#{guestNameTitle},${guest.mail})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>',messages_french='accessToLinshareBTn = Accéder
subject = {0} {1} vous a supprimé de la liste des modérateurs d''invité
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a supprimé de la liste des modérateurs de <b>{2}</b> <b>{3}</b>.
guestNameTitle = Invité',
messages_english='accessToLinshareBTn = Access
subject = {0} {1} deleted you from guest moderator''s list
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> deleted you from moderators list of <b>{2}</b> <b>{3}</b>.
guestNameTitle = Guest',
messages_russian='accessToLinshareBTn = Access
subject = {0} {1} deleted you from guest moderator''s list
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> deleted you from moderators list of <b>{2}</b> <b>{3}</b>.
guestNameTitle = Guest' WHERE id=43;


-- Fix issue of wrong remaing days of UPLOAD_REQUEST_WARN_BEFORE_EXPIRY
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
beginningMainMsgIndividual =   Votre Invitation de dépôt individuelle: {0}, sera clôturée dans  <b>{1} jours</b>.
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

CREATE TABLE external_recipient_favourite (
  id                      int8 NOT NULL,
  uuid                    varchar(255) NOT NULL UNIQUE,
  recipient_favourite_id  int8 NOT NULL,
  expiration_date         timestamp(6),
  CONSTRAINT linshare_external_recipient_favourite_pkey
    PRIMARY KEY (id));

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
    (56,
     'UNDEFINED',
     'OPTIONAL_POPULATE_EXTERNAL_FAVOURITE_RECIPIENT',
     'OPTIONALS',
     56,
     'NEW',
     'OPTIONAL',
     now(),
     now());
---- End of your queries

-- Upgrade LinShare version
SELECT ls_version();

COMMIT;

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
	DECLARE nb_upgrade_tasks INT := (SELECT count(*)::int  FROM upgrade_task WHERE status != 'SUCCESS' OR status != 'SKIPPED');
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

ALTER TABLE domain_abstract ADD COLUMN purge_step varchar(255) DEFAULT 'IN_USE' NOT NULL;
ALTER TABLE domain_abstract ALTER COLUMN domain_policy_id DROP NOT NULL;

ALTER TABLE mime_policy DROP COLUMN version;

ALTER TABLE quota ADD COLUMN domain_shared bool;
ALTER TABLE quota ADD COLUMN domain_shared_override bool;
ALTER TABLE quota ADD COLUMN default_domain_shared bool;
ALTER TABLE quota ADD COLUMN default_domain_shared_override bool;

ALTER TABLE upgrade_task ALTER COLUMN creation_date TYPE timestamp(6);
ALTER TABLE upgrade_task ALTER COLUMN creation_date SET NOT NULL;
ALTER TABLE upgrade_task ALTER COLUMN modification_date TYPE timestamp(6);
ALTER TABLE upgrade_task ALTER COLUMN modification_date SET NOT NULL;
ALTER TABLE upgrade_task ALTER COLUMN priority SET NOT NULL;

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

-- for top domains and sub domains
UPDATE quota SET domain_shared_override = false, domain_shared = false WHERE quota_type = 'DOMAIN_QUOTA';
-- for root domain
UPDATE quota SET domain_shared_override = null, domain_shared = true WHERE id = 1;

-- for root domain
UPDATE quota SET default_domain_shared_override = null, default_domain_shared = false WHERE id = 1;
-- for top domains and sub domains
UPDATE quota SET default_domain_shared_override = false, default_domain_shared = false WHERE domain_parent_id = 1 AND quota_type = 'DOMAIN_QUOTA';

-- every accounts.
UPDATE quota SET domain_shared_override = false, domain_shared = false WHERE quota_type = 'ACCOUNT_QUOTA';
-- root account
UPDATE quota SET domain_shared_override = null, domain_shared = true WHERE account_id = 1;

-- New functionnalities
-- Functionality : ANONYMOUS_URL__FORCE_ANONYMOUS_SHARING
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (279, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (280, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (281, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
	VALUES(58, false, 'ANONYMOUS_URL__FORCE_ANONYMOUS_SHARING', 279, 280, 281, 1, 'ANONYMOUS_URL', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value)
	VALUES (58, false);

-- Functionality : ANONYMOUS_URL__HIDE_RECEIVED_SHARE_MENU
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (282, false, false, 2, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (283, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param)
	VALUES(59, false, 'ANONYMOUS_URL__HIDE_RECEIVED_SHARE_MENU', 282, 283, 1, 'ANONYMOUS_URL', true);


-- Begin Upgrade Task 2.1.0
  -- TASK: UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR
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

-- TASK: UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS
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
  (14,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS',
  'UPGRADE_2_1',
  null,
  null,
  14,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_REMOVE_ALL_THREAD_ENTRIES
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
  (15,
  'UNDEFINED',
  'UPGRADE_2_1_REMOVE_ALL_THREAD_ENTRIES',
  'UPGRADE_2_1',
  null,
  null,
  15,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS
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
  (16,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  'UPGRADE_2_1',
  null,
  'UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS',
  16,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA
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
  (17,
  'UNDEFINED',
  'UPGRADE_2_1_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA',
  'UPGRADE_2_1',
  null,
  'UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS',
  17,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_1_ADD_ALL_NEW_MIME_TYPE
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
  (18,
  'UNDEFINED',
  'UPGRADE_2_1_ADD_ALL_NEW_MIME_TYPE',
  'UPGRADE_2_1',
  null,
  null,
  18,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- End Upgrade Task 2.1.0

-- MailActivation : BEGIN
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (231, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (232, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (233, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(17, false, 'UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT', 231, 232, 233, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (234, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (235, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (236, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(18, false, 'UPLOAD_REQUEST_ACTIVATED_FOR_OWNER', 234, 235, 236, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (237, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (238, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (239, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(19, false, 'UPLOAD_REQUEST_REMINDER', 237, 238, 239, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (240, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (241, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (242, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(20, false, 'UPLOAD_REQUEST_PASSWORD_RENEWAL', 240, 241, 242, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (243, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (244, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (245, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(21, false, 'UPLOAD_REQUEST_CREATED', 243, 244, 245, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (246, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (247, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (248, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(22, false, 'UPLOAD_REQUEST_CLOSED_BY_OWNER', 246, 247, 248, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (249, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (250, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (251, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(23, false, 'UPLOAD_REQUEST_RECIPIENT_REMOVED', 249, 250, 251, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (252, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (253, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (254, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(24, false, 'UPLOAD_REQUEST_UPDATED_SETTINGS', 252, 253, 254, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (255, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (256, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (257, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(25, false, 'UPLOAD_REQUEST_FILE_DELETED_BY_OWNER', 255, 256, 257, 1, true);

INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (258, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (259, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (260, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(26, false, 'GUEST_WARN_OWNER_ABOUT_GUEST_EXPIRATION', 258, 259, 260, 1, true);

-- MailActivation : SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (261, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (262, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (263, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 	VALUES(27, false, 'SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD', 261, 262, 263, 1, true);

-- MailActivation : SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (264, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (265, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (266, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(28, false, 'SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE', 264, 265, 266, 1, true);

-- MailActivation : WORKGROUP_WARN_NEW_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (267, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (268, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (269, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)  
 	VALUES(29, false, 'WORKGROUP_WARN_NEW_MEMBER', 267, 268, 269, 1, true);

-- MailActivation : WORKGROUP_WARN_UPDATED_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (270, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (271, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (272, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(30, false, 'WORKGROUP_WARN_UPDATED_MEMBER', 270, 271, 272, 1, true);

-- MailActivation : WORKGROUP_WARN_DELETED_MEMBER
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (273, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (274, true, true, 1, false);
	INSERT INTO policy(id, status, default_status, policy, system)
VALUES (275, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(31, false, 'WORKGROUP_WARN_DELETED_MEMBER', 273, 274, 275, 1, true);

-- MailActivation : GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (276, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (277, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (278, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable) 
 	VALUES(32, false, 'GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET', 276, 277, 278, 1, true);
-- MailActivation : END

-- Mail Layout
UPDATE mail_layout SET  messages_french = 'common.availableUntil = Expire le
common.byYou= | Par vous
common.download= Télécharger
common.filesInShare=Fichiers joints
common.recipients = Destinataires
common.titleSharedThe= Partagé le
date.format=d MMMM, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrateur
workGroupRightWirteTitle = Écriture
workGroupRightReadTitle = Lecture
welcomeMessage = Bonjour {0},', messages_english = 'common.availableUntil = Expiry date
common.byYou= | By you
common.download= Download
common.filesInShare = Attached files
common.recipients = Recipients
common.titleSharedThe= Creation date
date.format= MMMM d, yyyy
productCompagny=Linagora
productName=LinShare
workGroupRightAdminTitle = Administrator
workGroupRightWirteTitle = Write
workGroupRightReadTitle = Read
welcomeMessage = Hello {0},' WHERE id = 1 ;
-- End Mail Layout

-- Mail Content
UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(customMessage)}">
      <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${shareOwner.firstName} , ${shareOwner.lastName})}">Peter Wilson</b> :
        </span>name = {0} {1}
        <span id="message-content" data-th-text="*{customMessage}">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div data-th-if="(${!anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        </div>
        <div data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        </div> <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
            <span data-th-if="(${sharesCount} ==  1)"
                  data-th-utext="#{mainMsgSingular(${shareOwner.firstName},${shareOwner.lastName},${sharesCount})}">
            Peter WILSON has shared 4 file with you
            </span>
          <span data-th-if="(${sharesCount} > 1)"
                data-th-utext="#{mainMsgPlural(${shareOwner.firstName},${shareOwner.lastName},${sharesCount})}">
            Peter WILSON has shared 4 files with you
            </span>
          <br/>
          <!--/* Check if the external user has a password protected file share */-->
          <span data-th-if="(${protected})">
       <span data-th-if="(${sharesCount} ==  1)" data-th-text="#{helpPasswordMsgSingular}">Click on the link below in order to download it     </span>
            <span data-th-if="(${sharesCount} >  1)" data-th-text="#{helpPasswordMsgPlural}">Click on the links below in order to download them </span>
            </span>
          <span data-th-if="(${!anonymous})">
            <span data-th-if="(${sharesCount} ==  1)">
              <span  data-th-utext="#{click}"></span>
                <span>
                 <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{link}" th:href="@{${filesSharesLink}}" >
                  link
                 </a>
               </span>
              <span data-th-utext="#{helpMsgSingular}"></span>
            </span>
            <span data-th-if="(${sharesCount} >  1)">
              <span  data-th-utext="#{click}"></span>
              <span>
                <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{link}" th:href="@{${filesSharesLink}}" >
                 link
               </a>
              </span>
             <span data-th-utext="#{helpMsgPlural}"></span>
            </span>
            </span>
        </p>
        <!--/* Single download link for external recipient */-->
        <div data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: actionButtonLink(#{downloadBtn},${anonymousURL})"/>
        </div>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <div data-th-if="(${protected})">
      <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
    </div>
    <div data-th-if="(${anonymous})">
      <th:block data-th-replace="layout :: infoActionLink(#{downloadLink},${anonymousURL})"/>
    </div>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shares[0].creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shares[0].expirationDate})"/>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${shares},${anonymous})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>', messages_french =  'downloadBtn = Télécharger
downloadLink = Lien de téléchargement
helpMsgSingular =  pour visualiser le document partagé.
helpMsgPlural =pour visualiser tous les documents du partage.
helpPasswordMsgSingular = Cliquez sur le lien pour le télécharger et saisissez le mot de passe fourni ci.
helpPasswordMsgPlural = Cliquez sur le lien pour les télécharger et saisissez le mot de passe fourni.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a partagé {2} fichiers avec vous.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a partagé {2} fichier  avec vous.
msgFrom = Vous avez un message de
name = {0} {1}
password = Mot de passe
subjectCustomAlt =de {0} {1}
subjectPlural =  {0} {1} a partagé des fichiers avec vous
subjectSingular =  {0} {1} vous a partagé un fichier avec vous
click = Cliquez sur ce
link = lien', messages_english = 'downloadBtn = Download
downloadLink = Download link
helpMsgPlural = to access to all documents in this share.
helpMsgSingular = to access to the document in this share.
helpPasswordMsgSingular = Click on the link below in order to download it and enter the provided password.
helpPasswordMsgPlural = Click on the link below in order to download them and enter the provided password.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} files</b> with you.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} file</b> with you.
msgFrom = You have a message from
name = {0} {1}
password = Password
subjectCustomAlt =by {0} {1}
subjectPlural = {0} {1} has shared some files with you
subjectSingular = {0} {1} has shared a file with you
click = Follow this
link = link' WHERE id = 2;

UPDATE mail_content SET body = '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(customMessage)}">
      <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFor}">You have a message from</span>
        </span>
        <span id="message-content" data-th-text="*{customMessage}">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{numFilesMsgPlural(${documentsCount})}">
            Peter WILSON has  shared 4 files
            </span>
          <span data-th-if="(${documentsCount} ==  1)" data-th-utext="#{numFilesMsgSingular(${documentsCount})}">
            Peter WILSON has  shared 1 file
            </span>
          <span data-th-if="(${recipientsCount} >  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgPlural(${#dates.format(expirationDate,df)},${recipientsCount})}">
             to 3 recipients set to expire for the 7th December 2018
            </span>
          <span data-th-if="(${recipientsCount} ==  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgSingular(${#dates.format(expirationDate,df)},${recipientsCount})}">
            to 1 recipient set to expire for the 7th December 2018
            </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End upper of main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoRecipientListingArea(#{common.recipients},${recipients})"/>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{common.filesInShare},${documents},false)"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', messages_french = 'numFilesMsgPlural = Vous avez partagé <b>{0} fichiers</b>
numFilesMsgSingular = Vous avez partagé <b>{0} fichier</b>
recipientCountMsgPlural = avec <b>{1} destinataires</b>. Ce partage expirera le <b>{0}</b>.
recipientCountMsgSingular = avec <b>{1} destinataire</b>. Ce partage expirera le <b>{0}</b>.
subjectPlural = Vous avez partagé des fichiers
subjectSingular = Vous avez partagé un fichier
msgFor = Votre message de partage', messages_english = 'numFilesMsgPlural = You have shared <b>{0} files</b>
numFilesMsgSingular = You have shared <b>{0} file</b>
recipientCountMsgPlural =   to <b>{1} recipients</b>. The fileshare will expire on : {0}.
recipientCountMsgSingular =   to <b>{1} recipient</b>. The fileshare will  expire on : {0}.
subjectPlural =  You have shared some files
subjectSingular = You have shared a file
msgFor = Your message of sharing' WHERE id = 3;

INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (31, 1, '', true, 31, '[( #{subject})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${guest.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(#{productName},${guest.mail})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'd5c4e4ba-d6b5-11e7-9bac-0f07881b63bc', now(), now(), true, 'accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d\''''expiration
mainMsg = Le mot de passe du compte {0} <b>{1}</b> a été modifié.
subject = Votre mot de passe a été modifié', 'accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
mainMsg = The password of the account {0} <b>{1}</b> was modified.
subject = Your password has been modified');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (28, 1, '', true, 28, '[( #{subject(${workGroupName})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
            <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${threadMember.admin})">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
    </th:block>
    <th:block data-th-if="(!${threadMember.admin})">
        <th:block data-th-if="(${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
        </th:block>
        <th:block data-th-if="(!${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
        </th:block>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupCreationDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'cd33405c-c617-11e7-be9c-c763a78e452c', now(), now(), true, 'workGroupCreationDateTitle = Date de création
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a ajouté au groupe de travail : <b>{2}</b>
subject = Vous avez été ajouté au groupe de travail {0}
workGroupRight = Droit par défaut 
workGroupNameTitle = Nom du groupe de travail', 'workGroupCreationDateTitle = Creation date
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> added you to the workgroup : <b>{2}</b>
subject = You have been added to the workgroup {0}
workGroupRight = Default right
workGroupNameTitle = Workgroup Name');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (16, 1, NULL, true, 16, '[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="*{body}">
                  Hi Amy,<br>
                  As agreed,  could you send me the report. Feel free to contact me if need be. <br/>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                   <th:block data-th-if="(${!request.wasPreviouslyCreated})">
                       <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                          Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                       </span>
                   </th:block>
                    <th:block data-th-if="(${request.wasPreviouslyCreated})">
                       <span data-th-text="#{msgAlt}"> Peter Wilson''s Upload Request depot is now activated..</span>
                     </th:block>
                     <br/>
                     <!--/* Check if the external user has a password protected file share */-->
                     <span data-th-if="(${!protected})">
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                     </span>
                     <span data-th-if="(${protected})">
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                     </span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="(${protected})">
               <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
           <div data-th-if="(${totalMaxDepotSize})">
                    <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="(${isgrouped})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f00708c-60e7-11e7-a8eb-0800271467bb', now(), now(), true, 'buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu\''''au
depotSize = Taille du dépôt
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers dans l\''''Invitation de Dépôt intitulée : {2}.
msgAlt = L\''''Invitation de Dépôt de {0} {1} est désormais activée.
msgFrom = Le message de
msgProtected = Vous pouvez y accéder en cliquant sur le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires associés au dépôt
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}', 'buttonMsg = Access to the depot
closureDate = Depot closure date
depotSize = Size of the depot
mainMsg = <b>{0} {1}</b> invited you to upload some files in the Upload Request depot labeled : {2}.
msgAlt = {0} {1} \''''s Upload Request depot is now activated.
msgFrom = Message from
msgProtected = In order to access it click the link below and enter the provided password.
msgUnProtected = In order to access it click the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients of the depot
subject = {0} {1} invites you to upload some files in the depot : {2}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (20, 1, '', true, 20, '[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <!--/* If the sender has added a  customized message */-->
    <th:block data-th-if="${!#strings.isEmpty(body)}">
      <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgFrom}">You have a message from</span>
          <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
        </span>
        <span id="message-content" data-th-text="*{body}">
          Hi Amy,<br>
          As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
        </span>
      </div>
    </th:block>
    <!--/* End of customized message */-->
    <!--/* main-content container */-->
    <div th:replace="layout :: contentUpperSection(~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings for external or internal user */-->
        <div>
          <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
        </div>
          <!--/* End of Greetings for external or internal recipient */-->
        <!--/* Main email  message content*/-->
        <p>
                 <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                </span>
 <span th:with="df=#{customDate}" data-th-text="${#dates.format(request.activationDate,df)}">7th of November, 2018</span>
        </p>
        <!--/* End of Main email message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
  <div data-th-if="${!#strings.isEmpty(request.activationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{activationDate},${request.activationDate})"/>
            </div>
     <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
       <div data-th-if="(${totalMaxDepotSize})">
               <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
         </div>
  <div data-th-if="(${isgrouped})">
         <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
    </div>
  </section>  <!--/* End of Secondary content for bottom email section */-->
  </div>
</body>
</html>', '9f0d6ac6-60e7-11e7-b1b6-0800271467bb', now(), now(), true, 'activationDate = Ouverture du dépôt le
closureDate = Dépôt  disponible jusqu\''''au
customDate= d MMMM, yyyy
depotSize = Taille du dépôt
mainMsg = <b>{0} {1}</b> vous a créé une Invitation de Dépôt, qui sera ouverte au dépôt le
msgFrom = Le message de
name = {0} {1}
recipientsOfDepot = Destinataires associés au dépôt
subject = {0} {1} vous a créé une Invitation de Dépot', 'activationDate = Depot activation date
closureDate = Depot closure date
customDate= MMMM d, yyyy
depotSize = Size of the depot
mainMsg = <b>{0} {1}</b> has created an Upload Request Depot for you, set to open for
msgFrom = Message from
name = {0} {1}
recipientsOfDepot = Recipients of the depot
subject = {0} {1} has created an Upload Request Depot for you.');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (22, 1, '', true, 22, '[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has deleted your access to the depot : : subject.
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <th:block data-th-replace="layout :: infoDateArea(#{deletionDate},${deletionDate})"/>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f146074-60e7-11e7-94ba-0800271467bb', now(), now(), true, 'deletionDate = Dépôt  supprimé le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  vous invite à  déposer des fichiers dans l\''''Invitation de Dépôt intitulée : {2}.
subject = {0} {1} a supprimé votre accès au dépôt : {2}', 'deletionDate = Deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has deleted your access to the depot : {2}.
subject = {0} {1} has removed your access to the depot : {2}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (23, 1, '', true, 23, '[# th:if="${!subject.modified}"]
[(#{subject(${subject.value})})]
[/]
[# th:if="${subject.modified}"]
[(#{subject(${subject.oldValue})})]
[/]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                     <span data-th-utext="#{secondaryMsg}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                  </p>
                  <!--/* If the sender has added a  customized message */-->
                  <th:block data-th-if="(${message.modified})">
                     <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                        <span id="message-title">
                        <span data-th-text="#{msgFrom}">You have a message from</span>
                        <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                        </span>
                        <span id="message-content" data-th-text="*{message.value}">
                        Hi Amy,<br>
                        As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
                        </span>
                     </div>
                  </th:block>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <span data-th-if="(${expiryDate.modified})">
               <th:block data-th-replace="layout :: infoEditedDateArea(#{expiryDate},${expiryDate.oldValue},${expiryDate.value})"/>
            </span>
            <span data-th-if="(${activationDate.modified})">
               <th:block data-th-replace="layout :: infoEditedDateArea(#{activationDate},${activationDate.oldValue},${activationDate.value})"/>
            </span>
            <span data-th-if="(${subject.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{nameOfDepot},${subject.oldValue},${subject.value})"/>
            </span>
            <span data-th-if="(${closureRight.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{closureRight},${closureRight.oldValue},${closureRight.value})"/>
            </span>
            <span data-th-if="(${deletionRight.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{deletionRight},${deletionRight.oldValue},${deletionRight.value})"/>
            </span>
            <span data-th-if="(${maxFileSize.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{maxFileSize},${maxFileSize.oldValue},${maxFileSize.value})"/>
            </span>
            <span data-th-if="(${maxFileNum.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{maxFileNum},${maxFileNum.oldValue},${maxFileNum.value})"/>
            </span>
            <span data-th-if="(${totalMaxDepotSize.modified})">
               <th:block data-th-replace="layout :: infoEditedItem(#{depotSize},${totalMaxDepotSize.oldValue},${totalMaxDepotSize.value})"/>
            </span>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f17d614-60e7-11e7-94e3-0800271467bb', now(), now(), true, 'activationDate = Date d\''''activation
closureRight = Droit de dépôt
deletionRight = Droit de suppression
depotSize = Taille du dépôt
expiryDate = Date de clôture
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  a modifié des paramètres de l\''''Invitation de dépôt.
maxFileNum = Nbr. max de fichier
maxFileSize = Taille max. de fichier
msgFrom = Nouveau message de
name = {0} {1}
nameOfDepot: Nom du dépôt
secondaryMsg = Ces modifications sont listés ci-dessous.
subject = Modifications des paramètres de l\''''Invitation de dépôt : {0}', 'activationDate = Activation date
closureRight = Closure rights
deletionRight = Deletion rights
depotSize = Max. Depot size
expiryDate = Closure date
mainMsg =   <b> {0} <span style="text-transform:uppercase">{1}</span> </b>  has updated some settings of the upload request depot.
maxFileNum = Max file number
maxFileSize = Max. file size
msgFrom =  New message from
name = {0} {1}
nameOfDepot: Name of the depot
secondaryMsg = You may find the updated settings listed below.
subject = Updated Settings for the Upload Request : {0}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (17, 1, '', true, 17, '[(#{subject(${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${request.subject})}">
                     Your Upload Request depot labeled $subject is now activated.
                     </span>
                     <span data-th-text="#{msgLink}">In order to access it click the link below.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
             <div data-th-if="(${totalMaxDepotSize})">
                   <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f03b0bc-60e7-11e7-a512-0800271467bb', now(), now(), true, 'buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu\''''au
depotSize = Taille du dépôt
mainMsg = Votre Invitation de Dépôt : {0}, est désormais active.
msgLink = Vous pouvez y accéder en cliquant sur le lien ci-dessous.
recipientsOfDepot = Destinataires associés au dépôt
subject = Votre invitation de dépôt : {0}, est désormais active', 'buttonMsg = Access to the depot
closureDate = Depot closure date
depotSize = Size of the depot
mainMsg = Your Upload Request depot labeled : {0}, is now activated.
msgLink = In order to access it click the link below.
recipientsOfDepot = Recipients of the depot
subject = Your Upload Request  : {0}, has been activated');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (18, 1, '', true, 18, '[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a  customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection(~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName}, ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="*{body}">
                  Hi Amy,<br>
                  As agreed,  i am sending you the report as well as the related files. Feel free to contact me if need be. <br>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson reminds you that he still has not received the requested files. 
                     </span>
                     <span data-th-utext="#{mainMsgEnd}">
                     You can upload your files in the provided depot made available to you labeled  subject.
                     </span>
                     <!--/* Check if the external user has a password protected file share */-->
                     <br/>
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
            <div data-th-if="(${totalMaxDepotSize})">
                 <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="(${isgrouped})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f06f22c-60e7-11e7-a753-0800271467bb', now(), now(), true, 'buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu\''''au
depotSize = Taille du dépôt
mainMsg = <b>{0} {1}</b> vous rappelle qu''il n\''''a toujours pas reçu les fichiers demandés. 
mainMsgEnd = Vous pouvez déposer vos fichiers dans le dépôt qui a été mise à votre disposition.
msgFrom =  Le message de
msgUnProtected = Pour y accéder, cliquer sur le lien ci-dessous.
name = {0} {1}
recipientsOfDepot = Destinataires associés au dépôt
subject = {0} {1} attend toujours des fichiers de votre part', 'buttonMsg = Access to the depot
closureDate = Depot closure date
depotSize = Size of the depot
mainMsg = <b>{0} {1}</b> reminds you that he still has not received the requested files. 
mainMsgEnd = You can upload your files in the provided depot made available to you.
msgFrom = Message from
msgUnProtected = In order to access it click the link below.
name = {0} {1}
recipientsOfDepot = Recipients of the depot
subject = {0} {1} is still waiting for some files');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (19, 1, '', true, 19, '[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                     </span>
                     <br/>
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f0a2758-60e7-11e7-b1e9-0800271467bb', now(), now(), true, 'buttonMsg = Accès au dépôt
closureDate = Dépôt disponible jusqu\''''au
mainMsg = <b>{0} {1}</b> a modifié le mot de passe d\''''accès à l\''''Invitation de Dépôt : {2}.
msgProtected = Vous trouverez ci-dessous le nouveau mot de passe ainsi que le lien d\''''accès.
password = Mot de passe
subject = {0} {1} vous envoie le nouveau mot de passe du dépôt : {2}', 'buttonMsg = Access to the depot
closureDate = Depot closure date
mainMsg = <b>{0} {1}</b> has changed the password of the Upload Request : {2}
msgProtected = You may find the new password below as well as the access link.
password = Password
subject = {0} {1} sent you the new password for the depot: {2}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (21, 1, '', true, 21, '[( #{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                     Peter WILSON has closed prematurely his Upload Request Depot labeled : subject.
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="(${isgrouped})">
               <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f10ba3c-60e7-11e7-9a73-0800271467bb', now(), now(), true, 'closureDate = Dépôt clôturé le
filesInURDepot = Fichiers déposés
mainMsg = <b>{0} {1}</b> a clôturé prématurément son Invitation de Dépôt intitulée : {2}.
recipientsOfDepot = Destinataires
subject = {0} {1} a clôturé l\''''Invitation de Dépot : {2}', 'closureDate = Depot closure date
filesInURDepot = Files uploaded
mainMsg = <b>{0} {1}</b> has closed prematurely, his Upload Request Depot labeled : {2}.
recipientsOfDepot = Recipients
subject = {0} {1} has closed his Upload Request depot : {2}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (24, 1, '', true, 24, '[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${document.name})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                     <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${document.name},${subject})}">
                 Peter WILSON has deleted the file my-file.txt from the depot : subject
                     </span>
                  </p>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <th:block data-th-replace="layout :: infoDateArea(#{deletionDate},${deletionDate})"/>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>', '9f1aca72-60e7-11e7-a75f-0800271467bb', now(), now(), true, 'closureDate = Dépôt disponible jusqu\''''au
deletionDate = Fichier supprimé le
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b>a supprimé le fichier  <b>{2} </b> de  l\''''Invitation de Dépôt : {3}
subject = {0} {1} a supprimé {2} du dépôt', 'closureDate = Depot closure date
deletionDate = File deletion date
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has deleted the file <b>{2} </b>from the depot  : {3}.
subject = {0} {1} has deleted {2} from the depot');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (29, 1, '', true, 29, '[(#{subject(${workGroupName})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${threadMember.admin})">
       <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightAdminTitle})"/>
    </th:block>
    <th:block data-th-if="(!${threadMember.admin})">
        <th:block data-th-if="(${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightWirteTitle})"/>
        </th:block>
        <th:block data-th-if="(!${threadMember.canUpload})">
             <th:block data-th-replace="layout :: infoStandardArea(#{workGroupRight}, #{workGroupRightReadTitle})"/>
        </th:block>
    </th:block>
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{workGroupUpdatedDateTitle},${threadMember.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'a4ef5ac0-c619-11e7-886b-7bf95112b643', now(), now(), true, 'workGroupUpdatedDateTitle = Date de la mise à jour
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a mis à jour vos droits sur le  groupe de travail <b>{2}</b>.
subject =  Vos droits sur le groupe de travail {0} ont été mis à jour
workGroupRight =  Nouveau droit
workGroupNameTitle = Nom du groupe de travail', 'workGroupUpdatedDateTitle = Updated date
mainMsg = Your rights on the workgroup  <b>{2}</b> were updated by  <b> {0} <span style="text-transform:uppercase">{1}</span></b>.
subject =  Your rights on the workgroup {0} was updated.
workGroupRight = Current right
workGroupNameTitle = Workgroup Name');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (27, 1, '', true, 27, '[( #{subject(${share.name})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${shareOwner.firstName},${shareOwner.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '935a0086-c53c-11e7-83d4-3fe6e27902d8', now(), now(), true, 'shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d\''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b> a expiré et a été supprimé par le <b>système</b>.
subject = Le partage {0} a expiré
fileNameEndOfLine = {0}', 'shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b> has expired and been deleted by the <b>system</b>.
subject = The fileshare {0} has expired
fileNameEndOfLine = {0}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (30, 1, '', true, 30, '[( #{subject(${workGroupName})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${member.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${owner.firstName},${owner.lastName},${workGroupName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{workGroupNameTitle},${workGroupName})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '47404f3c-c61a-11e7-bc5e-27c80414733b', now(), now(), true, 'subject = Les accès au groupe de travail {0} vous ont été retirés.
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a retiré du groupe de travail <b>{2}</b>
workGroupNameTitle = Nom du groupe de travail', 'subject = Your access to the workgroup {0} was withdrawn
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> removed you from the workgroup  <b>{2}</b>
workGroupNameTitle = Workgroup Name');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (25, 1, '', true, 25, '[( #{subject(${guest.firstName},${guest.lastName}, #{productName})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{mainMsg(${guest.firstName},${guest.lastName},${daysLeft})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{accountCreationDateTitle},${guestCreationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{userEmailTitle},${guest.mail})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '82cd65c6-b968-11e7-aee9-eb159cedc719', now(), now(), true, 'accessToLinshareBTn = Le compte de votre invité expire
accountCreationDateTitle = Date de création
accountExpiryDateTitle = Date d\''''expiration
activationLinkTitle = Initialization link
mainMsg = Le compte invité de : <b> {0} <span style="text-transform:uppercase">{1}</span></b> expirera dans {2} jours. Pensez à prolonger la validité du compte si besoin.
subject = Le compte invité de {0}  {1} expire bientôt
userEmailTitle = Email', 'accessToLinshareBTn = Expiration account
accountCreationDateTitle = Account creation date
accountExpiryDateTitle = Account expiry date
activationLinkTitle = Initialization link
mainMsg = The  <b> {0} <span style="text-transform:uppercase">{1}</span></b> guest account is about to expire in {2} days. If this account is still needed,  postpone its expiration date.
subject = {0}  {1} guest account will expire soon.
userEmailTitle = Email');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
	VALUES (26, 1, '', true, 26, '[( #{subject})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
          <span data-th-utext="#{endingMainMsg(${daysLeft},${shareRecipient.firstName},${shareRecipient.lastName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
             </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{shareRecipientTitle},#{name(${shareRecipient.firstName}, ${shareRecipient.lastName})})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{shareFileTitle},${share.name})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareCreationDateTitle},${share.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{shareExpiryDateTitle},${share.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '4375a5b6-c3ca-11e7-bd7c-47cacbfe09d9', now(), now(), true, 'accessToLinshareBTn = Votre partage expire bientôt
shareRecipientTitle =  Destinataire
shareFileTitle = Le fichier partagé
shareCreationDateTitle = Date de création
shareExpiryDateTitle = Date d\''''expiration
activationLinkTitle = Initialization link
beginningMainMsg = Le partage
endingMainMsg =  expire dans {0} jours sans avoir été téléchargé par <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Votre partage expire bientôt et n\''''a pas encore été téléchargé
name = {0} {1}
fileNameEndOfLine = {0}', 'accessToLinshareBTn = Your share will expire soon
shareRecipientTitle = Recipient
shareFileTitle = The shared file
shareCreationDateTitle = Creation date
shareExpiryDateTitle = Expiration date
activationLinkTitle = Initialization link
beginningMainMsg =  The fileshare
endingMainMsg =  will expire in {0} days and has not been downloaded by the recipient <b> {1} <span style="text-transform:uppercase">{2}</span></b>.
subject = Your share will expire soon and has not been downloaded
name = {0} {1}
fileNameEndOfLine = {0}');

INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (16, 0, 16, 1, 16, '9f017ae0-60e7-11e7-b430-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (116, 1, 16, 1, 16, '9f02736e-60e7-11e7-bf58-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (17, 0, 17, 1, 17, '9f04eafe-60e7-11e7-813f-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (117, 1, 17, 1, 17, '9f05d3ec-60e7-11e7-98a3-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (18, 0, 18, 1, 18, '9f07da3e-60e7-11e7-94a2-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (118, 1, 18, 1, 18, '9f08b468-60e7-11e7-87e7-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (19, 0, 19, 1, 19, '9f0b1a00-60e7-11e7-bac1-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (119, 1, 19, 1, 19, '9f0c0672-60e7-11e7-ba0a-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (20, 0, 20, 1, 20, '9f0e565c-60e7-11e7-b12b-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (120, 1, 20, 1, 20, '9f0f3ea0-60e7-11e7-a25e-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (21, 0, 21, 1, 21, '9f11f578-60e7-11e7-8f05-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (121, 1, 21, 1, 21, '9f12e0f0-60e7-11e7-8c20-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (22, 0, 22, 1, 22, '9f15538a-60e7-11e7-9782-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (122, 1, 22, 1, 22, '9f164a06-60e7-11e7-998e-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (23, 0, 23, 1, 23, '9f18c682-60e7-11e7-a184-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (123, 1, 23, 1, 23, '9f199652-60e7-11e7-a9cf-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (24, 0, 24, 1, 24, '9f1bae1a-60e7-11e7-9c81-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (124, 1, 24, 1, 24, '9f1c879a-60e7-11e7-95d8-0800271467bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (25, 0, 25, 1, 25, '82cde226-b968-11e7-8d63-83050cc4d746', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (125, 1, 25, 1, 25, '82ce572e-b968-11e7-9f2c-8b110ac99bc9', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (26, 0, 26, 1, 26, '4375f264-c3ca-11e7-a27a-bf234a0daed3', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (126, 1, 26, 1, 26, '4376471e-c3ca-11e7-96f0-df378884d9bd', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (27, 0, 27, 1, 27, '935a40fa-c53c-11e7-8fbc-ebfc048f79f6', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) 
	VALUES (127, 1, 27, 1, 27, '935a7b10-c53c-11e7-8ce9-17fe85e6b389', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (28, 0, 28, 1, 28, 'cd339002-c617-11e7-8d48-eb704ae08d79', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (128, 1, 28, 1, 28, 'cd33d42c-c617-11e7-979a-6bf962f5c6c8', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (29, 0, 29, 1, 29, 'a4ef9882-c619-11e7-94d7-239170350774', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (129, 1, 29, 1, 29, 'a4efd518-c619-11e7-8cdf-13a90ce64cda', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (30, 0, 30, 1, 30, '47409334-c61a-11e7-bfd9-fbd9e2c973bb', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (130, 1, 30, 1, 30, '4740d3f8-c61a-11e7-8d5a-3f431ce9643a', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (31, 0, 31, 1, 31, 'd5c520c4-d6b5-11e7-8fb4-eb93819bda25', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly)
	VALUES (131, 1, 31, 1, 31, 'd5c55f44-d6b5-11e7-b521-4f65da9d047d', true);

-- End mail layout

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

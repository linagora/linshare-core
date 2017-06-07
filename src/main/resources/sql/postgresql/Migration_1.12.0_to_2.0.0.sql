-- Postgresql migration script template

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'2.0.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '2.0.0';
	DECLARE version_from VARCHAR := '1.12.0';
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
	DECLARE user_connected VARCHAR = (SELECT usename FROM pg_stat_activity where datname = database);
	DECLARE error VARCHAR := ('You are actually connected with the user "postgres", you should be connected with your LinShare database user, we are about to stop the migration script.');
	BEGIN
		IF (user_connected = 'postgres') THEN
			RAISE WARNING '%', error;
		--	DIRTY: did it to stop the process cause there is no clean way to do it.
		--	Expected error: query has no destination for result data.
			SELECT '';
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

-- TODO:FMA
-- SELECT ls_check_user_connected();
SELECT ls_prechecks();

SET client_min_messages = warning;

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;
-- Here your queries



-- -- could be null, legacy documents haven't bucket.
-- ALTER TABLE document ADD COLUMN bucket_uuid CHARACTER VARYING(255);
--
-- -- domain migration from identifier to uuid
-- ALTER TABLE domain_abstract ADD COLUMN uuid CHARACTER VARYING(255);
-- UPDATE domain_abstract set uuid = identifier;
-- ALTER TABLE domain_abstract ALTER COLUMN uuid SET NOT NULL;
--
-- -- domain policy migration from identifier to uuid
-- ALTER TABLE domain_policy ADD COLUMN label CHARACTER VARYING(255);
-- UPDATE domain_policy SET label = identifier;
-- ALTER TABLE domain_policy ALTER COLUMN label SET NOT NULL;
-- ALTER TABLE domain_policy RENAME COLUMN identifier TO uuid;


TRUNCATE functionality_boolean CASCADE;
TRUNCATE functionality_unit CASCADE;
TRUNCATE functionality_string CASCADE;
-- from 1.12 scratch, do not exist !
-- TRUNCATE functionality_range_unit CASCADE;
TRUNCATE functionality_integer CASCADE;
TRUNCATE functionality_enum_lang CASCADE;
TRUNCATE mail_activation;
TRUNCATE mail_notification;

TRUNCATE functionality CASCADE;
TRUNCATE unit CASCADE;
TRUNCATE policy CASCADE;

UPDATE domain_abstract SET mailconfig_id = NULL;
UPDATE domain_abstract SET welcome_messages_id = NULL;


TRUNCATE mail_content CASCADE;
TRUNCATE mail_footer CASCADE;
DELETE FROM mail_config;
DELETE FROM mail_layout;

TRUNCATE welcome_messages_entry;
DELETE FROM welcome_messages;



ALTER TABLE welcome_messages_entry DROP COLUMN value;
ALTER TABLE welcome_messages_entry ADD COLUMN value text;

ALTER TABLE document ADD COLUMN bucket_uuid varchar(255);
ALTER TABLE document ADD COLUMN to_upgrade bool DEFAULT 'false' NOT NULL;
-- TODO : manage null rows
-- ALTER TABLE document_entry ALTER COLUMN sha256sum SET NOT NULL;

ALTER TABLE domain_abstract RENAME COLUMN identifier TO uuid;
ALTER TABLE domain_abstract DROP COLUMN used_space;

-- domain_policy
ALTER TABLE domain_policy ADD COLUMN uuid varchar(255) UNIQUE;
ALTER table domain_policy RENAME COLUMN identifier TO label;
ALTER TABLE domain_policy ALTER COLUMN label SET NOT NULL;
UPDATE domain_policy SET uuid = CONCAT(label, '-', id);
ALTER TABLE domain_policy ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE domain_policy ADD UNIQUE (uuid);

-- fix : NOT NULL constraint was probably forgotten during previous upgrade process.
ALTER TABLE functionality ALTER COLUMN policy_activation_id SET NOT NULL;
-- fix : NOT NULL constraint was probably forgotten during previous upgrade process.
ALTER TABLE functionality ALTER COLUMN policy_configuration_id SET NOT NULL;

ALTER TABLE thread ADD COLUMN to_upgrade bool DEFAULT 'false' NOT NULL;

-- sha256sum upgrade
UPDATE document SET sha256sum = 'UNDEFINED' WHERE sha256sum IS NULL;
UPDATE document set to_upgrade = true;
ALTER TABLE document ALTER COLUMN sha256sum SET NOT NULL;
ALTER TABLE document ALTER COLUMN sha256sum SET DEFAULT 'UNDEFINED';
UPDATE document_entry SET sha256sum = 'UNDEFINED' WHERE sha256sum IS NULL;
ALTER TABLE document_entry ALTER COLUMN sha256sum SET NOT NULL;
ALTER TABLE document_entry ALTER COLUMN sha256sum SET DEFAULT 'UNDEFINED';


UPDATE thread set to_upgrade = true;
-- ALTER TABLE thread_entry ALTER COLUMN sha256sum SET NOT NULL;

ALTER TABLE users ALTER COLUMN inconsistent SET default 'False';

-- mail_config
ALTER TABLE mail_config ADD COLUMN mail_layout_id int8 NOT NULL;
ALTER TABLE mail_config ADD COLUMN readonly bool DEFAULT 'false' NOT NULL;
ALTER TABLE mail_config DROP COLUMN mail_layout_html_id;
ALTER TABLE mail_config DROP COLUMN mail_layout_text_id;
ALTER TABLE mail_config DROP COLUMN name;
ALTER TABLE mail_config ADD COLUMN name text;
-- ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs160138 FOREIGN KEY (mailconfig_id) REFERENCES mail_config (id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi688067 FOREIGN KEY (mail_layout_id) REFERENCES mail_layout (id);

-- mail_layout
ALTER TABLE mail_layout DROP COLUMN name;
ALTER TABLE mail_layout DROP COLUMN plaintext;
ALTER TABLE mail_layout ADD COLUMN description text;
ALTER TABLE mail_layout ADD COLUMN readonly bool DEFAULT 'false' NOT NULL;
ALTER TABLE mail_layout ADD COLUMN messages_french text;
ALTER TABLE mail_layout ADD COLUMN messages_english text;

-- mail_footer
ALTER TABLE mail_footer DROP COLUMN name;
ALTER TABLE mail_footer DROP COLUMN language;
ALTER TABLE mail_footer DROP COLUMN plaintext;
ALTER TABLE mail_footer ADD COLUMN description text;
ALTER TABLE mail_footer ADD COLUMN readonly bool DEFAULT 'false' NOT NULL;
ALTER TABLE mail_footer ADD COLUMN messages_french text;
ALTER TABLE mail_footer ADD COLUMN messages_english text;
ALTER TABLE mail_footer_lang ADD COLUMN readonly bool DEFAULT 'false' NOT NULL;
-- ALTER TABLE mail_footer_lang ADD CONSTRAINT FKmail_foote801249 FOREIGN KEY (mail_footer_id) REFERENCES mail_footer (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT mailconfig_mailfooterlang FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);

-- mail_content
ALTER TABLE mail_content DROP COLUMN name;
ALTER TABLE mail_content DROP COLUMN plaintext;
ALTER TABLE mail_content DROP COLUMN language;
ALTER TABLE mail_content DROP COLUMN greetings;
ALTER TABLE mail_content ADD COLUMN description text;
ALTER TABLE mail_content ADD COLUMN readonly bool DEFAULT 'false' NOT NULL;
ALTER TABLE mail_content ADD COLUMN messages_french text;
ALTER TABLE mail_content ADD COLUMN messages_english text;
ALTER TABLE mail_content_lang ADD COLUMN readonly bool DEFAULT 'false' NOT NULL;
-- ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte33952 FOREIGN KEY (mail_content_id) REFERENCES mail_content (id);
-- ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte910199 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);

-- upload_request
ALTER TABLE upload_request ALTER COLUMN notification_date drop NOT NULL;
ALTER TABLE upload_request ALTER COLUMN notified SET default 'FALSE';
ALTER TABLE upload_request_history ALTER COLUMN expiry_date drop NOT NULL;
ALTER TABLE upload_request_history ALTER COLUMN notification_date drop NOT NULL;

--
ALTER TABLE mailing_list_contact ALTER COLUMN mailing_list_contact_index SET NOT NULL;
ALTER TABLE functionality_enum_lang ALTER COLUMN lang_value SET NOT NULL;
ALTER TABLE async_task ADD COLUMN upgrade_task_id int8;
CREATE TABLE quota (
  id                              BIGSERIAL NOT NULL,
  uuid                           varchar(255) NOT NULL UNIQUE,
  quota_type                     varchar(255) NOT NULL,
  maintenance                    bool DEFAULT 'false' NOT NULL,
  quota                          int8 NOT NULL,
  quota_override                 bool,
  default_quota                  int8,
  default_quota_override         bool,
  quota_warning                  int8 NOT NULL,
  current_value                  int8 NOT NULL,
  last_value                     int8 NOT NULL,
  current_value_for_subdomains   int8,
  max_file_size                  int8,
  max_file_size_override         bool,
  default_max_file_size          int8,
  default_max_file_size_override bool,
  default_account_quota          int8,
  default_account_quota_override bool,
  account_quota                  int8,
  account_quota_override         bool,
  creation_date                  timestamp(6) NOT NULL,
  modification_date              timestamp(6) NOT NULL,
  batch_modification_date        timestamp(6),
  container_type                 varchar(255),
  shared                         bool,
  account_id                     int8,
  domain_id                      int8 NOT NULL,
  domain_parent_id               int8,
  quota_domain_id                int8,
  quota_container_id             int8,
  PRIMARY KEY (id));
ALTER TABLE quota ADD CONSTRAINT domain FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE quota ADD CONSTRAINT parentDomain FOREIGN KEY (domain_parent_id) REFERENCES domain_abstract (id);
ALTER TABLE quota ADD CONSTRAINT account FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE quota ADD CONSTRAINT FKquota572570 FOREIGN KEY (quota_domain_id) REFERENCES quota (id);
ALTER TABLE quota ADD CONSTRAINT FKquota430815 FOREIGN KEY (quota_container_id) REFERENCES quota (id);

CREATE TABLE operation_history (
  id               BIGSERIAL NOT NULL,
  uuid            varchar(255) NOT NULL,
  operation_value int8 NOT NULL,
  operation_type  int4 NOT NULL,
  container_type  varchar(255) NOT NULL,
  creation_date   timestamp(6) NOT NULL,
  domain_id       int8 NOT NULL,
  account_id      int8 NOT NULL,
  PRIMARY KEY (id));
ALTER TABLE operation_history ADD CONSTRAINT FKoperation_38651 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE operation_history ADD CONSTRAINT FKoperation_531280 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);

CREATE TABLE statistic (
  id                      BIGSERIAL NOT NULL,
  statistic_type         varchar(255) NOT NULL,
  creation_date          timestamp(6) NOT NULL,
  statistic_date         timestamp(6) NOT NULL,
  operation_count        int8 NOT NULL,
  delete_operation_count int8 NOT NULL,
  create_operation_count int8 NOT NULL,
  create_operation_sum   int8 NOT NULL,
  delete_operation_sum   int8 NOT NULL,
  diff_operation_sum     int8 NOT NULL,
  actual_operation_sum   int8 NOT NULL,
  account_id             int8,
  domain_id              int8 NOT NULL,
  domain_parent_id       int8 NOT NULL,
  PRIMARY KEY (id));
ALTER TABLE statistic ADD CONSTRAINT FKstatistic57774 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE statistic ADD CONSTRAINT FKstatistic343885 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE statistic ADD CONSTRAINT FKstatistic161509 FOREIGN KEY (domain_parent_id) REFERENCES domain_abstract (id);

CREATE TABLE batch_history (
  id                BIGSERIAL NOT NULL,
  uuid             varchar(255) NOT NULL UNIQUE,
  status           varchar(255) NOT NULL,
  batch_type       varchar(255) NOT NULL,
  execution_date   timestamp(6) NOT NULL,
  active_date      timestamp(6) NOT NULL,
  errors           int8 NOT NULL,
  unhandled_errors int8 NOT NULL,
  once             bool DEFAULT 'false' NOT NULL,
  extras           text,
  PRIMARY KEY (id));

CREATE TABLE upgrade_task (
  id                 BIGSERIAL NOT NULL,
  uuid              varchar(255) NOT NULL,
  identifier        varchar(255) NOT NULL,
  task_group        varchar(255) NOT NULL,
  parent_uuid       varchar(255),
  parent_identifier varchar(255),
  task_order        int4 NOT NULL,
  status            varchar(255) NOT NULL,
  priority            varchar(255) NOT NULL,
  creation_date     date NOT NULL,
  modification_date date NOT NULL,
  extras            text,
  async_task_uuid   varchar(255),
  PRIMARY KEY (id));

ALTER TABLE upload_request_entry ADD CONSTRAINT upload_request_entry_fk_url FOREIGN KEY (upload_request_url_id) REFERENCES upload_request_url (id);
-- ALTER TABLE functionality ADD CONSTRAINT FKfunctional788903 FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKMailingListContact FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);
-- ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif791766 FOREIGN KEY (configuration_policy_id) REFERENCES policy (id);
ALTER TABLE welcome_messages_entry ADD CONSTRAINT welcome_messages_entry_fk_welcome_message FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE async_task ADD CONSTRAINT FKasync_task970702 FOREIGN KEY (upgrade_task_id) REFERENCES upgrade_task (id);





-- root domain quota
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	current_value, last_value, domain_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    quota_type, current_value_for_subdomains)
VALUES (1, '2a01ac66-a279-11e5-9086-5404a683a462', NOW(), NOW(), null,
	0, 0, 1,
	10000000000000, null,
	10000000000000,
    1000000000000, true,
    'DOMAIN_QUOTA', 0);
-- quota : 10 To
-- quota_warning : 10000000000000 : 10 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)


-- 'CONTAINER_QUOTA', 'USER' for root domain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value, domain_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (11, '26323798-a1a8-11e6-ad47-0800271467bb', NOW(), NOW(), null,
	1, 0, 0, 1,
	400000000000, null,
    400000000000,
    400000000000, false,
    10000000000, null,
    100000000000, null,
    100000000000, null,
    100000000000, null,
    'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go : default value for container created inside a container of a top domain
-- max_file_size : 100000000000  : 100 Go
-- account_quota : 100000000000 : 100 Go : value for account created inside container the root domain

-- 'CONTAINER_QUOTA', 'WORK_GROUP' for root domain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value, domain_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (12, '63de4f14-a1a8-11e6-a369-0800271467bb', NOW(), NOW(), null,
	1, 0, 0, 1,
	400000000000, null,
    400000000000,
    400000000000, false,
    10000000000, null,
    400000000000, null,
    10000000000, null,
    400000000000, null,
    'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup


--Welcome messages
INSERT INTO welcome_messages(id, uuid, name, description, creation_date, modification_date, domain_id) VALUES (1, '4bc57114-c8c9-11e4-a859-37b5db95d856', 'WelcomeName', 'a Welcome description', now(), now(), 1);

--Melcome messages Entry
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (1, 'en', '<h2>Welcome to LinShare</h2><p>Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.</p>', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (2, 'fr', '<h2>Bienvenue dans LinShare</h2><p>Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.</p>', 1);
-- INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (3, 'mq', 'Bienvini an lè Linshare, an solusyon lib de partaj de fichié sékirisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (4, 'vi', '<h2>Chào mừng bạn đến với Linshare</h2><p>Chào mừng bạn đến với Linshare, phần mềm nguồn mở chia sẻ file bảo mật.</p>', 1);
-- INSERT INTO welcome_messages_entry(id, lang, value, welcome_messages_id) VALUES (5, 'nl', 'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 1);

-- Default setting welcome messages for all domains
UPDATE domain_abstract SET welcome_messages_id = 1;


-- root user ACCOUNT QUOTA
INSERT INTO quota(
    id, uuid, creation_date, modification_date, batch_modification_date,
    quota_container_id, current_value, last_value,
    domain_id, account_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    max_file_size, max_file_size_override,
    shared, quota_type)
VALUES (
    13, '815e1d22-49e0-4817-ac01-e7eefbee56ba', NOW(), NOW(), null,
    11, 0, 0,
    1, 1, null,
    100000000000, true,
    100000000000,
    100000000000, true,
    100000000000, true,
    false, 'ACCOUNT_QUOTA');


-- Functionality : MIME_TYPE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (7, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (8, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (4, true, 'MIME_TYPE', 7, 8, 1);

-- Functionality : TIME_STAMPING
INSERT INTO policy(id, status, default_status, policy, system) VALUES (13, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (14, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (7, false, 'TIME_STAMPING', 13, 14, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (7, 'http://localhost:8080/signserver/tsa?signerId=1');


-- Functionality : ANTIVIRUS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (15, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (16, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (8, true, 'ANTIVIRUS', 15, 16, 1);

-- Functionality : GUESTS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (27, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (28, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (14, true, 'GUESTS', 27, 28, 1);

-- Functionality : GUESTS__EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (19, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (20, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (111, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES (10, false, 'GUESTS__EXPIRATION', 19, 20, 111, 1, 'GUESTS', true);
INSERT INTO unit(id, unit_type, unit_value) VALUES (4, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (10, 3, 4);

-- Functionality : GUESTS__RESTRICTED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (47, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (48, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (112, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES (24, false, 'GUESTS__RESTRICTED', 47, 48, 112, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (24, true);

-- Functionality : GUESTS__CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (113, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (114, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (115, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES (48, false, 'GUESTS__CAN_UPLOAD', 113, 114, 115, 1, 'GUESTS', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (48, true);

-- Functionality : DOCUMENT_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (21, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (22, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (11, false, 'DOCUMENT_EXPIRATION', 21, 22, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (5, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (11, 3, 5);


-- Functionality : SHARE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (23, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (24, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (122, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES (12, false, 'SHARE_EXPIRATION', 23, 24, 122, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (6, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (12, 3, 6);

-- Functionality : SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (120, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (121, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (50, false, 'SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION', 120, 121, 1, 'SHARE_EXPIRATION', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (50, false);

-- Functionality : ANONYMOUS_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (25, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (26, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (116, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES (13, false, 'ANONYMOUS_URL', 25, 26, 116, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (13, true);


-- Functionality : INTERNAL_CAN_UPLOAD formerly known as USER_CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (29, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (30, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (15, true, 'INTERNAL_CAN_UPLOAD', 29, 30, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (15, true);


-- Functionality : COMPLETION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (31, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (32, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (16, false, 'COMPLETION', 31, 32, 1);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (16, 3);

-- Functionality : SHARE_NOTIFICATION_BEFORE_EXPIRATION
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)
INSERT INTO policy(id, status, default_status, policy, system) VALUES (43, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (44, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (22, false, 'SHARE_NOTIFICATION_BEFORE_EXPIRATION', 43, 44, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (22, '2,7');

-- Functionality : WORK_GROUP
INSERT INTO policy(id, status, default_status, policy, system) VALUES (45, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (46, false, false, 1, true);
-- if a functionality is system, you will not be able see/modify its parameters
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (23, true, 'WORK_GROUP', 45, 46, 1);

-- Functionality : WORK_GROUP__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (57, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (58, false, false, 1, false);
-- INSERT INTO policy(id, status, default_status, policy, system) VALUES (117, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (28, false, 'WORK_GROUP__CREATION_RIGHT', 57, 58, 1, 'WORK_GROUP', true);
-- INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (28, true);

-- Functionality : CONTACTS_LIST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (53, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (54, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (26, true, 'CONTACTS_LIST', 53, 54, 1);

--Functionality : CONTACTS_LIST__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (55, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (56, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES(27, false, 'CONTACTS_LIST__CREATION_RIGHT', 55, 56, null, 1, 'CONTACTS_LIST', true);


-- Functionality : DOMAIN
INSERT INTO policy(id, status, default_status, policy, system) VALUES (118, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (119, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(49, false, 'DOMAIN', 118, 119, 1);

-- Functionality : DOMAIN__NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (61, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (62, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES(30, false, 'DOMAIN__NOTIFICATION_URL', 61, 62, 1, 'DOMAIN', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (30, 'http://linshare-ui-user.local/');

-- Functionality : DOMAIN__MAIL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (49, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (50, false, false, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES (25, false, 'DOMAIN__MAIL', 49, 50, 1, 'DOMAIN', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (25, 'linshare-noreply@linagora.com');


-- Functionality : UPLOAD_REQUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (63, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (64, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(31, false, 'UPLOAD_REQUEST', 63, 64, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (31, 'http://linshare-upload-request.local');

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (65, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (66, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (67, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(32, false, 'UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION', 65, 66, 67, 1, 'UPLOAD_REQUEST', true);
INSERT INTO unit(id, unit_type, unit_value) VALUES (7, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (32, 0, 7);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (68, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (69, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (70, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(33, false, 'UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION', 68, 69, 70, 1, 'UPLOAD_REQUEST', true);
-- time unit : month
 INSERT INTO unit(id, unit_type, unit_value) VALUES (8, 0, 2);
-- month : 1 month
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (33, 1, 8);

-- Functionality : UPLOAD_REQUEST__GROUPED_MODE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (71, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (72, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (73, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(34, false, 'UPLOAD_REQUEST__GROUPED_MODE', 71, 72, 73, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (34, false);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_COUNT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (74, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (75, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (76, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(35, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_COUNT', 74, 75, 76, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (35, 3);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_FILE_SIZE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (77, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (78, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (79, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(36, false, 'UPLOAD_REQUEST__MAXIMUM_FILE_SIZE', 77, 78, 79, 1, 'UPLOAD_REQUEST', true);
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value) VALUES (9, 1, 1);
-- size : 10 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (36, 10, 9);

-- Functionality : UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (80, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (81, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (82, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(37, false, 'UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE', 80, 81, 82, 1, 'UPLOAD_REQUEST', true);
 -- file size unit : Mega
INSERT INTO unit(id, unit_type, unit_value) VALUES (10, 1, 1);
-- size : 30 Mega
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (37, 30, 10);

-- Functionality : UPLOAD_REQUEST__NOTIFICATION_LANGUAGE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (83, true, true, 1, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (84, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (85, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(38, false, 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE', 83, 84, 85, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_enum_lang(functionality_id, lang_value) VALUES (38, 'en');

-- Functionality : UPLOAD_REQUEST__SECURED_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (86, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (87, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (88, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(39, false, 'UPLOAD_REQUEST__SECURED_URL', 86, 87, 88, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (39, false);

-- Functionality : UPLOAD_REQUEST__PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (89, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (90, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (91, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(40, false, 'UPLOAD_REQUEST__PROLONGATION', 89, 90, 91, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (40, false);

-- Functionality : UPLOAD_REQUEST__CAN_DELETE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (92, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (93, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (94, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(41, false, 'UPLOAD_REQUEST__CAN_DELETE', 92, 93, 94, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (41, true);

-- Functionality : UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (95, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (96, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (97, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(42, false, 'UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION', 95, 96, 97, 1, 'UPLOAD_REQUEST', true);
-- time unit : day
INSERT INTO unit(id, unit_type, unit_value) VALUES (11, 0, 0);
-- time : 7 days
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (42, 7, 11);

-- Functionality : UPLOAD_REQUEST__CAN_CLOSE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (98, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (99, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (100, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(43, false, 'UPLOAD_REQUEST__CAN_CLOSE', 98, 99, 100, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (43, true);

 -- Functionality : UPLOAD_PROPOSITION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (101, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (102, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id)
 VALUES(44, false, 'UPLOAD_PROPOSITION', 101, 102, 1);

-- Functionality : GUEST__EXPIRATION_ALLOW_PROLONGATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (123, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (124, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param) VALUES(51, false, 'GUESTS__EXPIRATION_ALLOW_PROLONGATION', 123, 124, null, 1, 'GUESTS', true);

-- Functionality : UPLOAD_REQUEST_ENABLE_TEMPLATE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (129, false, false, 2, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (130, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param)
 VALUES(53, false, 'UPLOAD_REQUEST_ENABLE_TEMPLATE', 129, 130, 1, false);

-- Functionality : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (126, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (127, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (128, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) VALUES(52, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 126, 127, 128, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (52, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (131, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (132, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (133, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id)
 VALUES(54, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', 131, 132, 133, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (54, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (134, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (135, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (136, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(55, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION', 134, 135, 136, 1, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', true);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (55, 3);

-- Functionality : ANONYMOUS_URL__NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (224, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (225, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (226, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(56, false, 'ANONYMOUS_URL__NOTIFICATION', 224, 225, 226, 1, 'ANONYMOUS_URL', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (56, true);

-- Functionality : ANONYMOUS_URL__NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (228, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (229, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (230, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 	VALUES(57, false, 'ANONYMOUS_URL__NOTIFICATION_URL', 228, 229, 230, 1, 'ANONYMOUS_URL', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (57, 'http://linshare-ui-user.local/');

-- Functionality : END


-- MailActivation : BEGIN

-- MailActivation : FILE_WARN_OWNER_BEFORE_FILE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (137, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (138, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (139, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(1, false, 'FILE_WARN_OWNER_BEFORE_FILE_EXPIRY', 137, 138, 139, 1, true);

-- MailActivation : SHARE_NEW_SHARE_FOR_RECIPIENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (140, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (141, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (142, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(2, false, 'SHARE_NEW_SHARE_FOR_RECIPIENT', 140, 141, 142, 1, true);

-- MailActivation : SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (143, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (144, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (145, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(3, false, 'SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER', 143, 144, 145, 1, true);

-- MailActivation : SHARE_FILE_DOWNLOAD_ANONYMOUS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (146, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (147, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (148, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(4, false, 'SHARE_FILE_DOWNLOAD_ANONYMOUS', 146, 147, 148, 1, true);

-- MailActivation : SHARE_FILE_DOWNLOAD_USERS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (149, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (150, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (151, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(5, false, 'SHARE_FILE_DOWNLOAD_USERS', 149, 150, 151, 1, true);

-- MailActivation : SHARE_FILE_SHARE_DELETED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (152, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (153, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (154, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(6, false, 'SHARE_FILE_SHARE_DELETED', 152, 153, 154, 1, true);

-- MailActivation : SHARE_WARN_RECIPIENT_BEFORE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (155, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (156, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (157, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(7, false, 'SHARE_WARN_RECIPIENT_BEFORE_EXPIRY', 155, 156, 157, 1, true);

-- MailActivation : SHARE_WARN_UNDOWNLOADED_FILESHARES
INSERT INTO policy(id, status, default_status, policy, system) VALUES (158, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (159, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (160, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(8, false, 'SHARE_WARN_UNDOWNLOADED_FILESHARES', 158, 159, 160, 1, true);

-- MailActivation : GUEST_ACCOUNT_NEW_CREATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (161, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (162, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (163, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(9, false, 'GUEST_ACCOUNT_NEW_CREATION', 161, 162, 163, 1, true);

-- MailActivation : GUEST_ACCOUNT_RESET_PASSWORD_LINK
INSERT INTO policy(id, status, default_status, policy, system) VALUES (164, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (165, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (166, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(10, false, 'GUEST_ACCOUNT_RESET_PASSWORD_LINK', 164, 165, 166, 1, true);

-- MailActivation : UPLOAD_REQUEST_UPLOADED_FILE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (167, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (168, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (169, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(11, false, 'UPLOAD_REQUEST_UPLOADED_FILE', 167, 168, 169, 1, true);

-- MailActivation : UPLOAD_REQUEST_UNAVAILABLE_SPACE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (170, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (171, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (172, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(12, false, 'UPLOAD_REQUEST_UNAVAILABLE_SPACE', 170, 171, 172, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_BEFORE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (173, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (174, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (175, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(13, false, 'UPLOAD_REQUEST_WARN_BEFORE_EXPIRY', 173, 174, 175, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (176, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (177, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (178, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(14, false, 'UPLOAD_REQUEST_WARN_EXPIRY', 176, 177, 178, 1, true);

-- MailActivation : UPLOAD_REQUEST_CLOSED_BY_RECIPIENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (179, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (180, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (181, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(15, false, 'UPLOAD_REQUEST_CLOSED_BY_RECIPIENT', 179, 180, 181, 1, true);

-- MailActivation : UPLOAD_REQUEST_FILE_DELETED_BY_RECIPIENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (182, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (183, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (184, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(16, false, 'UPLOAD_REQUEST_FILE_DELETED_BY_RECIPIENT', 182, 183, 184, 1, true);
-- MailActivation : END


-- ###BEGIN-PART-2###
INSERT INTO mail_layout (id, domain_abstract_id, description, visible, layout, creation_date, modification_date, uuid, readonly, messages_french, messages_english) VALUES (1, 1, 'Default HTML layout', true, '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!--/* Beginning of common base layout template*/-->
<div data-th-fragment="email_base(upperMainContentArea,bottomSecondaryContentArea)">
  <div style="width:100%!important;margin:0;padding:0;background-color:#ffffff;font-family:''Open Sans'',arial,Helveticasans-serif;">
    <center>
      <table bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" height="100% !important"
             style="height:100%!important;margin:0;padding:0;background-color:#ffffff;width:90%;max-width:450px"
             width="90%">
        <tbody>
        <tr>
          <td align="center" style="border-collapse:collapse" valign="top">
            <table border="0" cellpadding="0" cellspacing="0" style="border:0px;width:90%;max-width:500px" width="90%">
              <tbody>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table bgcolor="transparent" border="0" cellpadding="0" cellspacing="0"
                         style="background-color:transparent;border-bottom:0;padding:0px">
                    <tbody>
                    <tr>
                      <td align="center" bgcolor="#ffffff"
                          style="border-collapse:collapse;color:#202020;background-color:#ffffff;font-size:34px;font-weight:bold;line-height:100%;padding:0;text-align:center;vertical-align:middle">
                        <div align="center" style="text-align:center">
                          <a target="_blank"
                             style="border:0;line-height:100%;outline:none;text-decoration:none;width:233px;height:57px;padding:20px 0 20px 0"
                             data-th-href="@{${linshareURL}}">
                            <img
                              data-th-src="#{logoLinShareBase64}"
                              style="display:inline-block;margin-bottom:20px;margin-top:20px" width="233" alt="Logo"
                              height="57"/>
                          </a>
                        </div>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table border="0" cellpadding="0" cellspacing="0" style="width:95%;max-width:500px" width="95%">
                    <tbody>
                    <tr>
                      <td
                        style="border-collapse:collapse;border-radius:3px;font-weight:300;border:1px solid #e1e1e1;background:white;border-top:none;"
                        valign="top">
                        <table border="0" cellpadding="20" cellspacing="0" width="100%">
                          <tbody>
                          <tr>
                            <td style="border-collapse:collapse;padding:0px" valign="top">
                              <div align="left"
                                   style="color:#505050;font-size:14px;line-height:150%;text-align:left">
                                <th:block data-th-replace="${upperMainContentArea}"/>
                              </div>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%"
                                     style="background-color: #f8f8f8;">
                                <tbody>
                                <tr>
                                  <td width="15" style="border-top:1px solid #c9cacc;">
                                  </td>
                                  <td width="20"><img
                                    src="data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAABQAAAAJCAYAAAAywQxIAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAsSAAALEgHS3X78AAABWklEQVQoz52Q3UvCYBSHz0ZO3LtNL5KsiJTMfTSjoKsglKD+6IIypKDwJvY9E6KwArtQt72bzbmuLCvt69ye8zznx4+o189Pswu56vpaoUuSBPxnRnEMdrOVcR2nRlxeNZaBIE5omi5JAg9/lb6EIdh2Cxynb9M0vU8AACiqnnEc55hLp3ckkYffKsNhBLpuAsZeI5VKHWxvbXbf2GtFm8cY19McJ/B8CX4KGg0j0AwT/CAwEUJ75Q3xGQA+hlENK+v0+zWOS0sCXwSSmG4NwyEYhgX+INAZlq3KIt8Z78jJQ1nkOyzLVnq9rmJZzenJohEYpgU48BWGYSqTsi8Jx6NoxqLnuhcIobwk8kCS739VzQDP824Ry+yWJfHxMzuzKVU3lzzXPUsmk8VCfhXiOIa7dht87N8ghCqyJDxM476tXjWs3CAIjnyMVyiKgrlE4p6iqENZEp5mMa+E9JG/m4wFNQAAAABJRU5ErkJggg=="
                                    width="20" height="9" border="0" style="display:block;" alt="down arrow"/></td>
                                  <td style="border-top:1px solid #c9cacc;"></td>
                                </tr>
                                </tbody>
                              </table>
                              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                <tbody>
                                <tr>
                                  <td>
                                    <div align="left"
                                         style="font-size:14px;padding: 0px 17px;background: #f8f8f8;text-align:left;color:#7f7f7f;line-height:20px;">
                                      <div align="left"
                                           style="font-size:13px;line-height:20px;margin:0;padding: 15px 0 20px;">
                                        <th:block data-th-replace="${bottomSecondaryContentArea}"/>
                                      </div>
                                    </div>
                                  </td>
                                </tr>
                                </tbody>
                              </table>
                              <table width="100%"
                                     style="background:#f0f0f0;text-align:left;color:#a9a9a9;line-height:20px;border-top:1px solid #e1e1e1">
                                <tbody>
                                <tr data-th-insert="footer :: email_footer">
                                </tr>
                                </tbody>
                              </table>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              <tr>
                <td align="center" style="border-collapse:collapse" valign="top">
                  <table bgcolor="white" border="0" cellpadding="10" cellspacing="0"
                         style="background-color:white;border-top:0" width="400">
                    <tbody>
                    <tr>
                      <td style="border-collapse:collapse" valign="top">
                        <table border="0" cellpadding="10" cellspacing="0" width="100%">
                          <tbody>
                          <tr>
                            <td bgcolor="#ffffff" colspan="2"
                                style="border-collapse:collapse;background-color:#ffffff;border:0;padding: 0 8px;"
                                valign="middle">
                              <div align="center"
                                   style="color:#707070;font-size:12px;line-height:125%;text-align:center">
                                <!--/* Do not remove the copyright  ! */-->
                                <div data-th-insert="copyright :: copyright">
                                  <p
                                    style="line-height:15px;font-weight:300;margin-bottom:0;color:#b2b2b2;font-size:10px;margin-top:0">
                                    You are using the Open Source and free version of
                                    <a href="http://www.linshare.org/"
                                       style="text-decoration:none;color:#b2b2b2;"><strong>LinShare</strong>™</a>,
                                    powered by <a href="http://www.linshare.org/"
                                                  style="text-decoration:none;color:#b2b2b2;"><strong>Linagora</strong></a>
                                    ©&nbsp;2009–2017. Contribute to
                                    Linshare R&amp;D by subscribing to an Enterprise offer.
                                  </p>
                                </div>
                              </div>
                            </td>
                          </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              </tbody>
            </table>
          </td>
        </tr>
        </tbody>
      </table>
    </center>
  </div>
</div>
<!--/* End of common base layout template*/-->
 </body>
 </html>
<!--/* Common header template */-->
<head  data-th-fragment="header">
  <title data-th-text="${mailSubject}">Mail subject</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<!--/* Common greeting  template */-->
<div data-th-fragment="greetings(currentFirstName)">
  <p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px"
 data-th-text="#{welcomeMessage(${currentFirstName})}">
Hello Amy,</p>
</div>
<!--/* Common upper email section  template */-->
<div data-th-fragment="contentUpperSection(sectionContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;">
   <div align="left" style="padding:24px 17px 5px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;
border-top: 1px solid #e1e1e1;">
      <th:block th:replace="${sectionContent}" />
       </div>
</div>
<!--/* Common message section template */-->
<div data-th-fragment="contentMessageSection(messageTitle,messageContent)" style="margin-bottom:17px;border-top: 1px solid #e1e1e1;" >
          <div align="left" style="padding:24px 17px 15px;line-height: 21px;margin:0px;text-align:left;font-size: 13px;">
<p style="color:#505050;margin-top:0;font-weight:300;margin-bottom:10px">
<th:block th:replace="${messageTitle}" />
</p>
<p style="margin:0;color: #88a3b1;">
<th:block th:replace="${messageContent}" />
</p>
</div>
</div>
<!--/* Common link style */-->
<div data-th-fragment="infoActionLink(titleInfo,urlLink)"  style="margin-bottom:17px;" >
<span style="font-weight:bold;" data-th-text="${titleInfo}" >Download link title  </span>
  <br/>
<a target="_blank" style="color:#1294dc;text-decoration:none;"
                          data-th-text="${urlLink}"  th:href="@{${urlLink}}"   >Link </a>
</div>
<!--/* Common date display  style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoDateArea(titleInfo,contentInfo)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
    <br/>
 <span  th:with="df=#{date.format}" data-th-text="${#dates.format(contentInfo,df)}">7th of November, 2017</span>
</div>
<!--/* Common lower info title style */-->
<div style="margin-bottom:17px;" data-th-fragment="infoStandardArea(titleInfo,contentInfo)">
     <span style="font-weight:bold;" ><th:block th:replace="${titleInfo}" /> </span>
    <br/>
      <th:block th:replace="${contentInfo}" />
</div>
<!--/* Common button action style */-->
<span   data-th-fragment="actionButtonLink(labelBtn,urlLink)">
<a
style="border-radius:3px;font-size:15px;color:white;text-decoration:none;padding: 10px 7px;width:auto;max-width:50%;display:block;background-color: #42abe0;text-align: center;margin-top: 17px;"  target="_blank"
data-th-text="${labelBtn}"  th:href="@{${urlLink}}">Button label</a>
</span>
<!--/* Common recipient listing for external and internal users */-->
<div  style="margin-bottom:17px;" data-th-fragment="infoRecipientListingArea(titleInfo,arrayRecipients)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Recipients</span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="recipientData: ${arrayRecipients}">
<div data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
         <span style="color:#787878;font-size:13px"  data-th-utext="${recipientData.mail}">
        my-file-name.pdf
         </span>
</div>
<div data-th-if="(${!#strings.isEmpty(recipientData.lastName)})">
         <span  style="color:#787878;font-size:13px">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
</div>
      </li>
   </ul>
</div>
<div data-th-if="(${!isAnonymous})">
         <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share   */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileLinksListingArea(titleInfo,arrayFileLinks,isAnonymous)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
<div data-th-if="(${!isAnonymous})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}" th:href="@{${shareLink.href}}">
        my-file-name.pdf
         </a>
</div>
<div data-th-if="(${isAnonymous})">
         <span style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
</div>
</div>
<!--/* Lists all file links in a share  and checks witch one are the recpient\s */-->
<div   style="margin-bottom:17px;" data-th-fragment="infoFileListWithMyUploadRefs(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
      <li style="color:#787878;font-size:10px" th:each="shareLink : ${arrayFileLinks}">
         <a style="color:#787878;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
<th:block  data-th-if="(${shareLink.mine})"> <span  data-th-text="#{common.byYou}">|  By You</span></th:block >
      </li>
   </ul>
</div>
<!--/* Lists all file links in a share along with their download status   */-->
<div  data-th-fragment="infoFileListUploadState(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;">
<li style="color:#00b800;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${shareLink.downloaded})">
 <th:block data-th-if="(${shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px ;font-weight:bold"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
 <th:block data-th-if="(${!shareLink.isDownloading})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
  </th:block>
      </li>
<li style="color:#787878;font-size:15px" th:each="shareLink : ${arrayFileLinks}" data-th-if="(${!shareLink.downloaded})">
         <a style="color:#1294dc;text-decoration:none;font-size:13px"  data-th-utext="${shareLink.name}">
        my-file-name.pdf
         </a>
      </li>
   </ul>
</div>
<!--/* Lists all recpients download states per file   */-->
<div   style="margin-bottom:17px;"  data-th-fragment="infoFileListRecipientUpload(titleInfo,arrayFileLinks)">
     <span style="font-weight:bold;" data-th-text="${titleInfo}" >Shared the </span>
   <ul style="padding: 5px 0px; margin: 0;list-style-type:none;">
<li style="color:#787878;font-size:10px;margin-top:10px;"  th:each="shareLink : ${arrayFileLinks}" >
    <span style="border-bottom: 1px solid #e3e3e3;display: inline-block;width: 100%;margin-bottom: 3px;">
  <a target="_blank" style="color:#1294dc;text-decoration:none;font-size:13px" th:href="@{${shareLink.href}}">
    <span align="left" style="display: inline-block; width: 96%;"  data-th-utext="${shareLink.name}">
test-file.jpg</span></a>
    <span data-th-if="(${!shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #787878;"></span>
    <span data-th-if="(${shareLink.allDownloaded})" align="right" style="text-align: right; display: inline-block;height: 0;width: 6px;height: 6px;border-radius: 50%;background-color: #00b800;"></span>
    </span>
    <ul style="padding: 5px 17px; margin: 0;list-style-type:disc;" >
 <th:block  th:each="recipientData: ${shareLink.shares}">
   <th:block data-th-if="(${!recipientData.downloaded})" >
      <li style="color:#787878;font-size:15px"  >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
      <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"
           data-th-if="(${#strings.isEmpty(recipientData.lastName)})">able.cornell@linshare.com </span>
      </li>
   </th:block>
<th:block data-th-if="(${recipientData.downloaded})">
   <li style="color:#00b800;font-size:15px" >
      <th:block data-th-if="(${!#strings.isEmpty(recipientData.lastName)})" >
        <span  style="color:#7f7f7f;font-size:13px;">
          <th:block  data-th-utext="${recipientData.firstName}"/>
          <th:block data-th-utext="${recipientData.lastName}"/>
       </span>
     </th:block>
<th:block  data-th-if="(${#strings.isEmpty(recipientData.lastName)})">
  <span style="color:#7f7f7f;font-size:13px;" data-th-utext="${recipientData.mail}"> able.cornell@linshare.com </span>
  </th:block>
      </li>
   </th:block>
</th:block>
    </ul>
</li>
   </ul>
</div>', now(), now(), '15044750-89d1-11e3-8d50-5404a683a462', true, 'common.availableUntil =Disponible jusqu\''''au
common.byYou= | Par vous
common.download= Télécharger
common.filesInShare=Fichiers associés au partage
common.recipients = Destinataires
common.titleSharedThe= Partagé le
date.format=d MMMM, yyyy
productCompagny=Linagora
productName=LinShare
welcomeMessage = Bonjour {0},
logoLinShareBase64=data:image/png;base64, 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', 'common.availableUntil =Available until
common.byYou= | By you
common.download= Download
common.filesInShare=Files associated with the share
common.recipients = Recipients
common.titleSharedThe= Share creation date
date.format= MMMM d, yyyy
productCompagny=Linagora
productName=LinShare
welcomeMessage = Hello {0},
logoLinShareBase64=data:image/png;base64, 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');
INSERT INTO mail_config (id, mail_layout_id, domain_abstract_id, name, visible, uuid, creation_date, modification_date, readonly) VALUES (1, 1, 1, 'Default mail config', true, '946b190d-4c95-485f-bfe6-d288a2de1edd', now(), now(), true);
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (9, 1, NULL, true, 9, '[( #{subject})]', '<!DOCTYPE html>
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
        <p style="font-weight:bold;font-size:15px;"  data-th-utext="#{mainTile}">Did you forget your password ?</p>
        <p>
          <span data-th-utext="#{beginingMainMsg}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block data-th-replace="layout :: actionButtonLink(#{changePasswordBtn},${resetLink})"/>
          <br/>
        </p>
        <p  data-th-utext="#{endingMainMsg}"></p>
        <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{resetLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '753d57a8-4fcc-4346-ac92-f71828aca77c', now(), now(), true, 'accountExpiryDateTitle = Compte disponible jusqu\''''au
beginingMainMsg =  Suivez le lien ci-dessous afin de réinitialiser le mot de passe de votre compte LinShare.
changePasswordBtn = Réinitialiser
endingMainMsg = Si vous n\''''avez pas sollicité ce changement de mot de passe, merci d\''''ignorer cet email. Votre mot de passe ne sera pas mis à jour tant que vous n\''''en créé pas un nouveau, via le lien ci-dessus.
mainTile = Vous avez oublié votre mot de Passe ?
resetLinkTitle = Lien de réinitialisation
subject =  LinShare instruction de réinitialisation de mot de passe
userNameTitle = Identifiant', 'accountExpiryDateTitle = Account available until
beginingMainMsg =  Follow the link below to reset your Linshare Account password.
changePasswordBtn = Change password
endingMainMsg = If you did not request a password reset, please ignore this email. Your password will not change until you create a new one it within the link above.
mainTile = Did you forget your password ?
resetLinkTitle = LinShare reset password link
subject =  LinShare reset password instructions
userNameTitle = Username');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (11, 1, NULL, true, 11, '[( #{subject(${requestRecipient.mail},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content */-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p style="font-size: 14px;font-weight: bold;color: #df5656;margin-bottom: 7px;" data-th-utext="#{mainMsgTitle}">
          You have no available space.</p>
        <p>
          <span data-th-utext="#{mainMsg(${requestRecipient.mail})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      <!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
      <span id="message-title">
        <span data-th-text="#{msgTitle}">You have a message from</span>
      </span>
      <span id="message-content" data-th-text="*{body}">
        Hi design,<br>
       Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
        within my LinShare space.  <br>Best regards, Peter.
       </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper  main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${maxDepositSize != null})">
      <th:block data-th-replace="layout :: infoStandardArea(#{maxUploadDepotSize},${maxDepositSize})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '48fee30b-b2d3-4f85-b9ee-22044f9dbb4d', now(), now(), true, 'invitationClosureDate = Invitation disponible jusqu\''''au
invitationCreationDate = Invitation activé le
mainMsg =  <b>{0}</b>  n\''''a pas pu déposer des fichiers dans votre invitation de dépôt car vous n\''''avez plus d\''''espace disponible dans votre Espace Personnel. Libérez de l\''''espace afin de réceptionner les dépôts de vos destinataires.
mainMsgTitle = Vous n\''''avez plus d\''''espace disponible.
maxUploadDepotSize =  Taille total du dépôt
msgTitle = Message lié à l\''''invitation de dépôt :
recipientsURequest = Destinataires de l\''''invitation du dépôt
subject =  {0}  n\''''a pu déposer un fichier car il n\''''y a plus d\''''espace : {1}', 'invitationClosureDate = Invitation opened until
invitationCreationDate = Invitation activation date
mainMsg =  <b>{0}</b> could not upload his file in your upload request depot, since you have no space left in your Personal space. Free up some storage space to receive your recipients uploads.
mainMsgTitle = You have no available space.
maxUploadDepotSize = Max size of the depot
msgTitle = Upload request\''''s  attached message :
recipientsURequest = Recipients of the upload request
subject =  {0} could not upload a file since there is no more space left : {1}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (14, 1, NULL, true, 14, '[( #{subject(${requestRecipient.mail},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <section id="main-content">
    <!--/* Upper main-content*/-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${isgrouped})" data-th-utext="#{groupedBeginningMainMsg(${requestRecipient.mail})}"></span>
          <span data-th-if="(${!isgrouped})"
                data-th-utext="#{ungroupedBeginningMainMsg(${requestRecipient.mail})}"></span>
          <span data-th-if="(${documentsCount} == 1)" data-th-utext="#{endingMainMsgSingular}"></span>
          <span data-th-if="(${documentsCount} > 1)" data-th-utext="#{endingMainMsgPlural(${documentsCount})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-if="(${isgrouped})">
       <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, false)"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize}, ${totalSize})"/>
    <th:block data-th-if="(${request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{uploadedOverTotal(${request.uploadedFilesCount},${request.authorizedFiles})})"/>
    </th:block>
    <th:block data-th-if="(${!request.authorizedFiles})">
       <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
        #{totalUploaded(${request.uploadedFilesCount})})"/>
    </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '6c0c1214-0a77-46d0-92c5-c41d225bf9aa', now(), now(), true, 'endingMainMsgPlural =  Il y a au total <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = Il y a au total  <b>1 fichier </b> dans le dépôt.
filesInURDepot =  Fichiers déposés dans le dépot
fileSize =  Taille total
groupedBeginningMainMsg =  <b>{0}</b> a clôturé votre invitation de dépôt groupé.
invitationClosureDate = Invitation disponible jusqu\''''au
invitationCreationDate = Invitation activé le
msgTitle = Message lié à l\''''invitation de dépôt :
numFilesInDepot = Nombre de fichiers déposés
recipientsURequest = Destinataires de l\''''invitation du dépôt
subject = {0} a clôturé votre invitation de dépôt :  {1}
ungroupedBeginningMainMsg = <b>{0}</b> a clôturé votre invitation de dépôt.
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} files', 'endingMainMsgPlural = There are in total <b> {0} files </b> in the depot.
endingMainMsgSingular =  There is  in total <b>1 file </b> in the depot.
filesInURDepot = Files uploaded to the depot
fileSize =  Total filesize
groupedBeginningMainMsg = <b>{0}</b> has closed your grouped upload request depot.
invitationClosureDate = Invitation opened until
invitationCreationDate = Invitation activation date
msgTitle =  Upload request\''''s  attached message :
numFilesInDepot = Number of files within the depot
recipientsURequest = Recipients of the upload request
subject =  {0}  has closed  your upload request depot : {1}
ungroupedBeginningMainMsg  = <b>{0}</b> has closed your  upload request depot.
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (5, 1, NULL, true, 5, '[( #{subject(${shareOwner.firstName},${shareOwner.lastName},${share.name})})]', '<!DOCTYPE html>
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
          <span data-th-utext="#{mainMsg(${shareOwner.firstName},${shareOwner.lastName})}">
             Peter WILSON has downloaded your file
          </span>
          <span style="font-weight:bold" data-th-text="${share.name}" >
             filename.ext
          </span>.
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${share.expirationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{deletedDate},${share.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '554a3a2b-53b1-4ec8-9462-2d6053b80078', now(), now(), true, 'deletedDate = Supprimé le
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a supprimé le partage de :
subject = {0} {1} a supprimé le partage de : {2}', 'deletedDate = Deletion date
mainMsg = <b>{0} <span style="text-transform:uppercase">{1}</span> </b> has deleted the  fileshare :
subject = {0} {1} has deleted the fileshare  :  {2}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (2, 1, NULL, true, 2, '[# th:if="${#strings.isEmpty(customSubject)}"]
       [# th:if="${sharesCount} > 1"]
          [( #{subjectPlural(${shareOwner.firstName},${ shareOwner.lastName})})]
       [/]
        [# th:if="${sharesCount} ==  1"]
          [( #{subjectSingular(${shareOwner.firstName },${ shareOwner.lastName})})]
       [/]
[/]
  [# th:if="${!#strings.isEmpty(customSubject)}"]
    [(${customSubject})]   [( #{subjectCustomAlt(${shareOwner.firstName },${shareOwner.lastName})})]
[/]', '<!DOCTYPE html>
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
            </span>
          <span data-th-if="(${!anonymous})">
            <span data-th-if="(${sharesCount} ==  1)" data-th-text="#{helpMsgSingular}">Click on the link below in order to download it     </span>
            <span data-th-if="(${sharesCount} >  1)" data-th-text="#{helpMsgPlural}">Click on the links below in order to download them </span>
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
</html>', '250e4572-7bb9-4735-84ff-6a8af93e3a42', now(), now(), true, 'downloadBtn = Téléchargé
downloadLink = Lien de téléchargement
helpMsg = Cliquez sur le lien pour le télécharger.
helpMsgPlural = Cliquez sur les liens pour les télécharger.
helpPasswordMsgSingular = Cliquez sur le lien pour le télécharger et saisissez le mot de passe ci-dessous.
helpPasswordMsgPlural = Cliquez sur le lien pour les télécharger et saisissez le mot de passe ci-dessous.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>vous a partagé {2} fichiers.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> vous a partagé {2} fichier.
msgFrom = Vous avez un message de
name = {0} {1}
password = Mot de passe
subjectCustomAlt = de {0} {1}
subjectPlural =  {0} {1} vous a partagé des fichiers
subjectSingular =  {0} {1} vous a partagé un fichier', 'downloadBtn = Download
downloadLink = Download link
helpMsgPlural = Click on the links below in order to download them.
helpMsgSingular = Click on the link below in order to download.
helpPasswordMsgSingular = Click on the link below in order to download it and enter the following password.
helpPasswordMsgPlural = Click on the link below in order to download them and enter the following password.
mainMsgPlural = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} files</b> with you.
mainMsgSingular = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has shared <b>{2} file</b> with you.
msgFrom = You have a message from
name = {0} {1}
password = Password
subjectCustomAlt = by {0} {1}
subjectPlural = {0} {1} has shared some files with you
subjectSingular = {0} {1} has shared a file with you');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (10, 1, NULL, true, 10, '[( #{subject(${requestRecipient.mail},${document.name},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-utext="#{beginningMainMsg(${requestRecipient.mail})}"></span>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >
                  filename.ext
             </a>.
          </span>
          <span data-th-utext="#{endingMainMsg(${requestRecipient.mail})}"></span>
          <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${requestUrl})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{fileUploadedThe},${document.creationDate})"/>
    <th:block data-th-replace="layout :: infoStandardArea(#{fileSize},${document.size})"/>
 <th:block data-th-if="(${request.authorizedFiles})">
      <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
         #{uploadedOverTotal(${request.uploadedFilesCount},${request.authorizedFiles})})"/>
 </th:block>
 <th:block data-th-if="(${!request.authorizedFiles})">
      <th:block data-th-replace="layout :: infoStandardArea(#{numFilesInDepot},
         #{totalUploaded(${request.uploadedFilesCount})})"/>
 </th:block>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '5ea27e5b-9260-4ce1-b1bd-27372c5b653d', now(), now(), true, 'endingMainMsg = dans votre "invitation de dépôt" situé dans votre Espace Personnel.
fileSize =  Taille du fichier
fileUploadedThe= Fichier déposé le
invitationClosureDate = Invitation disponible jusqu\''''au
invitationCreationDate = Invitation activé le
beginningMainMsg = <b> {0} </b> vous a déposé un fichier intitulé :
numFilesInDepot = Nombre de fichiers déposés
subject =  {0}  vous a déposé {1}  dans votre invitation de dépôt : {2}
uploadedOverTotal = {0} / {1} fichiers
totalUploaded = {0} fichiers', 'endingMainMsg = in your Personal Space upload request depot
fileSize =  File size
fileUploadedThe = File upload date
invitationClosureDate = Invitation opened until
invitationCreationDate = Invitation activation date
beginningMainMsg =  <b> {0} </b> has uploaded the file :
endingMainMsg = in your Personal Space upload request depot.
numFilesInDepot = Number of files within the depot
subject =  {0}  has uploaded {1}  in your upload request depot : {2}
uploadedOverTotal = {0} / {1} files
totalUploaded = {0} files');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (1, 1, NULL, true, 1, '[( #{subject(${document.name})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head  data-th-replace="layout :: header"></head>
  <body>
    <div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
    <section id="main-content">
      <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
        <div id="section-content">
          <!--/* Greetings */-->
            <th:block data-th-replace="layout :: greetings(${owner.firstName})"/>
          <!--/* End of Greetings */-->
          <!--/* Main email  message content*/-->
          <p>
     <span  data-th-utext="#{beginningMainMsgInt}"></span>
            <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="${document.name}" th:href="@{${document.href}}" >
                  filename.ext
              </a>
          </span>
  <span  data-th-utext="#{endingMainMsgInt(${daysLeft})}">  </span>
           <!--/* Single download link for external recipient */-->
            <th:block   data-th-replace="layout :: actionButtonLink(#{common.download},${document.href})"/>
          </p> <!--/* End of Main email  message content*/-->
        </div><!--/* End of section-content*/-->
      </div><!--/* End of main-content container*/-->
    </section> <!--/* End of main-content*/-->
    <!--/* Secondary content for  bottom email section */-->
    <section id="secondary-content">
      <th:block data-th-replace="layout :: infoDateArea(#{uploadedThe},${document.expirationDate})"/>
      <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${document.creationDate})"/>
    </section>  <!--/* End of Secondary content for bottom email section */-->
  </body>
</html>', '1507e9c0-c1e1-4e0f-9efb-506f63cbba97', now(), now(), true, 'beginningMainMsgInt =  Votre fichier :
endingMainMsgInt = sera automatiquement supprimé dans <b> {0} jours</b>,  de votre espace personnel.
subject = Le fichier : {0}  va bientôt être supprimé
uploadedThe = Déposé le', 'beginningMainMsgInt =  Your file :
endingMainMsgInt = will automatically be deleted in <b> {0} days</b>,  from your Personal Space.
subject = The file :  {0}  is about to be deleted
uploadedThe = Upload date');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (6, 1, NULL, true, 6, '[( #{subject(${share.name})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-if="(${!anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.firstName})"/>
        </th:block>
        <th:block data-th-if="(${anonymous})">
          <th:block data-th-replace="layout :: greetings(${shareRecipient.mail})"/>
        </th:block>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <th:block data-th-if="(${anonymous})">
            <span
              data-th-utext="#{mainMsgExt(${share.name}, ${shareOwner.firstName},${shareOwner.lastName},${daysLeft})}">
              Your share link for Peter sent by Peter WILSON, will expire in 8 days. a-shared-file.txt.
            </span>
          </th:block>
          <th:block data-th-if="(${!anonymous})">
            <span data-th-utext="#{beginningMainMsgInt}"></span>
            <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;" data-th-text="${share.name}"
                th:href="@{${share.href}}">
                  filename.ext
             </a>
          </span>
            <span
              data-th-utext="#{endingMainMsgInt(${shareOwner.firstName},${shareOwner.lastName},${daysLeft})}">  </span>
            <!--/* Single download link for external recipient */-->
            <th:block data-th-replace="layout :: actionButtonLink(#{common.download},${share.href})"/>
          </th:block>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block
      data-th-replace="layout :: infoStandardArea(#{sharedBy},#{name(${shareOwner.firstName},${shareOwner.lastName})})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${share.expirationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${share.creationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'e7bf56c2-b015-4e64-9f07-3c7e2f3f9ca8', now(), now(), true, 'beginningMainMsgInt = Le lien de partage pour :
endingMainMsgInt = émis par <b> {0} <span style="text-transform:uppercase">{1}</span></b>,   va expirer dans <b>{2} jours</b>.
mainMsgExt =  Le lien de partage pour : <b>{0}</b> émis par <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  va expirer dans  <b>{3} jours</b>.
name = {0} {1}
sharedBy = Partagé par
subject =  Le partage de : {0} va bientôt expiré', 'beginningMainMsgInt = Your share link for:
endingMainMsgInt = sent by <b> {0} <span style="text-transform:uppercase">{1}</span></b>,  will expire in <b>{2} days</b>.
mainMsgExt = Your share link for: <b>{0}</b>  sent by <b> {1} <span style="text-transform:uppercase">{2}</span></b>,  will expire in <b>{3} days</b>.
name = {0} {1}
sharedBy = Shared by
subject = The download link for {0} is about to expire');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (12, 1, NULL, true, 12, '[# th:if="${warnOwner}"] [( #{subjectForOwner})]
       [/]
        [# th:if="${!warnOwner}"]
           [( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName})})]
       [/]
[# th:if="${!#strings.isEmpty(mailSubject)}"]
  [( #{formatMailSubject(${mailSubject})})]
[/]', '<!DOCTYPE html>
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
            <span  data-th-if="(${!isgrouped})"   data-th-utext="#{beginningMainMsgUnGrouped(${remainingDays})}"></span>
            <span  data-th-if="(${isgrouped})"   data-th-utext="#{beginningMainMsgGrouped(${remainingDays})}"></span>
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
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"   data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
            <th:block   data-th-replace="layout :: actionButtonLink(#{uploadFileBtn},${requestUrl})"/>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
      <span id="message-title">
        <span data-th-text="#{msgTitle}">You have a message from</span>
      </span>
      <span id="message-content"  data-th-text="*{body}">
          Hi design team,<br>
         Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
      </span>
    </div> <!--/* End of customized message */-->
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
      <th:block  data-th-if="(${isgrouped})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${!isgrouped})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'd43b22d6-d915-41cc-99e4-9c9db66c5aac', now(), now(), true, 'beginningMainMsgForRecipient =   L\''''invitation de dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> va atteindre sa date de clôture dans <b>{2} jours</b>
beginningMainMsgGrouped =   Votre invitation de dépôt "groupé" sera clôturé dans  <b>{0} jours</b>.
beginningMainMsgUnGrouped =   Votre invitation de dépôt sera clôturé dans  <b>{0} jours</b>.
defaultSubject = : {0}
endingMainMsgPlural = et vous avez actuellement reçu <b>{0} fichiers</b>.
endingMainMsgPlural = Il y a au total <b> {0} fichiers </b> dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez actuellement envoyé  <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = et vous avez actuellement reçu <b>1 fichier</b>.
endingMainMsgSingular = Il y a au total <b>1 fichier </b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez actuellement envoyé  <b>1 fichier </b> dans le dépôt.
filesInURDepot = Fichiers déposés dans le dépot
formatMailSubject = : {0}
invitationClosureDate = Invitation disponible jusqu\''''au
invitationCreationDate = Invitation activé le
msgTitle = Message lié à l\''''invitation de dépôt :
recipientsURequest = Destinataires de l\''''invitation du dépôt
subjectForOwner =  Votre invitation de dépôt sera bientôt clôturé
subjectForRecipient = L\'''' invitation de dépôt de {0} {1} sera bientôt clôturé
uploadFileBtn = Déposer un fichier', 'beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>\''''s upload request  is about to  reach it\''''s closure date in <b>{2} days</b>
beginningMainMsgGrouped = Your grouped upload  request is about to be closed in  <b>{0} days</b>.
beginningMainMsgUnGrouped =  Your upload request is about to be closed in  <b>{0} days</b>.
endingMainMsgPlural =  and you currently have received<b>{0} files</b>.
endingMainMsgPlural = There are in total <b> {0} files </b> in the depot.
endingMainMsgPluralForRecipient = and you currently have sent <b> {0} files </b> in the depot.
endingMainMsgSingular =   and you currently have received<b>1 file</b>.
endingMainMsgSingular = There is  in total <b>1 file </b> in the depot.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the depot.
filesInURDepot = Files uploaded to the depot
formatMailSubject = : {0}
invitationClosureDate = Invitation opened until
invitationCreationDate = Invitation activation date
msgTitle =  Upload request\''''s  attached message :
recipientsURequest = Recipients of the upload request
subjectForOwner =  Your upload request depot is about to be closed
subjectForRecipient =  {0} {1}\''''s  upload request is about to be closed
uploadFileBtn = Upload a file');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (15, 1, NULL, true, 15, '[( #{subject(${requestRecipient.mail},${subject})})]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <section id="main-content">
    <!--/* Upper main-content*/-->
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
  <span  data-th-utext="#{mainMsg(${requestRecipient.mail},${deleted.name})}"></span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div th:assert="${!#strings.isEmpty(body)}"
         th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
     <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section> <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '88b90304-e9c9-11e4-b6b4-5404a6202d2c', now(), now(), true, 'invitationClosureDate = Invitation disponible jusqu\''''au
invitationCreationDate = Invitation activé le
mainMsg = <b>{0}</b> a supprimé le fichier : <b> {1} </b>de votre invitation de dépôt.
msgTitle = Message lié à l\''''invitation de dépot :
subject =  {0} a supprimé un fichier de votre invitation de dépôt {1}', 'invitationClosureDate = Invitation opened until
invitationCreationDate = Invitation activation date
mainMsg = <b>{0}</b> has deleted the file : <b> {1} </b> from your upload request depot.
msgTitle = Upload request\''''s  attached message :
subject = {0}  has deleted a file from the upload request depot :  {1}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (4, 1, NULL, true, 4, '[# th:if="${!anonymous}"] 
      [( #{subject(${shareRecipient.firstName},${shareRecipient.lastName},${share.name})})]
[/]
[# th:if="${anonymous}"]
     [( #{subjectAnonymous(${shareRecipient.mail},${share.name})})]
 [/]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <th:block th:if="${!anonymous}" >
            <span data-th-utext="#{mainMsgInt(${shareRecipient.firstName},${shareRecipient.lastName})}">
            Peter WILSON has downloaded your file
            </span>
          </th:block>
          <th:block th:if="${anonymous} ">
            <span data-th-utext="#{mainMsgExt(${shareRecipient.mail})}">
              unknown@domain.com has downloaded your file
            </span>
          </th:block>
          <span>
             <a target="_blank" style="color:#1294dc;text-decoration:none;"  data-th-text="#{fileNameEndOfLine(${share.name})}" th:href="@{${share.href}}" >
                  filename.ext
              </a>
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoDateArea(#{downloadDate},${actionDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${expiryDate})"/>
     <th:block th:if="${sharesCount}  > 1 ">
         <th:block data-th-replace="layout :: infoFileListUploadState(#{common.filesInShare},${shares})"/>
   </th:block>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '403e5d8b-bc38-443d-8b94-bab39a4460af', now(), now(), true, 'downloadDate = Téléchargé le
fileNameEndOfLine = {0} .
mainMsgExt = Le destinataire externe <b>{0}</b> a téléchargé votre fichier :
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a téléchargé votre fichier :
subject =  {0} {1} a téléchargé {2}
subjectAnonymous = {0} a téléchargé : {1}', 'downloadDate = Download date
fileNameEndOfLine = {0} .
mainMsgExt = The external recipient <b>{0}</b> has downloaded your file:
mainMsgInt = <b> {0} <span style="text-transform:uppercase">{1}</span> </b> has downloaded your file :
subject = {0} {1} has downloaded {2}
subjectAnonymous = {0} has downloaded : {1}');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (13, 1, NULL, true, 13, '[# th:if="${warnOwner}"] 
           [( #{subjectForOwner})]
       [/]
        [# th:if="${!warnOwner}"]
           [( #{subjectForRecipient(${requestOwner.firstName},${requestOwner.lastName})})]
       [/]
[# th:if="${!#strings.isEmpty(subject)}"]
  [( #{formatMailSubject(${subject})})]
[/]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head  data-th-replace="layout :: header"></head>
<body>
<div th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content container*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Upper message content for the owner of the upload request */-->
        <th:block data-th-if="(${warnOwner})" >
          <!--/* Greetings */-->
          <th:block    data-th-replace="layout :: greetings(${requestOwner.firstName})"/>
          <!--/* End of Greetings  */-->
          <!--/* Main email  message content*/-->
          <p>
            <span  data-th-if="(${!isgrouped})"   data-th-utext="#{beginningMainMsgUnGrouped}"></span>
            <span  data-th-if="(${isgrouped})"   data-th-utext="#{beginningMainMsgGrouped}"></span>
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
            <span  data-th-utext="#{beginningMainMsgForRecipient(${requestOwner.firstName},${requestOwner.lastName},${remainingDays})}"></span>
            <span data-th-if="(${request.uploadedFilesCount} ==  1)"  data-th-utext="#{endingMainMsgSingularForRecipient}" ></span>
            <span  data-th-if="(${request.uploadedFilesCount} >  1)"   data-th-utext="#{endingMainMsgSingularForRecipient(${request.uploadedFilesCount})}"></span>
          </p>
        </th:block>
        <!--/* End of Main email  message content*/-->
        <!--/* End of upper message content for recipients of the upload request */-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of upper main-content container*/-->
    <!--/* If the sender has added a  customized message */-->
    <div   th:assert="${!#strings.isEmpty(body)}" th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
        <span id="message-title">
          <span data-th-text="#{msgTitle}">You have a message from</span>
        </span>
      <span id="message-content" data-th-text="*{body}">
          Hi design team,<br>
          Could you send me some screenshots of the app please. I am sending you a file depot link so that you can upload the files
          within my LinShare space.  <br>Best regards, Peter.
        </span>
    </div> <!--/* End of customized message */-->
  </section><!--/* End of uppermain-content*/-->
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
      <th:block  data-th-if="(${isgrouped})">
        <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsURequest},${recipients})"/>
        <th:block data-th-replace="layout :: infoFileListWithMyUploadRefs(#{filesInURDepot},${documents})"/>
      </th:block>
      <th:block  data-th-if="(${!isgrouped})">
        <th:block data-th-replace="layout :: infoFileLinksListingArea(#{filesInURDepot},${documents}, true)"/>
      </th:block>
    </th:block>
    <!--/* End of lower message content for recipients of the upload request */-->
    <th:block data-th-replace="layout :: infoDateArea(#{invitationCreationDate},${request.activationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{invitationClosureDate},${request.expirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', '0cd705f3-f1f5-450d-bfcd-f2f5a60c57f8', now(), now(), true, 'beginningMainMsgForRecipient = L\''''invitation de dépôt de <b> {0} <span style="text-transform:uppercase">{1}</span> </b> a atteint sa date de clôture
beginningMainMsgGrouped = Votre invitation de dépôt "groupé" a atteint sa date de clôture
beginningMainMsgUnGrouped = Votre invitation de dépôt a atteint sa date de clôture
endingMainMsgPlural = et vous avez  reçu au total  <b>{0} fichiers</b>  dans le dépôt.
endingMainMsgPluralForRecipient = et vous avez  envoyé  <b> {0} fichiers </b> dans le dépôt.
endingMainMsgSingular = et vous avez  reçu au total <b>1 fichier</b> dans le dépôt.
endingMainMsgSingularForRecipient = et vous avez  envoyé <b>1 fichier </b> dans le dépôt.
filesInURDepot = Fichiers déposés dans le dépot
formatMailSubject = : {0}
invitationClosureDate = Invitation clôturé le
invitationCreationDate = Invitation activé le
msgTitle = Message lié à l\''''invitation de dépôt :
recipientsURequest = Destinataires de l\''''invitation du dépôt
subjectForOwner = Votre invitation de dépôt est clôturé
subjectForRecipient = L\'''' invitation de dépôt de {0} est clôturé', 'beginningMainMsgForRecipient = <b> {0} <span style="text-transform:uppercase">{1}</span> </b>\''''s upload request  has reached it\''''s closure date
beginningMainMsgGrouped = Your grouped upload  request has reached it\''''s closure date
beginningMainMsgUnGrouped = Your upload request has reached it\''''s closure date
endingMainMsgPlural = and you have received in total <b>{0} files</b> in the depot.
endingMainMsgPluralForRecipient = and you currently have sent  <b> {0} files </b> in the depot.
endingMainMsgSingular = and you have received in total <b>1 file</b> in the depot.
endingMainMsgSingularForRecipient = and you currently have sent <b>1 file </b>in the depot.
filesInURDepot = Files uploaded to the depot
formatMailSubject = : {0}
invitationClosureDate = Invitation closure date
invitationCreationDate = Invitation activation date
msgTitle = Upload request\''''s  attached message :
recipientsURequest = Recipients of the upload request
subjectForOwner = Your upload request depot is now closed
subjectForRecipient =  {0}\''''s  upload request is now closed
subjectForRecipient =  {0} {1}\''''s  upload request is now closed');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (8, 1, NULL, true, 8, '[( #{subject(${creator.firstName},${creator.lastName}, #{productName})})]', '<!DOCTYPE html>
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
          <span data-th-utext="#{mainMsg(${creator.firstName},${creator.lastName},#{productName})}"></span>
          <!--/* Activation link for initialisation of the guest account */-->
          <th:block  data-th-replace="layout :: actionButtonLink(#{accessToLinshareBTn},${resetLink})"/>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoStandardArea(#{userNameTitle},${guest.mail})"/>
    <th:block data-th-replace="layout :: infoActionLink(#{activationLinkTitle},${resetLink})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{accountExpiryDateTitle},${guestExpirationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'a1ca74a5-433d-444a-8e53-8daa08fa0ddb', now(), now(), true, 'accessToLinshareBTn = Activé mon compte
accountExpiryDateTitle = Compte disponible jusqu\''''au
activationLinkTitle = Lien d\''''initialisation
mainMsg =  <b> {0} <span style="text-transform:uppercase">{1}</span></b> vous a créé un compte invité sur <b>LinShare</b> qui vous permet de partager des fichiers de façon sécurisé. <br/> Pour vous connecter, vous devez finaliser votre inscription en créant votre mot de passe à l\''''aide du lien  ci-dessous.
subject = {0}  {1} vous invite a activer votre compte {1}
userNameTitle = Identifiant', 'accessToLinshareBTn = Activate account
accountExpiryDateTitle = Account available until
activationLinkTitle = Initialization link
mainMsg = <b> {0} <span style="text-transform:uppercase">{1}</span></b> has created a <b>{2}</b> guest account for you, which enables you to transfer files more securely. <br/>To log into your account, you will need to finalize your subscription by creating your password using the following link.
subject = {0}  {1} invited you to activate your {2} account
userNameTitle = Username');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (7, 1, NULL, true, 7, '[# th:if="${documentsCount} > 1"] 
          [( #{subjectPlural(${documentsCount})})]
       [/]
        [# th:if="${documentsCount} ==  1"]
          [( #{subjectSingular(${documentsCount})})]
       [/]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/* Upper main-content*/-->
  <section id="main-content">
    <div th:replace="layout :: contentUpperSection( ~{::#section-content})">
      <div id="section-content">
        <!--/* Greetings */-->
        <th:block data-th-replace="layout :: greetings(${shareOwner.firstName})"/>
        <!--/* End of Greetings  */-->
        <!--/* Main email  message content*/-->
        <p>
          <span data-th-if="(${documentsCount} ==  1)" data-th-utext="#{mainMsgSingular(${documentsCount})}">
            Some recipients have not downloaded 2 files yet. You may find further details of the recipients downloads, below.
          </span>
          <span data-th-if="(${documentsCount} >  1)" data-th-utext="#{mainMsgplural(${documentsCount})}">
            Some recipients have not downloaded 2 files yet. You may find further details of the recipients downloads, below.
          </span>
        </p> <!--/* End of Main email  message content*/-->
      </div><!--/* End of section-content*/-->
    </div><!--/* End of main-content container*/-->
  </section> <!--/* End of upper main-content*/-->
  <!--/* Secondary content for  bottom email section */-->
  <section id="secondary-content">
    <th:block data-th-replace="layout :: infoFileListRecipientUpload(#{downloadStatesTile},${documents})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.titleSharedThe},${shareGroup.creationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{common.availableUntil},${shareGroup.expirationDate})"/>
    <th:block data-th-replace="layout :: infoDateArea(#{undownloadedDateTitle},${shareGroup.notificationDate})"/>
  </section>  <!--/* End of Secondary content for bottom email section */-->
</div>
</body>
</html>', 'eb291876-53fc-419b-831b-53a480399f7c', now(), now(), true, 'downloadStatesTile = Etat de téléchargement du partage
mainMsgplural = Certains destinataires n\''''ont pas téléchargés <b>{0} files</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
mainMsgSingular = Certains destinataires n\''''ont pas téléchargés <b>{0} fichier</b>. Vous trouverez ci-dessous le récapitulatif de téléchargement de vos destinataires.
subjectPlural = Rappel de non téléchargement : {0} fichiers n\''''ont pas été téléchargés.
subjectSingular = Rappel de non téléchargement :  {0} fichier n\''''a pas été téléchargé.
undownloadedDateTitle = Date de rappel de non-téléchargement', 'downloadStatesTile = Downloads states of the share
mainMsgplural = Some recipients have not downloaded <b>{0} files</b> yet. You may find further details of the recipients downloads, below.
mainMsgSingular = Some recipients have not downloaded <b>{0} file</b> yet. You may find further details of the recipients downloads, below.
subjectPlural = Undownloaded shared files alert : {0} files have not been downloaded yet.
subjectSingular = Undownloaded shared files alert : {0} file have not been downloaded yet.
undownloadedDateTitle = Reminder date of the Undownloaded files');
INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english) VALUES (3, 1, NULL, true, 3, '[# th:if="${documentsCount} > 1"] 
           [( #{subjectPlural})]
       [/]
        [# th:if="${documentsCount} ==  1"]
           [( #{subjectSingular})]
       [/]
[# th:if="${!#strings.isEmpty(customSubject)}"]
[(${ ": " +customSubject})]
[/]', '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head data-th-replace="layout :: header"></head>
<body>
<div
  th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
  <!--/*  Upper main-content */-->
  <section id="main-content">
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
             to 3 recipients set to expire for the 7th December 2017
            </span>
          <span data-th-if="(${recipientsCount} ==  1)" th:with="df=#{date.format}"
                data-th-utext="#{recipientCountMsgSingular(${#dates.format(expirationDate,df)},${recipientsCount})}">
            to 1 recipient set to expire for the 7th December 2017
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
</html>', '01e0ac2e-f7ba-11e4-901b-08002722e7b1', now(), now(), true, 'numFilesMsgPlural = Vous avez partagé <b>{0} fichiers</b>
numFilesMsgSingular = Vous avez partagé <b>{0} fichier</b>
recipientCountMsgPlural =  expirant le : <b>{0} </b> à <b>{1} destinataires</b>.</b>
recipientCountMsgSingular =  expirant le : <b>{0} </b> à <b>{1} destinataire</b>.</b>
subjectPlural = Vous avez partagé des fichiers
subjectSingular = Vous avez partagé un fichier', 'numFilesMsgPlural = You have shared  <b>{0} files</b>
numFilesMsgSingular = You have shared  <b>{0} file</b>
recipientCountMsgPlural =   to <b>{1} recipients</b>, set to expire on : {0}.
recipientCountMsgSingular =   to <b>{1} recipient</b>, set to expire on : {0}.
subjectPlural =  You have shared some files
subjectSingular = You have shared a file');
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (1, 0, 1, 1, 1, '4f3c4723-531e-449b-a1ae-d304fd3d2387', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (2, 0, 2, 1, 2, '81041673-c699-4849-8be4-58eea4507305', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (3, 0, 3, 1, 3, '85538234-1fc1-47a2-850d-7f7b59f1640e', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (4, 0, 4, 1, 4, 'ed70cc00-099e-4c44-8937-e8f51835000b', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (5, 0, 5, 1, 5, 'f355793b-17d4-499c-bb2b-e3264bc13dbd', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (6, 0, 6, 1, 6, '5a6764fc-350c-4f10-bdb0-e95ca7607607', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (7, 0, 7, 1, 7, '8d707581-3920-4d82-a8ba-f7984afc54ca', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (8, 0, 8, 1, 8, 'fd6011cf-e4cf-478d-835b-75b25e024b81', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (9, 0, 9, 1, 9, '7a560359-fa35-4ffd-ac1d-1d9ceef1b1e0', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (10, 0, 10, 1, 10, '822b3ede-daea-4b60-a8a2-2216c7d36fea', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (11, 0, 11, 1, 11, '9bf9d474-fd10-48da-843c-dfadebd2b455', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (12, 0, 12, 1, 12, 'ec270da7-e9cb-11e4-b6b4-5404a6202d2c', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (13, 0, 13, 1, 13, '447217e4-e1ee-11e4-8a45-fb8c68777bdf', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (14, 0, 14, 1, 14, 'bfcced12-7325-49df-bf84-65ed90ff7f59', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (15, 0, 15, 1, 15, '2837ac03-fb65-4007-a344-693d3fb31533', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (101, 1, 1, 1, 1, '28e5855a-c0e7-40fc-8401-9cf25eb53f03', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (102, 1, 2, 1, 2, '41d0f03d-57dd-420e-84b0-7908179c8329', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (103, 1, 3, 1, 3, '72c0fff4-4638-4e98-8223-df27f8f8ea8b', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (104, 1, 4, 1, 4, '6fbabf1a-58c0-49b9-859e-d24b0af38c87', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (105, 1, 5, 1, 5, 'b85fc62f-d9eb-454b-9289-fec5eab51a76', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (106, 1, 6, 1, 6, '25540d2d-b3b8-46a9-811b-0549ad300fe0', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (107, 1, 7, 1, 7, '6580009b-36fd-472d-9937-41d0097ead91', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (108, 1, 8, 1, 8, '86fdc43c-5fd7-4aba-b01a-90fccbfb5489', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (109, 1, 9, 1, 9, 'f9455b1d-3582-4998-8675-bc0a8137fc73', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (110, 1, 10, 1, 10, 'e5a9f689-c005-47c2-958f-b68071b1bf6f', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (111, 1, 11, 1, 11, '2daaea2a-1b13-48b4-89a6-032f7e034a2d', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (112, 1, 12, 1, 12, '8f579a8a-e352-11e4-99b3-08002722e7b1', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (113, 1, 13, 1, 13, 'fa7a23cb-f545-45b4-b9dc-c39586cb2398', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (114, 1, 14, 1, 14, '44bc0912-cf91-4fc0-b376-f0ebb82acd51', true);
INSERT INTO mail_content_lang (id, language, mail_content_id, mail_config_id, mail_content_type, uuid, readonly) VALUES (115, 1, 15, 1, 15, 'cccb263e-1c24-4eb9-bff7-298713cc3ab7', true);
INSERT INTO mail_footer (id, domain_abstract_id, description, visible, footer, creation_date, modification_date, uuid, readonly, messages_french, messages_english) VALUES (1, 1, 'footer html', true, '<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <div data-th-fragment="email_footer">
                                <td style="border-collapse:collapse;padding: 6px 0 4px 17px;" valign="top">
                                  <p style="margin: 0; font-size: 10px;"><span th:text="#{learnMoreAbout}">En savoir plus sur</span>
<a   th:href="@{#{productOfficialWebsite}}"  target="_blank"style="text-decoration:none; color:#a9a9a9;"><strong th:text="#{productName}">LinShare</strong>™</a>
                                  </p>
                                </td>
                                <td style="border-collapse:collapse; padding:  6px 17px 4px 0;"  valign="top" width="60">
                                  <img alt="libre-and-free" height="9"
 src="data:image/png;base64, iVBORw0KGgoAAAANSUhEUgAAADwAAAAJCAYAAABuS09sAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QAAAAAAAD5Q7t/AAAACXBIWXMAAAsSAAALEgHS3X78AAACHUlEQVRIx92UP2tTYRTGfylZtd6iDo4XxMFJUvQLpIuz6aLgMyUfIakfoJhJXAQ7yAMukqCDIIgJCi4u0Q+gmE0Q/zR2cFKsQ85bbq+3te3YA5d7z3nve/485zmnZvsMcAs4DZwAloGTwGegI+kVx0hqtq8Cz0JfB+4CZ4E14BLQBV5K+lm+bHsCrEiahd4FcuAtkEnqHzYh2y1gAMyAZUnTirMkM0lLh/FfB04BH4A3wHoU9gW4bvsb8BR4DFyruJ+lYgFSgbZvR9FHkTawCjSAJrBROMuB3lGALBa8BryXdLOE9CIwBX4DXys6kcV50TaIBBtAO+mSOrZHYZ8GGG0quhjn7QCwVwqbA8NSzD39xi8TIEt5LACLzOd3l0jaYk7xj0A5MIF+uYtNSeMIsAosAa1Csh2gH//Vwm+3wm8uaaUiZgMY2d62vX0AvwPme6gGtGxnC8Aj4IrtixUBHgDfo/iy5OUOB7JEwuOge7JlkoaRdB4J3w9wigzpAH3bXduTYNJOwZJq6TmA3wYwCFsG5PVA4xzwxPYP4B3wArgHbEWnqmQXvWw3gKntZgLCdrtgGxdA2WsO83gPgU3mNEwLMSsz6n9+bW+Wl1pd0h/gRqDbAi4D54HnQFvSr30KHtlOei8SygtIp1kq0n8DmMRimzLf8okpQ2AU3+O4t9Nd/h2hfD+/wZRE/Z6kfqIFti8Ad4DXwENJnziG8hdyJvydlDef+gAAAABJRU5ErkJggg"
                                       style="line-height:100%;width:60px;height:9px;padding:0" width="60" />
 </td>
   </div>
 </body>
 </html>', now(), now(), 'e85f4a22-8cf2-11e3-8a7a-5404a683a462', true, 'learnMoreAbout=En savoir plus sur
productOfficialWebsite=http://www.linshare.org/', 'learnMoreAbout=Learn more about
productOfficialWebsite=http://www.linshare.org/');
INSERT INTO mail_footer_lang (id, mail_config_id, mail_footer_id, language, uuid, readonly) VALUES (1, 1, 1, 0, 'bf87e580-fb25-49bb-8d63-579a31a8f81e', true);
INSERT INTO mail_footer_lang (id, mail_config_id, mail_footer_id, language, uuid, readonly) VALUES (2, 1, 1, 1, 'a6c8ee84-b5a8-4c96-b148-43301fbccdd9', true);
UPDATE domain_abstract SET mailconfig_id = 1;
UPDATE mail_footer SET readonly = true;
UPDATE mail_layout SET readonly = true;
UPDATE mail_content SET readonly = true;
UPDATE mail_config SET readonly = true;
UPDATE mail_content_lang SET readonly = true;
UPDATE mail_footer_lang SET readonly = true;

-- ###END-PART-2###

-- TASK: UPGRADE_2_0_DOMAIN_UUID
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
  (1,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_UUID',
  'UPGRADE_2_0',
  null,
  null,
  1,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_DOMAIN_POLICIES_UUID
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
  (2,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_POLICIES_UUID',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_UUID',
  2,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS
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
  (4,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_POLICIES_UUID',
  4,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS
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
  (5,
  'UNDEFINED',
  'UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS',
  5,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_ACCOUNT_QUOTA
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
  (6,
  'UNDEFINED',
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS',
  6,
  'NEW',
  'MANDATORY',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_CLEANUP_EXPIRED_GUEST
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
  (7,
  'UNDEFINED',
  'UPGRADE_2_0_CLEANUP_EXPIRED_GUEST',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_ACCOUNT_QUOTA',
  7,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT
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
  (8,
  'UNDEFINED',
  'UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_CLEANUP_EXPIRED_GUEST',
  8,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_PURGE_ACCOUNT
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
  (9,
  'UNDEFINED',
  'UPGRADE_2_0_PURGE_ACCOUNT',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT',
  9,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

  -- TASK: UPGRADE_2_0_SHA256SUM
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
  (10,
  'UNDEFINED',
  'UPGRADE_2_0_SHA256SUM',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_PURGE_ACCOUNT',
  10,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_UPGRADE_STORAGE
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
  (11,
   'UNDEFINED',
  'UPGRADE_2_0_UPGRADE_STORAGE',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_SHA256SUM',
  11,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);

-- TASK: UPGRADE_2_0_THREAD_TO_WORKGROUP
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
  (12,
   'UNDEFINED',
  'UPGRADE_2_0_THREAD_TO_WORKGROUP',
  'UPGRADE_2_0',
  null,
  'UPGRADE_2_0_UPGRADE_STORAGE',
  12,
  'NEW',
  'REQUIRED',
  now(),
  now(),
  null);
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

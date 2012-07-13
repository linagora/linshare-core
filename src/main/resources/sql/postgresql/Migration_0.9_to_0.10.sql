-- Postgresql migration script : 0.9 to 0.10

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


--
-- SCHEMA MIGRATION
--

ALTER TABLE linshare_allowed_contact
	DROP CONSTRAINT linshare_allowed_contact_pkey;

ALTER TABLE linshare_group_members
	DROP CONSTRAINT linshare_group_members_pkey;

ALTER TABLE linshare_share_expiry_rules
	DROP CONSTRAINT linshare_share_expiry_rules_pkey;

ALTER TABLE linshare_allowed_contact
	DROP CONSTRAINT fk3684ff4c67ff97ff;

ALTER TABLE linshare_allowed_contact
	DROP CONSTRAINT fk4284aa4c675aa721;

ALTER TABLE linshare_group
	DROP CONSTRAINT fk3684ccccccae97a1;

ALTER TABLE linshare_group_members
	DROP CONSTRAINT fk3684ae4c675e97a1;

ALTER TABLE linshare_group_members
	DROP CONSTRAINT fk4284ae4c675e9722;

ALTER TABLE linshare_mail_subjects
	DROP CONSTRAINT fdd6cccabca44789eb;

ALTER TABLE linshare_mail_templates
	DROP CONSTRAINT fdd6a0cabca44b78eb;

ALTER TABLE linshare_share_expiry_rules
	DROP CONSTRAINT fkfda1673ca44b78eb;

ALTER TABLE linshare_user
	DROP CONSTRAINT fk56dfc97c6f5f97f1;

ALTER TABLE linshare_welcome_texts
	DROP CONSTRAINT fk36a0c738a44b78eb;

DROP INDEX index_domain_pattern;

DROP INDEX index_group_user_id;

DROP INDEX index_ldap_connection;

CREATE TABLE linshare_domain_abstract (
	id bigint NOT NULL,
	type integer NOT NULL,
	identifier character varying(255) NOT NULL,
	label character varying(255) NOT NULL,
	enable boolean NOT NULL,
	template boolean NOT NULL,
	description text,
	default_role integer,
	default_locale character varying(255),
	used_space bigint NOT NULL,
	user_provider_id bigint,
	domain_policy_id bigint NOT NULL,
	parent_id bigint,
	messages_configuration_id bigint
);

CREATE TABLE linshare_domain_access_policy (
	id bigint NOT NULL
);

CREATE TABLE linshare_domain_access_rule (
	id bigint NOT NULL,
	domain_access_rule_type integer NOT NULL,
	regexp character varying(255),
	domain_id bigint,
	domain_access_policy_id bigint NOT NULL,
	rule_index integer
);

CREATE TABLE linshare_domain_policy (
	id bigint NOT NULL,
	description text,
	identifier character varying(255),
	domain_access_policy_id bigint
);

CREATE TABLE linshare_functionality (
	id bigint NOT NULL,
	system boolean NOT NULL,
	identifier character varying(255) NOT NULL,
	policy_activation_id bigint,
	policy_configuration_id bigint,
	domain_id bigint NOT NULL
);

CREATE TABLE linshare_functionality_integer (
	functionality_id bigint NOT NULL,
	integer_value integer
);

CREATE TABLE linshare_functionality_range_unit (
	functionality_id bigint NOT NULL,
	min integer,
	max integer,
	unit_min_id bigint,
	unit_max_id bigint
);

CREATE TABLE linshare_functionality_string (
	functionality_id bigint NOT NULL,
	string_value character varying(255)
);

CREATE TABLE linshare_functionality_unit (
	functionality_id bigint NOT NULL,
	integer_value integer,
	unit_id bigint
);

CREATE TABLE linshare_functionality_unit_boolean (
	functionality_id bigint NOT NULL,
	integer_value integer,
	boolean_value boolean,
	unit_id bigint
);

CREATE TABLE linshare_policy (
	id bigint NOT NULL,
	status boolean NOT NULL,
	default_status boolean NOT NULL,
	policy integer NOT NULL,
	system boolean NOT NULL
);

CREATE TABLE linshare_unit (
	id bigint NOT NULL,
	unit_type integer NOT NULL,
	unit_value integer
);

CREATE TABLE linshare_user_provider_ldap (
	id bigint NOT NULL,
	differential_key character varying(255),
	domain_pattern_id bigint NOT NULL,
	ldap_connection_id bigint NOT NULL
);

ALTER TABLE linshare_allowed_contact
	ALTER COLUMN user_id DROP NOT NULL,
	ALTER COLUMN contact_id DROP NOT NULL;

ALTER TABLE linshare_cookie
	ALTER COLUMN identifier TYPE character varying(255) /* TYPE change - table: linshare_cookie original: character varying(64) new: character varying(255) */,
	ALTER COLUMN value TYPE character varying(255) /* TYPE change - table: linshare_cookie original: character varying(64) new: character varying(255) */;

ALTER TABLE linshare_document
	ALTER COLUMN file_comment TYPE character varying(255) /* TYPE change - table: linshare_document original: text new: character varying(255) */;

ALTER TABLE linshare_domain_pattern
	ADD COLUMN user_mail character varying(255),
	ADD COLUMN user_firstname character varying(255),
	ADD COLUMN user_lastname character varying(255),
	ALTER COLUMN description TYPE text /* TYPE change - table: linshare_domain_pattern original: character varying(255) new: text */,
	ALTER COLUMN get_user_command TYPE text /* TYPE change - table: linshare_domain_pattern original: character varying(255) new: text */,
	ALTER COLUMN get_all_domain_users_command TYPE text /* TYPE change - table: linshare_domain_pattern original: character varying(255) new: text */,
	ALTER COLUMN auth_command TYPE text /* TYPE change - table: linshare_domain_pattern original: character varying(255) new: text */,
	ALTER COLUMN search_user_command TYPE text /* TYPE change - table: linshare_domain_pattern original: character varying(255) new: text */;

ALTER TABLE linshare_group_members
	ALTER COLUMN member_type_id DROP NOT NULL,
	ALTER COLUMN user_id DROP NOT NULL;

ALTER TABLE linshare_log_entry
	ALTER COLUMN description TYPE text /* TYPE change - table: linshare_log_entry original: character varying(255) new: text */;

ALTER TABLE linshare_share_expiry_rules
	ADD COLUMN domain_id bigint NOT NULL;

ALTER TABLE linshare_user
	ALTER COLUMN can_create_guest DROP DEFAULT,
	ALTER COLUMN restricted DROP DEFAULT;

ALTER TABLE linshare_version
	ALTER COLUMN description TYPE text /* TYPE change - table: linshare_version original: character varying(255) new: text */;


--
-- DATAS
--

-- default domain policy
INSERT INTO linshare_domain_access_policy(id) VALUES (1);
INSERT INTO linshare_domain_access_rule(id, domain_access_rule_type, regexp, domain_id, domain_access_policy_id, rule_index) VALUES (1, 0, '', null, 1,0);
INSERT INTO linshare_domain_policy(id, identifier, domain_access_policy_id) VALUES (1, 'DefaultDomainPolicy', 1);


-- Root domain (application domain)
INSERT INTO linshare_domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id) VALUES (1, 0, 'LinShareRootDomain', 'LinShareRootDomain', true, false, 'The root application domain', 3, 'en', 0, null, 1, null, 1);
UPDATE linshare_user SET domain_id = 1 WHERE user_id = 1 OR user_id = 2;

-- Functionality : FILESIZE_MAX
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (1, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (2, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (1, false, 'FILESIZE_MAX', 1, 2, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (1, 1, 1);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (1, 10, 1);


-- Functionality : QUOTA_GLOBAL
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (3, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (4, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (2, false, 'QUOTA_GLOBAL', 3, 4, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (2, 1, 1);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (2, 1, 2);


-- Functionality : QUOTA_USER
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (5, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (6, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (3, false, 'QUOTA_USER', 5, 6, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (3, 1, 1);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (3, 100, 3);


-- Functionality : MIME_TYPE
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (7, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (8, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (4, true, 'MIME_TYPE', 7, 8, 1);


-- Functionality : SIGNATURE
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (9, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (10, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (5, true, 'SIGNATURE', 9, 10, 1);


-- Functionality : ENCIPHERMENT
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (11, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (12, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (6, true, 'ENCIPHERMENT', 11, 12, 1);


-- Functionality : TIME_STAMPING
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (13, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (14, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (7, false, 'TIME_STAMPING', 13, 14, 1);
INSERT INTO linshare_functionality_string(functionality_id, string_value) VALUES (7, 'http://localhost:8080/signserver/tsa?signerId=1');


-- Functionality : ANTIVIRUS
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (15, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (16, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (8, true, 'ANTIVIRUS', 15, 16, 1);


-- Functionality : CUSTOM_LOGO
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (17, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (18, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (9, false, 'CUSTOM_LOGO', 17, 18, 1);
INSERT INTO linshare_functionality_string(functionality_id, string_value) VALUES (9, 'http://localhost/images/logo.png');


-- Functionality : ACCOUNT_EXPIRATION (for Guests)
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (19, true, true, 0, true);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (20, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (10, false, 'ACCOUNT_EXPIRATION', 19, 20, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (4, 0, 2);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (10, 3, 4);


-- Functionality : FILE_EXPIRATION
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (21, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (22, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (11, false, 'FILE_EXPIRATION', 21, 22, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (5, 0, 2);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (11, 3, 5);


-- Functionality : SHARE_EXPIRATION
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (23, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (24, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (12, false, 'SHARE_EXPIRATION', 23, 24, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (6, 0, 2);
INSERT INTO linshare_functionality_unit_boolean(functionality_id, integer_value, unit_id, boolean_value) VALUES (12, 3, 6, false);


-- Functionality : ANONYMOUS_URL
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (25, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (26, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (13, true, 'ANONYMOUS_URL', 25, 26, 1);


-- Functionality : GUESTS
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (27, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (28, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (14, true, 'GUESTS', 27, 28, 1);


-- Functionality : USER_CAN_UPLOAD
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (29, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (30, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (15, true, 'USER_CAN_UPLOAD', 29, 30, 1);


-- Functionality : COMPLETION
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (31, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (32, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (16, false, 'COMPLETION', 31, 32, 1);
INSERT INTO linshare_functionality_integer(functionality_id, integer_value) VALUES (16, 3);


-- Functionality : TAB_HELP
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (33, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (34, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (17, true, 'TAB_HELP', 33, 34, 1);


-- Functionality : TAB_AUDIT
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (35, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (36, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (18, true, 'TAB_AUDIT', 35, 36, 1);


-- Functionality : TAB_USER
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (37, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (38, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (19, true, 'TAB_USER', 37, 38, 1);


-- Functionality : TAB_GROUP
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (39, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (40, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (20, true, 'TAB_GROUP', 39, 40, 1);



INSERT INTO linshare_user_provider_ldap (id, differential_key, domain_pattern_id, ldap_connection_id)
    SELECT t.domain_id, t.differential_key, t.domain_pattern_id, t.ldap_connection_id
    FROM linshare_domain t;

UPDATE linshare_domain_pattern
    SET (user_mail, user_firstname, user_lastname) =
    (split_part(get_user_result, ' ', 1), split_part(get_user_result, ' ', 2), split_part(get_user_result, ' ', 3));


-- Guests
DROP LANGUAGE IF EXISTS plpgsql;
DROP FUNCTION IF EXISTS plpgsql_call_handler();

CREATE OR REPLACE FUNCTION plpgsql_call_handler () RETURNS language_handler
    AS '$libdir/plpgsql.so', 'plpgsql_call_handler'
        LANGUAGE C;

CREATE TRUSTED PROCEDURAL LANGUAGE plpgsql HANDLER plpgsql_call_handler;

GRANT USAGE ON LANGUAGE plpgsql TO public;

CREATE OR REPLACE FUNCTION create_guest_domain_f () RETURNS TRIGGER AS $$
    BEGIN
        IF NEW.label NOT LIKE 'guest_%' AND NEW.type = 1 THEN
            INSERT INTO linshare_domain_abstract (id, type, label, identifier, enable, template, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, used_space)
                VALUES ((SELECT nextVal('hibernate_sequence')), 3, 'guest_' || NEW.label, 'guest_' || NEW.label, false, false, 0, NEW.default_locale, null, 1, NEW.id, 1, 0);
            UPDATE linshare_user SET domain_id = (SELECT currval('hibernate_sequence')) WHERE user_type_id = '1' AND domain_id = NEW.id;
        END IF;
        RETURN NULL;
    END
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_guest_domain_t AFTER INSERT ON linshare_domain_abstract
    FOR EACH ROW
    EXECUTE PROCEDURE create_guest_domain_f();

INSERT INTO linshare_domain_abstract
    (id, type, identifier, label, enable, template, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, default_role, default_locale)
    SELECT d.domain_id, 1, d.identifier, d.identifier, true, false, p.global_used_quota, up.id, 1, 1, 1, 1, 'fr'
    FROM linshare_domain d, linshare_user_provider_ldap up, linshare_parameter p
    WHERE d.domain_id = up.id AND d.parameter_id = p.parameter_id;

DROP TRIGGER create_guest_domain_t ON linshare_domain_abstract;
DROP FUNCTION create_guest_domain_f();


INSERT INTO linshare_version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'0.10.0');



--
-- CONSTRAINTS / INDEXES
--

ALTER TABLE linshare_allowed_contact
	ADD CONSTRAINT linshare_version_pkey PRIMARY KEY (id);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT linshare_domain_abstract_pkey PRIMARY KEY (id);

ALTER TABLE linshare_domain_access_policy
	ADD CONSTRAINT linshare_domain_access_policy_pkey PRIMARY KEY (id);

ALTER TABLE linshare_domain_access_rule
	ADD CONSTRAINT linshare_domain_access_rule_pkey PRIMARY KEY (id);

ALTER TABLE linshare_domain_policy
	ADD CONSTRAINT linshare_domain_policy_pkey PRIMARY KEY (id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT linshare_functionality_pkey PRIMARY KEY (id);

ALTER TABLE linshare_functionality_integer
	ADD CONSTRAINT linshare_functionality_integer_pkey PRIMARY KEY (functionality_id);

ALTER TABLE linshare_functionality_range_unit
	ADD CONSTRAINT linshare_functionality_range_unit_pkey PRIMARY KEY (functionality_id);

ALTER TABLE linshare_functionality_string
	ADD CONSTRAINT linshare_functionality_string_pkey PRIMARY KEY (functionality_id);

ALTER TABLE linshare_functionality_unit
	ADD CONSTRAINT linshare_functionality_unit_pkey PRIMARY KEY (functionality_id);

ALTER TABLE linshare_functionality_unit_boolean
	ADD CONSTRAINT linshare_functionality_unit_boolean_pkey PRIMARY KEY (functionality_id);

ALTER TABLE linshare_group_members
	ADD CONSTRAINT linshare_group_members_pkey PRIMARY KEY (group_id, membership_date);

ALTER TABLE linshare_mail_subjects
	ADD CONSTRAINT linshare_mail_subjects_pkey PRIMARY KEY (messages_configuration_id, subject_id, language_id);

ALTER TABLE linshare_mail_templates
	ADD CONSTRAINT linshare_mail_templates_pkey PRIMARY KEY (messages_configuration_id, template_id, language_id);

ALTER TABLE linshare_policy
	ADD CONSTRAINT linshare_policy_pkey PRIMARY KEY (id);

ALTER TABLE linshare_share_expiry_rules
	ADD CONSTRAINT linshare_share_expiry_rules_pkey PRIMARY KEY (domain_id, rule_sort_order);

ALTER TABLE linshare_unit
	ADD CONSTRAINT linshare_unit_pkey PRIMARY KEY (id);

ALTER TABLE linshare_user_provider_ldap
	ADD CONSTRAINT linshare_user_provider_ldap_pkey PRIMARY KEY (id);

ALTER TABLE linshare_version
	ADD CONSTRAINT linshare_allowed_contact_pkey PRIMARY KEY (id);

ALTER TABLE linshare_allowed_contact
	ADD CONSTRAINT fkdfe3fe38c9452f4 FOREIGN KEY (contact_id) REFERENCES linshare_user(user_id);

ALTER TABLE linshare_allowed_contact
	ADD CONSTRAINT fkdfe3fe38fb78e769 FOREIGN KEY (user_id) REFERENCES linshare_user(user_id);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT linshare_domain_abstract_identifier_key UNIQUE (identifier);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT linshare_domain_abstract_user_provider_id_key UNIQUE (user_provider_id);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT fk449bc2ec126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT fk449bc2ec4e302e7 FOREIGN KEY (user_provider_id) REFERENCES linshare_user_provider_ldap(id);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT fk449bc2ec59e1e332 FOREIGN KEY (domain_policy_id) REFERENCES linshare_domain_policy(id);

ALTER TABLE linshare_domain_abstract
	ADD CONSTRAINT fk449bc2ec9083e725 FOREIGN KEY (parent_id) REFERENCES linshare_domain_abstract(id);

ALTER TABLE linshare_domain_access_rule
	ADD CONSTRAINT fkf75719ed3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);

ALTER TABLE linshare_domain_access_rule
	ADD CONSTRAINT fkf75719ed85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES linshare_domain_access_policy(id);

ALTER TABLE linshare_domain_policy
	ADD CONSTRAINT linshare_domain_policy_domain_access_policy_id_key UNIQUE (domain_access_policy_id);

ALTER TABLE linshare_domain_policy
	ADD CONSTRAINT fk49c9a27c85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES linshare_domain_access_policy(id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT linshare_functionality_identifier_key UNIQUE (identifier, domain_id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT linshare_functionality_policy_activation_id_key UNIQUE (policy_activation_id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT linshare_functionality_policy_configuration_id_key UNIQUE (policy_configuration_id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT fk7430c53a3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT fk7430c53a58fe5398 FOREIGN KEY (policy_activation_id) REFERENCES linshare_policy(id);

ALTER TABLE linshare_functionality
	ADD CONSTRAINT fk7430c53a71796372 FOREIGN KEY (policy_configuration_id) REFERENCES linshare_policy(id);

ALTER TABLE linshare_functionality_integer
	ADD CONSTRAINT fk8662133910439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);

ALTER TABLE linshare_functionality_range_unit
	ADD CONSTRAINT linshare_functionality_range_unit_unit_max_id_key UNIQUE (unit_max_id);

ALTER TABLE linshare_functionality_range_unit
	ADD CONSTRAINT linshare_functionality_range_unit_unit_min_id_key UNIQUE (unit_min_id);

ALTER TABLE linshare_functionality_range_unit
	ADD CONSTRAINT fk55007f6b10439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);

ALTER TABLE linshare_functionality_range_unit
	ADD CONSTRAINT fk55007f6b4b6b3004 FOREIGN KEY (unit_max_id) REFERENCES linshare_unit(id);

ALTER TABLE linshare_functionality_range_unit
	ADD CONSTRAINT fk55007f6b4bd76056 FOREIGN KEY (unit_min_id) REFERENCES linshare_unit(id);

ALTER TABLE linshare_functionality_string
	ADD CONSTRAINT fkb2a122b610439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);

ALTER TABLE linshare_functionality_unit
	ADD CONSTRAINT linshare_functionality_unit_unit_id_key UNIQUE (unit_id);

ALTER TABLE linshare_functionality_unit
	ADD CONSTRAINT fk3ced016910439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);

ALTER TABLE linshare_functionality_unit
	ADD CONSTRAINT fk3ced0169f329e0c9 FOREIGN KEY (unit_id) REFERENCES linshare_unit(id);

ALTER TABLE linshare_functionality_unit_boolean
	ADD CONSTRAINT linshare_functionality_unit_boolean_unit_id_key UNIQUE (unit_id);

ALTER TABLE linshare_functionality_unit_boolean
	ADD CONSTRAINT fk3ced016910439d2c FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);

ALTER TABLE linshare_functionality_unit_boolean
	ADD CONSTRAINT fk3ced0169f329e0d9 FOREIGN KEY (unit_id) REFERENCES linshare_unit(id);

ALTER TABLE linshare_group
	ADD CONSTRAINT linshare_group_group_user_id_key UNIQUE (group_user_id);

ALTER TABLE linshare_group
	ADD CONSTRAINT fk833cceeefe8695a9 FOREIGN KEY (group_user_id) REFERENCES linshare_user(user_id);

ALTER TABLE linshare_group_members
	ADD CONSTRAINT fk354c70c8675f9781 FOREIGN KEY (owner_id) REFERENCES linshare_user(user_id);

ALTER TABLE linshare_group_members
	ADD CONSTRAINT fk354c70c8a0ea11ab FOREIGN KEY (group_id) REFERENCES linshare_group(group_id);

ALTER TABLE linshare_group_members
	ADD CONSTRAINT fk354c70c8fb78e769 FOREIGN KEY (user_id) REFERENCES linshare_user(user_id);

ALTER TABLE linshare_mail_subjects
	ADD CONSTRAINT fk1c97f3be126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);

ALTER TABLE linshare_mail_templates
	ADD CONSTRAINT fkdd1b7f22126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);

ALTER TABLE linshare_share_expiry_rules
	ADD CONSTRAINT fkfda1673c3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);

ALTER TABLE linshare_user
	ADD CONSTRAINT fk56d6c97c3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);

ALTER TABLE linshare_user_provider_ldap
	ADD CONSTRAINT fk409cafb2372a0802 FOREIGN KEY (domain_pattern_id) REFERENCES linshare_domain_pattern(domain_pattern_id);

ALTER TABLE linshare_user_provider_ldap
	ADD CONSTRAINT fk409cafb23834018 FOREIGN KEY (ldap_connection_id) REFERENCES linshare_ldap_connection(ldap_connection_id);

ALTER TABLE linshare_welcome_texts
	ADD CONSTRAINT fk36a0c738126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);

CREATE INDEX index_allowed_contact_id ON linshare_allowed_contact USING btree (id);

CREATE INDEX index_allowed_mime_type_id ON linshare_allowed_mimetype USING btree (id);

CREATE INDEX index_contact_id ON linshare_contact USING btree (contact_id);

CREATE INDEX index_cookie_id ON linshare_cookie USING btree (cookie_id);

CREATE INDEX index_document_id ON linshare_document USING btree (document_id);

CREATE INDEX index_abstract_domain_id ON linshare_domain_abstract USING btree (id);

CREATE INDEX index_abstract_domain_identifier ON linshare_domain_abstract USING btree (identifier);

CREATE INDEX index_domain_access_policy_id ON linshare_domain_access_policy USING btree (id);

CREATE INDEX index_domain_access_rule_id ON linshare_domain_access_rule USING btree (id);

CREATE INDEX index_domain_pattern_id ON linshare_domain_pattern USING btree (domain_pattern_id);

CREATE INDEX index_domain_pattern_identifier ON linshare_domain_pattern USING btree (identifier);

CREATE INDEX index_domain_policy_id ON linshare_domain_policy USING btree (id);

CREATE INDEX index_functionality_id ON linshare_functionality USING btree (id);

CREATE INDEX index_functionality_integer_id ON linshare_functionality_integer USING btree (functionality_id);

CREATE INDEX index_functionality_unit_range_id ON linshare_functionality_range_unit USING btree (functionality_id);

CREATE INDEX index_functionality_string_id ON linshare_functionality_string USING btree (functionality_id);

CREATE INDEX index_functionality_unit_id ON linshare_functionality_unit USING btree (functionality_id);

CREATE INDEX index_functionality_unit_boolean_id ON linshare_functionality_unit_boolean USING btree (functionality_id);

CREATE INDEX index_group_id ON linshare_group USING btree (group_id);

CREATE INDEX index_group_user_id ON linshare_group USING btree (group_user_id);

CREATE INDEX index_group_members_user_id ON linshare_group_members USING btree (user_id);

CREATE INDEX index_ldap_connection_id ON linshare_ldap_connection USING btree (ldap_connection_id);

CREATE INDEX index_ldap_connection_identifier ON linshare_ldap_connection USING btree (identifier);

CREATE INDEX index_log_entry_id ON linshare_log_entry USING btree (id);

CREATE INDEX index_messages_configuration_subject_id ON linshare_mail_subjects USING btree (messages_configuration_id);

CREATE INDEX index_messages_configuration_template_id ON linshare_mail_templates USING btree (messages_configuration_id);

CREATE INDEX index_policy_id ON linshare_policy USING btree (id);

CREATE INDEX index_recipient_favourite_id ON linshare_recipient_favourite USING btree (id);

CREATE INDEX index_secured_url_id ON linshare_secured_url USING btree (secured_url_id);

CREATE INDEX index_secured_url_secured_url_id ON linshare_secured_url_documents USING btree (secured_url_id);

CREATE INDEX index_secured_url_contact_id ON linshare_secured_url_recipients USING btree (contact_id);

CREATE INDEX index_share_id ON linshare_share USING btree (share_id);

CREATE INDEX index_share_sharing_date ON linshare_share USING btree (sharing_date);

CREATE INDEX index_share_expiry_rule_id ON linshare_share_expiry_rules USING btree (domain_id);

CREATE INDEX index_signature_id ON linshare_signature USING btree (signature_id);

CREATE INDEX index_unit_id ON linshare_unit USING btree (id);

CREATE INDEX index_user_id ON linshare_user USING btree (user_id);

CREATE INDEX index_ldap_user_provider_id ON linshare_user_provider_ldap USING btree (id);

CREATE INDEX index_messages_configuration_welcome_id ON linshare_welcome_texts USING btree (messages_configuration_id);


--
-- DROPS
--

DROP TABLE linshare_domain;

DROP TABLE linshare_parameter;

ALTER TABLE linshare_domain_pattern
	DROP COLUMN get_user_result;

ALTER TABLE linshare_share_expiry_rules
	DROP COLUMN parameter_id;

ALTER TABLE linshare_welcome_texts
	DROP COLUMN user_type_id;



--
-- RIGHTS
--
GRANT ALL ON public.linshare_allowed_contact			TO linshare;
GRANT ALL ON public.linshare_allowed_mimetype			TO linshare;
GRANT ALL ON public.linshare_contact			        TO linshare;
GRANT ALL ON public.linshare_cookie			            TO linshare;
GRANT ALL ON public.linshare_document			        TO linshare;
GRANT ALL ON public.linshare_domain_abstract			TO linshare;
GRANT ALL ON public.linshare_domain_access_policy		TO linshare;
GRANT ALL ON public.linshare_domain_access_rule			TO linshare;
GRANT ALL ON public.linshare_domain_pattern			    TO linshare;
GRANT ALL ON public.linshare_domain_policy			    TO linshare;
GRANT ALL ON public.linshare_functionality			    TO linshare;
GRANT ALL ON public.linshare_functionality_integer		TO linshare;
GRANT ALL ON public.linshare_functionality_range_unit	TO linshare;
GRANT ALL ON public.linshare_functionality_string		TO linshare;
GRANT ALL ON public.linshare_functionality_unit			TO linshare;
GRANT ALL ON public.linshare_functionality_unit_boolean	TO linshare;
GRANT ALL ON public.linshare_group			            TO linshare;
GRANT ALL ON public.linshare_group_members			    TO linshare;
GRANT ALL ON public.linshare_ldap_connection			TO linshare;
GRANT ALL ON public.linshare_log_entry			        TO linshare;
GRANT ALL ON public.linshare_mail_subjects			    TO linshare;
GRANT ALL ON public.linshare_mail_templates			    TO linshare;
GRANT ALL ON public.linshare_messages_configuration		TO linshare;
GRANT ALL ON public.linshare_policy			            TO linshare;
GRANT ALL ON public.linshare_recipient_favourite		TO linshare;
GRANT ALL ON public.linshare_secured_url			    TO linshare;
GRANT ALL ON public.linshare_secured_url_documents		TO linshare;
GRANT ALL ON public.linshare_secured_url_recipients		TO linshare;
GRANT ALL ON public.linshare_share			            TO linshare;
GRANT ALL ON public.linshare_share_expiry_rules			TO linshare;
GRANT ALL ON public.linshare_signature			        TO linshare;
GRANT ALL ON public.linshare_unit			            TO linshare;
GRANT ALL ON public.linshare_user			            TO linshare;
GRANT ALL ON public.linshare_user_provider_ldap			TO linshare;
GRANT ALL ON public.linshare_version			        TO linshare;
GRANT ALL ON public.linshare_welcome_texts			    TO linshare;

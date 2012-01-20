

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

-- other options : could be useful for some case
-- SET standard_conforming_strings = off;
-- SET check_function_bodies = false;

-- SET escape_string_warning = off;



CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE linshare_version
(
  id bigint NOT NULL,
  description text NOT NULL
);


CREATE TABLE linshare_allowed_contact (
    id bigint NOT NULL,
    user_id bigint,
    contact_id bigint
);



CREATE TABLE linshare_allowed_mimetype (
    id bigint NOT NULL,
    extensions character varying(255),
    mimetype character varying(255),
    status integer
);



CREATE TABLE linshare_contact (
    contact_id bigint NOT NULL,
    mail character varying(255) NOT NULL
);



CREATE TABLE linshare_cookie (
    cookie_id bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    user_name character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    last_use timestamp without time zone NOT NULL
);



CREATE TABLE linshare_document (
    document_id bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    creation_date timestamp without time zone NOT NULL,
    expiration_date timestamp without time zone NOT NULL,
    deletion_date timestamp without time zone,
    type character varying(255),
    encrypted boolean,
    shared boolean,
    shared_with_group boolean,
    size bigint,
    file_comment character varying(255),
    thmb_uuid character varying(255),
    "timestamp" bytea,
    owner_id bigint
);



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



CREATE TABLE linshare_domain_pattern (
    domain_pattern_id bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    description text,
    get_user_command text,
    get_all_domain_users_command text,
    auth_command text,
    search_user_command text,
    user_mail character varying(255),
    user_firstname character varying(255),
    user_lastname character varying(255)
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



CREATE TABLE linshare_group (
    group_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    group_user_id bigint NOT NULL,
    functional_email character varying(255),
    description text
);



CREATE TABLE linshare_group_members (
    group_id bigint NOT NULL,
    member_type_id integer,
    membership_date timestamp without time zone NOT NULL,
    user_id bigint,
    owner_id bigint
);



CREATE TABLE linshare_ldap_connection (
    ldap_connection_id bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    provider_url character varying(255),
    security_auth character varying(255),
    security_principal character varying(255),
    security_credentials character varying(255)
);



CREATE TABLE linshare_log_entry (
    id bigint NOT NULL,
    entry_type character varying(255) NOT NULL,
    action_date timestamp without time zone NOT NULL,
    actor_mail character varying(255) NOT NULL,
    actor_firstname character varying(255) NOT NULL,
    actor_lastname character varying(255) NOT NULL,
    actor_domain character varying(255),
    log_action character varying(255) NOT NULL,
    description text,
    file_name character varying(255),
    file_type character varying(255),
    file_size bigint,
    target_mail character varying(255),
    target_firstname character varying(255),
    target_lastname character varying(255),
    target_domain character varying(255),
    expiration_date timestamp without time zone
);



CREATE TABLE linshare_mail_subjects (
    messages_configuration_id bigint NOT NULL,
    subject_id integer NOT NULL,
    language_id integer NOT NULL,
    content text
);



CREATE TABLE linshare_mail_templates (
    messages_configuration_id bigint NOT NULL,
    template_id integer NOT NULL,
    language_id integer NOT NULL,
    content_html text,
    content_txt text
);



CREATE TABLE linshare_messages_configuration (
    messages_configuration_id bigint NOT NULL
);



CREATE TABLE linshare_policy (
    id bigint NOT NULL,
    status boolean NOT NULL,
    default_status boolean NOT NULL,
    policy integer NOT NULL,
    system boolean NOT NULL
);



CREATE TABLE linshare_recipient_favourite (
    id bigint NOT NULL,
    user_id bigint,
    recipient character varying(255),
    weight bigint
);



CREATE TABLE linshare_secured_url (
    secured_url_id bigint NOT NULL,
    url_path character varying(255) NOT NULL,
    alea character varying(255) NOT NULL,
    expiration_date timestamp without time zone NOT NULL,
    password character varying(255),
    sender_id bigint
);



CREATE TABLE linshare_secured_url_documents (
    secured_url_id bigint NOT NULL,
    elt bigint NOT NULL,
    document_index integer NOT NULL
);



CREATE TABLE linshare_secured_url_recipients (
    contact_id bigint NOT NULL,
    elt bigint NOT NULL,
    contact_index integer NOT NULL
);



CREATE TABLE linshare_share (
    share_id bigint NOT NULL,
    document_id bigint,
    sender_id bigint,
    recipient_id bigint,
    expiration_date timestamp without time zone,
    sharing_date timestamp without time zone,
    share_active boolean,
    downloaded boolean,
    comment text
);



CREATE TABLE linshare_share_expiry_rules (
    domain_id bigint NOT NULL,
    expiry_time integer,
    time_unit_id integer,
    share_size integer,
    size_unit_id integer,
    rule_sort_order integer NOT NULL
);



CREATE TABLE linshare_signature (
    signature_id bigint NOT NULL,
    identifier character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    creation_date timestamp without time zone NOT NULL,
    type character varying(255),
    size bigint,
    cert_subjectdn character varying(255),
    cert_issuerdn character varying(255),
    cert_notafter timestamp without time zone,
    cert text,
    signer_id bigint,
    document_id_fk bigint,
    sort_order integer
);



CREATE TABLE linshare_unit (
    id bigint NOT NULL,
    unit_type integer NOT NULL,
    unit_value integer
);



CREATE TABLE linshare_user (
    user_id bigint NOT NULL,
    user_type_id character varying(255) NOT NULL,
    login character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    encipherment_key_pass bytea,
    mail character varying(255) NOT NULL,
    creation_date timestamp without time zone,
    role_id integer NOT NULL,
    can_upload boolean,
    can_create_guest boolean,
    password character varying(255),
    locale character varying(255),
    domain_id bigint,
    expiry_date timestamp without time zone,
    comment text,
    restricted boolean,
    owner_id bigint
);



CREATE TABLE linshare_user_provider_ldap (
    id bigint NOT NULL,
    differential_key character varying(255),
    domain_pattern_id bigint NOT NULL,
    ldap_connection_id bigint NOT NULL
);



CREATE TABLE linshare_welcome_texts (
    messages_configuration_id bigint NOT NULL,
    welcome_text text,
    language_id integer
);



ALTER TABLE ONLY linshare_version
    ADD CONSTRAINT linshare_allowed_contact_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_allowed_contact
    ADD CONSTRAINT linshare_version_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_allowed_mimetype
    ADD CONSTRAINT linshare_allowed_mimetype_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_contact
    ADD CONSTRAINT linshare_contact_pkey PRIMARY KEY (contact_id);



ALTER TABLE ONLY linshare_cookie
    ADD CONSTRAINT linshare_cookie_identifier_key UNIQUE (identifier);



ALTER TABLE ONLY linshare_cookie
    ADD CONSTRAINT linshare_cookie_pkey PRIMARY KEY (cookie_id);



ALTER TABLE ONLY linshare_document
    ADD CONSTRAINT linshare_document_identifier_key UNIQUE (identifier);



ALTER TABLE ONLY linshare_document
    ADD CONSTRAINT linshare_document_pkey PRIMARY KEY (document_id);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT linshare_domain_abstract_identifier_key UNIQUE (identifier);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT linshare_domain_abstract_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT linshare_domain_abstract_user_provider_id_key UNIQUE (user_provider_id);



ALTER TABLE ONLY linshare_domain_access_policy
    ADD CONSTRAINT linshare_domain_access_policy_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_domain_access_rule
    ADD CONSTRAINT linshare_domain_access_rule_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_domain_pattern
    ADD CONSTRAINT linshare_domain_pattern_identifier_key UNIQUE (identifier);



ALTER TABLE ONLY linshare_domain_pattern
    ADD CONSTRAINT linshare_domain_pattern_pkey PRIMARY KEY (domain_pattern_id);



ALTER TABLE ONLY linshare_domain_policy
    ADD CONSTRAINT linshare_domain_policy_domain_access_policy_id_key UNIQUE (domain_access_policy_id);



ALTER TABLE ONLY linshare_domain_policy
    ADD CONSTRAINT linshare_domain_policy_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT linshare_functionality_identifier_key UNIQUE (identifier, domain_id);



ALTER TABLE ONLY linshare_functionality_integer
    ADD CONSTRAINT linshare_functionality_integer_pkey PRIMARY KEY (functionality_id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT linshare_functionality_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT linshare_functionality_policy_activation_id_key UNIQUE (policy_activation_id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT linshare_functionality_policy_configuration_id_key UNIQUE (policy_configuration_id);



ALTER TABLE ONLY linshare_functionality_range_unit
    ADD CONSTRAINT linshare_functionality_range_unit_pkey PRIMARY KEY (functionality_id);



ALTER TABLE ONLY linshare_functionality_range_unit
    ADD CONSTRAINT linshare_functionality_range_unit_unit_max_id_key UNIQUE (unit_max_id);



ALTER TABLE ONLY linshare_functionality_range_unit
    ADD CONSTRAINT linshare_functionality_range_unit_unit_min_id_key UNIQUE (unit_min_id);



ALTER TABLE ONLY linshare_functionality_string
    ADD CONSTRAINT linshare_functionality_string_pkey PRIMARY KEY (functionality_id);



ALTER TABLE ONLY linshare_functionality_unit
    ADD CONSTRAINT linshare_functionality_unit_pkey PRIMARY KEY (functionality_id);



ALTER TABLE ONLY linshare_functionality_unit
    ADD CONSTRAINT linshare_functionality_unit_unit_id_key UNIQUE (unit_id);

    
    
ALTER TABLE ONLY linshare_functionality_unit_boolean
    ADD CONSTRAINT linshare_functionality_unit_boolean_pkey PRIMARY KEY (functionality_id);

    
    
ALTER TABLE ONLY linshare_functionality_unit_boolean
    ADD CONSTRAINT linshare_functionality_unit_boolean_unit_id_key UNIQUE (unit_id);
    
    

ALTER TABLE ONLY linshare_group
    ADD CONSTRAINT linshare_group_group_user_id_key UNIQUE (group_user_id);



ALTER TABLE ONLY linshare_group_members
    ADD CONSTRAINT linshare_group_members_pkey PRIMARY KEY (group_id, membership_date);



ALTER TABLE ONLY linshare_group
    ADD CONSTRAINT linshare_group_pkey PRIMARY KEY (group_id);



ALTER TABLE ONLY linshare_ldap_connection
    ADD CONSTRAINT linshare_ldap_connection_identifier_key UNIQUE (identifier);



ALTER TABLE ONLY linshare_ldap_connection
    ADD CONSTRAINT linshare_ldap_connection_pkey PRIMARY KEY (ldap_connection_id);



ALTER TABLE ONLY linshare_log_entry
    ADD CONSTRAINT linshare_log_entry_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_mail_subjects
    ADD CONSTRAINT linshare_mail_subjects_pkey PRIMARY KEY (messages_configuration_id, subject_id, language_id);



ALTER TABLE ONLY linshare_mail_templates
    ADD CONSTRAINT linshare_mail_templates_pkey PRIMARY KEY (messages_configuration_id, template_id, language_id);



ALTER TABLE ONLY linshare_messages_configuration
    ADD CONSTRAINT linshare_messages_configuration_pkey PRIMARY KEY (messages_configuration_id);



ALTER TABLE ONLY linshare_policy
    ADD CONSTRAINT linshare_policy_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_recipient_favourite
    ADD CONSTRAINT linshare_recipient_favourite_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_secured_url_documents
    ADD CONSTRAINT linshare_secured_url_documents_pkey PRIMARY KEY (secured_url_id, document_index);



ALTER TABLE ONLY linshare_secured_url
    ADD CONSTRAINT linshare_secured_url_pkey PRIMARY KEY (secured_url_id);



ALTER TABLE ONLY linshare_secured_url_recipients
    ADD CONSTRAINT linshare_secured_url_recipients_pkey PRIMARY KEY (contact_id, contact_index);



ALTER TABLE ONLY linshare_secured_url
    ADD CONSTRAINT linshare_secured_url_url_path_key UNIQUE (url_path, alea);



ALTER TABLE ONLY linshare_share_expiry_rules
    ADD CONSTRAINT linshare_share_expiry_rules_pkey PRIMARY KEY (domain_id, rule_sort_order);



ALTER TABLE ONLY linshare_share
    ADD CONSTRAINT linshare_share_pkey PRIMARY KEY (share_id);



ALTER TABLE ONLY linshare_signature
    ADD CONSTRAINT linshare_signature_identifier_key UNIQUE (identifier);



ALTER TABLE ONLY linshare_signature
    ADD CONSTRAINT linshare_signature_pkey PRIMARY KEY (signature_id);



ALTER TABLE ONLY linshare_unit
    ADD CONSTRAINT linshare_unit_pkey PRIMARY KEY (id);



ALTER TABLE ONLY linshare_user
    ADD CONSTRAINT linshare_user_login_key UNIQUE (login);



ALTER TABLE ONLY linshare_user
    ADD CONSTRAINT linshare_user_mail_key UNIQUE (mail);



ALTER TABLE ONLY linshare_user
    ADD CONSTRAINT linshare_user_pkey PRIMARY KEY (user_id);



ALTER TABLE ONLY linshare_user_provider_ldap
    ADD CONSTRAINT linshare_user_provider_ldap_pkey PRIMARY KEY (id);



CREATE INDEX idx_secured_url ON linshare_secured_url USING btree (url_path, alea);



CREATE INDEX index_abstract_domain_id ON linshare_domain_abstract USING btree (id);



CREATE INDEX index_abstract_domain_identifier ON linshare_domain_abstract USING btree (identifier);



CREATE INDEX index_allowed_contact_contact_id ON linshare_allowed_contact USING btree (contact_id);



CREATE INDEX index_allowed_contact_id ON linshare_allowed_contact USING btree (id);



CREATE INDEX index_allowed_contact_user_id ON linshare_allowed_contact USING btree (user_id);



CREATE INDEX index_allowed_mime_type_id ON linshare_allowed_mimetype USING btree (id);



CREATE INDEX index_contact_id ON linshare_contact USING btree (contact_id);



CREATE INDEX index_cookie_id ON linshare_cookie USING btree (cookie_id);



CREATE INDEX index_cookie_identifier ON linshare_cookie USING btree (identifier);



CREATE INDEX index_document_expiration_date ON linshare_document USING btree (expiration_date);



CREATE INDEX index_document_id ON linshare_document USING btree (document_id);



CREATE INDEX index_document_identifier ON linshare_document USING btree (identifier);



CREATE INDEX index_document_name ON linshare_document USING btree (name);



CREATE INDEX index_document_owner_id ON linshare_document USING btree (owner_id);



CREATE INDEX index_domain_access_policy_id ON linshare_domain_access_policy USING btree (id);



CREATE INDEX index_domain_access_rule_id ON linshare_domain_access_rule USING btree (id);



CREATE INDEX index_domain_pattern_id ON linshare_domain_pattern USING btree (domain_pattern_id);



CREATE INDEX index_domain_pattern_identifier ON linshare_domain_pattern USING btree (identifier);



CREATE INDEX index_domain_policy_id ON linshare_domain_policy USING btree (id);



CREATE INDEX index_favourite_recipient_id ON linshare_recipient_favourite USING btree (user_id);



CREATE INDEX index_filelog_entry_file_name ON linshare_log_entry USING btree (file_name);



CREATE INDEX index_functionality_id ON linshare_functionality USING btree (id);



CREATE INDEX index_functionality_integer_id ON linshare_functionality_integer USING btree (functionality_id);



CREATE INDEX index_functionality_string_id ON linshare_functionality_string USING btree (functionality_id);



CREATE INDEX index_functionality_unit_id ON linshare_functionality_unit USING btree (functionality_id);



CREATE INDEX index_functionality_unit_boolean_id ON linshare_functionality_unit_boolean USING btree (functionality_id);



CREATE INDEX index_functionality_unit_range_id ON linshare_functionality_range_unit USING btree (functionality_id);



CREATE INDEX index_group_id ON linshare_group USING btree (group_id);



CREATE INDEX index_group_members_user_id ON linshare_group_members USING btree (user_id);



CREATE INDEX index_group_name ON linshare_group USING btree (name);



CREATE INDEX index_group_user_id ON linshare_group USING btree (group_user_id);



CREATE INDEX index_ldap_connection_id ON linshare_ldap_connection USING btree (ldap_connection_id);



CREATE INDEX index_ldap_connection_identifier ON linshare_ldap_connection USING btree (identifier);



CREATE INDEX index_ldap_user_provider_id ON linshare_user_provider_ldap USING btree (id);



CREATE INDEX index_log_entry_action ON linshare_log_entry USING btree (log_action);



CREATE INDEX index_log_entry_action_date ON linshare_log_entry USING btree (action_date);



CREATE INDEX index_log_entry_actor_domain ON linshare_log_entry USING btree (actor_domain);



CREATE INDEX index_log_entry_actor_first_name ON linshare_log_entry USING btree (actor_firstname);



CREATE INDEX index_log_entry_actor_last_name ON linshare_log_entry USING btree (actor_lastname);



CREATE INDEX index_log_entry_actor_mail ON linshare_log_entry USING btree (actor_mail);



CREATE INDEX index_log_entry_id ON linshare_log_entry USING btree (id);



CREATE INDEX index_messages_configuration_subject_id ON linshare_mail_subjects USING btree (messages_configuration_id);



CREATE INDEX index_messages_configuration_template_id ON linshare_mail_templates USING btree (messages_configuration_id);



CREATE INDEX index_messages_configuration_welcome_id ON linshare_welcome_texts USING btree (messages_configuration_id);



CREATE INDEX index_policy_id ON linshare_policy USING btree (id);



CREATE INDEX index_recipient_favourite_id ON linshare_recipient_favourite USING btree (id);



CREATE INDEX index_secured_url_contact_id ON linshare_secured_url_recipients USING btree (contact_id);



CREATE INDEX index_secured_url_id ON linshare_secured_url USING btree (secured_url_id);



CREATE INDEX index_secured_url_secured_url_id ON linshare_secured_url_documents USING btree (secured_url_id);



CREATE INDEX index_securedurl_sender_id ON linshare_secured_url USING btree (sender_id);



CREATE INDEX index_share_document_id ON linshare_share USING btree (document_id);



CREATE INDEX index_share_expiration_date ON linshare_share USING btree (expiration_date);



CREATE INDEX index_share_expiry_rule_id ON linshare_share_expiry_rules USING btree (domain_id);



CREATE INDEX index_share_id ON linshare_share USING btree (share_id);



CREATE INDEX index_share_recipient_id ON linshare_share USING btree (recipient_id);



CREATE INDEX index_share_sender_id ON linshare_share USING btree (sender_id);



CREATE INDEX index_share_sharing_date ON linshare_share USING btree (sharing_date);



CREATE INDEX index_sharelog_entry_file_name ON linshare_log_entry USING btree (file_name);



CREATE INDEX index_sharelog_entry_target_mail ON linshare_log_entry USING btree (target_mail);



CREATE INDEX index_signature_id ON linshare_signature USING btree (signature_id);



CREATE INDEX index_signature_signer_id ON linshare_signature USING btree (signer_id);



CREATE INDEX index_unit_id ON linshare_unit USING btree (id);



CREATE INDEX index_user_first_name ON linshare_user USING btree (first_name);



CREATE INDEX index_user_id ON linshare_user USING btree (user_id);



CREATE INDEX index_user_last_name ON linshare_user USING btree (last_name);



CREATE INDEX index_user_login ON linshare_user USING btree (login);



CREATE INDEX index_user_mail ON linshare_user USING btree (mail);



CREATE INDEX index_userlog_entry_target_mail ON linshare_log_entry USING btree (target_mail);



ALTER TABLE ONLY linshare_secured_url_documents
    ADD CONSTRAINT fk139f29651fbb6b4e FOREIGN KEY (secured_url_id) REFERENCES linshare_secured_url(secured_url_id);



ALTER TABLE ONLY linshare_secured_url_documents
    ADD CONSTRAINT fk139f29659af607d7 FOREIGN KEY (elt) REFERENCES linshare_document(document_id);



ALTER TABLE ONLY linshare_mail_subjects
    ADD CONSTRAINT fk1c97f3be126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);



ALTER TABLE ONLY linshare_group_members
    ADD CONSTRAINT fk354c70c8675f9781 FOREIGN KEY (owner_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_group_members
    ADD CONSTRAINT fk354c70c8a0ea11ab FOREIGN KEY (group_id) REFERENCES linshare_group(group_id);



ALTER TABLE ONLY linshare_group_members
    ADD CONSTRAINT fk354c70c8fb78e769 FOREIGN KEY (user_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_welcome_texts
    ADD CONSTRAINT fk36a0c738126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);



ALTER TABLE ONLY linshare_functionality_unit
    ADD CONSTRAINT fk3ced016910439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);



ALTER TABLE ONLY linshare_functionality_unit
    ADD CONSTRAINT fk3ced0169f329e0c9 FOREIGN KEY (unit_id) REFERENCES linshare_unit(id);


    
   ALTER TABLE ONLY linshare_functionality_unit_boolean
    ADD CONSTRAINT fk3ced016910439d2c FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);



ALTER TABLE ONLY linshare_functionality_unit_boolean
    ADD CONSTRAINT fk3ced0169f329e0d9 FOREIGN KEY (unit_id) REFERENCES linshare_unit(id);

    

ALTER TABLE ONLY linshare_user_provider_ldap
    ADD CONSTRAINT fk409cafb2372a0802 FOREIGN KEY (domain_pattern_id) REFERENCES linshare_domain_pattern(domain_pattern_id);



ALTER TABLE ONLY linshare_user_provider_ldap
    ADD CONSTRAINT fk409cafb23834018 FOREIGN KEY (ldap_connection_id) REFERENCES linshare_ldap_connection(ldap_connection_id);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT fk449bc2ec126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT fk449bc2ec4e302e7 FOREIGN KEY (user_provider_id) REFERENCES linshare_user_provider_ldap(id);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT fk449bc2ec59e1e332 FOREIGN KEY (domain_policy_id) REFERENCES linshare_domain_policy(id);



ALTER TABLE ONLY linshare_domain_abstract
    ADD CONSTRAINT fk449bc2ec9083e725 FOREIGN KEY (parent_id) REFERENCES linshare_domain_abstract(id);



ALTER TABLE ONLY linshare_domain_policy
    ADD CONSTRAINT fk49c9a27c85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES linshare_domain_access_policy(id);



ALTER TABLE ONLY linshare_secured_url
    ADD CONSTRAINT fk5391e32c62928bf FOREIGN KEY (sender_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_functionality_range_unit
    ADD CONSTRAINT fk55007f6b10439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);



ALTER TABLE ONLY linshare_functionality_range_unit
    ADD CONSTRAINT fk55007f6b4b6b3004 FOREIGN KEY (unit_max_id) REFERENCES linshare_unit(id);



ALTER TABLE ONLY linshare_functionality_range_unit
    ADD CONSTRAINT fk55007f6b4bd76056 FOREIGN KEY (unit_min_id) REFERENCES linshare_unit(id);



ALTER TABLE ONLY linshare_document
    ADD CONSTRAINT fk56846e4c675f9781 FOREIGN KEY (owner_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_user
    ADD CONSTRAINT fk56d6c97c3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);



ALTER TABLE ONLY linshare_user
    ADD CONSTRAINT fk56d6c97c675f9781 FOREIGN KEY (owner_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT fk7430c53a3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT fk7430c53a58fe5398 FOREIGN KEY (policy_activation_id) REFERENCES linshare_policy(id);



ALTER TABLE ONLY linshare_functionality
    ADD CONSTRAINT fk7430c53a71796372 FOREIGN KEY (policy_configuration_id) REFERENCES linshare_policy(id);



ALTER TABLE ONLY linshare_secured_url_recipients
    ADD CONSTRAINT fk7c25d06d464c4a4b FOREIGN KEY (contact_id) REFERENCES linshare_secured_url(secured_url_id);



ALTER TABLE ONLY linshare_secured_url_recipients
    ADD CONSTRAINT fk7c25d06de97b80de FOREIGN KEY (elt) REFERENCES linshare_contact(contact_id);



ALTER TABLE ONLY linshare_signature
    ADD CONSTRAINT fk81c9a1a74472b3aa FOREIGN KEY (signer_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_signature
    ADD CONSTRAINT fk81c9a1a7c0bbd6f FOREIGN KEY (document_id_fk) REFERENCES linshare_document(document_id);



ALTER TABLE ONLY linshare_group
    ADD CONSTRAINT fk833cceeefe8695a9 FOREIGN KEY (group_user_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_share
    ADD CONSTRAINT fk83e1284e4f9c165b FOREIGN KEY (recipient_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_share
    ADD CONSTRAINT fk83e1284e62928bf FOREIGN KEY (sender_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_share
    ADD CONSTRAINT fk83e1284eb927c5e9 FOREIGN KEY (document_id) REFERENCES linshare_document(document_id);



ALTER TABLE ONLY linshare_recipient_favourite
    ADD CONSTRAINT fk847bec32fb78e769 FOREIGN KEY (user_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_functionality_integer
    ADD CONSTRAINT fk8662133910439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);



ALTER TABLE ONLY linshare_functionality_string
    ADD CONSTRAINT fkb2a122b610439d2b FOREIGN KEY (functionality_id) REFERENCES linshare_functionality(id);



ALTER TABLE ONLY linshare_mail_templates
    ADD CONSTRAINT fkdd1b7f22126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES linshare_messages_configuration(messages_configuration_id);



ALTER TABLE ONLY linshare_allowed_contact
    ADD CONSTRAINT fkdfe3fe38c9452f4 FOREIGN KEY (contact_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_allowed_contact
    ADD CONSTRAINT fkdfe3fe38fb78e769 FOREIGN KEY (user_id) REFERENCES linshare_user(user_id);



ALTER TABLE ONLY linshare_domain_access_rule
    ADD CONSTRAINT fkf75719ed3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);



ALTER TABLE ONLY linshare_domain_access_rule
    ADD CONSTRAINT fkf75719ed85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES linshare_domain_access_policy(id);



ALTER TABLE ONLY linshare_share_expiry_rules
    ADD CONSTRAINT fkfda1673c3c036ccb FOREIGN KEY (domain_id) REFERENCES linshare_domain_abstract(id);





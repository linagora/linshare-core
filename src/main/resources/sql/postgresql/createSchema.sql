

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

-- other options : could be useful for some case
-- SET standard_conforming_strings = off;
-- SET check_function_bodies = false;

-- SET escape_string_warning = off;


CREATE SEQUENCE hibernate_sequence INCREMENT BY 1 NO MINVALUE NO MAXVALUE START WITH 1 CACHE 1;
CREATE TABLE account (
  id                 BIGSERIAL NOT NULL, 
  ls_uid            varchar(255) NOT NULL, 
  creation_date     timestamp(6) NOT NULL, 
  modification_date timestamp(6) NOT NULL, 
  role_id           int4 NOT NULL, 
  locale            varchar(255), 
  enable            bool NOT NULL, 
  account_type      int4 NOT NULL, 
  domain_id         int8 NOT NULL, 
  password          varchar(255) NOT NULL, 
  destroyed         bool NOT NULL, 
  owner_id          int8, 
  CONSTRAINT account_pkey 
    PRIMARY KEY (id));
CREATE TABLE allowed_mimetype (
  id          BIGSERIAL NOT NULL, 
  extensions varchar(255), 
  mimetype   varchar(255), 
  status     int4, 
  CONSTRAINT linshare_allowed_mimetype_pkey 
    PRIMARY KEY (id));
CREATE TABLE anonymous_share_entry (
  id                int8 NOT NULL, 
  mail              varchar(255) NOT NULL, 
  downloaded        int8 NOT NULL, 
  secured_url_id    int8 NOT NULL, 
  document_entry_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE cookie (
  cookie_id   BIGSERIAL NOT NULL, 
  identifier varchar(255) NOT NULL, 
  user_name  varchar(255) NOT NULL, 
  value      varchar(255) NOT NULL, 
  last_use   timestamp(6) NOT NULL, 
  CONSTRAINT linshare_cookie_pkey 
    PRIMARY KEY (cookie_id));
CREATE TABLE default_view (
  identifier      varchar(255) NOT NULL, 
  view_context_id int8 NOT NULL, 
  view_id         int8 NOT NULL, 
  PRIMARY KEY (identifier), 
  CONSTRAINT unique_default_view 
    UNIQUE (view_context_id, view_id));
CREATE TABLE document (
  document_id    BIGSERIAL NOT NULL, 
  identifier    varchar(255) NOT NULL, 
  creation_date timestamp(6) NOT NULL, 
  type          varchar(255) NOT NULL, 
  size          int8 NOT NULL, 
  thmb_uuid     varchar(255), 
  timestamp     bytea, 
  CONSTRAINT linshare_document_pkey 
    PRIMARY KEY (document_id));
CREATE TABLE document_entry (
  id              int8 NOT NULL, 
  document_id     int8 NOT NULL, 
  expiration_date timestamp(6) NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE domain_abstract (
  id                         BIGSERIAL NOT NULL, 
  type                      int4 NOT NULL, 
  identifier                varchar(255) NOT NULL, 
  label                     varchar(255) NOT NULL, 
  enable                    bool NOT NULL, 
  template                  bool NOT NULL, 
  description               text NOT NULL, 
  default_role              int4 NOT NULL, 
  default_locale            varchar(255), 
  used_space                int8 NOT NULL, 
  user_provider_id          int8, 
  domain_policy_id          int8 NOT NULL, 
  parent_id                 int8, 
  messages_configuration_id int8 NOT NULL, 
  auth_show_order           int8 NOT NULL, 
  CONSTRAINT linshare_domain_abstract_pkey 
    PRIMARY KEY (id));
CREATE TABLE domain_access_policy (
  id  BIGSERIAL NOT NULL, 
  CONSTRAINT linshare_domain_access_policy_pkey 
    PRIMARY KEY (id));
CREATE TABLE domain_access_rule (
  id                       BIGSERIAL NOT NULL, 
  domain_access_rule_type int4 NOT NULL, 
  regexp                  varchar(255), 
  domain_id               int8, 
  domain_access_policy_id int8 NOT NULL, 
  rule_index              int4, 
  CONSTRAINT linshare_domain_access_rule_pkey 
    PRIMARY KEY (id));
CREATE TABLE domain_pattern (
  domain_pattern_id    BIGSERIAL NOT NULL, 
  identifier          varchar(255) NOT NULL, 
  description         text, 
  auth_command        text NOT NULL, 
  search_user_command text NOT NULL, 
  user_mail           varchar(255) NOT NULL, 
  user_firstname      varchar(255) NOT NULL, 
  user_lastname       varchar(255) NOT NULL, 
  user_uid            varchar(255) NOT NULL, 
  CONSTRAINT linshare_domain_pattern_pkey 
    PRIMARY KEY (domain_pattern_id));
CREATE TABLE domain_policy (
  id                       BIGSERIAL NOT NULL, 
  description             text, 
  identifier              varchar(255), 
  domain_access_policy_id int8, 
  CONSTRAINT linshare_domain_policy_pkey 
    PRIMARY KEY (id));
CREATE TABLE entry (
  id                 BIGSERIAL NOT NULL, 
  entry_type        int4 NOT NULL, 
  creation_date     timestamp(6) NOT NULL, 
  modification_date timestamp(6) NOT NULL, 
  name              varchar(255) NOT NULL, 
  comment           text NOT NULL, 
  ciphered          bool NOT NULL, 
  accountls_uid     varchar(255) NOT NULL, 
  Discriminator     varchar(255) NOT NULL, 
  accountid         int8, 
  PRIMARY KEY (id));
CREATE TABLE functionality (
  id                       BIGSERIAL NOT NULL, 
  system                  bool NOT NULL, 
  identifier              varchar(255) NOT NULL, 
  policy_activation_id    int8, 
  policy_configuration_id int8, 
  domain_id               int8 NOT NULL, 
  CONSTRAINT linshare_functionality_pkey 
    PRIMARY KEY (id));
CREATE TABLE functionality_integer (
  functionality_id int8 NOT NULL, 
  integer_value    int4, 
  CONSTRAINT linshare_functionality_integer_pkey 
    PRIMARY KEY (functionality_id));
CREATE TABLE functionality_range_unit (
  functionality_id int8 NOT NULL, 
  min              int4, 
  max              int4, 
  unit_min_id      int8, 
  unit_max_id      int8, 
  CONSTRAINT linshare_functionality_range_unit_pkey 
    PRIMARY KEY (functionality_id));
CREATE TABLE functionality_string (
  functionality_id int8 NOT NULL, 
  string_value     varchar(255), 
  CONSTRAINT linshare_functionality_string_pkey 
    PRIMARY KEY (functionality_id));
CREATE TABLE functionality_unit (
  functionality_id int8 NOT NULL, 
  integer_value    int4, 
  unit_id          int8, 
  CONSTRAINT linshare_functionality_unit_pkey 
    PRIMARY KEY (functionality_id));
CREATE TABLE functionality_unit_boolean (
  functionality_id int8 NOT NULL, 
  integer_value    int4, 
  boolean_value    bool, 
  unit_id          int8, 
  CONSTRAINT linshare_functionality_unit_boolean_pkey 
    PRIMARY KEY (functionality_id));
CREATE TABLE ldap_connection (
  ldap_connection_id    BIGSERIAL NOT NULL, 
  identifier           varchar(255) NOT NULL, 
  provider_url         varchar(255) NOT NULL, 
  security_auth        varchar(255), 
  security_principal   varchar(255), 
  security_credentials varchar(255), 
  CONSTRAINT linshare_ldap_connection_pkey 
    PRIMARY KEY (ldap_connection_id));
CREATE TABLE log_entry (
  id                BIGSERIAL NOT NULL, 
  entry_type       varchar(255) NOT NULL, 
  action_date      timestamp(6) NOT NULL, 
  actor_mail       varchar(255) NOT NULL, 
  actor_firstname  varchar(255) NOT NULL, 
  actor_lastname   varchar(255) NOT NULL, 
  actor_domain     varchar(255), 
  log_action       varchar(255) NOT NULL, 
  description      text, 
  file_name        varchar(255), 
  file_type        varchar(255), 
  file_size        int8, 
  target_mail      varchar(255), 
  target_firstname varchar(255), 
  target_lastname  varchar(255), 
  target_domain    varchar(255), 
  expiration_date  timestamp(6), 
  CONSTRAINT linshare_log_entry_pkey 
    PRIMARY KEY (id));
CREATE TABLE mail_subjects (
  messages_configuration_id int8 NOT NULL, 
  subject_id                int4 NOT NULL, 
  language_id               int4 NOT NULL, 
  content                   text);
CREATE TABLE mail_templates (
  messages_configuration_id int8 NOT NULL, 
  template_id               int4 NOT NULL, 
  language_id               int4 NOT NULL, 
  content_html              text, 
  content_txt               text);
CREATE TABLE messages_configuration (
  messages_configuration_id  BIGSERIAL NOT NULL, 
  CONSTRAINT linshare_messages_configuration_pkey 
    PRIMARY KEY (messages_configuration_id));
CREATE TABLE policy (
  id              BIGSERIAL NOT NULL, 
  status         bool NOT NULL, 
  default_status bool NOT NULL, 
  policy         int4 NOT NULL, 
  system         bool NOT NULL, 
  CONSTRAINT linshare_policy_pkey 
    PRIMARY KEY (id));
CREATE TABLE recipient_favourite (
  id                  BIGSERIAL NOT NULL, 
  recipient_mail     varchar(255) NOT NULL, 
  weight             int8 NOT NULL, 
  accountls_uid      varchar(255) NOT NULL, 
  useraccountls_uid2 varchar(255) NOT NULL, 
  useraccountid      int8, 
  CONSTRAINT linshare_recipient_favourite_pkey 
    PRIMARY KEY (id));
CREATE TABLE secured_url (
  secured_url_id   BIGSERIAL NOT NULL, 
  url_path        varchar(255) NOT NULL, 
  salt            varchar(255) NOT NULL, 
  expiration_date timestamp(6) NOT NULL, 
  password        varchar(255), 
  CONSTRAINT linshare_secured_url_pkey 
    PRIMARY KEY (secured_url_id));
CREATE TABLE share_entry (
  id                   int8 NOT NULL, 
  downloaded           int8 NOT NULL, 
  document_entry_id    int8 NOT NULL, 
  recipient_account_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE share_expiry_rules (
  domain_id       int8 NOT NULL, 
  expiry_time     int4, 
  time_unit_id    int4, 
  share_size      int4, 
  size_unit_id    int4, 
  rule_sort_order int4 NOT NULL, 
  CONSTRAINT linshare_share_expiry_rules_pkey 
    PRIMARY KEY (domain_id, 
  rule_sort_order));
CREATE TABLE signature (
  signature_id     BIGSERIAL NOT NULL, 
  identifier      varchar(255) NOT NULL, 
  name            varchar(255) NOT NULL, 
  creation_date   timestamp(6) NOT NULL, 
  type            varchar(255), 
  size            int8, 
  cert_subjectdn  varchar(255), 
  cert_issuerdn   varchar(255), 
  cert_notafter   timestamp(6), 
  cert            text, 
  sort_order      int4, 
  document_id     int8, 
  user_account_id int8, 
  CONSTRAINT linshare_signature_pkey 
    PRIMARY KEY (signature_id));
CREATE TABLE synchro_user_data (
  id                               BIGSERIAL NOT NULL, 
  field                           varchar(255) NOT NULL, 
  attribute                       varchar(255) NOT NULL, 
  sync                            bool NOT NULL, 
  system                          bool NOT NULL, 
  domain_patterndomain_pattern_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE tag (
  id          BIGSERIAL NOT NULL, 
  name       varchar(255) NOT NULL, 
  system     bool DEFAULT 'false' NOT NULL, 
  visible    bool DEFAULT 'true' NOT NULL, 
  account_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE tag_enum (
  tag_id    int8 NOT NULL, 
  not_null  bool NOT NULL, 
  tag_value int8, 
  PRIMARY KEY (tag_id));
CREATE TABLE tag_enum_values (
  id           BIGSERIAL NOT NULL, 
  tag_enum_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE tag_filter (
  id          BIGSERIAL NOT NULL, 
  name       varchar(255) NOT NULL, 
  account_id int8 NOT NULL, 
  PRIMARY KEY (id), 
  CONSTRAINT unique_rulename_by_account 
    UNIQUE (name, account_id));
CREATE TABLE tag_filter_list_tag (
  Tagid              int8 NOT NULL, 
  tag_filter_rule_id int8 NOT NULL, 
  value              varchar(255) NOT NULL, 
  PRIMARY KEY (Tagid));
CREATE TABLE tag_filter_rule (
  id             BIGSERIAL NOT NULL, 
  tag_filter_id int8 NOT NULL, 
  tag_rule_type int4 NOT NULL, 
  regexp        varchar(255), 
  PRIMARY KEY (id));
CREATE TABLE tag_linshare_anonymous_share_entry (
  tag_id                   int8 NOT NULL, 
  anonymous_share_entry_id int8 NOT NULL, 
  PRIMARY KEY (tag_id, 
  anonymous_share_entry_id));
CREATE TABLE tag_linshare_document_entry (
  tag_id            int8 NOT NULL, 
  document_entry_id int8 NOT NULL, 
  PRIMARY KEY (tag_id, 
  document_entry_id));
CREATE TABLE tag_share_entry (
  tag_id         int8 NOT NULL, 
  share_entry_id int8 NOT NULL, 
  PRIMARY KEY (tag_id, 
  share_entry_id));
CREATE TABLE technical_account_permission (
  id          BIGSERIAL NOT NULL, 
  write      bool NOT NULL, 
  account_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE technical_account_permission_account (
  technical_account_permission_id int8 NOT NULL, 
  account_id                      int8 NOT NULL, 
  PRIMARY KEY (technical_account_permission_id, 
  account_id));
CREATE TABLE technical_account_permission_domain_abstract (
  technical_account_permission_id int8 NOT NULL, 
  domain_abstract_id              int8 NOT NULL, 
  PRIMARY KEY (technical_account_permission_id, 
  domain_abstract_id));
CREATE TABLE thread (
  name       varchar(255) NOT NULL, 
  account_id int8 NOT NULL, 
  PRIMARY KEY (account_id));
CREATE TABLE thread_entry (
  id          int8 NOT NULL, 
  document_id int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE thread_member (
  ls_uid            varchar(255) NOT NULL, 
  admin             bool NOT NULL, 
  can_upload        bool NOT NULL, 
  creation_date     timestamp(6) NOT NULL, 
  modification_date timestamp(6) NOT NULL, 
  account_id        int8 NOT NULL, 
  thread_account_id int8 NOT NULL, 
  PRIMARY KEY (ls_uid, 
  account_id, 
  thread_account_id));
CREATE TABLE thread_member_history (
  id                 BIGSERIAL NOT NULL, 
  creation_date     timestamp(6) NOT NULL, 
  operation_type    int4 NOT NULL, 
  thread_account_id int8, 
  accountid         int8, 
  accountid2        int8, 
  PRIMARY KEY (id));
CREATE TABLE unit (
  id          BIGSERIAL NOT NULL, 
  unit_type  int4 NOT NULL, 
  unit_value int4, 
  CONSTRAINT linshare_unit_pkey 
    PRIMARY KEY (id));
CREATE TABLE users (
  account_id            int8 NOT NULL, 
  First_name            varchar(255), 
  Last_name             varchar(255), 
  Mail                  varchar(255), 
  Encipherment_key_pass bytea, 
  Not_after             timestamp(6), 
  Not_before            timestamp(6), 
  Can_upload            bool NOT NULL, 
  Comment               text, 
  Restricted            bool NOT NULL, 
  expiration_date       timestamp(6), 
  Ldap_uid              varchar(255), 
  CONSTRAINT user_pkey 
    PRIMARY KEY (account_id));
CREATE TABLE user_provider_ldap (
  id                 int8 NOT NULL, 
  differential_key   varchar(255) NOT NULL, 
  domain_pattern_id  int8 NOT NULL, 
  ldap_connection_id int8 NOT NULL, 
  CONSTRAINT linshare_user_provider_ldap_pkey 
    PRIMARY KEY (id));
CREATE TABLE version (
  id           BIGSERIAL NOT NULL, 
  description text NOT NULL, 
  CONSTRAINT linshare_allowed_contact_pkey 
    PRIMARY KEY (id));
CREATE TABLE "view" (
  id              BIGSERIAL NOT NULL, 
  context_id     int8 NOT NULL, 
  account_ls_uid varchar(255) NOT NULL, 
  name           varchar(255) NOT NULL, 
  _public        bool NOT NULL, 
  accountid      int8, 
  PRIMARY KEY (id), 
  CONSTRAINT unique_viewname_by_account_and_context 
    UNIQUE (context_id, account_ls_uid, name));
CREATE TABLE view_context (
  id           BIGSERIAL NOT NULL, 
  name        varchar(255) NOT NULL, 
  description text, 
  PRIMARY KEY (id));
CREATE TABLE view_tag_list (
  id       BIGSERIAL NOT NULL, 
  viewid  int8 NOT NULL, 
  "order" int8 NOT NULL, 
  tag_id  int8 NOT NULL, 
  PRIMARY KEY (id), 
  CONSTRAINT unique_tag_by_view 
    UNIQUE (viewid, tag_id), 
  CONSTRAINT unique_order_by_view 
    UNIQUE (viewid, "order"));
CREATE TABLE welcome_texts (
  messages_configuration_id int8 NOT NULL, 
  welcome_text              text, 
  language_id               int4);
CREATE TABLE tag_thread_entry (
  tag_id          int8 NOT NULL, 
  thread_entry_id int8 NOT NULL, 
  PRIMARY KEY (thread_entry_id));
CREATE TABLE guest (
  Expiration_date timestamp(6), 
  account_id      int8 NOT NULL, 
  PRIMARY KEY (account_id));
CREATE TABLE internal (
  ldap_uid   varchar(255), 
  account_id int8 NOT NULL, 
  PRIMARY KEY (account_id));
CREATE TABLE allowed_contact (
  id          BIGSERIAL NOT NULL, 
  account_id int8 NOT NULL, 
  contact_id int8 NOT NULL, 
  PRIMARY KEY (id), 
  CONSTRAINT unique_account_contact_relationship 
    UNIQUE (account_id, contact_id));
CREATE TABLE technical_account (
  account_id int8 NOT NULL, 
  PRIMARY KEY (account_id));
ALTER TABLE domain_abstract ADD CONSTRAINT fk449bc2ec4e302e7 FOREIGN KEY (user_provider_id) REFERENCES user_provider_ldap (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD CONSTRAINT fk449bc2ec59e1e332 FOREIGN KEY (domain_policy_id) REFERENCES domain_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD CONSTRAINT fk449bc2ec9083e725 FOREIGN KEY (parent_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD CONSTRAINT fk449bc2ec126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD CONSTRAINT fkf75719ed3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD CONSTRAINT fkf75719ed85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_policy ADD CONSTRAINT fk49c9a27c85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD CONSTRAINT fk7430c53a58fe5398 FOREIGN KEY (policy_activation_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD CONSTRAINT fk7430c53a71796372 FOREIGN KEY (policy_configuration_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD CONSTRAINT fk7430c53a3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_integer ADD CONSTRAINT fk8662133910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_range_unit ADD CONSTRAINT fk55007f6b10439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_range_unit ADD CONSTRAINT fk55007f6b4bd76056 FOREIGN KEY (unit_min_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_range_unit ADD CONSTRAINT fk55007f6b4b6b3004 FOREIGN KEY (unit_max_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_string ADD CONSTRAINT fkb2a122b610439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD CONSTRAINT fk3ced016910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD CONSTRAINT fk3ced0169f329e0c9 FOREIGN KEY (unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit_boolean ADD CONSTRAINT fk3ced016910439d2c FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit_boolean ADD CONSTRAINT fk3ced0169f329e0d9 FOREIGN KEY (unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE mail_subjects ADD CONSTRAINT fk1c97f3be126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE mail_templates ADD CONSTRAINT fkdd1b7f22126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE share_expiry_rules ADD CONSTRAINT fkfda1673c3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE signature ADD CONSTRAINT fk81c9a1a7c0bbd6f FOREIGN KEY (document_id) REFERENCES document (document_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE user_provider_ldap ADD CONSTRAINT fk409cafb2372a0802 FOREIGN KEY (domain_pattern_id) REFERENCES domain_pattern (domain_pattern_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE user_provider_ldap ADD CONSTRAINT fk409cafb23834018 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (ldap_connection_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE welcome_texts ADD CONSTRAINT fk36a0c738126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE thread ADD CONSTRAINT inheritance_account_thread FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE share_entry ADD CONSTRAINT inheritance_share FOREIGN KEY (id) REFERENCES entry (id);
ALTER TABLE thread_member ADD CONSTRAINT FKthread_mem133684 FOREIGN KEY (thread_account_id) REFERENCES thread (account_id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT inheritance_anonymous_thread FOREIGN KEY (id) REFERENCES entry (id);
ALTER TABLE document_entry ADD CONSTRAINT inheritance_document FOREIGN KEY (id) REFERENCES entry (id);
ALTER TABLE document_entry ADD CONSTRAINT FKdocument_e920038 FOREIGN KEY (document_id) REFERENCES document (document_id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr593985 FOREIGN KEY (document_entry_id) REFERENCES document_entry (id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_164812 FOREIGN KEY (document_entry_id) REFERENCES document_entry (id);
ALTER TABLE signature ADD CONSTRAINT FKsignature641503 FOREIGN KEY (user_account_id) REFERENCES users (account_id);
ALTER TABLE recipient_favourite ADD CONSTRAINT FKrecipient_16438 FOREIGN KEY (useraccountid) REFERENCES users (account_id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_126686 FOREIGN KEY (secured_url_id) REFERENCES secured_url (secured_url_id);
ALTER TABLE tag ADD CONSTRAINT FKtag535917 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE tag_filter ADD CONSTRAINT list_of_rules FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE tag_enum ADD CONSTRAINT inheritance_enum_tag FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_enum_values ADD CONSTRAINT FKtag_enum_v679118 FOREIGN KEY (tag_enum_id) REFERENCES tag_enum (tag_id);
ALTER TABLE tag_linshare_anonymous_share_entry ADD CONSTRAINT tag_anonymous_share_entry FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_linshare_anonymous_share_entry ADD CONSTRAINT FKtag_linsha882777 FOREIGN KEY (anonymous_share_entry_id) REFERENCES anonymous_share_entry (id);
ALTER TABLE tag_linshare_document_entry ADD CONSTRAINT tag_document_entry FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_linshare_document_entry ADD CONSTRAINT FKtag_linsha581924 FOREIGN KEY (document_entry_id) REFERENCES document_entry (id);
ALTER TABLE tag_share_entry ADD CONSTRAINT tag_share_entry FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_share_entry ADD CONSTRAINT FKtag_share_6461 FOREIGN KEY (share_entry_id) REFERENCES share_entry (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr128058 FOREIGN KEY (recipient_account_id) REFERENCES account (id);
ALTER TABLE entry ADD CONSTRAINT FKentry291949 FOREIGN KEY (accountid) REFERENCES account (id);
ALTER TABLE thread_entry ADD CONSTRAINT FKthread_ent814735 FOREIGN KEY (document_id) REFERENCES document (document_id);
ALTER TABLE thread_member_history ADD CONSTRAINT FKthread_mem915547 FOREIGN KEY (thread_account_id) REFERENCES thread (account_id);
ALTER TABLE default_view ADD CONSTRAINT FKdefault_vi37393 FOREIGN KEY (view_context_id) REFERENCES view_context (id);
ALTER TABLE "view" ADD CONSTRAINT FKview986540 FOREIGN KEY (context_id) REFERENCES view_context (id);
ALTER TABLE default_view ADD CONSTRAINT FKdefault_vi340520 FOREIGN KEY (view_id) REFERENCES "view" (id);
ALTER TABLE "view" ADD CONSTRAINT FKview243587 FOREIGN KEY (accountid) REFERENCES account (id);
ALTER TABLE view_tag_list ADD CONSTRAINT FKview_tag_l651590 FOREIGN KEY (viewid) REFERENCES "view" (id);
ALTER TABLE view_tag_list ADD CONSTRAINT FKview_tag_l536663 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_filter_list_tag ADD CONSTRAINT FKtag_filter960257 FOREIGN KEY (Tagid) REFERENCES tag (id);
ALTER TABLE tag_filter_rule ADD CONSTRAINT FKtag_filter70274 FOREIGN KEY (tag_filter_id) REFERENCES tag_filter (id);
ALTER TABLE account ADD CONSTRAINT FKaccount400616 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE thread_member ADD CONSTRAINT FKthread_mem327421 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE thread_member_history ADD CONSTRAINT FKthread_mem270963 FOREIGN KEY (accountid) REFERENCES account (id);
ALTER TABLE thread_member_history ADD CONSTRAINT FKthread_mem435438 FOREIGN KEY (accountid2) REFERENCES account (id);
ALTER TABLE synchro_user_data ADD CONSTRAINT FKsynchro_us464387 FOREIGN KEY (domain_patterndomain_pattern_id) REFERENCES domain_pattern (domain_pattern_id);
ALTER TABLE tag_filter_list_tag ADD CONSTRAINT FKtag_filter153598 FOREIGN KEY (tag_filter_rule_id) REFERENCES tag_filter_rule (id);
ALTER TABLE technical_account_permission_domain_abstract ADD CONSTRAINT FKtechnical_303831 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE technical_account_permission_domain_abstract ADD CONSTRAINT FKtechnical_231219 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE technical_account_permission_account ADD CONSTRAINT FKtechnical_69967 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE technical_account_permission_account ADD CONSTRAINT FKtechnical_622557 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE technical_account_permission ADD CONSTRAINT FKtechnical_948194 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE tag_thread_entry ADD CONSTRAINT FKtag_thread386462 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_thread_entry ADD CONSTRAINT FKtag_thread891662 FOREIGN KEY (thread_entry_id) REFERENCES thread_entry (id);
ALTER TABLE thread_entry ADD CONSTRAINT FKthread_ent587283 FOREIGN KEY (id) REFERENCES entry (id);
ALTER TABLE users ADD CONSTRAINT FKusers71760 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE guest ADD CONSTRAINT FKguest941452 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE internal ADD CONSTRAINT FKinternal239248 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE technical_account ADD CONSTRAINT FKtechnical_438809 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE allowed_contact ADD CONSTRAINT FKallowed_co409962 FOREIGN KEY (account_id) REFERENCES users (account_id);
ALTER TABLE allowed_contact ADD CONSTRAINT FKallowed_co620678 FOREIGN KEY (contact_id) REFERENCES users (account_id);
ALTER TABLE account ADD CONSTRAINT FKaccount487511 FOREIGN KEY (owner_id) REFERENCES account (id);

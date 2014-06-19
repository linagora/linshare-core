SET storage_engine=INNODB;
CREATE TABLE account (
  id                              bigint(8) NOT NULL AUTO_INCREMENT,
  domain_id                       bigint(8) NOT NULL,
  technical_account_permission_id bigint(8),
  owner_id                        bigint(8),
  ls_uuid                         varchar(255) NOT NULL,
  creation_date                   timestamp NOT NULL,
  modification_date               timestamp NOT NULL,
  role_id                         int(4) NOT NULL,
  locale                          varchar(255) NOT NULL,
  external_mail_locale            varchar(255) NOT NULL,
  enable                          bit NOT NULL,
  account_type                    int(4) NOT NULL,
  password                        varchar(255),
  destroyed                       bit NOT NULL,
  CONSTRAINT account_pkey
    PRIMARY KEY (id),
  UNIQUE INDEX (ls_uuid),
  INDEX (account_type)) CHARACTER SET UTF8;
CREATE TABLE allowed_mimetype (
  id         bigint(8) NOT NULL AUTO_INCREMENT,
  extensions varchar(255),
  mimetype   varchar(255) UNIQUE,
  status     int(4),
  CONSTRAINT linshare_allowed_mimetype_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE anonymous_share_entry (
  entry_id          bigint(8) NOT NULL,
  downloaded        bigint(8) NOT NULL,
  document_entry_id bigint(8) NOT NULL,
  anonymous_url_id  bigint(8) NOT NULL,
  PRIMARY KEY (entry_id)) CHARACTER SET UTF8;
CREATE TABLE cookie (
  cookie_id  bigint(8) NOT NULL AUTO_INCREMENT,
  identifier varchar(255) NOT NULL,
  user_name  varchar(255) NOT NULL,
  value      varchar(255) NOT NULL,
  last_use   timestamp NOT NULL,
  CONSTRAINT linshare_cookie_pkey
    PRIMARY KEY (cookie_id)) CHARACTER SET UTF8;
CREATE TABLE document (
  id            bigint(8) NOT NULL AUTO_INCREMENT,
  uuid          varchar(255) NOT NULL UNIQUE,
  creation_date timestamp NOT NULL,
  type          varchar(255) NOT NULL,
  check_mime_type  bit DEFAULT false NOT NULL, 
  size          bigint(8) NOT NULL,
  thmb_uuid     varchar(255),
  timestamp     blob,
  CONSTRAINT linshare_document_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE document_entry (
  entry_id    bigint(8) NOT NULL,
  document_id bigint(8) NOT NULL,
  ciphered    bit NOT NULL,
  PRIMARY KEY (entry_id),
  CONSTRAINT `unique document entry`
    UNIQUE (entry_id, document_id)) CHARACTER SET UTF8;
CREATE TABLE domain_abstract (
  id                        bigint(8) NOT NULL AUTO_INCREMENT,
  type                      int(4) NOT NULL,
  identifier                varchar(255) NOT NULL,
  label                     varchar(255) NOT NULL,
  enable                    bit NOT NULL,
  template                  bit NOT NULL,
  description               varchar(255) NOT NULL,
  default_role              int(4) NOT NULL,
  default_locale            varchar(255),
  used_space                bigint(8) NOT NULL,
  user_provider_id          bigint(8),
  domain_policy_id          bigint(8) NOT NULL,
  parent_id                 bigint(8),
  messages_configuration_id bigint(8) NOT NULL,
  auth_show_order           bigint(8) NOT NULL,
  CONSTRAINT linshare_domain_abstract_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE domain_access_policy (
  id bigint(8) NOT NULL AUTO_INCREMENT,
  CONSTRAINT linshare_domain_access_policy_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE domain_access_rule (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  domain_access_rule_type int(4) NOT NULL,
  `regexp`                  varchar(255),
  domain_id               bigint(8),
  domain_access_policy_id bigint(8) NOT NULL,
  rule_index              int(4),
  CONSTRAINT linshare_domain_access_rule_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE domain_pattern (
  domain_pattern_id                            bigint(8) NOT NULL AUTO_INCREMENT,
  identifier                                   varchar(255) NOT NULL,
  description                                  text NOT NULL,
  auth_command                                 text NOT NULL,
  search_user_command                          text NOT NULL,
  system                                       bit NOT NULL,
  auto_complete_command_on_first_and_last_name text NOT NULL,
  auto_complete_command_on_all_attributes      text NOT NULL,
  search_page_size                             int(4) NOT NULL,
  search_size_limit                            int(4) NOT NULL,
  completion_page_size                         int(4) NOT NULL,
  completion_size_limit                        int(4) NOT NULL,
  CONSTRAINT linshare_domain_pattern_pkey
    PRIMARY KEY (domain_pattern_id)) CHARACTER SET UTF8;
CREATE TABLE domain_policy (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  description             text,
  identifier              varchar(255),
  domain_access_policy_id bigint(8),
  CONSTRAINT linshare_domain_policy_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE entry (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  owner_id          bigint(8) NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  name              varchar(255) NOT NULL,
  comment           text NOT NULL,
  expiration_date   timestamp NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE functionality (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  system                  bit NOT NULL,
  identifier              varchar(255) NOT NULL,
  policy_activation_id    bigint(8),
  policy_configuration_id bigint(8),
  domain_id               bigint(8) NOT NULL,
  CONSTRAINT linshare_functionality_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE functionality_integer (
  functionality_id bigint(8) NOT NULL,
  integer_value    int(4),
  CONSTRAINT linshare_functionality_integer_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_range_unit (
  functionality_id bigint(8) NOT NULL,
  min              int(4) NOT NULL,
  max              int(4) NOT NULL,
  unit_min_id      bigint(8) NOT NULL,
  unit_max_id      bigint(8) NOT NULL,
  CONSTRAINT linshare_functionality_range_unit_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_string (
  functionality_id bigint(8) NOT NULL,
  string_value     varchar(255),
  CONSTRAINT linshare_functionality_string_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_unit (
  functionality_id bigint(8) NOT NULL,
  integer_value    int(4),
  unit_id          bigint(8),
  CONSTRAINT linshare_functionality_unit_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_unit_boolean (
  functionality_id bigint(8) NOT NULL,
  integer_value    int(4) NOT NULL,
  boolean_value    bit NOT NULL,
  unit_id          bigint(8) NOT NULL,
  CONSTRAINT linshare_functionality_unit_boolean_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE ldap_connection (
  ldap_connection_id   bigint(8) NOT NULL AUTO_INCREMENT,
  identifier           varchar(255) NOT NULL,
  provider_url         varchar(255) NOT NULL,
  security_auth        varchar(255),
  security_principal   varchar(255),
  security_credentials varchar(255),
  CONSTRAINT linshare_ldap_connection_pkey
    PRIMARY KEY (ldap_connection_id)) CHARACTER SET UTF8;
CREATE TABLE log_entry (
  id               bigint(8) NOT NULL AUTO_INCREMENT,
  entry_type       varchar(255) NOT NULL,
  action_date      timestamp NOT NULL,
  actor_mail       varchar(255) NOT NULL,
  actor_firstname  varchar(255) NOT NULL,
  actor_lastname   varchar(255) NOT NULL,
  actor_domain     varchar(255),
  log_action       varchar(255) NOT NULL,
  description      text,
  file_name        varchar(255),
  file_type        varchar(255),
  file_size        bigint(8),
  target_mail      varchar(255),
  target_firstname varchar(255),
  target_lastname  varchar(255),
  target_domain    varchar(255),
  expiration_date  timestamp NULL,
  CONSTRAINT linshare_log_entry_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_subjects (
  Id                        bigint(8) NOT NULL AUTO_INCREMENT,
  messages_configuration_id bigint(8) NOT NULL,
  subject_id                int(4) NOT NULL,
  language_id               int(4) NOT NULL,
  content                   text,
  CONSTRAINT linshare_mail_subjects_pkey
    PRIMARY KEY (Id)) CHARACTER SET UTF8;
CREATE TABLE mail_templates (
  Id                        bigint(8) NOT NULL AUTO_INCREMENT,
  messages_configuration_id bigint(8) NOT NULL,
  template_id               int(4) NOT NULL,
  language_id               int(4) NOT NULL,
  content_html              text,
  content_txt               text,
  CONSTRAINT linshare_mail_templates_pkey
    PRIMARY KEY (Id)) CHARACTER SET UTF8;
CREATE TABLE messages_configuration (
  messages_configuration_id bigint(8) NOT NULL AUTO_INCREMENT,
  CONSTRAINT linshare_messages_configuration_pkey
    PRIMARY KEY (messages_configuration_id)) CHARACTER SET UTF8;
CREATE TABLE policy (
  id             bigint(8) NOT NULL AUTO_INCREMENT,
  status         bit NOT NULL,
  default_status bit NOT NULL,
  policy         int(4) NOT NULL,
  system         bit NOT NULL,
  CONSTRAINT linshare_policy_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE recipient_favourite (
  id             bigint(8) NOT NULL AUTO_INCREMENT,
  recipient_mail varchar(255) NOT NULL,
  weight         bigint(8) NOT NULL,
  user_id        bigint(8) NOT NULL,
  CONSTRAINT linshare_recipient_favourite_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE anonymous_url (
  id         bigint(8) NOT NULL AUTO_INCREMENT,
  url_path   varchar(255) NOT NULL,
  uuid       varchar(255) NOT NULL UNIQUE,
  password   varchar(255),
  contact_id bigint(8) NOT NULL,
  CONSTRAINT linshare_secured_url_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE share_entry (
  entry_id          bigint(8) NOT NULL,
  document_entry_id bigint(8) NOT NULL,
  downloaded        bigint(8) NOT NULL,
  recipient_id      bigint(8) NOT NULL,
  PRIMARY KEY (entry_id)) CHARACTER SET UTF8;
CREATE TABLE share_expiry_rules (
  domain_id       bigint(8) NOT NULL,
  expiry_time     int(4),
  time_unit_id    int(4),
  share_size      int(4),
  size_unit_id    int(4),
  rule_sort_order int(4) NOT NULL,
  CONSTRAINT linshare_share_expiry_rules_pkey
    PRIMARY KEY (domain_id,
  rule_sort_order)) CHARACTER SET UTF8;
CREATE TABLE signature (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  owner_id          bigint(8),
  document_id       bigint(8) NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  name              varchar(255) NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  type              varchar(255),
  size              bigint(8),
  cert_subject_dn   varchar(255),
  cert_issuer_dn    varchar(255),
  cert_not_after    timestamp NULL,
  cert              text,
  sort_order        int(4),
  CONSTRAINT linshare_signature_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE ldap_attribute (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  domain_pattern_id bigint(8),
  field             varchar(255) NOT NULL,
  attribute         varchar(255) NOT NULL,
  sync              bit NOT NULL,
  system            bit NOT NULL,
  enable            bit NOT NULL,
  completion        bit NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE tag (
  id         bigint(8) NOT NULL AUTO_INCREMENT,
  account_id bigint(8) NOT NULL,
  name       varchar(255) NOT NULL,
  system     bit DEFAULT false NOT NULL,
  visible    bit DEFAULT true NOT NULL,
  not_null   bit,
  tag_type   int(4) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE tag_enum_value (
  id     bigint(8) NOT NULL AUTO_INCREMENT,
  tag_id bigint(8),
  value  varchar(255) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE tag_filter (
  id         bigint(8) NOT NULL AUTO_INCREMENT,
  account_id bigint(8),
  name       varchar(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unique_rulename_by_account
    UNIQUE (name, account_id)) CHARACTER SET UTF8;
CREATE TABLE tag_filter_rule (
  id            bigint(8) NOT NULL AUTO_INCREMENT,
  tag_filter_id bigint(8),
  `regexp`        text,
  tag_rule_type int(4) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE thread (
  account_id     bigint(8) NOT NULL,
  name           varchar(255) NOT NULL,
  thread_view_id bigint(8),
  PRIMARY KEY (account_id)) CHARACTER SET UTF8;
CREATE TABLE thread_entry (
  entry_id    bigint(8) NOT NULL,
  document_id bigint(8) NOT NULL,
  ciphered    bit NOT NULL,
  PRIMARY KEY (entry_id),
  CONSTRAINT `unique thread entry`
    UNIQUE (entry_id, document_id)) CHARACTER SET UTF8;
CREATE TABLE thread_member (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  thread_id         bigint(8) NOT NULL,
  admin             bit NOT NULL,
  can_upload        bit NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  user_id           bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE unit (
  id         bigint(8) NOT NULL AUTO_INCREMENT,
  unit_type  int(4) NOT NULL,
  unit_value int(4) NOT NULL,
  CONSTRAINT linshare_unit_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE users (
  account_id            bigint(8) NOT NULL,
  first_name            varchar(255),
  last_name             varchar(255),
  mail                  varchar(255),
  encipherment_key_pass blob,
  not_after             timestamp NULL,
  not_before            timestamp NULL,
  can_upload            bit NOT NULL,
  comment               text,
  restricted            bit NOT NULL,
  expiration_date       timestamp NULL,
  ldap_uid              varchar(255),
  can_create_guest      bit NOT NULL,
  CONSTRAINT user_pkey
    PRIMARY KEY (account_id)) CHARACTER SET UTF8;
CREATE TABLE user_provider_ldap (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  differential_key   varchar(255) NOT NULL,
  domain_pattern_id  bigint(8) NOT NULL,
  ldap_connection_id bigint(8) NOT NULL,
  CONSTRAINT linshare_user_provider_ldap_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE version (
  id          bigint(8) NOT NULL AUTO_INCREMENT,
  description varchar(255) NOT NULL UNIQUE,
  CONSTRAINT linshare_version_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE views (
  id              bigint(8) NOT NULL AUTO_INCREMENT,
  account_id      bigint(8) NOT NULL,
  view_context_id bigint(8) NOT NULL,
  name            varchar(255) NOT NULL,
  _public         bit NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unique_viewname_by_account_and_context
    UNIQUE (view_context_id, name, account_id)) CHARACTER SET UTF8;
CREATE TABLE view_context (
  id          bigint(8) NOT NULL AUTO_INCREMENT,
  name        varchar(255) NOT NULL,
  description text NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE welcome_texts (
  messages_configuration_id bigint(8) NOT NULL,
  welcome_text              text,
  language_id               int(4)) CHARACTER SET UTF8;
CREATE TABLE allowed_contact (
  id         bigint(8) NOT NULL AUTO_INCREMENT,
  account_id bigint(8) NOT NULL,
  contact_id bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE technical_account_permission_domain_abstract (
  technical_account_permission_id bigint(8) NOT NULL,
  domain_abstract_id              bigint(8) NOT NULL,
  PRIMARY KEY (technical_account_permission_id,
  domain_abstract_id)) CHARACTER SET UTF8;
CREATE TABLE technical_account_permission (
  id              bigint(8) NOT NULL AUTO_INCREMENT,
  `write`           bit NOT NULL,
  all_permissions bit NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE entry_tag_association (
  id            bigint(8) NOT NULL AUTO_INCREMENT,
  entry_id      bigint(8) NOT NULL,
  tag_id        bigint(8) NOT NULL,
  enum_value_id bigint(8),
  PRIMARY KEY (id),
  CONSTRAINT `unique relation`
    UNIQUE (entry_id, tag_id)) CHARACTER SET UTF8;
CREATE TABLE tag_filter_rule_tag_association (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  tag_filter_rule_id bigint(8),
  tag_id             bigint(8) NOT NULL,
  enum_value_id      bigint(8),
  PRIMARY KEY (id),
  CONSTRAINT `tag filter rules unique constraint`
    UNIQUE (tag_id, tag_filter_rule_id)) CHARACTER SET UTF8;
CREATE TABLE contact (
  id   bigint(8) NOT NULL AUTO_INCREMENT,
  mail varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE technical_account_permission_account (
  technical_account_permission_id bigint(8) NOT NULL,
  account_id                      bigint(8) NOT NULL,
  PRIMARY KEY (technical_account_permission_id,
  account_id)) CHARACTER SET UTF8;
CREATE TABLE default_view (
  id              bigint(8) NOT NULL AUTO_INCREMENT,
  identifier      varchar(255) NOT NULL UNIQUE,
  view_id         bigint(8) NOT NULL,
  view_context_id bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE view_tag_asso (
  id       bigint(8) NOT NULL AUTO_INCREMENT,
  tag_id   bigint(8) NOT NULL,
  views_id bigint(8) NOT NULL,
  depth    int(4) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE thread_view (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  thread_account_id bigint(8) NOT NULL,
  name              varchar(255) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE thread_view_asso (
  id             bigint(8) NOT NULL AUTO_INCREMENT,
  tag_id         bigint(8) NOT NULL,
  thread_view_id bigint(8) NOT NULL,
  depth          int(4) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mailing_list (
  id                  bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id bigint(8) NOT NULL,
  user_id            bigint(8) NOT NULL,
  is_public          bool NOT NULL,
  identifier         varchar(255) NOT NULL,
  description        text,
  uuid               varchar(255) NOT NULL,
  creation_date      timestamp NOT NULL,
  modification_date  timestamp NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mailing_list_contact (
  id                 bigint(8) NOT NULL,
  mailing_list_id    bigint(8) NOT NULL,
  mail               varchar(255) NOT NULL,
  first_name         varchar(255),
  last_name          varchar(255),
  uuid               varchar(255) NOT NULL,
  creation_date      timestamp NOT NULL,
  modification_date  timestamp NOT NULL,
  mailing_list_contact_index int4,
  PRIMARY KEY (id));



  
  
ALTER TABLE domain_abstract ADD INDEX fk449bc2ec4e302e7 (user_provider_id), ADD CONSTRAINT fk449bc2ec4e302e7 FOREIGN KEY (user_provider_id) REFERENCES user_provider_ldap (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD INDEX fk449bc2ec59e1e332 (domain_policy_id), ADD CONSTRAINT fk449bc2ec59e1e332 FOREIGN KEY (domain_policy_id) REFERENCES domain_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD INDEX fk449bc2ec9083e725 (parent_id), ADD CONSTRAINT fk449bc2ec9083e725 FOREIGN KEY (parent_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD INDEX fk449bc2ec126ff4f2 (messages_configuration_id), ADD CONSTRAINT fk449bc2ec126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD INDEX fkf75719ed3c036ccb (domain_id), ADD CONSTRAINT fkf75719ed3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD INDEX fkf75719ed85924e31 (domain_access_policy_id), ADD CONSTRAINT fkf75719ed85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_policy ADD INDEX fk49c9a27c85924e31 (domain_access_policy_id), ADD CONSTRAINT fk49c9a27c85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD INDEX fk7430c53a58fe5398 (policy_activation_id), ADD CONSTRAINT fk7430c53a58fe5398 FOREIGN KEY (policy_activation_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD INDEX fk7430c53a71796372 (policy_configuration_id), ADD CONSTRAINT fk7430c53a71796372 FOREIGN KEY (policy_configuration_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD INDEX fk7430c53a3c036ccb (domain_id), ADD CONSTRAINT fk7430c53a3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_integer ADD INDEX fk8662133910439d2b (functionality_id), ADD CONSTRAINT fk8662133910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_range_unit ADD INDEX fk55007f6b10439d2b (functionality_id), ADD CONSTRAINT fk55007f6b10439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_range_unit ADD INDEX fk55007f6b4bd76056 (unit_min_id), ADD CONSTRAINT fk55007f6b4bd76056 FOREIGN KEY (unit_min_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_range_unit ADD INDEX fk55007f6b4b6b3004 (unit_max_id), ADD CONSTRAINT fk55007f6b4b6b3004 FOREIGN KEY (unit_max_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_string ADD INDEX fkb2a122b610439d2b (functionality_id), ADD CONSTRAINT fkb2a122b610439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD INDEX fk3ced016910439d2b (functionality_id), ADD CONSTRAINT fk3ced016910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD INDEX fk3ced0169f329e0c9 (unit_id), ADD CONSTRAINT fk3ced0169f329e0c9 FOREIGN KEY (unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit_boolean ADD INDEX fk3ced016910439d2c (functionality_id), ADD CONSTRAINT fk3ced016910439d2c FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit_boolean ADD INDEX fk3ced0169f329e0d9 (unit_id), ADD CONSTRAINT fk3ced0169f329e0d9 FOREIGN KEY (unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE mail_subjects ADD INDEX fk1c97f3be126ff4f2 (messages_configuration_id), ADD CONSTRAINT fk1c97f3be126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE mail_templates ADD INDEX fkdd1b7f22126ff4f2 (messages_configuration_id), ADD CONSTRAINT fkdd1b7f22126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE share_expiry_rules ADD INDEX fkfda1673c3c036ccb (domain_id), ADD CONSTRAINT fkfda1673c3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE signature ADD INDEX fk81c9a1a7c0bbd6f (document_id), ADD CONSTRAINT fk81c9a1a7c0bbd6f FOREIGN KEY (document_id) REFERENCES document (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE user_provider_ldap ADD INDEX fk409cafb2372a0802 (domain_pattern_id), ADD CONSTRAINT fk409cafb2372a0802 FOREIGN KEY (domain_pattern_id) REFERENCES domain_pattern (domain_pattern_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE user_provider_ldap ADD INDEX fk409cafb23834018 (ldap_connection_id), ADD CONSTRAINT fk409cafb23834018 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (ldap_connection_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE welcome_texts ADD INDEX fk36a0c738126ff4f2 (messages_configuration_id), ADD CONSTRAINT fk36a0c738126ff4f2 FOREIGN KEY (messages_configuration_id) REFERENCES messages_configuration (messages_configuration_id) ON UPDATE No action ON DELETE No action;
ALTER TABLE thread ADD INDEX inheritance_account_thread (account_id), ADD CONSTRAINT inheritance_account_thread FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE document_entry ADD INDEX FKdocument_e594117 (document_id), ADD CONSTRAINT FKdocument_e594117 FOREIGN KEY (document_id) REFERENCES document (id);
ALTER TABLE share_entry ADD INDEX FKshare_entr708932 (document_entry_id), ADD CONSTRAINT FKshare_entr708932 FOREIGN KEY (document_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE anonymous_share_entry ADD INDEX FKanonymous_138106 (document_entry_id), ADD CONSTRAINT FKanonymous_138106 FOREIGN KEY (document_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE recipient_favourite ADD INDEX FKrecipient_90791 (user_id), ADD CONSTRAINT FKrecipient_90791 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE anonymous_share_entry ADD INDEX FKanonymous_732508 (anonymous_url_id), ADD CONSTRAINT FKanonymous_732508 FOREIGN KEY (anonymous_url_id) REFERENCES anonymous_url (id);
ALTER TABLE tag ADD INDEX FKtag535917 (account_id), ADD CONSTRAINT FKtag535917 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE tag_enum_value ADD INDEX FKtag_enum_v488575 (tag_id), ADD CONSTRAINT FKtag_enum_v488575 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE share_entry ADD INDEX FKshare_entr87036 (recipient_id), ADD CONSTRAINT FKshare_entr87036 FOREIGN KEY (recipient_id) REFERENCES account (id);
ALTER TABLE views ADD INDEX FKviews640843 (view_context_id), ADD CONSTRAINT FKviews640843 FOREIGN KEY (view_context_id) REFERENCES view_context (id);
ALTER TABLE views ADD INDEX FKviews445993 (account_id), ADD CONSTRAINT FKviews445993 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE tag_filter_rule ADD INDEX FKtag_filter70274 (tag_filter_id), ADD CONSTRAINT FKtag_filter70274 FOREIGN KEY (tag_filter_id) REFERENCES tag_filter (id);
ALTER TABLE account ADD INDEX FKaccount400616 (domain_id), ADD CONSTRAINT FKaccount400616 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE ldap_attribute ADD INDEX FKldap_attri687153 (domain_pattern_id), ADD CONSTRAINT FKldap_attri687153 FOREIGN KEY (domain_pattern_id) REFERENCES domain_pattern (domain_pattern_id);
ALTER TABLE allowed_contact ADD INDEX FKallowed_co409962 (account_id), ADD CONSTRAINT FKallowed_co409962 FOREIGN KEY (account_id) REFERENCES users (account_id);
ALTER TABLE allowed_contact ADD INDEX FKallowed_co620678 (contact_id), ADD CONSTRAINT FKallowed_co620678 FOREIGN KEY (contact_id) REFERENCES users (account_id);
ALTER TABLE account ADD INDEX FKaccount487511 (owner_id), ADD CONSTRAINT FKaccount487511 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE users ADD INDEX FKusers71760 (account_id), ADD CONSTRAINT FKusers71760 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE thread_member ADD INDEX FKthread_mem280144 (thread_id), ADD CONSTRAINT FKthread_mem280144 FOREIGN KEY (thread_id) REFERENCES thread (account_id);
ALTER TABLE thread_member ADD INDEX FKthread_mem565048 (user_id), ADD CONSTRAINT FKthread_mem565048 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE technical_account_permission_domain_abstract ADD INDEX FKtechnical_303831 (technical_account_permission_id), ADD CONSTRAINT FKtechnical_303831 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE technical_account_permission_domain_abstract ADD INDEX FKtechnical_231219 (domain_abstract_id), ADD CONSTRAINT FKtechnical_231219 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE anonymous_share_entry ADD INDEX FKanonymous_621478 (entry_id), ADD CONSTRAINT FKanonymous_621478 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE document_entry ADD INDEX FKdocument_e19140 (entry_id), ADD CONSTRAINT FKdocument_e19140 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE share_entry ADD INDEX FKshare_entr50652 (entry_id), ADD CONSTRAINT FKshare_entr50652 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE thread_entry ADD INDEX FKthread_ent715634 (entry_id), ADD CONSTRAINT FKthread_ent715634 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE entry ADD INDEX FKentry500391 (owner_id), ADD CONSTRAINT FKentry500391 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE entry_tag_association ADD INDEX FKentry_tag_900675 (entry_id), ADD CONSTRAINT FKentry_tag_900675 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE entry_tag_association ADD INDEX FKentry_tag_30632 (tag_id), ADD CONSTRAINT FKentry_tag_30632 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_filter ADD INDEX FKtag_filter987269 (account_id), ADD CONSTRAINT FKtag_filter987269 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE tag_filter_rule_tag_association ADD INDEX FKtag_filter901563 (tag_id), ADD CONSTRAINT FKtag_filter901563 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE tag_filter_rule_tag_association ADD INDEX FKtag_filter565646 (enum_value_id), ADD CONSTRAINT FKtag_filter565646 FOREIGN KEY (enum_value_id) REFERENCES tag_enum_value (id);
ALTER TABLE entry_tag_association ADD INDEX FKentry_tag_305285 (enum_value_id), ADD CONSTRAINT FKentry_tag_305285 FOREIGN KEY (enum_value_id) REFERENCES tag_enum_value (id);
ALTER TABLE tag_filter_rule_tag_association ADD INDEX FKtag_filter766081 (tag_filter_rule_id), ADD CONSTRAINT FKtag_filter766081 FOREIGN KEY (tag_filter_rule_id) REFERENCES tag_filter_rule (id);
ALTER TABLE anonymous_url ADD INDEX FKanonymous_877695 (contact_id), ADD CONSTRAINT FKanonymous_877695 FOREIGN KEY (contact_id) REFERENCES contact (id);
ALTER TABLE signature ADD INDEX FKsignature417918 (owner_id), ADD CONSTRAINT FKsignature417918 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE technical_account_permission_account ADD INDEX FKtechnical_69967 (technical_account_permission_id), ADD CONSTRAINT FKtechnical_69967 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE technical_account_permission_account ADD INDEX FKtechnical_622557 (account_id), ADD CONSTRAINT FKtechnical_622557 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE account ADD INDEX FKaccount693567 (technical_account_permission_id), ADD CONSTRAINT FKaccount693567 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE default_view ADD INDEX FKdefault_vi37393 (view_context_id), ADD CONSTRAINT FKdefault_vi37393 FOREIGN KEY (view_context_id) REFERENCES view_context (id);
ALTER TABLE default_view ADD INDEX FKdefault_vi755506 (view_id), ADD CONSTRAINT FKdefault_vi755506 FOREIGN KEY (view_id) REFERENCES views (id);
ALTER TABLE thread_entry ADD INDEX FKthread_ent140657 (document_id), ADD CONSTRAINT FKthread_ent140657 FOREIGN KEY (document_id) REFERENCES document (id);
ALTER TABLE view_tag_asso ADD INDEX FKview_tag_a660721 (views_id), ADD CONSTRAINT FKview_tag_a660721 FOREIGN KEY (views_id) REFERENCES views (id);
ALTER TABLE view_tag_asso ADD INDEX FKview_tag_a218567 (tag_id), ADD CONSTRAINT FKview_tag_a218567 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE thread_view ADD INDEX FKthread_vie68184 (thread_account_id), ADD CONSTRAINT FKthread_vie68184 FOREIGN KEY (thread_account_id) REFERENCES thread (account_id);
ALTER TABLE thread_view_asso ADD INDEX FKthread_vie285846 (thread_view_id), ADD CONSTRAINT FKthread_vie285846 FOREIGN KEY (thread_view_id) REFERENCES thread_view (id);
ALTER TABLE thread_view_asso ADD INDEX FKthread_vie896171 (tag_id), ADD CONSTRAINT FKthread_vie896171 FOREIGN KEY (tag_id) REFERENCES tag (id);
ALTER TABLE thread ADD INDEX FKthread957862 (thread_view_id), ADD CONSTRAINT FKthread957862 FOREIGN KEY (thread_view_id) REFERENCES thread_view (id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKMailingLis595962 FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);

CREATE UNIQUE INDEX account_lsuid_index
  ON account (ls_uuid);
CREATE INDEX allowed_mimetype_index
  ON allowed_mimetype (id);
CREATE INDEX cookie2
  ON cookie (identifier);
CREATE INDEX cookie_i
  ON cookie (cookie_id);
CREATE INDEX document_id_index
  ON document (id);
CREATE INDEX document_i
  ON document (uuid);
CREATE INDEX domain_abstract_id_index
  ON domain_abstract (identifier);
CREATE UNIQUE INDEX domain_abstract_i
  ON domain_abstract (user_provider_id);
CREATE INDEX domain_abstract_i2
  ON domain_abstract (id);
CREATE INDEX domain_access_policy_index
  ON domain_access_policy (id);
CREATE INDEX domain_access_rule_index
  ON domain_access_rule (id);
CREATE INDEX domain_pattern_index
  ON domain_pattern (identifier);
CREATE INDEX domain_policy_index
  ON domain_policy (id);
CREATE UNIQUE INDEX domain_policy_i
  ON domain_policy (domain_access_policy_id);
CREATE INDEX functionality_i
  ON functionality (id);
CREATE UNIQUE INDEX functionality_index
  ON functionality (policy_activation_id);
CREATE UNIQUE INDEX functionality_i2
  ON functionality (identifier, domain_id);
CREATE UNIQUE INDEX functionality_i3
  ON functionality (policy_configuration_id);
CREATE UNIQUE INDEX functionality_range_unit_index
  ON functionality_range_unit (unit_max_id);
CREATE UNIQUE INDEX functionality_range_unit_i
  ON functionality_range_unit (unit_min_id);
CREATE UNIQUE INDEX functionality_unit_index
  ON functionality_unit (unit_id);
CREATE UNIQUE INDEX functionality_unit_boolean_index
  ON functionality_unit_boolean (unit_id);
CREATE INDEX ldap_connection_index
  ON ldap_connection (identifier);
CREATE INDEX log_entry_i
  ON log_entry (actor_domain);
CREATE INDEX log_entry_i2
  ON log_entry (file_name);
CREATE INDEX log_entry_i3
  ON log_entry (actor_firstname);
CREATE INDEX log_entry_i4
  ON log_entry (actor_mail);
CREATE INDEX log_entry_i5
  ON log_entry (action_date);
CREATE INDEX log_entry_i6
  ON log_entry (log_action);
CREATE INDEX log_entry_i7
  ON log_entry (target_mail);
CREATE INDEX log_entry_i8
  ON log_entry (actor_lastname);
CREATE INDEX log_entry_i9
  ON log_entry (file_name);
CREATE INDEX log_entry_i10
  ON log_entry (id);
CREATE INDEX policy_index
  ON policy (id);
CREATE UNIQUE INDEX anonymous_url_i
  ON anonymous_url (url_path, uuid);
CREATE INDEX anonymous_url_i2
  ON anonymous_url (id);
CREATE INDEX signature_index
  ON signature (id);
CREATE UNIQUE INDEX signature_i
  ON signature (uuid);
CREATE INDEX unit_index
  ON unit (id);
CREATE INDEX user_provider_ldap_index
  ON user_provider_ldap (id);

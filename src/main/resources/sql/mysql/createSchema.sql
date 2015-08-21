SET storage_engine=INNODB;
CREATE TABLE account (
  id                              bigint(8) NOT NULL AUTO_INCREMENT,
  domain_id                       bigint(8) NOT NULL,
  technical_account_permission_id bigint(8),
  owner_id                        bigint(8),
  ls_uuid                         varchar(255) NOT NULL,
  creation_date                   datetime NOT NULL,
  modification_date               datetime NOT NULL,
  role_id                         int(4) NOT NULL,
  locale                          varchar(255) NOT NULL,
  external_mail_locale            varchar(255) NOT NULL,
  enable                          tinyint(1) NOT NULL,
  account_type                    int(4) NOT NULL,
  password                        varchar(255),
  destroyed                       tinyint(1) NOT NULL,
  purge_step                      varchar(255) DEFAULT 'IN_USE' NOT NULL,
  cmis_locale                     varchar(255) NOT NULL,
  CONSTRAINT account_pkey
    PRIMARY KEY (id),
  UNIQUE INDEX (ls_uuid),
  INDEX (account_type)) CHARACTER SET UTF8;
CREATE TABLE anonymous_share_entry (
  entry_id             bigint(8) NOT NULL,
  downloaded           bigint(8) NOT NULL,
  document_entry_id    bigint(8) NOT NULL,
  anonymous_url_id     bigint(8) NOT NULL,
  share_entry_group_id bigint(8),
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
  id              bigint(8) NOT NULL AUTO_INCREMENT,
  uuid            varchar(255) NOT NULL UNIQUE,
  creation_date   datetime NOT NULL,
  type            varchar(255) NOT NULL,
  `size`          bigint(8) NOT NULL,
  thmb_uuid       varchar(255),
  timestamp       blob,
  check_mime_type tinyint(1) DEFAULT false NOT NULL,
  sha1sum         varchar(255),
  sha256sum       varchar(255),
  CONSTRAINT linshare_document_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE document_entry (
  entry_id      bigint(8) NOT NULL,
  document_id   bigint(8) NOT NULL,
  ciphered      tinyint(1) NOT NULL,
  type          varchar(255) NOT NULL,
  `size`        bigint(8) NOT NULL,
  sha256sum     varchar(255) NOT NULL,
  has_thumbnail tinyint(1) NOT NULL,
  shared        bigint(8) NOT NULL,
  PRIMARY KEY (entry_id),
  CONSTRAINT `unique document entry`
    UNIQUE (entry_id, document_id)) CHARACTER SET UTF8;
CREATE TABLE domain_abstract (
  id                  bigint(8) NOT NULL AUTO_INCREMENT,
  type                int(4) NOT NULL,
  identifier          varchar(255) NOT NULL,
  label               varchar(255) NOT NULL,
  enable              tinyint(1) NOT NULL,
  template            tinyint(1) NOT NULL,
  description         text NOT NULL,
  default_role        int(4) NOT NULL,
  default_locale      varchar(255) NOT NULL,
  default_mail_locale varchar(255) NOT NULL,
  used_space          bigint(8) NOT NULL,
  auth_show_order     bigint(8) NOT NULL,
  domain_policy_id    bigint(8) NOT NULL,
  parent_id           bigint(8),
  mime_policy_id      bigint(8),
  mailconfig_id       bigint(8),
  user_provider_id    bigint(8),
  welcome_messages_id bigint(8),
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
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  name              varchar(255) NOT NULL,
  comment           text NOT NULL,
  expiration_date   datetime NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  meta_data         text,
  cmis_sync         tinyint(1) DEFAULT false NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE functionality (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  system                  tinyint(1) NOT NULL,
  identifier              varchar(255) NOT NULL,
  policy_activation_id    bigint(8) NOT NULL,
  policy_configuration_id bigint(8) NOT NULL,
  policy_delegation_id    bigint(8),
  domain_id               bigint(8) NOT NULL,
  param                   tinyint(1) DEFAULT false NOT NULL,
  parent_identifier       varchar(255),
  CONSTRAINT linshare_functionality_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE functionality_integer (
  functionality_id bigint(8) NOT NULL,
  integer_value    int(4),
  CONSTRAINT linshare_functionality_integer_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_string (
  functionality_id bigint(8) NOT NULL,
  string_value     varchar(255),
  CONSTRAINT linshare_functionality_string_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_unit (
  functionality_id bigint(8) NOT NULL,
  integer_value    int(4) NOT NULL,
  unit_id          bigint(8) NOT NULL,
  CONSTRAINT linshare_functionality_unit_pkey
    PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE ldap_connection (
  id                   bigint(8) NOT NULL AUTO_INCREMENT,
  uuid                 varchar(255) NOT NULL UNIQUE,
  label                varchar(255) NOT NULL,
  provider_url         varchar(255) NOT NULL,
  security_auth        varchar(255),
  security_principal   varchar(255),
  security_credentials varchar(255),
  creation_date        datetime NOT NULL,
  modification_date    datetime NOT NULL,
  CONSTRAINT linshare_ldap_connection_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE log_entry (
  id               bigint(8) NOT NULL AUTO_INCREMENT,
  entry_type       varchar(255) NOT NULL,
  action_date      datetime NOT NULL,
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
  expiration_date  datetime NULL,
  CONSTRAINT linshare_log_entry_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE policy (
  id             bigint(8) NOT NULL AUTO_INCREMENT,
  status         tinyint(1) NOT NULL,
  default_status tinyint(1) NOT NULL,
  policy         int(4) NOT NULL,
  system         tinyint(1) NOT NULL,
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
  entry_id             bigint(8) NOT NULL,
  document_entry_id    bigint(8) NOT NULL,
  downloaded           bigint(8) NOT NULL,
  recipient_id         bigint(8) NOT NULL,
  share_entry_group_id bigint(8),
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
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  type              varchar(255),
  `size`            bigint(8),
  cert_subject_dn   varchar(255),
  cert_issuer_dn    varchar(255),
  cert_not_after    datetime NULL,
  cert              text,
  sort_order        int(4),
  CONSTRAINT linshare_signature_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE ldap_attribute (
  id              bigint(8) NOT NULL AUTO_INCREMENT,
  attribute       varchar(255) NOT NULL,
  field           varchar(255) NOT NULL,
  sync            tinyint(1) NOT NULL,
  system          tinyint(1) NOT NULL,
  enable          tinyint(1) NOT NULL,
  completion      tinyint(1) NOT NULL,
  ldap_pattern_id bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE thread (
  account_id bigint(8) NOT NULL,
  name       varchar(255) NOT NULL,
  PRIMARY KEY (account_id)) CHARACTER SET UTF8;
CREATE TABLE thread_entry (
  entry_id      bigint(8) NOT NULL,
  document_id   bigint(8) NOT NULL,
  ciphered      tinyint(1) NOT NULL,
  type          varchar(255) NOT NULL,
  `size`        bigint(8) NOT NULL,
  sha256sum     varchar(255) NOT NULL,
  has_thumbnail tinyint(1) NOT NULL,
  PRIMARY KEY (entry_id),
  CONSTRAINT `unique thread entry`
    UNIQUE (entry_id, document_id)) CHARACTER SET UTF8;
CREATE TABLE thread_member (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  thread_id         bigint(8) NOT NULL,
  admin             tinyint(1) NOT NULL,
  can_upload        tinyint(1) NOT NULL,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
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
  not_after             datetime NULL,
  not_before            datetime NULL,
  can_upload            tinyint(1) NOT NULL,
  comment               text,
  restricted            tinyint(1),
  expiration_date       datetime NULL,
  ldap_uid              varchar(255),
  can_create_guest      tinyint(1) NOT NULL,
  inconsistent          tinyint(1) DEFAULT False,
  CONSTRAINT user_pkey
    PRIMARY KEY (account_id)) CHARACTER SET UTF8;
CREATE TABLE version (
  id      bigint(8) NOT NULL AUTO_INCREMENT,
  version varchar(255) NOT NULL UNIQUE,
  CONSTRAINT linshare_version_pkey
    PRIMARY KEY (id)) CHARACTER SET UTF8;
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
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  uuid              varchar(255) NOT NULL UNIQUE,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE contact (
  id   bigint(8) NOT NULL AUTO_INCREMENT,
  mail varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_notification (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  configuration_policy_id bigint(8) NOT NULL,
  domain_abstract_id      bigint(8) NOT NULL,
  activation_policy_id    bigint(8) NOT NULL,
  identifier              varchar(255) NOT NULL,
  system                  tinyint(1) NOT NULL,
  creation_date           datetime NOT NULL,
  modification_date       datetime NOT NULL,
  uuid                    varchar(255) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_config (
  id                  bigint(8) NOT NULL AUTO_INCREMENT,
  mail_layout_html_id bigint(8) NOT NULL,
  domain_abstract_id  bigint(8) NOT NULL,
  name                varchar(255) NOT NULL,
  visible             tinyint(1) NOT NULL,
  mail_layout_text_id bigint(8) NOT NULL,
  uuid                varchar(255) NOT NULL,
  creation_date       datetime NOT NULL,
  modification_date   datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_layout (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id bigint(8) NOT NULL,
  name               varchar(255) NOT NULL,
  visible            tinyint(1) NOT NULL,
  layout             text NOT NULL,
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  uuid               varchar(255) NOT NULL,
  plaintext          tinyint(1) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_footer (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id bigint(8) NOT NULL,
  name               varchar(255) NOT NULL,
  visible            tinyint(1) NOT NULL,
  language           int(4) NOT NULL,
  footer             text NOT NULL,
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  uuid               varchar(255) NOT NULL,
  plaintext          tinyint(1) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_footer_lang (
  id             bigint(8) NOT NULL AUTO_INCREMENT,
  mail_config_id bigint(8) NOT NULL,
  mail_footer_id bigint(8) NOT NULL,
  language       int(4) NOT NULL,
  uuid           varchar(255) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_content (
  id                  bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id  bigint(8) NOT NULL,
  name                varchar(255) NOT NULL,
  visible             tinyint(1) NOT NULL,
  mail_content_type   int(4) NOT NULL,
  language            int(4) NOT NULL,
  subject             text NOT NULL,
  greetings           text NOT NULL,
  body                text NOT NULL,
  uuid                varchar(255) NOT NULL,
  plaintext           tinyint(1) NOT NULL,
  creation_date       datetime NOT NULL,
  modification_date   datetime NOT NULL,
  alternative_subject text,
  enable_as           tinyint(1) DEFAULT False NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_content_lang (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  language          int(4) NOT NULL,
  mail_content_id   bigint(8) NOT NULL,
  mail_config_id    bigint(8) NOT NULL,
  mail_content_type int(4) NOT NULL,
  uuid              varchar(255) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_request (
  id                              bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id              bigint(8) NOT NULL,
  account_id                      bigint(8) NOT NULL,
  upload_request_group_id         bigint(8) NOT NULL,
  uuid                            varchar(255) NOT NULL UNIQUE,
  max_file                        int(4),
  max_deposit_size                bigint(8),
  max_file_size                   bigint(8),
  status                          varchar(255) NOT NULL,
  activation_date                 datetime NOT NULL,
  creation_date                   datetime NOT NULL,
  modification_date               datetime NOT NULL,
  notification_date               datetime NULL,
  expiry_date                     datetime NULL,
  upload_proposition_request_uuid varchar(255),
  can_delete                      tinyint(1) NOT NULL,
  can_close                       tinyint(1) NOT NULL,
  can_edit_expiry_date            tinyint(1) NOT NULL,
  locale                          varchar(255) NOT NULL,
  secured                         tinyint(1) NOT NULL,
  mail_message_id                 varchar(255),
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_request_url (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  contact_id        bigint(8) NOT NULL,
  upload_request_id bigint(8) NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  path              varchar(255) NOT NULL,
  password          varchar(255),
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_request_group (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  subject           text NOT NULL,
  body              text NOT NULL,
  uuid              varchar(255) NOT NULL,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_request_history (
  id                              bigint(8) NOT NULL AUTO_INCREMENT,
  upload_request_id               bigint(8) NOT NULL,
  status                          varchar(255) NOT NULL,
  status_updated                  tinyint(1) NOT NULL,
  event_type                      varchar(255) NOT NULL,
  uuid                            varchar(255) NOT NULL UNIQUE,
  activation_date                 datetime NOT NULL,
  expiry_date                     datetime NULL,
  notification_date               datetime NULL,
  max_deposit_size                bigint(8),
  max_file_count                  int(4),
  max_file_size                   bigint(8),
  upload_proposition_request_uuid varchar(255),
  can_delete                      tinyint(1) NOT NULL,
  can_close                       tinyint(1) NOT NULL,
  can_edit_expiry_date            tinyint(1) NOT NULL,
  locale                          varchar(255) NOT NULL,
  secured                         tinyint(1) NOT NULL,
  creation_date                   datetime NOT NULL,
  modification_date               datetime NOT NULL,
  mail_message_id                 varchar(255),
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_request_entry (
  entry_id                bigint(8) NOT NULL,
  document_entry_entry_id bigint(8),
  upload_request_id       bigint(8) NOT NULL,
  `size`                  bigint(8) NOT NULL,
  PRIMARY KEY (entry_id)) CHARACTER SET UTF8;
CREATE TABLE upload_proposition_filter (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id bigint(8) NOT NULL,
  uuid               varchar(255) NOT NULL,
  name               varchar(255) NOT NULL,
  `match`            varchar(255) NOT NULL,
  enable             tinyint(1) NOT NULL,
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  sort_order         int(4) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_proposition_rule (
  id                           bigint(8) NOT NULL AUTO_INCREMENT,
  uuid                         varchar(255) NOT NULL,
  upload_proposition_filter_id bigint(8) NOT NULL,
  operator                     varchar(255) NOT NULL,
  field                        varchar(255) NOT NULL,
  value                        varchar(255),
  creation_date                datetime NOT NULL,
  modification_date            datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_proposition_action (
  id                           bigint(8) NOT NULL AUTO_INCREMENT,
  uuid                         varchar(255) NOT NULL,
  upload_proposition_filter_id bigint(8) NOT NULL,
  action_type                  varchar(255) NOT NULL,
  data                         text,
  creation_date                datetime NOT NULL,
  modification_date            datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE statistic_event (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  event_type        int(4) NOT NULL,
  event_action      int(4) NOT NULL,
  event_source      int(4) NOT NULL,
  event_actor_type  int(4) NOT NULL,
  action_count      bigint(8),
  description       text,
  transfer_size     bigint(8),
  transfer_duration bigint(8),
  transfer_rate     bigint(8),
  recipient_count   bigint(8),
  document_count    bigint(8),
  secured           tinyint(1),
  daily             int(10),
  creation_date     datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mailing_list (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  domain_abstract_id bigint(8) NOT NULL,
  user_id            bigint(8) NOT NULL,
  is_public          tinyint(1) NOT NULL,
  identifier         varchar(255) NOT NULL,
  description        text,
  uuid               varchar(255) NOT NULL,
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  PRIMARY KEY (id),
  INDEX (uuid)) CHARACTER SET UTF8;
CREATE TABLE mailing_list_contact (
  id                         bigint(8) NOT NULL AUTO_INCREMENT,
  mailing_list_id            bigint(8) NOT NULL,
  mail                       varchar(255) NOT NULL,
  first_name                 varchar(255),
  last_name                  varchar(255),
  uuid                       varchar(255) NOT NULL,
  creation_date              datetime NOT NULL,
  modification_date          datetime NOT NULL,
  mailing_list_contact_index int(4) NOT NULL,
  PRIMARY KEY (id),
  INDEX (uuid)) CHARACTER SET UTF8;
CREATE TABLE upload_request_template (
  id                         bigint(8) NOT NULL AUTO_INCREMENT,
  uuid                       varchar(255) NOT NULL,
  account_id                 bigint(8) NOT NULL,
  name                       varchar(255) NOT NULL,
  description                varchar(255),
  duration_before_activation bigint(8),
  unit_before_activation     bigint(8),
  duration_before_expiry     bigint(8),
  unit_before_expiry         bigint(8),
  group_mode                 tinyint(1),
  deposit_mode               tinyint(1),
  max_file                   bigint(8),
  max_file_size              bigint(8),
  max_deposit_size           bigint(8),
  locale                     varchar(255),
  secured                    tinyint(1),
  day_before_notification    bigint(8),
  prolongation_mode          tinyint(1),
  creation_date              datetime NOT NULL,
  modification_date          datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE upload_proposition (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  uuid               varchar(255) NOT NULL,
  domain_abstract_id bigint(8) NOT NULL,
  status             varchar(255) NOT NULL,
  subject            varchar(255) NOT NULL,
  body               text,
  mail               varchar(255) NOT NULL,
  first_name         varchar(255) NOT NULL,
  last_name          varchar(255) NOT NULL,
  domain_source      varchar(255),
  recipient_mail     varchar(255) NOT NULL,
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mime_policy (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  domain_id         bigint(8) NOT NULL,
  uuid              varchar(255) NOT NULL,
  name              varchar(255) NOT NULL,
  mode              int(10) NOT NULL,
  displayable       int(10) NOT NULL,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  version           int(4) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mime_type (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  mime_policy_id    bigint(8) NOT NULL,
  uuid              varchar(255) NOT NULL,
  mime_type         varchar(255) NOT NULL,
  extensions        varchar(255) NOT NULL,
  enable            tinyint(1) NOT NULL,
  displayable       tinyint(1) NOT NULL,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unicity_type_and_policy
    UNIQUE (mime_policy_id, mime_type)) CHARACTER SET UTF8;
CREATE TABLE account_permission (
  id                              bigint(8) NOT NULL AUTO_INCREMENT,
  technical_account_permission_id bigint(8) NOT NULL,
  permission                      varchar(255) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE functionality_enum_lang (
  functionality_id bigint(8) NOT NULL,
  lang_value       varchar(255) NOT NULL,
  PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE functionality_boolean (
  functionality_id bigint(8) NOT NULL,
  boolean_value    tinyint(1) NOT NULL,
  PRIMARY KEY (functionality_id)) CHARACTER SET UTF8;
CREATE TABLE upload_request_entry_url (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  upload_request_entry_id bigint(8) NOT NULL,
  uuid                    varchar(255) NOT NULL,
  path                    varchar(255) NOT NULL,
  password                varchar(255),
  creation_date           datetime NOT NULL,
  modification_date       datetime NOT NULL,
  expiry_date             datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE contact_provider (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  uuid               varchar(255) NOT NULL UNIQUE,
  provider_type      varchar(255) NOT NULL,
  base_dn            varchar(255),
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  domain_abstract_id bigint(8) NOT NULL,
  ldap_pattern_id    bigint(8) NOT NULL,
  ldap_connection_id bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE user_provider (
  id                 bigint(8) NOT NULL AUTO_INCREMENT,
  uuid               varchar(255) NOT NULL UNIQUE,
  provider_type      varchar(255) NOT NULL,
  base_dn            varchar(255),
  creation_date      datetime NOT NULL,
  modification_date  datetime NOT NULL,
  ldap_connection_id bigint(8) NOT NULL,
  ldap_pattern_id    bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE ldap_pattern (
  id                                           bigint(8) NOT NULL AUTO_INCREMENT,
  uuid                                         varchar(255) NOT NULL UNIQUE,
  pattern_type                                 varchar(255) NOT NULL,
  label                                        varchar(255) NOT NULL,
  system                                       tinyint(1) NOT NULL,
  description                                  text NOT NULL,
  auth_command                                 text,
  search_user_command                          text,
  search_page_size                             int(4),
  search_size_limit                            int(4),
  auto_complete_command_on_first_and_last_name text,
  auto_complete_command_on_all_attributes      text,
  completion_page_size                         int(4),
  completion_size_limit                        int(4),
  creation_date                                datetime NOT NULL,
  modification_date                            datetime NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE welcome_messages (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  uuid              varchar(255) NOT NULL,
  name              varchar(255) NOT NULL,
  description       text NOT NULL,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  domain_id         bigint(8) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX (uuid)) CHARACTER SET UTF8;
CREATE TABLE welcome_messages_entry (
  id                  bigint(8) NOT NULL AUTO_INCREMENT,
  lang                varchar(255) NOT NULL,
  value               varchar(255) NOT NULL,
  welcome_messages_id bigint(8) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE share_entry_group (
  id                bigint(8) NOT NULL AUTO_INCREMENT,
  account_id        bigint(8) NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  subject           text,
  notification_date datetime NULL,
  creation_date     datetime NOT NULL,
  modification_date datetime NOT NULL,
  expiration_date   datetime NULL,
  notified          tinyint(1) DEFAULT false NOT NULL,
  processed         tinyint(1) DEFAULT false NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE TABLE mail_activation (
  id                      bigint(8) NOT NULL AUTO_INCREMENT,
  identifier              varchar(255) NOT NULL,
  system                  tinyint(1) NOT NULL,
  policy_activation_id    bigint(8) NOT NULL,
  policy_configuration_id bigint(8) NOT NULL,
  policy_delegation_id    bigint(8) NOT NULL,
  domain_id               bigint(8) NOT NULL,
  enable                  tinyint(1) NOT NULL,
  PRIMARY KEY (id)) CHARACTER SET UTF8;
CREATE UNIQUE INDEX account_lsuid_index
  ON account (ls_uuid);
CREATE INDEX account_account_type
  ON account (account_type);
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
CREATE INDEX domain_abstract_i2
  ON domain_abstract (id);
CREATE INDEX domain_access_policy_index
  ON domain_access_policy (id);
CREATE INDEX domain_access_rule_index
  ON domain_access_rule (id);
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
CREATE INDEX functionality_integer_index
  ON functionality_integer (functionality_id);
CREATE INDEX functionality_string_index
  ON functionality_string (functionality_id);
CREATE UNIQUE INDEX functionality_unit_index
  ON functionality_unit (unit_id);
CREATE INDEX functionality_unit_i
  ON functionality_unit (functionality_id);
CREATE INDEX ldap_connection_index
  ON ldap_connection (label);
CREATE INDEX ldap_connection_i
  ON ldap_connection (id);
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
CREATE INDEX share_expiry_rules_index
  ON share_expiry_rules (domain_id);
CREATE INDEX signature_index
  ON signature (id);
CREATE UNIQUE INDEX signature_i
  ON signature (uuid);
CREATE INDEX unit_index
  ON unit (id);
  CREATE UNIQUE INDEX welcome_messages_uuid 
  ON welcome_messages (uuid);
ALTER TABLE domain_abstract ADD INDEX fk449bc2ec59e1e332 (domain_policy_id), ADD CONSTRAINT fk449bc2ec59e1e332 FOREIGN KEY (domain_policy_id) REFERENCES domain_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD INDEX fk449bc2ec9083e725 (parent_id), ADD CONSTRAINT fk449bc2ec9083e725 FOREIGN KEY (parent_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD INDEX fkf75719ed3c036ccb (domain_id), ADD CONSTRAINT fkf75719ed3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD INDEX fkf75719ed85924e31 (domain_access_policy_id), ADD CONSTRAINT fkf75719ed85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_policy ADD INDEX fk49c9a27c85924e31 (domain_access_policy_id), ADD CONSTRAINT fk49c9a27c85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD INDEX fk7430c53a58fe5398 (policy_activation_id), ADD CONSTRAINT fk7430c53a58fe5398 FOREIGN KEY (policy_activation_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD INDEX fk7430c53a71796372 (policy_configuration_id), ADD CONSTRAINT fk7430c53a71796372 FOREIGN KEY (policy_configuration_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD INDEX fk7430c53a3c036ccb (domain_id), ADD CONSTRAINT fk7430c53a3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_integer ADD INDEX fk8662133910439d2b (functionality_id), ADD CONSTRAINT fk8662133910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_string ADD INDEX fkb2a122b610439d2b (functionality_id), ADD CONSTRAINT fkb2a122b610439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD INDEX fk3ced016910439d2b (functionality_id), ADD CONSTRAINT fk3ced016910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD INDEX fk3ced0169f329e0c9 (unit_id), ADD CONSTRAINT fk3ced0169f329e0c9 FOREIGN KEY (unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE share_expiry_rules ADD INDEX fkfda1673c3c036ccb (domain_id), ADD CONSTRAINT fkfda1673c3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE signature ADD INDEX fk81c9a1a7c0bbd6f (document_id), ADD CONSTRAINT fk81c9a1a7c0bbd6f FOREIGN KEY (document_id) REFERENCES document (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE thread ADD INDEX inheritance_account_thread (account_id), ADD CONSTRAINT inheritance_account_thread FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE document_entry ADD INDEX FKdocument_e594117 (document_id), ADD CONSTRAINT FKdocument_e594117 FOREIGN KEY (document_id) REFERENCES document (id);
ALTER TABLE share_entry ADD INDEX FKshare_entr708932 (document_entry_id), ADD CONSTRAINT FKshare_entr708932 FOREIGN KEY (document_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE anonymous_share_entry ADD INDEX FKanonymous_138106 (document_entry_id), ADD CONSTRAINT FKanonymous_138106 FOREIGN KEY (document_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE recipient_favourite ADD INDEX FKrecipient_90791 (user_id), ADD CONSTRAINT FKrecipient_90791 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE anonymous_share_entry ADD INDEX FKanonymous_732508 (anonymous_url_id), ADD CONSTRAINT FKanonymous_732508 FOREIGN KEY (anonymous_url_id) REFERENCES anonymous_url (id);
ALTER TABLE share_entry ADD INDEX FKshare_entr87036 (recipient_id), ADD CONSTRAINT FKshare_entr87036 FOREIGN KEY (recipient_id) REFERENCES account (id);
ALTER TABLE account ADD INDEX FKaccount400616 (domain_id), ADD CONSTRAINT FKaccount400616 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
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
ALTER TABLE anonymous_url ADD INDEX FKanonymous_877695 (contact_id), ADD CONSTRAINT FKanonymous_877695 FOREIGN KEY (contact_id) REFERENCES contact (id);
ALTER TABLE signature ADD INDEX FKsignature417918 (owner_id), ADD CONSTRAINT FKsignature417918 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE account ADD INDEX FKaccount693567 (technical_account_permission_id), ADD CONSTRAINT FKaccount693567 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE thread_entry ADD INDEX FKthread_ent140657 (document_id), ADD CONSTRAINT FKthread_ent140657 FOREIGN KEY (document_id) REFERENCES document (id);
ALTER TABLE mail_notification ADD INDEX FKmail_notif244118 (activation_policy_id), ADD CONSTRAINT FKmail_notif244118 FOREIGN KEY (activation_policy_id) REFERENCES policy (id);
ALTER TABLE mail_notification ADD INDEX FKmail_notif777760 (domain_abstract_id), ADD CONSTRAINT FKmail_notif777760 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_layout ADD INDEX FKmail_layou627738 (domain_abstract_id), ADD CONSTRAINT FKmail_layou627738 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer ADD INDEX FKmail_foote767112 (domain_abstract_id), ADD CONSTRAINT FKmail_foote767112 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD INDEX FKmail_foote801249 (mail_footer_id), ADD CONSTRAINT FKmail_foote801249 FOREIGN KEY (mail_footer_id) REFERENCES mail_footer (id);
ALTER TABLE domain_abstract ADD INDEX FKdomain_abs160138 (mailconfig_id), ADD CONSTRAINT FKdomain_abs160138 FOREIGN KEY (mailconfig_id) REFERENCES mail_config (id);
ALTER TABLE mail_config ADD INDEX FKmail_confi697783 (domain_abstract_id), ADD CONSTRAINT FKmail_confi697783 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD INDEX FKmail_foote321102 (mail_config_id), ADD CONSTRAINT FKmail_foote321102 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);
ALTER TABLE mail_content ADD INDEX FKmail_conte385227 (domain_abstract_id), ADD CONSTRAINT FKmail_conte385227 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_content_lang ADD INDEX FKmail_conte910199 (mail_config_id), ADD CONSTRAINT FKmail_conte910199 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);
ALTER TABLE mail_content_lang ADD INDEX FKmail_conte33952 (mail_content_id), ADD CONSTRAINT FKmail_conte33952 FOREIGN KEY (mail_content_id) REFERENCES mail_content (id);
ALTER TABLE mail_config ADD INDEX FKmail_confi541299 (mail_layout_html_id), ADD CONSTRAINT FKmail_confi541299 FOREIGN KEY (mail_layout_html_id) REFERENCES mail_layout (id);
ALTER TABLE mail_config ADD INDEX FKmail_confi612314 (mail_layout_text_id), ADD CONSTRAINT FKmail_confi612314 FOREIGN KEY (mail_layout_text_id) REFERENCES mail_layout (id);
ALTER TABLE upload_request_url ADD INDEX FKupload_req833645 (upload_request_id), ADD CONSTRAINT FKupload_req833645 FOREIGN KEY (upload_request_id) REFERENCES upload_request (id);
ALTER TABLE upload_request ADD INDEX FKupload_req916400 (upload_request_group_id), ADD CONSTRAINT FKupload_req916400 FOREIGN KEY (upload_request_group_id) REFERENCES upload_request_group (id);
ALTER TABLE upload_request_url ADD INDEX FKupload_req601912 (contact_id), ADD CONSTRAINT FKupload_req601912 FOREIGN KEY (contact_id) REFERENCES contact (id);
ALTER TABLE upload_request_history ADD INDEX FKupload_req678768 (upload_request_id), ADD CONSTRAINT FKupload_req678768 FOREIGN KEY (upload_request_id) REFERENCES upload_request (id);
ALTER TABLE upload_request_entry ADD INDEX FKupload_req220981 (upload_request_id), ADD CONSTRAINT FKupload_req220981 FOREIGN KEY (upload_request_id) REFERENCES upload_request (id);
ALTER TABLE upload_request_entry ADD INDEX FKupload_req254795 (entry_id), ADD CONSTRAINT FKupload_req254795 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE upload_request_entry ADD INDEX FKupload_req11781 (document_entry_entry_id), ADD CONSTRAINT FKupload_req11781 FOREIGN KEY (document_entry_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE upload_proposition_rule ADD INDEX FKupload_pro672390 (upload_proposition_filter_id), ADD CONSTRAINT FKupload_pro672390 FOREIGN KEY (upload_proposition_filter_id) REFERENCES upload_proposition_filter (id);
ALTER TABLE upload_proposition_action ADD INDEX FKupload_pro841666 (upload_proposition_filter_id), ADD CONSTRAINT FKupload_pro841666 FOREIGN KEY (upload_proposition_filter_id) REFERENCES upload_proposition_filter (id);
ALTER TABLE functionality ADD INDEX FKfunctional788903 (policy_delegation_id), ADD CONSTRAINT FKfunctional788903 FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);
ALTER TABLE mailing_list ADD INDEX FKmailing_li478123 (user_id), ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD INDEX FKmailing_li335663 (domain_abstract_id), ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD INDEX FKMailingLis595962 (mailing_list_id), ADD CONSTRAINT FKMailingLis595962 FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);
ALTER TABLE upload_request_template ADD INDEX FKupload_req618325 (account_id), ADD CONSTRAINT FKupload_req618325 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE mail_notification ADD INDEX FKmail_notif791766 (configuration_policy_id), ADD CONSTRAINT FKmail_notif791766 FOREIGN KEY (configuration_policy_id) REFERENCES policy (id);
ALTER TABLE upload_proposition ADD INDEX FKupload_pro226633 (domain_abstract_id), ADD CONSTRAINT FKupload_pro226633 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mime_type ADD INDEX FKmime_type145742 (mime_policy_id), ADD CONSTRAINT FKmime_type145742 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);
ALTER TABLE mime_policy ADD INDEX FKmime_polic613419 (domain_id), ADD CONSTRAINT FKmime_polic613419 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE domain_abstract ADD INDEX FKdomain_abs809928 (mime_policy_id), ADD CONSTRAINT FKdomain_abs809928 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);
ALTER TABLE account_permission ADD INDEX FKaccount_pe759382 (technical_account_permission_id), ADD CONSTRAINT FKaccount_pe759382 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE upload_request ADD INDEX FKupload_req220337 (account_id), ADD CONSTRAINT FKupload_req220337 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE upload_request ADD INDEX FKupload_req840249 (domain_abstract_id), ADD CONSTRAINT FKupload_req840249 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE upload_proposition_filter ADD INDEX FKupload_pro316142 (domain_abstract_id), ADD CONSTRAINT FKupload_pro316142 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE functionality_enum_lang ADD INDEX FKfunctional140416 (functionality_id), ADD CONSTRAINT FKfunctional140416 FOREIGN KEY (functionality_id) REFERENCES functionality (id);
ALTER TABLE functionality_boolean ADD INDEX FKfunctional171577 (functionality_id), ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);
ALTER TABLE upload_request_entry_url ADD INDEX FKupload_req784409 (upload_request_entry_id), ADD CONSTRAINT FKupload_req784409 FOREIGN KEY (upload_request_entry_id) REFERENCES upload_request_entry (entry_id);
ALTER TABLE contact_provider ADD INDEX FKcontact_p266833 (ldap_connection_id), ADD CONSTRAINT FKcontact_p266833 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE contact_provider ADD INDEX FKcontact_p706697 (domain_abstract_id), ADD CONSTRAINT FKcontact_p706697 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE domain_abstract ADD INDEX FKdomain_abs163989 (user_provider_id), ADD CONSTRAINT FKdomain_abs163989 FOREIGN KEY (user_provider_id) REFERENCES user_provider (id);
ALTER TABLE user_provider ADD INDEX FKuser_provi1640 (ldap_connection_id), ADD CONSTRAINT FKuser_provi1640 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE contact_provider ADD INDEX FKcontact_p455269 (ldap_pattern_id), ADD CONSTRAINT FKcontact_p455269 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE ldap_attribute ADD INDEX FKldap_attri49928 (ldap_pattern_id), ADD CONSTRAINT FKldap_attri49928 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE user_provider ADD INDEX FKuser_provi813203 (ldap_pattern_id), ADD CONSTRAINT FKuser_provi813203 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);

ALTER TABLE welcome_messages_entry ADD INDEX FKwelcome_me856948 (welcome_messages_id), ADD CONSTRAINT FKwelcome_me856948 FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD INDEX use_customisation (welcome_messages_id), ADD CONSTRAINT use_customisation FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE welcome_messages ADD INDEX own_welcome_messages (domain_id), ADD CONSTRAINT own_welcome_messages FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE anonymous_share_entry ADD INDEX FKanonymous_708340 (share_entry_group_id), ADD CONSTRAINT FKanonymous_708340 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE share_entry ADD INDEX FKshare_entr137514 (share_entry_group_id), ADD CONSTRAINT FKshare_entr137514 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE mail_activation ADD INDEX FKmail_activ188698 (domain_id), ADD CONSTRAINT FKmail_activ188698 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_activation ADD INDEX activation (policy_activation_id), ADD CONSTRAINT activation FOREIGN KEY (policy_activation_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD INDEX configuration (policy_configuration_id), ADD CONSTRAINT configuration FOREIGN KEY (policy_configuration_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD INDEX delegation (policy_delegation_id), ADD CONSTRAINT delegation FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);
ALTER TABLE share_entry_group ADD INDEX shareEntryGroup (account_id), ADD CONSTRAINT shareEntryGroup FOREIGN KEY (account_id) REFERENCES account (id);

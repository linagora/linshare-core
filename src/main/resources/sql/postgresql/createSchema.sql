SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

CREATE SEQUENCE hibernate_sequence INCREMENT BY 1 NO MINVALUE NO MAXVALUE START WITH 1 CACHE 1;
CREATE TABLE account (
  id                               int8 NOT NULL,
  domain_id                       int8 NOT NULL,
  technical_account_permission_id int8,
  owner_id                        int8,
  ls_uuid                         varchar(255) NOT NULL,
  mail                            varchar(255) NOT NULL,
  creation_date                   timestamp(6) NOT NULL,
  modification_date               timestamp(6) NOT NULL,
  role_id                         int4 NOT NULL,
  locale                          varchar(255) NOT NULL,
  external_mail_locale            varchar(255) NOT NULL,
  enable                          bool NOT NULL,
  account_type                    int4 NOT NULL,
  password                        varchar(255),
  destroyed                       int8 NOT NULL,
  purge_step                      varchar(255) DEFAULT 'IN_USE' NOT NULL,
  cmis_locale                     varchar(255) NOT NULL,
  CONSTRAINT account_pkey
    PRIMARY KEY (id),
  CONSTRAINT account_unique_mail_domain_destroyed
    UNIQUE (domain_id, mail, destroyed));
CREATE TABLE anonymous_share_entry (
  entry_id             int8 NOT NULL,
  downloaded           int8 NOT NULL,
  document_entry_id    int8 NOT NULL,
  anonymous_url_id     int8 NOT NULL,
  share_entry_group_id int8,
  PRIMARY KEY (entry_id));
CREATE TABLE cookie (
  cookie_id   int8 NOT NULL,
  identifier varchar(255) NOT NULL,
  user_name  varchar(255) NOT NULL,
  value      varchar(255) NOT NULL,
  last_use   timestamp NOT NULL,
  CONSTRAINT linshare_cookie_pkey
    PRIMARY KEY (cookie_id));
CREATE TABLE document (
  id               int8 NOT NULL,
  uuid            varchar(255) NOT NULL UNIQUE,
  bucket_uuid     varchar(255),
  creation_date   timestamp NOT NULL,
  type            varchar(255) NOT NULL,
  ls_size         int8 NOT NULL,
  thmb_uuid       varchar(255),
  timestamp       bytea,
  check_mime_type bool DEFAULT 'false' NOT NULL,
  sha1sum         varchar(255),
  sha256sum       varchar(255),
  to_upgrade      bool DEFAULT 'false' NOT NULL,
  CONSTRAINT linshare_document_pkey
    PRIMARY KEY (id));
CREATE TABLE document_entry (
  entry_id      int8 NOT NULL,
  document_id   int8 NOT NULL,
  ciphered      bool NOT NULL,
  type          varchar(255) NOT NULL,
  ls_size       int8 NOT NULL,
  sha256sum     varchar(255) NOT NULL,
  has_thumbnail bool NOT NULL,
  shared        int8 NOT NULL,
  PRIMARY KEY (entry_id),
  CONSTRAINT "unique document entry"
    UNIQUE (entry_id, document_id));
CREATE TABLE domain_abstract (
  id                   int8 NOT NULL,
  type                int4 NOT NULL,
  uuid                varchar(255) NOT NULL,
  label               varchar(255) NOT NULL,
  enable              bool NOT NULL,
  template            bool NOT NULL,
  description         text NOT NULL,
  default_role        int4 NOT NULL,
  default_locale      varchar(255) NOT NULL,
  default_mail_locale varchar(255) NOT NULL,
  auth_show_order     int8 NOT NULL,
  domain_policy_id    int8 NOT NULL,
  parent_id           int8,
  mime_policy_id      int8,
  mailconfig_id       int8,
  user_provider_id    int8,
  welcome_messages_id int8,
  CONSTRAINT linshare_domain_abstract_pkey
    PRIMARY KEY (id));
CREATE TABLE domain_access_policy (
  id  int8 NOT NULL,
  CONSTRAINT linshare_domain_access_policy_pkey
    PRIMARY KEY (id));
CREATE TABLE domain_access_rule (
  id                       int8 NOT NULL,
  domain_access_rule_type int4 NOT NULL,
  ls_regexp               varchar(255),
  domain_id               int8,
  domain_access_policy_id int8 NOT NULL,
  rule_index              int4,
  CONSTRAINT linshare_domain_access_rule_pkey
    PRIMARY KEY (id));
CREATE TABLE domain_policy (
  id                       int8 NOT NULL,
  description             text,
  uuid                    varchar(255) NOT NULL UNIQUE,
  label                   varchar(255) NOT NULL,
  domain_access_policy_id int8,
  CONSTRAINT linshare_domain_policy_pkey
    PRIMARY KEY (id));
CREATE TABLE entry (
  id                 int8 NOT NULL,
  owner_id          int8 NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  name              varchar(255) NOT NULL,
  comment           text NOT NULL,
  expiration_date   timestamp,
  uuid              varchar(255) NOT NULL UNIQUE,
  meta_data         text,
  cmis_sync         bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE functionality (
  id                       int8 NOT NULL,
  system                  bool NOT NULL,
  identifier              varchar(255) NOT NULL,
  policy_activation_id    int8 NOT NULL,
  policy_configuration_id int8 NOT NULL,
  policy_delegation_id    int8,
  domain_id               int8 NOT NULL,
  param                   bool DEFAULT 'false' NOT NULL,
  parent_identifier       varchar(255),
  CONSTRAINT linshare_functionality_pkey
    PRIMARY KEY (id));
CREATE TABLE functionality_integer (
  functionality_id int8 NOT NULL,
  integer_value    int4,
  CONSTRAINT linshare_functionality_integer_pkey
    PRIMARY KEY (functionality_id));
CREATE TABLE functionality_string (
  functionality_id int8 NOT NULL,
  string_value     varchar(255),
  CONSTRAINT linshare_functionality_string_pkey
    PRIMARY KEY (functionality_id));
CREATE TABLE functionality_unit (
  functionality_id int8 NOT NULL,
  integer_value    int4 NOT NULL,
  unit_id          int8 NOT NULL,
  CONSTRAINT linshare_functionality_unit_pkey
    PRIMARY KEY (functionality_id));
CREATE TABLE ldap_connection (
  id                    int8 NOT NULL,
  uuid                 varchar(255) NOT NULL UNIQUE,
  label                varchar(255) NOT NULL,
  provider_url         varchar(255) NOT NULL,
  security_auth        varchar(255),
  security_principal   varchar(255),
  security_credentials varchar(255),
  creation_date        timestamp NOT NULL,
  modification_date    timestamp NOT NULL,
  CONSTRAINT linshare_ldap_connection_pkey
    PRIMARY KEY (id));
CREATE TABLE log_entry (
  id                int8 NOT NULL,
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
  file_size        int8,
  target_mail      varchar(255),
  target_firstname varchar(255),
  target_lastname  varchar(255),
  target_domain    varchar(255),
  expiration_date  timestamp,
  CONSTRAINT linshare_log_entry_pkey
    PRIMARY KEY (id));
CREATE TABLE policy (
  id              int8 NOT NULL,
  status         bool NOT NULL,
  default_status bool NOT NULL,
  policy         int4 NOT NULL,
  system         bool NOT NULL,
  CONSTRAINT linshare_policy_pkey
    PRIMARY KEY (id));
CREATE TABLE recipient_favourite (
  id              int8 NOT NULL,
  recipient_mail varchar(255) NOT NULL,
  weight         int8 NOT NULL,
  user_id        int8 NOT NULL,
  CONSTRAINT linshare_recipient_favourite_pkey
    PRIMARY KEY (id));
CREATE TABLE anonymous_url (
  id          int8 NOT NULL,
  url_path   varchar(255) NOT NULL,
  uuid       varchar(255) NOT NULL UNIQUE,
  password   varchar(255),
  contact_id int8 NOT NULL,
  CONSTRAINT linshare_secured_url_pkey
    PRIMARY KEY (id));
CREATE TABLE share_entry (
  entry_id             int8 NOT NULL,
  document_entry_id    int8 NOT NULL,
  downloaded           int8 NOT NULL,
  recipient_id         int8 NOT NULL,
  share_entry_group_id int8,
  PRIMARY KEY (entry_id));
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
  id                 int8 NOT NULL,
  owner_id          int8,
  document_id       int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  name              varchar(255) NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  type              varchar(255),
  ls_size           int8,
  cert_subject_dn   varchar(255),
  cert_issuer_dn    varchar(255),
  cert_not_after    timestamp,
  cert              text,
  sort_order        int4,
  CONSTRAINT linshare_signature_pkey
    PRIMARY KEY (id));
CREATE TABLE ldap_attribute (
  id               int8 NOT NULL,
  attribute       varchar(255) NOT NULL,
  field           varchar(255) NOT NULL,
  sync            bool NOT NULL,
  system          bool NOT NULL,
  enable          bool NOT NULL,
  completion      bool NOT NULL,
  ldap_pattern_id int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE thread (
  account_id int8 NOT NULL,
  name       varchar(255) NOT NULL,
  to_upgrade bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (account_id));
CREATE TABLE thread_entry (
  entry_id      int8 NOT NULL,
  document_id   int8 NOT NULL,
  ciphered      bool NOT NULL,
  type          varchar(255) NOT NULL,
  ls_size       int8 NOT NULL,
  sha256sum     varchar(255) NOT NULL,
  has_thumbnail bool NOT NULL,
  PRIMARY KEY (entry_id),
  CONSTRAINT "unique thread entry"
    UNIQUE (entry_id, document_id));
CREATE TABLE thread_member (
  id                 int8 NOT NULL,
  thread_id         int8 NOT NULL,
  admin             bool NOT NULL,
  can_upload        bool NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  user_id           int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE unit (
  id          int8 NOT NULL,
  unit_type  int4 NOT NULL,
  unit_value int4 NOT NULL,
  CONSTRAINT linshare_unit_pkey
    PRIMARY KEY (id));
CREATE TABLE users (
  account_id            int8 NOT NULL,
  first_name            varchar(255),
  last_name             varchar(255),
  encipherment_key_pass bytea,
  not_after             timestamp(6),
  not_before            timestamp(6),
  can_upload            bool NOT NULL,
  comment               text,
  restricted            bool,
  expiration_date       timestamp,
  ldap_uid              varchar(255),
  can_create_guest      bool NOT NULL,
  inconsistent          bool DEFAULT 'False',
  CONSTRAINT user_pkey
    PRIMARY KEY (account_id));
CREATE TABLE version (
  id       int8 NOT NULL,
  version text NOT NULL UNIQUE,
  CONSTRAINT linshare_version_pkey
    PRIMARY KEY (id));
CREATE TABLE allowed_contact (
  id          int8 NOT NULL,
  account_id int8 NOT NULL,
  contact_id int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE technical_account_permission_domain_abstract (
  technical_account_permission_id int8 NOT NULL,
  domain_abstract_id              int8 NOT NULL,
  PRIMARY KEY (technical_account_permission_id,
  domain_abstract_id));
CREATE TABLE technical_account_permission (
  id                 int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE contact (
  id    int8 NOT NULL,
  mail varchar(255) NOT NULL UNIQUE,
  PRIMARY KEY (id));
CREATE TABLE mail_notification (
  id                       int8 NOT NULL,
  configuration_policy_id int8 NOT NULL,
  domain_abstract_id      int8 NOT NULL,
  activation_policy_id    int8 NOT NULL,
  identifier              varchar(255) NOT NULL,
  system                  bool NOT NULL,
  creation_date           timestamp(6) NOT NULL,
  modification_date       timestamp(6) NOT NULL,
  uuid                    varchar(255) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mail_config (
  id                  int8 NOT NULL,
  mail_layout_id     int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  name               varchar(255) NOT NULL,
  visible            bool NOT NULL,
  uuid               varchar(255) NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  readonly           bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mail_layout (
  id                  int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  description        text,
  visible            bool NOT NULL,
  layout             text NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  uuid               varchar(255) NOT NULL,
  readonly           bool DEFAULT 'false' NOT NULL,
  messages_french    text,
  messages_english   text,
  PRIMARY KEY (id));
CREATE TABLE mail_footer (
  id                  int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  description        text,
  visible            bool NOT NULL,
  footer             text NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  uuid               varchar(255) NOT NULL,
  readonly           bool DEFAULT 'false' NOT NULL,
  messages_french    text,
  messages_english   text,
  PRIMARY KEY (id));
CREATE TABLE mail_footer_lang (
  id              int8 NOT NULL,
  mail_config_id int8 NOT NULL,
  mail_footer_id int8 NOT NULL,
  language       int4 NOT NULL,
  uuid           varchar(255) NOT NULL,
  readonly       bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mail_content (
  id                  int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  description        text,
  visible            bool NOT NULL,
  mail_content_type  int4 NOT NULL,
  subject            text NOT NULL,
  body               text NOT NULL,
  uuid               varchar(255) NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  readonly           bool DEFAULT 'false' NOT NULL,
  messages_french    text,
  messages_english   text,
  PRIMARY KEY (id));
CREATE TABLE mail_content_lang (
  id                 int8 NOT NULL,
  language          int4 NOT NULL,
  mail_content_id   int8 NOT NULL,
  mail_config_id    int8 NOT NULL,
  mail_content_type int4 NOT NULL,
  uuid              varchar(255) NOT NULL,
  readonly          bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_request (
  id                               int8 NOT NULL,
  domain_abstract_id              int8 NOT NULL,
  account_id                      int8 NOT NULL,
  upload_request_group_id         int8 NOT NULL,
  uuid                            varchar(255) NOT NULL UNIQUE,
  max_file                        int4,
  max_deposit_size                int8,
  max_file_size                   int8,
  status                          varchar(255) NOT NULL,
  activation_date                 timestamp(6) NOT NULL,
  creation_date                   timestamp(6) NOT NULL,
  modification_date               timestamp(6) NOT NULL,
  notification_date               timestamp(6),
  expiry_date                     timestamp(6),
  upload_proposition_request_uuid varchar(255),
  can_delete                      bool NOT NULL,
  can_close                       bool NOT NULL,
  can_edit_expiry_date            bool NOT NULL,
  notified                        bool DEFAULT 'FALSE' NOT NULL,
  locale                          varchar(255) NOT NULL,
  secured                         bool NOT NULL,
  mail_message_id                 varchar(255),
  PRIMARY KEY (id));
CREATE TABLE upload_request_url (
  id                 int8 NOT NULL,
  contact_id        int8 NOT NULL,
  upload_request_id int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  path              varchar(255) NOT NULL,
  password          varchar(255),
  creation_date     timestamp(6) NOT NULL,
  modification_date timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_request_group (
  id                 int8 NOT NULL,
  subject           text NOT NULL,
  body              text NOT NULL,
  uuid              varchar(255) NOT NULL,
  creation_date     timestamp(6) NOT NULL,
  modification_date timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_request_history (
  id                               int8 NOT NULL,
  upload_request_id               int8 NOT NULL,
  status                          varchar(255) NOT NULL,
  status_updated                  bool NOT NULL,
  event_type                      varchar(255) NOT NULL,
  uuid                            varchar(255) NOT NULL UNIQUE,
  activation_date                 timestamp(6) NOT NULL,
  expiry_date                     timestamp(6),
  notification_date               timestamp(6),
  max_deposit_size                int8,
  max_file_count                  int4,
  max_file_size                   int8,
  upload_proposition_request_uuid varchar(255),
  can_delete                      bool NOT NULL,
  can_close                       bool NOT NULL,
  can_edit_expiry_date            bool NOT NULL,
  locale                          varchar(255) NOT NULL,
  secured                         bool NOT NULL,
  creation_date                   timestamp(6) NOT NULL,
  modification_date               timestamp(6) NOT NULL,
  mail_message_id                 varchar(255),
  PRIMARY KEY (id));
CREATE TABLE upload_request_entry (
  entry_id                int8 NOT NULL,
  document_entry_entry_id int8,
  upload_request_url_id   int8 NOT NULL,
  ls_size                 int8 NOT NULL,
  PRIMARY KEY (entry_id));
CREATE TABLE upload_proposition_filter (
  id                  int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  uuid               varchar(255) NOT NULL,
  name               varchar(255) NOT NULL,
  ls_match           varchar(255) NOT NULL,
  enable             bool NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  sort_order         int4 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_proposition_rule (
  id                            int8 NOT NULL,
  uuid                         varchar(255) NOT NULL,
  upload_proposition_filter_id int8 NOT NULL,
  operator                     varchar(255) NOT NULL,
  field                        varchar(255) NOT NULL,
  value                        varchar(255),
  creation_date                timestamp(6) NOT NULL,
  modification_date            timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_proposition_action (
  id                            int8 NOT NULL,
  uuid                         varchar(255) NOT NULL,
  upload_proposition_filter_id int8 NOT NULL,
  action_type                  varchar(255) NOT NULL,
  data                         text,
  creation_date                timestamp(6) NOT NULL,
  modification_date            timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mailing_list (
  id                  int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  user_id            int8 NOT NULL,
  is_public          bool NOT NULL,
  identifier         varchar(255) NOT NULL,
  description        text,
  uuid               varchar(255) NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mailing_list_contact (
  id                          int8 NOT NULL,
  mailing_list_id            int8 NOT NULL,
  mail                       varchar(255) NOT NULL,
  first_name                 varchar(255),
  last_name                  varchar(255),
  uuid                       varchar(255) NOT NULL,
  creation_date              timestamp(6) NOT NULL,
  modification_date          timestamp(6) NOT NULL,
  mailing_list_contact_index int4 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_request_template (
  id                          int8 NOT NULL,
  uuid                       varchar(255) NOT NULL,
  account_id                 int8 NOT NULL,
  name                       varchar(255) NOT NULL,
  description                varchar(255),
  duration_before_activation int8,
  unit_before_activation     int8,
  duration_before_expiry     int8,
  unit_before_expiry         int8,
  group_mode                 bool,
  deposit_mode               bool,
  max_file                   int8,
  max_file_size              int8,
  max_deposit_size           int8,
  locale                     varchar(255),
  secured                    bool,
  day_before_notification    int8,
  prolongation_mode          bool,
  creation_date              timestamp(6) NOT NULL,
  modification_date          timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE upload_proposition (
  id                  int8 NOT NULL,
  uuid               varchar(255) NOT NULL,
  domain_abstract_id int8 NOT NULL,
  status             varchar(255) NOT NULL,
  subject            varchar(255) NOT NULL,
  body               text,
  mail               varchar(255) NOT NULL,
  first_name         varchar(255) NOT NULL,
  last_name          varchar(255) NOT NULL,
  domain_source      varchar(255),
  recipient_mail     varchar(255) NOT NULL,
  creation_date      timestamp(6) NOT NULL,
  modification_date  timestamp(6) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mime_policy (
  id                 int8 NOT NULL,
  domain_id         int8 NOT NULL,
  uuid              varchar(255) NOT NULL,
  name              varchar(255) NOT NULL,
  mode              int4 NOT NULL,
  displayable       int4 NOT NULL,
  creation_date     timestamp(6) NOT NULL,
  modification_date timestamp(6) NOT NULL,
  version           int4 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mime_type (
  id                 int8 NOT NULL,
  mime_policy_id    int8 NOT NULL,
  uuid              varchar(255) NOT NULL,
  mime_type         varchar(255) NOT NULL,
  extensions        varchar(255) NOT NULL,
  enable            bool NOT NULL,
  displayable       bool NOT NULL,
  creation_date     timestamp(6) NOT NULL,
  modification_date timestamp(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unicity_type_and_policy
    UNIQUE (mime_policy_id, mime_type));
CREATE TABLE account_permission (
  id                               int8 NOT NULL,
  technical_account_permission_id int8 NOT NULL,
  permission                      varchar(255) NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE functionality_enum_lang (
  functionality_id int8 NOT NULL,
  lang_value       varchar(255) NOT NULL,
  PRIMARY KEY (functionality_id));
CREATE TABLE functionality_boolean (
  functionality_id int8 NOT NULL,
  boolean_value    bool NOT NULL,
  PRIMARY KEY (functionality_id));
CREATE TABLE contact_provider (
  id                  int8 NOT NULL,
  uuid               varchar(255) NOT NULL UNIQUE,
  provider_type      varchar(255) NOT NULL,
  base_dn            varchar(255),
  creation_date      timestamp NOT NULL,
  modification_date  timestamp NOT NULL,
  domain_abstract_id int8 NOT NULL,
  ldap_pattern_id    int8 NOT NULL,
  ldap_connection_id int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE user_provider (
  id                  int8 NOT NULL,
  uuid               varchar(255) NOT NULL UNIQUE,
  provider_type      varchar(255) NOT NULL,
  base_dn            varchar(255),
  creation_date      timestamp NOT NULL,
  modification_date  timestamp NOT NULL,
  ldap_connection_id int8 NOT NULL,
  ldap_pattern_id    int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE ldap_pattern (
  id                                            int8 NOT NULL,
  uuid                                         varchar(255) NOT NULL UNIQUE,
  pattern_type                                 varchar(255) NOT NULL,
  label                                        varchar(255) NOT NULL,
  system                                       bool NOT NULL,
  description                                  text NOT NULL,
  auth_command                                 text,
  search_user_command                          text,
  search_page_size                             int4,
  search_size_limit                            int4,
  auto_complete_command_on_first_and_last_name text,
  auto_complete_command_on_all_attributes      text,
  completion_page_size                         int4,
  completion_size_limit                        int4,
  creation_date                                timestamp NOT NULL,
  modification_date                            timestamp NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE welcome_messages (
  id                 int8 NOT NULL,
  uuid              varchar(255) NOT NULL,
  name              varchar(255) NOT NULL,
  description       text NOT NULL,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  domain_id         int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE welcome_messages_entry (
  id                   int8 NOT NULL,
  lang                varchar(255) NOT NULL,
  value               text NOT NULL,
  welcome_messages_id int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE share_entry_group (
  id                 int8 NOT NULL,
  account_id        int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  subject           text,
  notification_date timestamp,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  notified          bool DEFAULT 'false' NOT NULL,
  processed         bool DEFAULT 'false' NOT NULL,
  expiration_date   timestamp,
  PRIMARY KEY (id));
CREATE TABLE mail_activation (
  id                       int8 NOT NULL,
  identifier              varchar(255) NOT NULL,
  system                  bool NOT NULL,
  policy_activation_id    int8 NOT NULL,
  policy_configuration_id int8 NOT NULL,
  policy_delegation_id    int8 NOT NULL,
  domain_id               int8 NOT NULL,
  enable                  bool NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE async_task (
  id                     int8 NOT NULL,
  upgrade_task_id       int8,
  owner_id              int8 NOT NULL,
  actor_id              int8 NOT NULL,
  domain_abstract_id    int8 NOT NULL,
  uuid                  varchar(255) NOT NULL,
  task_type             varchar(255) NOT NULL,
  resource_uuid         varchar(255),
  status                varchar(255) NOT NULL,
  creation_date         timestamp(6) NOT NULL,
  start_processing_date timestamp(6),
  end_processing_date   timestamp(6),
  processing_duration   int8,
  modification_date     timestamp(6) NOT NULL,
  error_code            int4,
  error_name            varchar(255),
  error_msg             text,
  ls_size               int8,
  file_name             text,
  frequency             int4,
  transfert_duration    int8,
  waiting_duration      int8,
  meta_data             text,
  PRIMARY KEY (id));
CREATE TABLE quota (
  id                              int8 NOT NULL,
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
CREATE TABLE operation_history (
  id               int8 NOT NULL,
  uuid            varchar(255) NOT NULL,
  operation_value int8 NOT NULL,
  operation_type  int4 NOT NULL,
  container_type  varchar(255) NOT NULL,
  creation_date   timestamp(6) NOT NULL,
  domain_id       int8 NOT NULL,
  account_id      int8 NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE statistic (
  id                      int8 NOT NULL,
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
CREATE TABLE batch_history (
  id                int8 NOT NULL,
  uuid             varchar(255) NOT NULL UNIQUE,
  status           varchar(255) NOT NULL,
  batch_type       varchar(255) NOT NULL,
  execution_date   timestamp(6) NOT NULL,
  active_date      timestamp(6) NOT NULL,
  errors           int8 NOT NULL,
  unhandled_errors int8 NOT NULL,
  once             bool DEFAULT 'false' NOT NULL,
  async_task_uuid  varchar(255),
  extras           text,
  PRIMARY KEY (id));
CREATE TABLE upgrade_task (
  id                 int8 NOT NULL,
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
CREATE UNIQUE INDEX account_lsuid_index
  ON account (ls_uuid);
CREATE UNIQUE INDEX account_ls_uuid
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
  ON domain_abstract (uuid);
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
CREATE INDEX mailing_list_index
  ON mailing_list (uuid);
CREATE INDEX mailing_list_contact_index
  ON mailing_list_contact (uuid);
CREATE UNIQUE INDEX welcome_messages_uuid
  ON welcome_messages (uuid);
CREATE INDEX async_task_id
  ON async_task (id);
CREATE INDEX async_task_owner_id
  ON async_task (owner_id);
CREATE INDEX async_task_actor_id
  ON async_task (actor_id);
CREATE INDEX async_task_domain_abstract_id
  ON async_task (domain_abstract_id);
CREATE UNIQUE INDEX async_task_uuid
  ON async_task (uuid);
ALTER TABLE domain_abstract ADD CONSTRAINT fk449bc2ec59e1e332 FOREIGN KEY (domain_policy_id) REFERENCES domain_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_abstract ADD CONSTRAINT fk449bc2ec9083e725 FOREIGN KEY (parent_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD CONSTRAINT fkf75719ed3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_access_rule ADD CONSTRAINT fkf75719ed85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE domain_policy ADD CONSTRAINT fk49c9a27c85924e31 FOREIGN KEY (domain_access_policy_id) REFERENCES domain_access_policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD CONSTRAINT fk7430c53a58fe5398 FOREIGN KEY (policy_activation_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD CONSTRAINT fk7430c53a71796372 FOREIGN KEY (policy_configuration_id) REFERENCES policy (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality ADD CONSTRAINT fk7430c53a3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_integer ADD CONSTRAINT fk8662133910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_string ADD CONSTRAINT fkb2a122b610439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD CONSTRAINT fk3ced016910439d2b FOREIGN KEY (functionality_id) REFERENCES functionality (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE functionality_unit ADD CONSTRAINT fk3ced0169f329e0c9 FOREIGN KEY (unit_id) REFERENCES unit (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE share_expiry_rules ADD CONSTRAINT fkfda1673c3c036ccb FOREIGN KEY (domain_id) REFERENCES domain_abstract (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE signature ADD CONSTRAINT fk81c9a1a7c0bbd6f FOREIGN KEY (document_id) REFERENCES document (id) ON UPDATE No action ON DELETE No action;
ALTER TABLE thread ADD CONSTRAINT inheritance_account_thread FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE document_entry ADD CONSTRAINT FKdocument_e594117 FOREIGN KEY (document_id) REFERENCES document (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr708932 FOREIGN KEY (document_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_138106 FOREIGN KEY (document_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE recipient_favourite ADD CONSTRAINT FKrecipient_90791 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_732508 FOREIGN KEY (anonymous_url_id) REFERENCES anonymous_url (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr87036 FOREIGN KEY (recipient_id) REFERENCES account (id);
ALTER TABLE account ADD CONSTRAINT FKaccount400616 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE allowed_contact ADD CONSTRAINT FKallowed_co409962 FOREIGN KEY (account_id) REFERENCES users (account_id);
ALTER TABLE allowed_contact ADD CONSTRAINT FKallowed_co620678 FOREIGN KEY (contact_id) REFERENCES users (account_id);
ALTER TABLE account ADD CONSTRAINT FKaccount487511 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE users ADD CONSTRAINT FKusers71760 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE thread_member ADD CONSTRAINT FKthread_mem280144 FOREIGN KEY (thread_id) REFERENCES thread (account_id);
ALTER TABLE thread_member ADD CONSTRAINT FKthread_mem565048 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE technical_account_permission_domain_abstract ADD CONSTRAINT FKtechnical_303831 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE technical_account_permission_domain_abstract ADD CONSTRAINT FKtechnical_231219 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_621478 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE document_entry ADD CONSTRAINT FKdocument_e19140 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr50652 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE thread_entry ADD CONSTRAINT FKthread_ent715634 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE entry ADD CONSTRAINT FKentry500391 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE anonymous_url ADD CONSTRAINT FKanonymous_877695 FOREIGN KEY (contact_id) REFERENCES contact (id);
ALTER TABLE signature ADD CONSTRAINT FKsignature417918 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE account ADD CONSTRAINT FKaccount693567 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE thread_entry ADD CONSTRAINT FKthread_ent140657 FOREIGN KEY (document_id) REFERENCES document (id);
ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif244118 FOREIGN KEY (activation_policy_id) REFERENCES policy (id);
ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif777760 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_layout ADD CONSTRAINT FKmail_layou627738 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer ADD CONSTRAINT FKmail_foote767112 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT FKmail_foote801249 FOREIGN KEY (mail_footer_id) REFERENCES mail_footer (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs160138 FOREIGN KEY (mailconfig_id) REFERENCES mail_config (id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi697783 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_content ADD CONSTRAINT FKmail_conte385227 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte33952 FOREIGN KEY (mail_content_id) REFERENCES mail_content (id);
ALTER TABLE upload_request_url ADD CONSTRAINT FKupload_req833645 FOREIGN KEY (upload_request_id) REFERENCES upload_request (id);
ALTER TABLE upload_request ADD CONSTRAINT FKupload_req916400 FOREIGN KEY (upload_request_group_id) REFERENCES upload_request_group (id);
ALTER TABLE upload_request_url ADD CONSTRAINT FKupload_req601912 FOREIGN KEY (contact_id) REFERENCES contact (id);
ALTER TABLE upload_request_history ADD CONSTRAINT FKupload_req678768 FOREIGN KEY (upload_request_id) REFERENCES upload_request (id);
ALTER TABLE upload_request_entry ADD CONSTRAINT upload_request_entry_fk_url FOREIGN KEY (upload_request_url_id) REFERENCES upload_request_url (id);
ALTER TABLE upload_request_entry ADD CONSTRAINT FKupload_req254795 FOREIGN KEY (entry_id) REFERENCES entry (id);
ALTER TABLE upload_request_entry ADD CONSTRAINT FKupload_req11781 FOREIGN KEY (document_entry_entry_id) REFERENCES document_entry (entry_id);
ALTER TABLE upload_proposition_rule ADD CONSTRAINT FKupload_pro672390 FOREIGN KEY (upload_proposition_filter_id) REFERENCES upload_proposition_filter (id);
ALTER TABLE upload_proposition_action ADD CONSTRAINT FKupload_pro841666 FOREIGN KEY (upload_proposition_filter_id) REFERENCES upload_proposition_filter (id);
ALTER TABLE functionality ADD CONSTRAINT FKfunctional788903 FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKMailingListContact FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);
ALTER TABLE upload_request_template ADD CONSTRAINT FKupload_req618325 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif791766 FOREIGN KEY (configuration_policy_id) REFERENCES policy (id);
ALTER TABLE upload_proposition ADD CONSTRAINT FKupload_pro226633 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mime_type ADD CONSTRAINT FKmime_type145742 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);
ALTER TABLE mime_policy ADD CONSTRAINT FKmime_polic613419 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs809928 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);
ALTER TABLE account_permission ADD CONSTRAINT FKaccount_pe759382 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE upload_request ADD CONSTRAINT FKupload_req220337 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE upload_request ADD CONSTRAINT FKupload_req840249 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE upload_proposition_filter ADD CONSTRAINT FKupload_pro316142 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE functionality_enum_lang ADD CONSTRAINT FKfunctional140416 FOREIGN KEY (functionality_id) REFERENCES functionality (id);
ALTER TABLE functionality_boolean ADD CONSTRAINT FKfunctional171577 FOREIGN KEY (functionality_id) REFERENCES functionality (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr166740 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr806790 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs163989 FOREIGN KEY (user_provider_id) REFERENCES user_provider (id);
ALTER TABLE user_provider ADD CONSTRAINT FKuser_provi1640 FOREIGN KEY (ldap_connection_id) REFERENCES ldap_connection (id);
ALTER TABLE contact_provider ADD CONSTRAINT FKcontact_pr355176 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE ldap_attribute ADD CONSTRAINT FKldap_attri49928 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE user_provider ADD CONSTRAINT FKuser_provi813203 FOREIGN KEY (ldap_pattern_id) REFERENCES ldap_pattern (id);
ALTER TABLE welcome_messages_entry ADD CONSTRAINT welcome_messages_entry_fk_welcome_message FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD CONSTRAINT use_customisation FOREIGN KEY (welcome_messages_id) REFERENCES welcome_messages (id);
ALTER TABLE welcome_messages ADD CONSTRAINT own_welcome_messages FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_708340 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr137514 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE mail_activation ADD CONSTRAINT FKmail_activ188698 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_activation ADD CONSTRAINT activation FOREIGN KEY (policy_activation_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD CONSTRAINT configuration FOREIGN KEY (policy_configuration_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD CONSTRAINT delegation FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);
ALTER TABLE share_entry_group ADD CONSTRAINT shareEntryGroup FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE async_task ADD CONSTRAINT FKasync_task548996 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE async_task ADD CONSTRAINT FKasync_task706276 FOREIGN KEY (actor_id) REFERENCES account (id);
ALTER TABLE async_task ADD CONSTRAINT FKasync_task559470 FOREIGN KEY (owner_id) REFERENCES account (id);
ALTER TABLE quota ADD CONSTRAINT parentDomain FOREIGN KEY (domain_parent_id) REFERENCES domain_abstract (id);
ALTER TABLE quota ADD CONSTRAINT account FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE quota ADD CONSTRAINT FKquota572570 FOREIGN KEY (quota_domain_id) REFERENCES quota (id);
ALTER TABLE quota ADD CONSTRAINT FKquota430815 FOREIGN KEY (quota_container_id) REFERENCES quota (id);
ALTER TABLE operation_history ADD CONSTRAINT FKoperation_38651 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE operation_history ADD CONSTRAINT FKoperation_531280 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE statistic ADD CONSTRAINT FKstatistic57774 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE statistic ADD CONSTRAINT FKstatistic343885 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE statistic ADD CONSTRAINT FKstatistic161509 FOREIGN KEY (domain_parent_id) REFERENCES domain_abstract (id);
ALTER TABLE quota ADD CONSTRAINT domain FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT mailconfig_mailfooterlang FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);
ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte910199 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi688067 FOREIGN KEY (mail_layout_id) REFERENCES mail_layout (id);
ALTER TABLE async_task ADD CONSTRAINT FKasync_task970702 FOREIGN KEY (upgrade_task_id) REFERENCES upgrade_task (id);

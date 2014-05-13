-- Postgresql migration script : 1.5.0 to 1.6.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;



CREATE TABLE mail_notification (
  id                       int8 NOT NULL, 
  configuration_policy_id int8 NOT NULL, 
  domain_abstract_id      int8 NOT NULL, 
  activation_policy_id    int8 NOT NULL, 
  identifier              varchar(255) NOT NULL, 
  system                  bool NOT NULL, 
  creation_date           date NOT NULL, 
  modification_date       date NOT NULL, 
  uuid                    varchar(255) NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_config (
  id                   int8 NOT NULL, 
  mail_layout_html_id int8 NOT NULL, 
  domain_abstract_id  int8 NOT NULL, 
  name                varchar(255) NOT NULL, 
  visible             bool NOT NULL, 
  mail_layout_text_id int8 NOT NULL, 
  uuid                varchar(255) NOT NULL, 
  creation_date       date NOT NULL, 
  modification_date   date NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_layout (
  id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  name               varchar(255) NOT NULL, 
  visible            bool NOT NULL, 
  layout             text NOT NULL, 
  creation_date      date NOT NULL, 
  modification_date  date NOT NULL, 
  uuid               varchar(255) NOT NULL, 
  plaintext          bool NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_footer (
  id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  name               varchar(255) NOT NULL, 
  visible            bool NOT NULL, 
  language           int4 NOT NULL, 
  footer             text NOT NULL, 
  creation_date      date NOT NULL, 
  modification_date  date NOT NULL, 
  uuid               varchar(255) NOT NULL, 
  plaintext          bool NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_footer_lang (
  id              int8 NOT NULL, 
  mail_config_id int8 NOT NULL, 
  mail_footer_id int8 NOT NULL, 
  language       int4 NOT NULL, 
  uuid           varchar(255) NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_content (
  id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  name               varchar(255) NOT NULL, 
  visible            bool NOT NULL, 
  mail_content_type  int4 NOT NULL, 
  language           int4 NOT NULL, 
  subject            text NOT NULL, 
  greetings          text NOT NULL, 
  body               text NOT NULL, 
  uuid               varchar(255) NOT NULL, 
  plaintext          bool NOT NULL, 
  creation_date      date NOT NULL, 
  modification_date  date NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_content_lang (
  id                 int8 NOT NULL, 
  language          int4 NOT NULL, 
  mail_content_id   int8 NOT NULL, 
  mail_config_id    int8 NOT NULL, 
  mail_content_type int4 NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  PRIMARY KEY (id));




ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif460924 FOREIGN KEY (policy_id) REFERENCES policy (id);
ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif777760 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_layout ADD CONSTRAINT FKmail_layou627738 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer ADD CONSTRAINT FKmail_foote767112 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT FKmail_foote800257 FOREIGN KEY (mail_footer_id) REFERENCES mail_footer (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs159146 FOREIGN KEY (mailconfig_id) REFERENCES mail_config (id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi697783 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT FKmail_foote320110 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);
ALTER TABLE mail_content ADD CONSTRAINT FKmail_conte385227 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte911191 FOREIGN KEY (mail_config_id) REFERENCES mail_config (id);
ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte32960 FOREIGN KEY (mail_content_id) REFERENCES mail_content (id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi540307 FOREIGN KEY (mail_layout_html_id) REFERENCES mail_layout (id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi611322 FOREIGN KEY (mail_layout_text_id) REFERENCES mail_layout (id);

DROP TABLE thread_view CASCADE;
DROP TABLE thread_view_asso CASCADE;
DROP TABLE view_context CASCADE;
DROP TABLE views CASCADE;
DROP TABLE view_tag_asso  CASCADE;
DROP TABLE default_view  CASCADE;
DROP TABLE entry_tag_association CASCADE;
DROP TABLE tag CASCADE;
DROP TABLE tag_enum_value CASCADE;
DROP TABLE tag_filter CASCADE;
DROP TABLE tag_filter_rule CASCADE;
DROP TABLE tag_filter_rule_tag_association CASCADE;




ALTER TABLE domain_abstract ADD mailconfig_id int8;
ALTER TABLE domain_abstract ADD mime_policy_id int8;


CREATE TABLE mime_policy (
  id                 int8 NOT NULL, 
  domain_id         int8 NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  name              varchar(255) NOT NULL, 
  mode              int4 NOT NULL, 
  displayable       int4 NOT NULL, 
  creation_date     date NOT NULL, 
  modification_date date NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mime_type (
  id                 int8 NOT NULL, 
  mime_policy_id    int8 NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  mime_type         text NOT NULL, 
  extensions        text NOT NULL, 
  enable            bool NOT NULL, 
  displayable       bool NOT NULL, 
  creation_date     date NOT NULL, 
  modification_date date NOT NULL, 
  PRIMARY KEY (id));


ALTER TABLE mime_type ADD CONSTRAINT FKmime_type145742 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);
ALTER TABLE mime_policy ADD CONSTRAINT FKmime_polic613419 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs809928 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);

ALTER TABLE version RENAME description TO version;
ALTER TABLE account DROP COLUMN account_id;


-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.6.0');

COMMIT;

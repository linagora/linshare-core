-- Postgresql migration script : 1.3.0 to 1.4.0
BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

-- update mail subjects
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 13, E'${actorSubject} from ${actorRepresentation}', 0);
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 13, E'${actorSubject} de la part de ${actorRepresentation}', 1);
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 13, E'${actorSubject} from ${actorRepresentation}', 2);

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
  id                 int8 NOT NULL, 
  mailing_list_id    int8 NOT NULL, 
  mail               varchar(255) NOT NULL, 
  first_name			 varchar(255),
  last_name			 varchar(255),
  uuid               varchar(255) NOT NULL, 
  creation_date      timestamp(6) NOT NULL, 
  modification_date  timestamp(6) NOT NULL, 
  mailing_list_contact_index int4, 
  PRIMARY KEY (id));

ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKmailing_li272876 FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);

CREATE INDEX mailing_list_index 
  ON mailing_list (uuid);
CREATE INDEX mailing_list_contact_index 
  ON mailing_list_contact (uuid);


-- Functionality : TAB_LIST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (53, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (54, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (26, true, 'TAB_LIST', 53, 54, 1);

-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.4.0');

COMMIT;

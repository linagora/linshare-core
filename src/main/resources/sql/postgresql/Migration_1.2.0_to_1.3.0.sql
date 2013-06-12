-- Postgresql migration script : 1.2.0 to 1.3.0

BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

ALTER TABLE account
  ADD COLUMN thread_view_id int8;
ALTER TABLE account
  ADD COLUMN account_id int8;
ALTER TABLE thread_view
  ADD COLUMN account_id int8;

--Schema Migration
CREATE TABLE mailing_list (
  id                  int8 NOT NULL,
  domain_abstract_id int8 NOT NULL,
  user_id            int8 NOT NULL,
  isPublic           bool NOT NULL,
  identifier         varchar(255) NOT NULL,
  description        text,
  PRIMARY KEY (id));
CREATE TABLE mailing_list_contact (
  id              int8 NOT NULL,
  mailing_list_id int8 NOT NULL,
  mail            varchar(255),
  PRIMARY KEY (id,
  mailing_list_id));

ALTER TABLE thread_view ADD CONSTRAINT FKthread_vie557698 FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKmailing_li272876 FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id); 

-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.3.0');
COMMIT;


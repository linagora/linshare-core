-- Postgresql migration script : 1.2.0 to 1.3.0
BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

ALTER SEQUENCE hibernate_sequence INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE mailing_list (
  id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  user_id            int8 NOT NULL, 
  is_public          bool NOT NULL, 
  identifier         varchar(255) NOT NULL, 
  description        text NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mailing_list_contact (
  id              int4 NOT NULL, 
  mailing_list_id int8 NOT NULL, 
  mail            varchar(255), 
  PRIMARY KEY (id, 
  mailing_list_id));

ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKmailing_li272876 FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);

COMMIT;
-- Postgresql migration script : 1.5.0 to 1.6.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;


DROP TABLE technical_account_permission_domain_abstract CASCADE;
DROP TABLE technical_account_permission_account CASCADE;
DROP TABLE technical_account_permission CASCADE;

CREATE TABLE technical_account_permission_domain_abstract (
  technical_account_permission_id int8 NOT NULL, 
  domain_abstract_id              int8 NOT NULL, 
  PRIMARY KEY (technical_account_permission_id, 
  domain_abstract_id));

CREATE TABLE technical_account_permission (
  id               int8 NOT NULL,
  PRIMARY KEY (id));

CREATE TABLE account_permission (
  id               int8 NOT NULL,  
  PRIMARY KEY (id));

ALTER TABLE technical_account_permission_domain_abstract ADD CONSTRAINT FKtechnical_303831 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);
ALTER TABLE technical_account_permission_domain_abstract ADD CONSTRAINT FKtechnical_231219 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE account ADD CONSTRAINT FKaccount693567 FOREIGN KEY (technical_account_permission_id) REFERENCES technical_account_permission (id);


-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.7.0');

COMMIT;

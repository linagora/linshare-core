-- DROP and add column from domain_abstract table
ALTER TABLE domain_abstract DROP COLUMN messages_configuration_id;

-- Drop all useless tables
DROP TABLE IF EXISTS mail_templates;
DROP TABLE IF EXISTS mail_subjects;
DROP TABLE IF EXISTS welcome_texts;
DROP TABLE IF EXISTS messages_configuration;

-- Creation of the new tables
CREATE TABLE welcome_messages (
  id                 BIGSERIAL NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  name              varchar(255) NOT NULL, 
  description       text NOT NULL, 
  creation_date     timestamp NOT NULL, 
  modification_date timestamp NOT NULL, 
  domain_id         int8 NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE welcome_messages_entry (
  id          BIGSERIAL NOT NULL, 
  lang       varchar(255) NOT NULL, 
  value      varchar(255) NOT NULL, 
  welcome_id int8 NOT NULL, 
  PRIMARY KEY (id));

--ADDING constraint foreign key on tables
ALTER TBALE domain_abstract ADD COLUMN welcome_id;
ALTER TABLE welcome_messages_entry ADD CONSTRAINT FKwelcome_me856948 FOREIGN KEY (welcome_id) REFERENCES welcome_messages (id);
ALTER TABLE domain_abstract ADD CONSTRAINT use_customisation FOREIGN KEY (welcome_id) REFERENCES welcome_messages (id);
ALTER TABLE welcome_messages ADD CONSTRAINT own_welcome_messages FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);

-- ADDING the value in the tables

--Welcome messages
INSERT INTO welcome_messages(id, uuid, name, description, creation_date, modification_date, domain_id) VALUES (1, '4bc57114-c8c9-11e4-a859-37b5db95d856', 'WelcomeName', 'a Welcome description', now(), now(), 1);

--Melcome messages Entry
INSERT INTO welcome_messages_entry(id, lang, value, welcome_id) VALUES (1, 'en', 'Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_id) VALUES (2, 'fr', 'Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_id) VALUES (3, 'mq', 'Bienvini an lè Linshare, an solusyon lib de partaj de fichié sékirisé.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_id) VALUES (4, 'vi', 'Chào mừng bạn đến với Linshare, phần mềm nguồn mở chia sẻ file bảo mật.', 1);
INSERT INTO welcome_messages_entry(id, lang, value, welcome_id) VALUES (5, 'nl', 'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 1);

--Setting welcome_id for all domains
UPDATE domain_abstract SET welcome_id = 1;
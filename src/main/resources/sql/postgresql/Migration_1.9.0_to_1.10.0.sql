BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

UPDATE mail_content SET language = 1 where id = 80;

ALTER TABLE users ADD COLUMN inconsistent bool DEFAULT 'False';

ALTER TABLE account ADD COLUMN cmis_locale varchar(255);
UPDATE account SET cmis_locale = 'en';

DROP VIEW IF EXISTS alias_users_list_all, alias_users_list_active, alias_users_list_destroyed, alias_threads_list_all, alias_threads_list_active, alias_threads_list_destroyed;
ALTER TABLE account
	ALTER COLUMN cmis_locale SET NOT NULL,
	ALTER COLUMN creation_date TYPE timestamp(6),
	ALTER COLUMN modification_date TYPE timestamp(6);

ALTER TABLE entry ADD COLUMN cmis_sync bool DEFAULT 'false' NOT NULL;

UPDATE mail_content set body = 'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationDate}, avec : <ul>${recipientNames}</ul><br/>Voici la liste des documents partagés : <ul>${documentNames}</ul>' where id = 82;

UPDATE mail_content set body = 'Vous avez partagé ${fileNumber} document(s), le ${creationDate}, expirant le ${expirationDate}, avec : <ul>${recipientNames}</ul>Votre message original est le suivant :<br/><i>${message}</i><br/><br/>Voici la liste des documents partagés :<br/><ul>${documentNames}</ul>' where id = 83;


CREATE TABLE share_entry_group (
  id                int8 NOT NULL,
  account_id        int8 NOT NULL,
  uuid              varchar(255) NOT NULL UNIQUE,
  subject           text,
  notification_date timestamp,
  creation_date     timestamp NOT NULL,
  modification_date timestamp NOT NULL,
  expiration_date   timestamp NULL,
  notified          bool DEFAULT 'false' NOT NULL,
  processed         bool DEFAULT 'false' NOT NULL,
  PRIMARY KEY (id));
CREATE TABLE mail_activation (
  id                      int8 NOT NULL,
  identifier              varchar(255) NOT NULL,
  system                  bool NOT NULL,
  policy_activation_id    int8 NOT NULL,
  policy_configuration_id int8 NOT NULL,
  policy_delegation_id    int8 NOT NULL,
  domain_id               int8 NOT NULL,
  enable                  bool NOT NULL,
  PRIMARY KEY (id));

ALTER TABLE anonymous_share_entry ADD COLUMN share_entry_group_id int8;
ALTER TABLE share_entry ADD COLUMN share_entry_group_id int8;

ALTER TABLE anonymous_share_entry ADD CONSTRAINT FKanonymous_708340 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE share_entry ADD CONSTRAINT FKshare_entr137514 FOREIGN KEY (share_entry_group_id) REFERENCES share_entry_group (id);
ALTER TABLE share_entry_group ADD CONSTRAINT shareEntryGroup FOREIGN KEY (account_id) REFERENCES account (id);
ALTER TABLE mail_activation ADD CONSTRAINT FKmail_activ188698 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_activation ADD CONSTRAINT activation FOREIGN KEY (policy_activation_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD CONSTRAINT configuration FOREIGN KEY (policy_configuration_id) REFERENCES policy (id);
ALTER TABLE mail_activation ADD CONSTRAINT delegation FOREIGN KEY (policy_delegation_id) REFERENCES policy (id);




-- ENGLISH
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (33, 'eb291876-53fc-419b-831b-53a480399f7c', 1, 0, 32, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Undownloaded shared documents alert', E'[Undownloaded shared documents alert] ${subject} Shared on ${date}.', E'Please find below the resume of the share you made on ${creationDate} with initial expiration date on ${expirationDate}.<br /> List of documents : <br /><table style="border-collapse: collapse;">${shareInfo}</table><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (33, 1, 0, 33, 32, 'bfcced12-7325-49df-bf84-65ed90ff7f59');
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 0, 33, 32, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);
-- FRENCH
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (84, 'f2cc5735-a3fe-43e8-ae9c-bace74195af0', 1, 1, 32, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Accusé de non téléchargement de fichiers', E'[Accusé de Non Téléchargement] ${subject} Partagé le ${date}.', E'Veuillez trouver ci-dessous le suivi du partage de documents réalisé le ${creationDate} avec pour date d’expiration initiale le ${expirationDate}.<br /> Liste des documents : <br /><table style="border-collapse: collapse;">${shareInfo}</table><br/>');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (84, 1, 1, 84, 32, 'fa7a23cb-f545-45b4-b9dc-c39586cb2398');
INSERT INTO mail_content_lang (id, mail_config_id, language, mail_content_id, mail_content_type, uuid) (SELECT nextVal('hibernate_sequence'), config.id, 1, 84, 32, uuid_in(md5(random()::text || now()::text)::cstring) FROM mail_config AS config WHERE id <> 1);


-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (131, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (132, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (133, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id) 
 VALUES(54, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', 131, 132, 133, 1);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (54, true);

-- Functionality : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (134, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (135, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (136, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(55, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION', 134, 135, 136, 1, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', true);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (55, 3);

-- Functionality : ANONYMOUS_URL__NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (224, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (225, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (226, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param)
 VALUES(56, false, 'ANONYMOUS_URL__NOTIFICATION', 224, 225, 226, 1, 'ANONYMOUS_URL', true);
INSERT INTO functionality_boolean(functionality_id, boolean_value) VALUES (56, true);


-- MailActivation : BEGIN

-- MailActivation : ANONYMOUS_DOWNLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (137, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (138, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (139, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(1, false, 'ANONYMOUS_DOWNLOAD', 137, 138, 139, 1, true);

-- MailActivation : REGISTERED_DOWNLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (140, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (141, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (142, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(2, false, 'REGISTERED_DOWNLOAD', 140, 141, 142, 1, true);

-- MailActivation : NEW_GUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (143, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (144, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (145, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(3, false, 'NEW_GUEST', 143, 144, 145, 1, true);

-- MailActivation : RESET_PASSWORD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (146, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (147, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (148, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(4, false, 'RESET_PASSWORD', 146, 147, 148, 1, true);

-- MailActivation : SHARED_DOC_UPDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (149, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (150, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (151, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(5, false, 'SHARED_DOC_UPDATED', 149, 150, 151, 1, true);

-- MailActivation : SHARED_DOC_DELETED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (152, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (153, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (154, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(6, false, 'SHARED_DOC_DELETED', 152, 153, 154, 1, true);

-- MailActivation : SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (155, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (156, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (157, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(7, false, 'SHARED_DOC_UPCOMING_OUTDATED', 155, 156, 157, 1, true);

-- MailActivation : DOC_UPCOMING_OUTDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (158, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (159, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (160, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(8, false, 'DOC_UPCOMING_OUTDATED', 158, 159, 160, 1, true);

-- MailActivation : NEW_SHARING
INSERT INTO policy(id, status, default_status, policy, system) VALUES (161, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (162, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (163, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(9, false, 'NEW_SHARING', 161, 162, 163, 1, true);

-- MailActivation : UPLOAD_PROPOSITION_CREATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (164, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (165, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (166, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(10, false, 'UPLOAD_PROPOSITION_CREATED', 164, 165, 166, 1, true);

-- MailActivation : UPLOAD_PROPOSITION_REJECTED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (167, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (168, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (169, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(11, false, 'UPLOAD_PROPOSITION_REJECTED', 167, 168, 169, 1, true);

-- MailActivation : UPLOAD_REQUEST_UPDATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (170, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (171, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (172, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(12, false, 'UPLOAD_REQUEST_UPDATED', 170, 171, 172, 1, true);

-- MailActivation : UPLOAD_REQUEST_ACTIVATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (173, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (174, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (175, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(13, false, 'UPLOAD_REQUEST_ACTIVATED', 173, 174, 175, 1, true);

-- MailActivation : UPLOAD_REQUEST_AUTO_FILTER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (176, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (177, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (178, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(14, false, 'UPLOAD_REQUEST_AUTO_FILTER', 176, 177, 178, 1, true);

-- MailActivation : UPLOAD_REQUEST_CREATED
INSERT INTO policy(id, status, default_status, policy, system) VALUES (179, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (180, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (181, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(15, false, 'UPLOAD_REQUEST_CREATED', 179, 180, 181, 1, true);

-- MailActivation : UPLOAD_REQUEST_ACKNOWLEDGEMENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (182, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (183, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (184, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(16, false, 'UPLOAD_REQUEST_ACKNOWLEDGEMENT', 182, 183, 184, 1, true);

-- MailActivation : UPLOAD_REQUEST_REMINDER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (185, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (186, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (187, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(17, false, 'UPLOAD_REQUEST_REMINDER', 185, 186, 187, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (188, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (189, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (190, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(18, false, 'UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY', 188, 189, 190, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (191, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (192, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (193, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(19, false, 'UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY', 191, 192, 193, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_OWNER_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (194, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (195, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (196, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(20, false, 'UPLOAD_REQUEST_WARN_OWNER_EXPIRY', 194, 195, 196, 1, true);

-- MailActivation : UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY
INSERT INTO policy(id, status, default_status, policy, system) VALUES (197, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (198, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (199, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(21, false, 'UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY', 197, 198, 199, 1, true);

-- MailActivation : UPLOAD_REQUEST_CLOSED_BY_RECIPIENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (200, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (201, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (202, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(22, false, 'UPLOAD_REQUEST_CLOSED_BY_RECIPIENT', 200, 201, 202, 1, true);

-- MailActivation : UPLOAD_REQUEST_CLOSED_BY_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (203, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (204, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (205, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(23, false, 'UPLOAD_REQUEST_CLOSED_BY_OWNER', 203, 204, 205, 1, true);

-- MailActivation : UPLOAD_REQUEST_DELETED_BY_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (206, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (207, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (208, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(24, false, 'UPLOAD_REQUEST_DELETED_BY_OWNER', 206, 207, 208, 1, true);

-- MailActivation : UPLOAD_REQUEST_NO_SPACE_LEFT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (209, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (210, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (211, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(25, false, 'UPLOAD_REQUEST_NO_SPACE_LEFT', 209, 210, 211, 1, true);

-- MailActivation : UPLOAD_REQUEST_FILE_DELETED_BY_SENDER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (212, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (213, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (214, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(26, false, 'UPLOAD_REQUEST_FILE_DELETED_BY_SENDER', 212, 213, 214, 1, true);

-- MailActivation : SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (215, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (216, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (217, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(27, false, 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER', 215, 216, 217, 1, true);

-- MailActivation : UNDOWNLOADED_SHARED_DOCUMENTS_ALERT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (218, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (219, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (220, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(28, false, 'UNDOWNLOADED_SHARED_DOCUMENTS_ALERT', 218, 219, 220, 1, true);

-- MailActivation : ANONYMOUS_URL__NOTIFICATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (221, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (222, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (223, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(29, false, 'ANONYMOUS_URL__NOTIFICATION', 221, 222, 223, 1, true);


-- MailActivation : END

-- UPDATE FUNCTIONALITIES THAT CONTAINS ACKNOWLEDGMENT INSTEAD OF ACKNOWLEDGMENT
UPDATE functionality SET identifier = 'SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER' WHERE idenditifier = 'SHARE_CREATION_ACKNOWLEDGMENT_FOR_OWNER';
UPDATE functionality SET identifier = 'UPLOAD_REQUEST_ACKNOWLEDGEMENT' WHERE identifier = 'UPLOAD_REQUEST_ACKNOWLEDGMENT';

-- step 1 : delete subclass functionality
CREATE TEMP TABLE temptable_1_10_unit (id int8);
INSERT INTO temptable_1_10_unit SELECT unit_id FROM functionality_unit as fu join functionality as f on f.id = fu.functionality_id WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';

CREATE TEMP TABLE temptable_1_10 (id int8);

INSERT INTO temptable_1_10 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';
INSERT INTO temptable_1_10 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';
INSERT INTO temptable_1_10 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';

INSERT INTO temptable_1_10 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';
INSERT INTO temptable_1_10 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';
INSERT INTO temptable_1_10 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';

INSERT INTO temptable_1_10 SELECT policy_activation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';
INSERT INTO temptable_1_10 SELECT policy_configuration_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';
INSERT INTO temptable_1_10 SELECT policy_delegation_id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';

-- step 2 : delete subclass functionality
DELETE FROM functionality_unit WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION');
DELETE FROM functionality_boolean WHERE functionality_id in (SELECT id FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD');

-- step 3 : delete unit
DELETE FROM unit WHERE id in (SELECT id FROM temptable_1_10_unit);

-- step 4 : delete subclass functionality
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__PASSWORD';
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL__EXPIRATION';
DELETE FROM functionality WHERE identifier = 'UPLOAD_REQUEST_ENTRY_URL';

-- step 5 : delete policies
DELETE FROM policy WHERE id in (SELECT id FROM temptable_1_10);

DROP TABLE upload_request_entry_url;
-- constraint was : FKupload_req784409


-- UPLOAD_REQUEST_ENTRY_URL = 28
DELETE FROM mail_content_lang WHERE mail_content_type = 28;
DELETE FROM mail_content WHERE mail_content_type = 28;


-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = False;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = True;

-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = False;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = True;

-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.10.0');

COMMIT;

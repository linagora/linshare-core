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
  creation_date           timestamp(6)NOT NULL, 
  modification_date       timestamp(6)NOT NULL, 
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
  creation_date       timestamp(6)NOT NULL, 
  modification_date   timestamp(6)NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_layout (
  id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  name               varchar(255) NOT NULL, 
  visible            bool NOT NULL, 
  layout             text NOT NULL, 
  creation_date      timestamp(6)NOT NULL, 
  modification_date  timestamp(6)NOT NULL, 
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
  creation_date      timestamp(6)NOT NULL, 
  modification_date   timestamp(6)NOT NULL, 
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
  creation_date      timestamp(6)NOT NULL, 
  modification_date  timestamp(6)NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mail_content_lang (
  id                 int8 NOT NULL, 
  language          int4 NOT NULL, 
  mail_content_id   int8 NOT NULL, 
  mail_config_id    int8 NOT NULL, 
  mail_content_type int4 NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  PRIMARY KEY (id));


ALTER TABLE domain_abstract ADD mailconfig_id int8;
ALTER TABLE domain_abstract ADD mime_policy_id int8;

ALTER TABLE functionality ADD param bool DEFAULT 'false' NOT NULL; 
ALTER TABLE functionality ADD parent_identifier varchar(255); 
ALTER TABLE functionality ADD policy_delegation_id int8;


ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif244118 FOREIGN KEY (activation_policy_id) REFERENCES policy (id);
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
DROP TABLE allowed_mimetype;



-- %{image}    <img src="cid:image.part.1@linshare.org" /><br/><br/>

INSERT INTO mail_layout (id, name,domain_abstract_id,visible,plaintext,modification_date,creation_date,uuid,layout) VALUES (1, 'Default HTML layout', 1,true,false,now(),now(),'15044750-89d1-11e3-8d50-5404a683a462',E'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n<html xmlns="http://www.w3.org/1999/xhtml">\n<head>\n<title>${mailSubject}</title>\n<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />\n<meta http-equiv="Content-Style-Type" content="text/css" />\n<style type="text/css">\npre { margin-top: .25em; font-family: Verdana, Arial, Helvetica, sans-serif; color: blue; }\nul { margin-top: .25em; padding-left: 1.5em; }\n</style>\n</head>\n<body>\n${image}\n${personalMessage}\n${greetings}\n${body}\n <hr/>\n${footer}\n</body>\n</html>');
INSERT INTO mail_layout (id, name,domain_abstract_id,visible,plaintext,modification_date,creation_date,uuid,layout) VALUES (2, 'Default plaintext layout', 1,true,true,now(),now(),'db044da6-89d1-11e3-b6a9-5404a683a462', E'${personalMessage}\n\n${greetings}\n\n${body}\n-- \n${footer}\n');

INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (1, 'FOOTER_HTML', 0,1, true, false, E'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - THE Secure, Open-Source File Sharing Tool','e85f4a22-8cf2-11e3-8a7a-5404a683a462',now(),now());
INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (2, 'FOOTER_HTML', 1,1, true, false, E'<a href="http://www.linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Logiciel libre de partage de fichiers sécurisé','c9e8e482-8daa-11e3-9d04-5404a683a462',now(),now());


INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (3, 'FOOTER_TXT', 0,1, true, true, E'LinShare - http://linshare.org - THE Secure, Open-Source File Sharing Tool','83e756e8-8cf7-11e3-b493-5404a683a462',now(),now());
INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (4, 'FOOTER_TXT', 1,1, true, true, E'LinShare - http://www.linshare.org/ - Logiciel libre de partage de fichiers sécurisé','d56a8f54-8daa-11e3-9cc2-5404a683a462',now(),now());


-- LANGUAGE DEFAULT 0

-- ANONYMOUS_DOWNLOAD
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (1, '938f91ab-b33c-4184-900f-c8a595fc6cd9', 1, 0,  0, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Anonymous user downloaded a file', E'An unknown user ${actorRepresentation} has just downloaded a file you made available for sharing', E'An unknown user ${email} has just downloaded the following file(s) you made available via LinShare:<ul>${documentNames}</ul><br/>');
-- REGISTERED_DOWNLOAD
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (2, '403e5d8b-bc38-443d-8b94-bab39a4460af', 1, 0,  1, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Registered user downloaded a file', E'A user ${actorRepresentation} has just downloaded a file you made available for sharing', E'${recipientFirstName} ${recipientLastName} has just downloaded the following file(s) you made available to her/him via LinShare:<ul>${documentNames}</ul><br/>');
-- NEW_GUEST
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (3, 'a1ca74a5-433d-444a-8e53-8daa08fa0ddb', 1, 0,  2, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'New guest', E'Your LinShare account has been sucessfully created', E'<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use and enjoy LinShare!<br/><br/>To login, please go to: <a href="${url}">${url}</a><br/><br/>Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul><br/>');
-- RESET_PASSWORD
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (4, '753d57a8-4fcc-4346-ac92-f71828aca77c', 1, 0,  3, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Password reset', E'Your password has been reset', E'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul><br/>');
-- SHARED_DOC_UPDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (5, '09a50c58-b430-4ccf-ab3e-0257c463d8df', 1, 0,  4, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Shared document was updated', E'A user ${actorRepresentation} has just modified a shared file you still have access to', E'<strong>${firstName} ${lastName}</strong> has just modified the following shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- SHARED_DOC_DELETED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (6, '554a3a2b-53b1-4ec8-9462-2d6053b80078', 1, 0,  5, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Shared document was deleted', E'A user ${actorRepresentation} has just deleted a shared file you had access to!', E'<strong>${firstName} ${lastName}</strong> has just deleted a previously shared file <strong>${documentName}</strong>.<br/>');
-- SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (7, 'e7bf56c2-b015-4e64-9f07-3c7e2f3f9ca8', 1, 0,  6, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Shared document is soon to be outdated', E'A LinShare workspace is about to be deleted', E'Your access to the shared file ${documentName}, granted by ${firstName} ${lastName}, will expire in ${nbDays} days. Remember to download it before!<br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- DOC_UPCOMING_OUTDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (8, '1507e9c0-c1e1-4e0f-9efb-506f63cbba97', 1, 0,  7, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'Document is soon to be outdated', E'A shared file is about to be deleted!', E'Your access to the file <strong>${documentName}</strong> will expire in ${nbDays} days!<br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- NEW_SHARING
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (9, '250e4572-7bb9-4735-84ff-6a8af93e3a42', 1, 0,  8, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'New sharing', E'A user ${actorRepresentation} has just made a file available to you!', E'<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- NEW_SHARING_PROTECTED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (10, '1e972f43-619c-4bd6-a1bd-10667b80af74', 1, 0,  9, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'New sharing with password protection', E'A user ${actorRepresentation} has just made a file available to you!', E'<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>The password to be used is: <code>${password}</code><br/><br/>');
-- NEW_SHARING_CYPHERED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (11, 'fef9a3f1-6011-46cd-8d39-6bd1bc02f899', 1, 0, 10, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'New sharing of a cyphered file', E'A user ${actorRepresentation} has just made a file available to you!', E'<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/><p>One or more received files are <b>encrypted</b>. After download is complete, make sure to decrypt them locally by using the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You must use the <i>password</i> granted to you by the user who made the file(s) available for sharing.</p><br/><br/>');
-- NEW_SHARING_CYPHERED_PROTECTED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (12, '2da85945-7793-43f4-b547-eacff15a6f88', 1, 0, 11, true, false, now(), now(), E'Hello ${firstName} ${lastName},<br/><br/>', E'New sharing with password protection of a cyphered file', E'A user ${actorRepresentation} has just made a file available to you!', E'<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul><br/>To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/><p>One or more received files are <b>encrypted</b>. After download is complete, make sure to decrypt them locally by using the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You must use the <i>password</i> granted to you by the user who made the file(s) available for sharing.</p><br/><br/>The password to be used is: <code>${password}</code><br/><br/>');


-- LANGUAGE FRENCH 1

-- ANONYMOUS_DOWNLOAD
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (13, 'cc3bb3c6-e21e-44fe-b552-9acf654e4988', 1, 1,  0, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Un extern a téléchargé un fichier', E'Un utilisateur anonyme ${actorRepresentation} a téléchargé des fichiers en partage', E'L’utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul><br/>');
-- REGISTERED_DOWNLOAD
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (14, '7e1b4fe3-c859-453d-ae80-9751e2c4811c', 1, 1,  1, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Un utilisateur a téléchargé un fichier', E'Un utilisateur ${actorRepresentation} a téléchargé des fichiers en partage', E'${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul><br/>');
-- NEW_GUEST
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (15, 'e68d9b75-1ff3-4e0e-8487-8579c531b391', 1, 1,  2, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouveau compte invité', E'Votre compte LinShare a été créé', E'<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/><br/>Vous pouvez vous connecter à cette adresse&nbsp;: <a href="${url}">${url}</a><br/><br/>Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul><br/>');
-- RESET_PASSWORD
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (16, 'ae789e03-bd78-428f-8986-d85b96e1e08d', 1, 1,  3, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouveau mot de passe', E'Votre nouveau mot de passe', E'Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul><br/>');
-- SHARED_DOC_UPDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (17, 'c88f9821-44d7-4330-97a1-8c47f0be4572', 1, 1,  4, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Mise à jour d´un partage', E'Un utilisateur ${actorRepresentation} a mis à jour un fichier partagé', E'<strong>${firstName} ${lastName}</strong> a mis à jour le fichier partagé <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nom du nouveau fichier&nbsp;: ${fileName}</li><li>Taille du fichier&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul><br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- SHARED_DOC_DELETED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (18, '9626399e-1152-471f-83a1-372b08800b1a', 1, 1,  5, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Suppression d´un partage', E'Un utilisateur ${actorRepresentation} a supprimé un fichier partagé', E'<strong>${firstName} ${lastName}</strong> a supprimé le fichier partagé <strong>${documentName}</strong>.<br/>');
-- SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (19, '06b1f018-9b3d-4a1b-af90-c03e5e1ec314', 1, 1,  6, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Expiration d´un partage', E'Un partage va bientôt expirer', E'Le partage du fichier ${documentName} provenant de <strong>${firstName} ${lastName}</strong> va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier avant son expiration.<br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- DOC_UPCOMING_OUTDATED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (20, 'edb87b54-007e-4654-8709-e8eb3db19366', 1, 1,  7, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Expiration d´un fichier', E'Un fichier va bientôt être supprimé', E'Le fichier <strong>${documentName}</strong> va expirer dans ${nbDays} jours.<br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- NEW_SHARING
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (21, 'ae9ced24-64a3-498d-a576-a23864c56127', 1, 1,  8, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouveau partage', E'Un utilisateur ${actorRepresentation} vous a déposé des fichiers en partage', E'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul><br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>');
-- NEW_SHARING_PROTECTED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (22, '0e602bb9-63f8-4c88-aa61-d2338cfcbb5b', 1, 1,  9, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouveau partage avec mot de passe', E'Un utilisateur ${actorRepresentation} vous a déposé des fichiers en partage', E'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul><br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/>');
-- NEW_SHARING_CYPHERED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (23, 'e9e8fd55-b06f-4d04-b46d-4ca4f58ebbef', 1, 1, 10, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouveau partage avec chiffrement', E'Un utilisateur ${actorRepresentation} vous a déposé des fichiers en partage', E'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul><br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/><p>Certains de vos fichiers sont <strong>chiffrés</strong>. Après le téléchargement, vous devez les déchiffrer localement avec l’application&nbsp;:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir du <em>mot de passe de déchiffrement</em> qui a dû vous être communiqué par l’expéditeur des fichiers.</p><br/>');
-- NEW_SHARING_CYPHERED_PROTECTED
INSERT INTO mail_content (id, uuid, domain_abstract_id, language, mail_content_type, visible, plaintext, modification_date, creation_date, greetings, name, subject, body) VALUES  (24, 'd1608d46-efb7-4465-897c-d8d34c036f21', 1, 1, 11, true, false, now(), now(), E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Nouveau partage avec chiffrement et mot de passe', E'Un utilisateur ${actorRepresentation} vous a déposé des fichiers en partage', E'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul><br/>Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/><p>Certains de vos fichiers sont <strong>chiffrés</strong>. Après le téléchargement, vous devez les déchiffrer localement avec l’application&nbsp;:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir du <em>mot de passe de déchiffrement</em> qui a dû vous être communiqué par l’expéditeur des fichiers.</p><br/>Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/><br/>');



INSERT INTO mail_config (id, name, domain_abstract_id, visible, mail_layout_html_id, mail_layout_text_id, modification_date, creation_date, uuid) VALUES (1, 'Default mail config', 1, true, 1, 2, now(), now(), '946b190d-4c95-485f-bfe6-d288a2de1edd');

INSERT INTO mail_footer_lang(id, mail_config_id, language, mail_footer_id, uuid) VALUES (1, 1, 0, 1, 'bf87e580-fb25-49bb-8d63-579a31a8f81e');
INSERT INTO mail_footer_lang(id, mail_config_id, language, mail_footer_id, uuid) VALUES (2, 1, 1, 2, 'a6c8ee84-b5a8-4c96-b148-43301fbccdd9');

INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (1, 1, 0, 1, 0, 'd6868568-f5bd-4677-b4e2-9d6924a58871');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (2, 1, 0, 2, 1, '4f3c4723-531e-449b-a1ae-d304fd3d2387');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (3, 1, 0, 3, 2, '81041673-c699-4849-8be4-58eea4507305');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (4, 1, 0, 4, 3, '85538234-1fc1-47a2-850d-7f7b59f1640e');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (5, 1, 0, 5, 4, '796a98eb-0b97-4756-b23e-74b5a939c2e3');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (6, 1, 0, 6, 5, 'ed70cc00-099e-4c44-8937-e8f51835000b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (7, 1, 0, 7, 6, 'f355793b-17d4-499c-bb2b-e3264bc13dbd');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (8, 1, 0, 8, 7, '5a6764fc-350c-4f10-bdb0-e95ca7607607');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (9, 1, 0, 9, 8, 'befd8182-88a6-4c72-8bae-5fcb7a79b8e7');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (10, 1, 0, 10, 9, 'fa59abad-490b-4cd5-9a31-3c3302fc4a18');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (11, 1, 0, 11, 10, '5bd828fa-d25e-47fa-9c0d-1bb84304e692');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (12, 1, 0, 12, 11, 'a9096a7e-949c-4fae-aedf-2347c40cd999');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (13, 1, 1, 13, 0, 'd0af96a7-6a9c-4c3f-8b8c-7c8e2d0449e1');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (14, 1, 1, 14, 1, '28e5855a-c0e7-40fc-8401-9cf25eb53f03');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (15, 1, 1, 15, 2, '41d0f03d-57dd-420e-84b0-7908179c8329');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (16, 1, 1, 16, 3, '72c0fff4-4638-4e98-8223-df27f8f8ea8b');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (17, 1, 1, 17, 4, '8b7f57c1-b4a1-4896-8e19-d3ebf3af4831');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (18, 1, 1, 18, 5, '6fbabf1a-58c0-49b9-859e-d24b0af38c87');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (19, 1, 1, 19, 6, 'b85fc62f-d9eb-454b-9289-fec5eab51a76');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (20, 1, 1, 20, 7, '25540d2d-b3b8-46a9-811b-0549ad300fe0');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (21, 1, 1, 21, 8, '72ae03e7-5865-433c-a2be-a95c655a8e17');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (22, 1, 1, 22, 9, 'e2af2ff6-585b-4cdc-a887-1755e42fcde6');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (23, 1, 1, 23, 10, '1ee1c8bc-75e9-4fbe-a34b-893a86704ec9');
INSERT INTO mail_content_lang(id, mail_config_id, language, mail_content_id, mail_content_type, uuid) VALUES (24, 1, 1, 24, 11, '12242aa8-b75e-404d-85df-68e7bb8c04af');

UPDATE domain_abstract SET mailconfig_id = 1;

CREATE TABLE mime_policy (
  id                 int8 NOT NULL, 
  domain_id         int8 NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  name              varchar(255) NOT NULL, 
  mode              int4 NOT NULL, 
  displayable       int4 NOT NULL, 
  creation_date     timestamp(6)NOT NULL, 
  modification_date timestamp(6)NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mime_type (
  id                 int8 NOT NULL, 
  mime_policy_id    int8 NOT NULL, 
  uuid              varchar(255) NOT NULL, 
  mime_type         text NOT NULL, 
  extensions        text NOT NULL, 
  enable            bool NOT NULL, 
  displayable       bool NOT NULL, 
  creation_date     timestamp(6)NOT NULL, 
  modification_date timestamp(6)NOT NULL, 
  PRIMARY KEY (id));


ALTER TABLE mime_type ADD CONSTRAINT FKmime_type145742 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);
ALTER TABLE mime_policy ADD CONSTRAINT FKmime_polic613419 FOREIGN KEY (domain_id) REFERENCES domain_abstract (id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs809928 FOREIGN KEY (mime_policy_id) REFERENCES mime_policy (id);

INSERT INTO mime_policy(id, domain_id, uuid, name, mode, displayable, creation_date, modification_date) VALUES(1, 1, '3d6d8800-e0f7-11e3-8ec0-080027c0eef0', 'Default Mime Policy', 0, 0, now(), now());
UPDATE domain_abstract SET mime_policy_id=1;




-- Active Directory domain pattern.
INSERT INTO domain_pattern(
 domain_pattern_id,
 identifier,
 description,
 auth_command,
 search_user_command,
 system,
 auto_complete_command_on_first_and_last_name,
 auto_complete_command_on_all_attributes,
 search_page_size,
 search_size_limit,
 completion_page_size,
 completion_size_limit)
VALUES (
 2,
 'default-pattern-AD',
 'This is pattern the default pattern for the Active Directory structure.',
 'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(sAMAccountName="+login+")))");',
 'ldap.search(domain, "(&(objectClass=user)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
 true,
 'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
 'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
 100,
 100,
 10,
 10
 );
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (5, 'user_mail', 'mail', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (6, 'user_firstname', 'givenName', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (7, 'user_lastname', 'sn', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (8, 'user_uid', 'sAMAccountName', false, true, true, 2, false);

-- OpenLdap domain pattern.
INSERT INTO domain_pattern(
 domain_pattern_id,
 identifier,
 description,
 auth_command,
 search_user_command,
 system,
 auto_complete_command_on_first_and_last_name,
 auto_complete_command_on_all_attributes,
 search_page_size,
 search_size_limit,
 completion_page_size,
 completion_size_limit)
VALUES (
 3,
 'default-pattern-openldap',
 'This is pattern the default pattern for the OpenLdap structure.',
 'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
 'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
 true,
 'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
 'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
 100,
 100,
 10,
 10
 );
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (9, 'user_mail', 'mail', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (10, 'user_firstname', 'givenName', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (11, 'user_lastname', 'sn', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (12, 'user_uid', 'uid', false, true, true, 3, false);


ALTER TABLE version RENAME description TO version;


-- LinShare version
INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.6.0');

COMMIT;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;



CREATE TABLE mail_notification (
  Id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  policy_id          int8 NOT NULL, 
  identifier         varchar(255) NOT NULL, 
  system             bool NOT NULL, 
  creation_date      date NOT NULL, 
  modification_date  date NOT NULL, 
  uuid               varchar(255) NOT NULL, 
  PRIMARY KEY (Id));
CREATE TABLE mail_config (
  Id                   int8 NOT NULL, 
  mail_layout_html_id int8 NOT NULL, 
  domain_abstract_id  int8 NOT NULL, 
  name                varchar(255) NOT NULL, 
  visible             bool NOT NULL, 
  mail_layout_text_id int8 NOT NULL, 
  uuid                varchar(255) NOT NULL, 
  PRIMARY KEY (Id));
CREATE TABLE mail_layout (
  Id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  name               varchar(255) NOT NULL, 
  visible            bool NOT NULL, 
  layout             text NOT NULL, 
  creation_date      date NOT NULL, 
  modification_date  date NOT NULL, 
  uuid               varchar(255) NOT NULL, 
  plaintext          bool NOT NULL, 
  PRIMARY KEY (Id));
CREATE TABLE mail_footer (
  Id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  name               varchar(255) NOT NULL, 
  visible            bool NOT NULL, 
  language           int4 NOT NULL, 
  footer             text NOT NULL, 
  creation_date      date NOT NULL, 
  modification_date  date NOT NULL, 
  uuid               varchar(255) NOT NULL, 
  plaintext          bool NOT NULL, 
  PRIMARY KEY (Id));
CREATE TABLE mail_footer_lang (
  Id                int8 NOT NULL, 
  language         int4 NOT NULL, 
  mail_config_id   int8 NOT NULL, 
  mail_footer_id   int8 NOT NULL, 
  PRIMARY KEY (Id));
CREATE TABLE mail_content (
  Id                  int8 NOT NULL, 
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
  PRIMARY KEY (Id));
CREATE TABLE mail_content_lang (
  Id                int8 NOT NULL, 
  language         int4 NOT NULL, 
  mail_content_id  int8 NOT NULL, 
  mail_config_id   int8 NOT NULL, 
  mail_content_type int4 NOT NULL, 
  PRIMARY KEY (Id));




ALTER TABLE domain_abstract ADD mailconfig_id int8;



ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif460924 FOREIGN KEY (policy_id) REFERENCES policy (id);
ALTER TABLE mail_notification ADD CONSTRAINT FKmail_notif777760 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_layout ADD CONSTRAINT FKmail_layou627738 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer ADD CONSTRAINT FKmail_foote767112 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT FKmail_foote800257 FOREIGN KEY (mail_footer_id) REFERENCES mail_footer (Id);
ALTER TABLE domain_abstract ADD CONSTRAINT FKdomain_abs159146 FOREIGN KEY (mailconfig_id) REFERENCES mail_config (Id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi697783 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_footer_lang ADD CONSTRAINT FKmail_foote320110 FOREIGN KEY (mail_config_id) REFERENCES mail_config (Id);
ALTER TABLE mail_content ADD CONSTRAINT FKmail_conte385227 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte911191 FOREIGN KEY (mail_config_id) REFERENCES mail_config (Id);
ALTER TABLE mail_content_lang ADD CONSTRAINT FKmail_conte32960 FOREIGN KEY (mail_content_id) REFERENCES mail_content (Id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi540307 FOREIGN KEY (mail_layout_html_id) REFERENCES mail_layout (Id);
ALTER TABLE mail_config ADD CONSTRAINT FKmail_confi611322 FOREIGN KEY (mail_layout_text_id) REFERENCES mail_layout (Id);



-- %{image}    <img src="cid:image.part.1@linshare.org" /><br/><br/>

INSERT INTO mail_layout (id, name,domain_abstract_id,visible,plaintext,modification_date,creation_date,uuid,layout) VALUES (1, 'html', 1,true,false,now(),now(),'15044750-89d1-11e3-8d50-5404a683a462',E'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n<html xmlns="http://www.w3.org/1999/xhtml">\n<head>\n<title>%{mailSubject}</title>\n<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />\n<meta http-equiv="Content-Style-Type" content="text/css" />\n<style type="text/css">\npre { margin-top: .25em; font-family: Verdana, Arial, Helvetica, sans-serif; color: blue; }\nul { margin-top: .25em; padding-left: 1.5em; }\n</style>\n</head>\n<body>\n%{image}\n%{personalMessage}\n%{greetings}\n%{body}\n <hr/>\n%{footer}\n</body>\n</html>');
INSERT INTO mail_layout (id, name,domain_abstract_id,visible,plaintext,modification_date,creation_date,uuid,layout) VALUES (2, 'text', 1,true,true,now(),now(),'db044da6-89d1-11e3-b6a9-5404a683a462', E'%{personalMessage}\n\n%{greetings}\n\n%{body}\n-- \n%{footer}\n');

-- language 0 is english


-- Template GUEST_INVITATION
-- Subject NEW_GUEST
INSERT INTO mail_content (id, name, language, domain_abstract_id, visible, plaintext, mail_content_type,subject,greetings,body,uuid) VALUES (1, 'GUEST_INVITATION', 0,1, true, false,8, E'Your LinShare account has been successfully created',E'Hello ${firstName} ${lastName},<br/><br/>',E'<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use and enjoy LinShare!<br/>','c7b41d62-8cf1-11e3-bbe6-5404a683a462');
INSERT INTO mail_content (id, name, language, domain_abstract_id, visible, plaintext, mail_content_type,subject,greetings,body,uuid) VALUES (2, 'GUEST_INVITATION', 1,1, true, false,8, E'Votre compte LinShare a été créé',E'Bonjour ${firstName} ${lastName},<br/><br/>',E'<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/>','df47481e-8cf1-11e3-b022-5404a683a462');


INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (1, 'FOOTER_HTML', 0,1, true, false, E'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - THE Secure, Open-Source File Sharing Tool','e85f4a22-8cf2-11e3-8a7a-5404a683a462',now(),now());
INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (2, 'FOOTER_HTML', 1,1, true, false, E'<a href="http://www.linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Logiciel libre de partage de fichiers sécurisé','c9e8e482-8daa-11e3-9d04-5404a683a462',now(),now());


INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (3, 'FOOTER_TXT', 0,1, true, true, E'LinShare - http://linshare.org - THE Secure, Open-Source File Sharing Tool','83e756e8-8cf7-11e3-b493-5404a683a462',now(),now());
INSERT INTO mail_footer (id, name, language, domain_abstract_id, visible, plaintext, footer,uuid,modification_date,creation_date) VALUES (4, 'FOOTER_TXT', 1,1, true, true, E'LinShare - http://www.linshare.org/ - Logiciel libre de partage de fichiers sécurisé','d56a8f54-8daa-11e3-9cc2-5404a683a462',now(),now());



INSERT INTO mail_config (id, name, domain_abstract_id, visible, mail_layout_html_id, mail_layout_text_id,uuid) VALUES (1,'config 1',1,true,1,2,'42cf9cf6-8da6-11e3-b6aa-5404a683a462');

INSERT INTO mail_content_lang(id,mail_config_id,language,mail_content_id,mail_content_type) VALUES (1,1,0,1,8);
INSERT INTO mail_content_lang(id,mail_config_id,language,mail_content_id,mail_content_type) VALUES (2,1,1,2,8);

INSERT INTO mail_footer_lang(id,mail_config_id,language,mail_footer_id) VALUES (1,1,0,1);
INSERT INTO mail_footer_lang(id,mail_config_id,language,mail_footer_id) VALUES (2,1,1,2);





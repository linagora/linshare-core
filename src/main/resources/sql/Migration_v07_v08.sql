insert into linshare_version (id, description) values (8, 'LinShare version 0.8');


-- Groups
create table linshare_group (
        group_id int8 not null,
		group_user_id int8 not null,
		name varchar(255) not null,
        functional_email varchar(255),
        description text,
        primary key (group_id)
);

create table linshare_group_members (
        group_id int8 not null,
        user_id int8 not null,
        member_type_id int4 not null,
        membership_date timestamp not null,
		primary key (group_id,user_id)
);

create index index_group_name on linshare_group (name);
create index index_group_user_id on linshare_group_members (user_id);

alter table linshare_group_members 
        add constraint FK3684AE4C675E97A1 
        foreign key (user_id) 
        references linshare_user;

alter table linshare_group_members 
        add constraint FK4284AE4C675E9722 
        foreign key (group_id) 
        references linshare_group;

alter table linshare_group
        add constraint FK3684CCCCCCAE97A1 
        foreign key (group_user_id) 
        references linshare_user;

ALTER TABLE linshare_document add column shared_with_group bool;
UPDATE linshare_document SET shared_with_group='false';


-- Restricted guests
create table linshare_allowed_contact (
        id int8 not null,
        user_id int8 not null,
        contact_id int8 not null,
        primary key (id)
);

create index index_allowed_contact_user_id on linshare_allowed_contact (user_id);
create index index_allowed_contact_contact_id on linshare_allowed_contact (contact_id);

alter table linshare_allowed_contact
        add constraint FK3684FF4C67FF97FF 
        foreign key (user_id) 
        references linshare_user;

alter table linshare_allowed_contact
        add constraint FK4284AA4C675AA721 
        foreign key (contact_id) 
        references linshare_user;

alter table linshare_user add column restricted bool default false;


-- Timestamp
alter table linshare_parameter add COLUMN active_doc_time_stamp bool default false;
alter table linshare_document add COLUMN timestamp bytea;


-- New mail content management system
create table linshare_mail_templates (
        parameter_id int8 not null,
        template_id int4 not null,
        content_html text,
        content_txt text,
        language_id int4 not null
);

create table linshare_mail_subjects (
        parameter_id int8 not null,
        subject_id int4 not null,
        content text,
        language_id int4 not null
);

alter table linshare_mail_templates 
        add constraint FDD6A0CABCA44B78EB 
        foreign key (parameter_id) 
        references linshare_parameter;
        
alter table linshare_mail_subjects 
        add constraint FDD6CCCABCA44789EB 
        foreign key (parameter_id) 
        references linshare_parameter;
        
-- Template GREETINGS
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,0,'Hi ${firstName} ${lastName},<br/><br/>','Hi ${firstName} ${lastName},',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,0,'Bonjour ${firstName} ${lastName},<br/><br/>','Bonjour ${firstName} ${lastName},',1);

-- Template FOOTER
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,1,'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Open Source secured file sharing application','LinShare - http://linshare.org - Open Source secured file sharing application',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,1,'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Logiciel libre de partage de fichiers sécurisé','LinShare - http://linshare.org - Logiciel libre de partage de fichiers sécurisé',1);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,2,'An anonymous user ${email} downloaded the following file(s) you shared via LinShare:<ul>${documentNames}</ul>','An anonymous user ${email} downloaded the following file(s) you shared via LinShare:\n${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,2,'Un utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>','Un utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare :\n${documentNamesTxt}',1);

-- Template CONFIRM_DOWNLOAD_REGISTERED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,3,'${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:<ul>${documentNames}</ul>','${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:\n${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,3,'${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) suivant que vous lui avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>','${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare :\n${documentNamesTxt}',1);

-- Template LINSHARE_URL
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,4,'You can login to this address: <a href="${url}">${url}</a><br/>','You can now login to this address: ${url}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,4,'Vous pouvez vous connecter à cette adresse&nbsp;: <a href="${url}">${url}</a><br/>','Vous pouvez vous connecter à cette adresse : ${url}',1);

-- Template FILE_DOWNLOAD_URL
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,5,'In order to download the files, click on this link or paste it into your browser: <a href="${url}${urlparam}">${url}${urlparam}</a>','In order to download the files, click on this link or paste it into your browser:\n${url}${urlparam}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,5,'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>','Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur :\n${url}${urlparam}',1);

-- Template DECRYPT_URL
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,6,'<p>Some received files are <b>encrypted</b>. After downloading, take care of decrypting localy with the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You have to use <i>password</i> which has been communicated by the person who has done this sharing.</p><br/>','Some received files are encrypted. After downloading, take care of decrypting localy with the application:\n${jwsEncryptUrl}\nYou have to use <i>password</i> which has been communicated by the person who has done this sharing.\n',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,6,'<p>Certains de vos fichiers sont <b>chiffrés</b>. Après le téléchargement, vous devez les déchiffrer localement avec l''application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir <i>du mot de passe de déchiffrement</i> qui a dû vous être communiqué par l''expéditeur des fichiers.</p><br/>','Certains de vos fichiers sont chiffrés. Après le téléchargement, vous devez les déchiffrer localement avec l''application:\n${jwsEncryptUrl}\nVous devez vous munir du mot de passe de déchiffrement qui a du vous être communiqué par l''expéditeur des fichiers.\n',1);

-- Template PERSONAL_MESSAGE
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,7,'<strong>Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>','Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,7,'<strong>Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>','Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------',1);

-- Template GUEST_INVITATION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,8,'<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use LinShare.<br/>','${ownerFirstName} ${ownerLastName} invites you to use LinShare.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,8,'<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/>','${ownerFirstName} ${ownerLastName} vous invite à utiliser LinShare.',1);

-- Template ACCOUNT_DESCRIPTION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,9,'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul>','Your LinShare account:\n- Login: ${mail}  (your e-mail address)\n- Password: ${password}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,9,'Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul>','Votre compte LinShare : \n- identifiant : ${mail}  (votre adresse électronique) \n- mot de passe : ${password}',1);

-- Template SHARE_NOTIFICATION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,10,'<strong>${firstName} ${lastName}</strong> sent you ${number} file(s):<ul>${documentNames}</ul>','${firstName} ${lastName} sent you ${number} file(s):\n\n${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,10,'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul>','${firstName} ${lastName} a mis en partage ${number} fichier(s) à votre attention :\n\n${documentNamesTxt}',1);

-- Template PASSWORD_GIVING
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,11,'The associated password to use is: <code>${password}</code><br/>','The associated password to use is: ${password}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,11,'Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/>','Le mot de passe à utiliser est : ${password}',1);

-- Template FILE_UPDATED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,12,'<strong>${firstName} ${lastName}</strong> has updated the shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul>','${firstName} ${lastName} has updated the shared file ${fileOldName}:\n- New file name: ${fileName}\n- File size: ${fileSize}\n- MIME type: ${mimeType}\n',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,12,'<strong>${firstName} ${lastName}</strong> a mis à jour le fichier partagé <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nom du nouveau fichier&nbsp;: ${fileName}</li><li>Taille du fichier&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>','${firstName} ${lastName} a mis à jour le fichier partagé ${fileOldName} : \n- nom du nouveau fichier : ${fileName}\n- taille du fichier : ${fileSize}\n- type MIME : ${mimeType}\n',1);

-- Template GROUP_SHARE_NOTIFICATION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,13,'<strong>${firstName} ${lastName}</strong> has shared ${number} file(s) with the group ${groupName}:<ul>${documentNames}</ul>','${firstName} ${lastName} has shared ${number} file(s) with the group ${groupName}:\n\n${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,13,'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) pour le groupe ${groupName}&nbsp;:<ul>${documentNames}</ul>','${firstName} ${lastName} a mis en partage ${number} fichier(s) pour le groupe ${groupName} :\n\n${documentNamesTxt}',1);

-- Template GROUP_NEW_MEMBER
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,14,'You are now member of the group ${groupName}.','You are now member of the group ${groupName}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,14,'Vous appartenez maintenant au groupe ${groupName}.','Vous appartenez maintenant au groupe ${groupName}.',1);

-- Template GROUP_MEMBERSHIP_STATUS
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,15,'Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.','Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,15,'Votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName} est ${status}.','Votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName} est ${status}.',1);

-- Template GROUP_SHARE_DELETED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,16,'${firstName} ${lastName} has deleted the file <strong>${documentName}</strong> shared with the group <strong>${groupName}</strong>.','${firstName} ${lastName} has deleted the file ${documentName} shared with the group ${groupName}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,16,'${firstName} ${lastName} a supprimé le fichier <strong>${documentName}</strong> partagé avec le groupe <strong>${groupName}</strong>.','${firstName} ${lastName} a supprimé le fichier ${documentName} partagé avec le groupe ${groupName}.',1);

-- Template SHARED_FILE_DELETED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,17,'<strong>${firstName} ${lastName}</strong> has deleted the shared file <strong>${documentName}</strong>.','${firstName} ${lastName} has deleted the shared file ${documentName}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,17,'<strong>${firstName} ${lastName}</strong> a supprimé le fichier partagé <strong>${documentName}</strong>.','${firstName} ${lastName} a supprimé le fichier partagé ${documentName}.',1);

-- Subject ANONYMOUS_DOWNLOAD
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 0, 'LinShare: An anonymous user downloaded the file you shared',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 0, 'LinShare : Un utilisateur anonyme a téléchargé des fichiers en partage',1);

-- Subject REGISTERED_DOWNLOAD
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 1, 'LinShare: An user downloaded the file you shared',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 1, 'LinShare : Un utilisateur a téléchargé des fichiers en partage',1);

-- Subject NEW_GUEST
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 2, 'LinShare: Your account on LinShare has been created',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 2, 'LinShare : Votre compte LinShare a été créé',1);

-- Subject RESET_PASSWORD
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 3, 'LinShare: Your password was reset',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 3, 'LinShare : Votre nouveau mot de passe',1);

-- Subject NEW_SHARING
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 4, 'LinShare: A user deposited files in sharing for you',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 4, 'LinShare : Un utilisateur vous a déposé des fichiers en partage',1);

-- Subject SHARED_DOC_UPDATED
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 5, 'LinShare: An user has updated a shared file',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 5, 'LinShare : Un utilisateur a mis à jour un fichier dans vos partages',1);

-- Subject NEW_GROUP_SHARING
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 6, 'LinShare: A user deposited files in sharing for the group ${groupName}',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 6, 'LinShare : Un utilisateur a déposé des fichiers en partage pour le groupe ${groupName}',1);

-- Subject MEMBERSHIP_REQUEST_STATUS
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 7, 'LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 7, 'LinShare : Statut de votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName}',1);

-- Subject NEW_GROUP_MEMBER
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 8, 'LinShare: You are now member of the group ${groupName}',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 8, 'LinShare : Vous appartenez maintenant au groupe ${groupName}',1);

-- Subject GROUP_SHARING_DELETED
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 9, 'LinShare: A file shared with the group ${groupName} has been deleted.',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 9, 'LinShare : Un fichier partagé avec le groupe ${groupName} a été supprimé',1);

-- Subject SHARED_DOC_DELETED
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 10, 'LinShare: An user has deleted a shared file',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 10, 'LinShare : Un utilisateur a supprimé un fichier partagé avec vous',1);



-- Groups
alter table linshare_group_members add column owner_id int8;


-- Sharing date
alter table linshare_share add column sharing_date timestamp;
update linshare_share set sharing_date = (expiration_date - INTERVAL '100 days');
create index index_share_sharing_date on linshare_share (sharing_date);






-----------------
-- AFTER 0.8.1 --
-----------------

-- Subject SHARED_DOC_UPCOMING_OUTDATED
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 11, 'LinShare: A sharing will be soon deleted',0);
insert into linshare_mail_subjects (parameter_id, subject_id, content, language_id) values (1, 11, 'LinShare : Un partage va bientôt expirer',1);

-- Template SECURED_URL_UPCOMING_OUTDATED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,18,'A share from ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the files before this date.','A share from ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the files before this date.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,18,'Un partage provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant cette date.','Un partage provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant cette date.',1);

-- Template SHARED_DOC_UPCOMING_OUTDATED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,19,'The sharing of the file ${documentName} by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download or copy this file.','The sharing of the file ${documentName} by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download or copy this file.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,19,'Le partage du fichier ${documentName} provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier rapidement.','Le partage du fichier ${documentName} provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier rapidement.',1);






-----------------
-- AFTER 0.8.2 --
-----------------
-- dutch translation

-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 0, E'LinShare : Een anonieme gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 1, E'LinShare : Een gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 2, E'LinShare : Uw LinShare account werd aangemaakt.', 2);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 3, E'LinShare : Uw nieuwe wachtwoord', 2);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 4, E'LinShare : Een gebruiker heeft te delen bestanden voor u klaargezet.', 2);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 5, E'LinShare : Een gebruiker heeft een gedeeld bestand bijgewerkt', 2);

-- Subject NEW_GROUP_SHARING
-- LinShare: A user deposited files in sharing for the group ${groupName}
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 6, E'LinShare : Een gebruiker heeft te delen bestanden klaargezet voor de groep ${groupName}', 2);

-- Subject MEMBERSHIP_REQUEST_STATUS
-- LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 7, E'LinShare : Status van uw aanvraag tot toetreding tot de groep ${groupName} en de gebruiker ${newMemberFirstName} ${newMemberLastName}', 2);

-- Subject NEW_GROUP_MEMBER
-- LinShare: You are now member of the group ${groupName}
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 8, E'LinShare : U bent nu lid van de groep ${groupName}', 2);

-- Subject GROUP_SHARING_DELETED
-- LinShare: A file shared with the group ${groupName} has been deleted.
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 9, E'LinShare : Een bestand gedeeld met de groep ${groupName} werd gewist', 2);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 10, E'LinShare : Een gebruiker heeft een gedeeld bestand gewist', 2);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 11, E'LinShare : Een share zal binnenkort gewist worden.', 2);
-- Mail templates
-- Template GREETINGS
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, E'Hallo ${firstName} ${lastName},<br/><br/>', E'Hallo ${firstName} ${lastName},', 2);

-- Template FOOTER
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, E'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Open Source toepassing voor het beveiligd delen van bestanden', E'LinShare - http://linshare.org - Open Source toepassing voor het beveiligd delen van bestanden', 2);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, E'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', E'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, E'${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', E'${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template LINSHARE_URL
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, E'U kan inloggen op dit adres&nbsp;: <a href="${url}">${url}</a><br/>', E'U kan inloggen op dit adres : ${url}', 2);

-- Template FILE_DOWNLOAD_URL
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, E'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', E'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser :\n${url}${urlparam}', 2);

-- Template DECRYPT_URL
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, E'<p>Sommige ontvangen bestanden zijn <b>versleuteld</b>. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>U moet in het bezit zijn van het <i>versleutelwachtwoord</i> dat u gekregen hebt van de persoon die u de bestanden stuurt.</p><br/>', E'Sommige ontvangen bestanden zijn versleuteld. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:\n${jwsEncryptUrl}\nU moet in het bezit zijn van het versleutelwachtwoord dat u gekregen hebt van de persoon die u de bestanden stuurt.\n', 2);

-- Template PERSONAL_MESSAGE
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, E'<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', E'<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 2);

-- Template GUEST_INVITATION
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, E'<strong>${ownerFirstName} ${ownerLastName}</strong> nodigt u uit gebruik te maken van LinShare.<br/>', E'${ownerFirstName} ${ownerLastName} nodigt u uit gebruik te maken van LinShare.', 2);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, E'Uw LinShare account&nbsp;:<ul><li>Identificatie&nbsp;: <code>${mail}</code> &nbsp;(uw e-mailadres)</li><li>Wachtwoord&nbsp;: <code>${password}</code></li></ul>', E'Uw LinShare account :\n- Identificatie : ${mail} (uw e-mailadres)\n- Wachtwoord : ${password}', 2);

-- Template SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, E'<strong>${firstName} ${lastName}</strong> heeft ${number} te delen bestand(en) voor u klaargezet&nbsp;:<ul>${documentNames}</ul>', E'${firstName} ${lastName} heeft ${number} te delen bestand(en) voor u klaargezet :\n\n${documentNamesTxt}', 2);

-- Template PASSWORD_GIVING
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, E'Het bijbehorende wachtwoord dat u moet gebruiken, is&nbsp;: <code>${password}</code><br/>', E'Het bijbehorende wachtwoord dat u moet gebruiken, is : ${password}', 2);

-- Template FILE_UPDATED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, E'<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand bijgewerkt <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nieuwe bestandsnaam&nbsp;: ${fileName}</li><li>Grootte van het bestand&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', E'${firstName} ${lastName} heeft het gedeelde bestand bijgewerkt ${fileOldName} :\n- Nieuwe bestandsnaam : ${fileName}\n- Grootte van het bestand : ${fileSize}\n- Type MIME : ${mimeType}\n', 2);

-- Template GROUP_SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 13, E'<strong>${firstName} ${lastName}</strong> heeft ${number} bestand(en) te delen aangeboden voor de groep${groupName}&nbsp;:<ul>${documentNames}</ul>', E'${firstName} ${lastName} heeft ${number} bestand(en) te delen aangeboden voor de groep${groupName} :\n\n${documentNamesTxt}', 2);

-- Template GROUP_NEW_MEMBER
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 14, E'U bent nu lid van de groep ${groupName}.', E'U bent nu lid van de groep ${groupName}.', 2);

-- Template GROUP_MEMBERSHIP_STATUS
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 15, E'Uw aanvraag tot toetreding van de groep ${groupName} en de gebruiker ${newMemberFirstName} ${newMemberLastName} is ${status}.', E'Uw aanvraag tot toetreding van de groep ${groupName} en de gebruiker ${newMemberFirstName} ${newMemberLastName} is ${status}.', 2);

-- Template GROUP_SHARE_DELETED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 16, E'${firstName} ${lastName} heeft het bestand <strong>${documentName}</strong> dat gedeeld werd met de groep <strong>${groupName}</strong> gewist.', E'${firstName} ${lastName} heeft het bestand ${documentName} dat gedeeld werd met de groep ${groupName} gewist.', 2);

-- Template SHARED_FILE_DELETED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, E'<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand <strong>${documentName}</strong> gewist.', E'${firstName} ${lastName} heeft het gedeelde bestand ${documentName} gewist.', 2);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, E'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', E'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', 2);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, E'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', E'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', 2);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) VALUES (1, E'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 0, 2);


-- Welcome to LinShare, the Open Source secure files sharing system.
INSERT INTO linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) VALUES (1, E'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 1, 2);




-----------------
-- AFTER 0.8.3 --
-----------------

ALTER TABLE linshare_document ADD COLUMN deletion_date timestamp DEFAULT NULL;
ALTER TABLE linshare_parameter ADD COLUMN default_file_expiry_time int4;
ALTER TABLE linshare_parameter ADD COLUMN default_file_expiry_time_unit_id int4;

ALTER TABLE linshare_parameter ADD COLUMN global_quota int8 DEFAULT 0;
ALTER TABLE linshare_parameter ADD COLUMN global_used_quota int8 DEFAULT 0;
ALTER TABLE linshare_parameter ADD COLUMN active_global_quota bool DEFAULT E'false';

UPDATE linshare_parameter SET global_used_quota = (SELECT sum(ld.size) FROM linshare_document ld);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 12, E'LinShare: A file will be soon deleted', 0);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 12, E'LinShare : Un fichier va bientôt être supprimé', 1);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (parameter_id, subject_id, content, language_id) VALUES (1, 12, E'LinShare : Een bestand zal binnenkort gewist worden.', 2);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, E'The file <strong>${documentName}</strong> will expire in ${nbDays} days.', E'The file ${documentName} will expire in ${nbDays} days.', 0);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, E'Le fichier <strong>${documentName}</strong> va expirer dans ${nbDays} jours.', E'Le fichier ${documentName} va expirer dans ${nbDays} jours.', 1);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, E'Het bestand <strong>${documentName}</strong> zal verlopen binnen ${nbDays} dagen.', E'Het bestand ${documentName} zal verlopen binnen ${nbDays} dagen.', 2);



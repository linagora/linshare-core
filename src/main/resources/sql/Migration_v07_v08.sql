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
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,2,'An anonymous user ${email} downloaded the following file(s) you shared via LinShare:<ul>${documentNames}</ul>','An anonymous user ${email} downloaded the following file(s) you shared via LinShare:\r${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,2,'Un utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>','Un utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare :\r${documentNamesTxt}',1);

-- Template CONFIRM_DOWNLOAD_REGISTERED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,3,'${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:<ul>${documentNames}</ul>','${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:\r${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,3,'${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) suivant que vous lui avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>','${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare :\r${documentNamesTxt}',1);

-- Template LINSHARE_URL
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,4,'You can login to this address: <a href="${url}">${url}</a><br/>','You can now login to this address: ${url}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,4,'Vous pouvez vous connecter à cette adresse&nbsp;: <a href="${url}">${url}</a><br/>','Vous pouvez vous connecter à cette adresse : ${url}',1);

-- Template FILE_DOWNLOAD_URL
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,5,'In order to download the files, click on this link or paste it into your browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>','In order to download the files, click on this link or paste it into your browser:\r${url}${urlparam}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,5,'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>','Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur :\r${url}${urlparam}',1);

-- Template DECRYPT_URL
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,6,'<p>Some received files are <b>encrypted</b>. After downloading, take care of decrypting localy with the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You have to use <i>password</i> which has been communicated by the person who has done this sharing.</p><br/>','Some received files are encrypted. After downloading, take care of decrypting localy with the application:\r${jwsEncryptUrl}\rYou have to use <i>password</i> which has been communicated by the person who has done this sharing.\r',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,6,'<p>Certains de vos fichiers sont <b>chiffrés</b>. Après le téléchargement, vous devez les déchiffrer localement avec l''application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir <i>du mot de passe de déchiffrement</i> qui a dû vous être communiqué par l''expéditeur des fichiers.</p><br/>','Certains de vos fichiers sont chiffrés. Après le téléchargement, vous devez les déchiffrer localement avec l''application:\r${jwsEncryptUrl}\rVous devez vous munir du mot de passe de déchiffrement qui a du vous être communiqué par l''expéditeur des fichiers.\r',1);

-- Template PERSONAL_MESSAGE
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,7,'<strong>Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>','Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare\r\r${message}\r\r--------------------------------------------------------------',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,7,'<strong>Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>','Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare\r\r${message}\r\r--------------------------------------------------------------',1);

-- Template GUEST_INVITATION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,8,'<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use LinShare.<br/>','${ownerFirstName} ${ownerLastName} invites you to use LinShare.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,8,'<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/>','${ownerFirstName} ${ownerLastName} vous invite à utiliser LinShare.',1);

-- Template ACCOUNT_DESCRIPTION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,9,'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul>','Your LinShare account:\r- Login: ${mail}  (your e-mail address)\r- Password: ${password}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,9,'Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul>','Votre compte LinShare : \r- identifiant : ${mail}  (votre adresse électronique) \r- mot de passe : ${password}',1);

-- Template SHARE_NOTIFICATION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,10,'<strong>${firstName} ${lastName}</strong> sent you ${number} file(s):<ul>${documentNames}</ul>','${firstName} ${lastName} sent you ${number} file(s):\r\r${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,10,'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul>','${firstName} ${lastName} a mis en partage ${number} fichier(s) à votre attention :\r\r${documentNamesTxt}',1);

-- Template PASSWORD_GIVING
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,11,'The associated password to use is: <code>${password}</code><br/>','The associated password to use is: ${password}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,11,'Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/>','Le mot de passe à utiliser est : ${password}',1);

-- Template FILE_UPDATED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,12,'<strong>${firstName} ${lastName}</strong> has updated the shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul>','${firstName} ${lastName} has updated the shared file ${fileOldName}:\r- New file name: ${fileName}\r- File size: ${fileSize}\r- MIME type: ${mimeType}\r',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,12,'<strong>${firstName} ${lastName}</strong> a mis à jour le fichier partagé <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nom du nouveau fichier&nbsp;: ${fileName}</li><li>Taille du fichier&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>','${firstName} ${lastName} a mis à jour le fichier partagé ${fileOldName} : \r- nom du nouveau fichier : ${fileName}\r- taille du fichier : ${fileSize}\r- type MIME : ${mimeType}\r',1);

-- Template GROUP_SHARE_NOTIFICATION
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,13,'<strong>${firstName} ${lastName}</strong> has shared ${number} file(s) with the group ${groupName}:<ul>${documentNames}</ul>','${firstName} ${lastName} has shared ${number} file(s) with the group ${groupName}:\r\r${documentNamesTxt}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,13,'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) pour le groupe ${groupName}&nbsp;:<ul>${documentNames}</ul>','${firstName} ${lastName} a mis en partage ${number} fichier(s) pour le groupe ${groupName} :\r\r${documentNamesTxt}',1);

-- Template GROUP_NEW_MEMBER
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,14,'You are now member of the group ${groupName}.','You are now member of the group ${groupName}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,14,'Vous appartenez maintenant au groupe ${groupName}.','Vous appartenez maintenant au groupe ${groupName}.',1);

-- Template GROUP_MEMBERSHIP_STATUS
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,15,'Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.','Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,15,'Votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName} est ${status}.','Votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName} est ${status}.',1);

-- Template GROUP_SHARE_DELETED
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,16,'${firstName} ${lastName} has deleted the file <strong>${documentName}</strong> shared with the group <strong>${groupName}</strong>.','${firstName} ${lastName} has deleted the file ${documentName} shared with the group ${groupName}.',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,16,'${firstName} ${lastName} a supprimé le fichier <strong>${documentName}</strong> partagé avec le groupe <strong>${groupName}</strong>.','${firstName} ${lastName} a supprimé le fichier ${documentName} partagé avec le groupe ${groupName}.',1);

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



-- Groups
alter table linshare_group_members add column owner_id int8;

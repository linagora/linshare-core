-- adding 40 days in expiry time
INSERT INTO linshare_parameter(parameter_id, file_size_max, user_available_size, active_mimetype,active_signature,active_encipherment,active_doc_time_stamp,user_expiry_time,user_expiry_time_unit_id, default_expiry_time,default_expiry_time_unit_id)  VALUES (1,10240000,51200000,0,0,0,0,'40','0', '100', '0');

-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (1, 0, 'root@localhost.localdomain', 'Administrator', 'LinShare', 'root@localhost.localdomain', '2009-01-01', 1, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '2019-01-01', 1,1);
INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (2, 0, 'system', '', '', 'system@localhost', '2009-01-01', 2, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '2019-01-01', 1,1);

-- Welcom texts
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Welcome to LinShare, the Open Source secure files sharing system.', 0, 0);
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.', 0, 1);
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Welcome to LinShare, the Open Source secure files sharing system.', 1, 0);
insert into linshare_welcome_texts (parameter_id , welcome_text, user_type_id, language_id) values (1, 'Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.', 1, 1);

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
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,5,'In order to download the files, click on this link or paste it into your browser: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>','In order to download the files, click on this link or paste it into your browser:\n${url}${urlparam}',0);
insert into linshare_mail_templates (parameter_id, template_id, content_html, content_txt, language_id) values (1,5,'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a><br/>','Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur :\n${url}${urlparam}',1);

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


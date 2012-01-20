INSERT INTO linshare_messages_configuration (messages_configuration_id) VALUES (1);


-- LOCALE en
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, 'LinShare: An anonymous user downloaded the file you shared', 0);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, 'LinShare: An user downloaded the file you shared', 0);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, 'LinShare: Your account on LinShare has been created', 0);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, 'LinShare: Your password was reset', 0);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, 'LinShare: A user deposited files in sharing for you', 0);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, 'LinShare: An user has updated a shared file', 0);

-- Subject NEW_GROUP_SHARING
-- LinShare: A user deposited files in sharing for the group ${groupName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 6, 'LinShare: A user deposited files in sharing for the group ${groupName}', 0);

-- Subject MEMBERSHIP_REQUEST_STATUS
-- LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 7, 'LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}', 0);

-- Subject NEW_GROUP_MEMBER
-- LinShare: You are now member of the group ${groupName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 8, 'LinShare: You are now member of the group ${groupName}', 0);

-- Subject GROUP_SHARING_DELETED
-- LinShare: A file shared with the group ${groupName} has been deleted.
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 9, 'LinShare: A file shared with the group ${groupName} has been deleted.', 0);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, 'LinShare: An user has deleted a shared file', 0);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, 'LinShare: A sharing will be soon deleted', 0);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, 'LinShare: A file will be soon deleted', 0);

-- Mail templates
-- Template GREETINGS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, 'Welcome to LinShare, the Open Source secure files sharing system.', 'Welcome to LinShare, the Open Source secure files sharing system.', 0);

-- Template FOOTER
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, '<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Open Source secured file sharing application', 'LinShare - http://linshare.org - Open Source secured file sharing application', 0);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, 'An anonymous user ${email} downloaded the following file(s) you shared via LinShare:<ul>${documentNames}</ul>', 'An anonymous user ${email} downloaded the following file(s) you shared via LinShare:\n${documentNamesTxt}', 0);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, '${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:<ul>${documentNames}</ul>', '${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:\n${documentNamesTxt}', 0);

-- Template LINSHARE_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, 'You can login to this address: <a href="${url}">${url}</a><br/>', 'You can now login to this address: ${url}', 0);

-- Template FILE_DOWNLOAD_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, 'In order to download the files, click on this link or paste it into your browser: <a href="${url}${urlparam}">${url}${urlparam}</a>', 'In order to download the files, click on this link or paste it into your browser:\n${url}${urlparam}', 0);

-- Template DECRYPT_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, '<p>Some received files are <b>encrypted</b>. After downloading, take care of decrypting localy with the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You have to use <i>password</i> which has been communicated by the person who has done this sharing.</p><br/>', 'Some received files are encrypted. After downloading, take care of decrypting localy with the application:\n${jwsEncryptUrl}\nYou have to use <i>password</i> which has been communicated by the person who has done this sharing.\n', 0);

-- Template PERSONAL_MESSAGE
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, '<strong>Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', 'Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 0);

-- Template GUEST_INVITATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, '<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use LinShare.<br/>', '${ownerFirstName} ${ownerLastName} invites you to use LinShare.', 0);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, 'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul>', 'Your LinShare account:\n- Login: ${mail}  (your e-mail address)\n- Password: ${password}', 0);

-- Template SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, '<strong>${firstName} ${lastName}</strong> sent you ${number} file(s):<ul>${documentNames}</ul>', '${firstName} ${lastName} sent you ${number} file(s):\n\n${documentNamesTxt}', 0);

-- Template PASSWORD_GIVING
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, 'The associated password to use is: <code>${password}</code><br/>', 'The associated password to use is: ${password}', 0);

-- Template FILE_UPDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, '<strong>${firstName} ${lastName}</strong> has updated the shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul>', '${firstName} ${lastName} has updated the shared file ${fileOldName}:\n- New file name: ${fileName}\n- File size: ${fileSize}\n- MIME type: ${mimeType}\n', 0);

-- Template GROUP_SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 13, '<strong>${firstName} ${lastName}</strong> has shared ${number} file(s) with the group ${groupName}:<ul>${documentNames}</ul>', '${firstName} ${lastName} has shared ${number} file(s) with the group ${groupName}:\n\n${documentNamesTxt}', 0);

-- Template GROUP_NEW_MEMBER
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 14, 'You are now member of the group ${groupName}.', 'You are now member of the group ${groupName}.', 0);

-- Template GROUP_MEMBERSHIP_STATUS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 15, 'Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.', 'Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.', 0);

-- Template GROUP_SHARE_DELETED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 16, '${firstName} ${lastName} has deleted the file <strong>${documentName}</strong> shared with the group <strong>${groupName}</strong>.', '${firstName} ${lastName} has deleted the file ${documentName} shared with the group ${groupName}.', 0);

-- Template SHARED_FILE_DELETED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, '<strong>${firstName} ${lastName}</strong> has deleted the shared file <strong>${documentName}</strong>.', '${firstName} ${lastName} has deleted the shared file ${documentName}.', 0);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, 'A share from ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the files before this date.', 'A share from ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the files before this date.', 0);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, 'The sharing of the file ${documentName} by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download or copy this file.', 'The sharing of the file ${documentName} by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download or copy this file.', 0);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, 'The file <strong>${documentName}</strong> will expire in ${nbDays} days.', 'The file ${documentName} will expire in ${nbDays} days.', 0);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system.
INSERT INTO linshare_welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, 'Welcome to LinShare, the Open Source secure files sharing system.', 0);


-- LOCALE fr
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, 'LinShare : Un utilisateur anonyme a téléchargé des fichiers en partage', 1);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, 'LinShare : Un utilisateur a téléchargé des fichiers en partage', 1);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, 'LinShare : Votre compte LinShare a été créé', 1);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, 'LinShare : Votre nouveau mot de passe', 1);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, 'LinShare : Un utilisateur vous a déposé des fichiers en partage', 1);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, 'LinShare : Un utilisateur a mis à jour un fichier dans vos partages', 1);

-- Subject NEW_GROUP_SHARING
-- LinShare: A user deposited files in sharing for the group ${groupName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 6, 'LinShare : Un utilisateur a déposé des fichiers en partage pour le groupe ${groupName}', 1);

-- Subject MEMBERSHIP_REQUEST_STATUS
-- LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 7, 'LinShare : Statut de votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName}', 1);

-- Subject NEW_GROUP_MEMBER
-- LinShare: You are now member of the group ${groupName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 8, 'LinShare : Vous appartenez maintenant au groupe ${groupName}', 1);

-- Subject GROUP_SHARING_DELETED
-- LinShare: A file shared with the group ${groupName} has been deleted.
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 9, 'LinShare : Un fichier partagé avec le groupe ${groupName} a été supprimé', 1);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, 'LinShare : Un utilisateur a supprimé un fichier partagé avec vous', 1);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, 'LinShare : Un partage va bientôt expirer', 1);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, 'LinShare : Un fichier va bientôt être supprimé', 1);

-- Mail templates
-- Template GREETINGS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, 'Bonjour ${firstName} ${lastName},<br/><br/>', 'Bonjour ${firstName} ${lastName},', 1);

-- Template FOOTER
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, '<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Logiciel libre de partage de fichiers sécurisé', 'LinShare - http://linshare.org - Logiciel libre de partage de fichiers sécurisé', 1);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, 'Un utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>', 'Un utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare :\n${documentNamesTxt}', 1);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, '${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) suivant que vous lui avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>', '${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare :\n${documentNamesTxt}', 1);

-- Template LINSHARE_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, 'Vous pouvez vous connecter à cette adresse&nbsp;: <a href="${url}">${url}</a><br/>', 'Vous pouvez vous connecter à cette adresse : ${url}', 1);

-- Template FILE_DOWNLOAD_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, 'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', 'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur :\n${url}${urlparam}', 1);

-- Template DECRYPT_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, '<p>Certains de vos fichiers sont <b>chiffrés</b>. Après le téléchargement, vous devez les déchiffrer localement avec l''application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir <i>du mot de passe de déchiffrement</i> qui a dû vous être communiqué par l''expéditeur des fichiers.</p><br/>', 'Certains de vos fichiers sont chiffrés. Après le téléchargement, vous devez les déchiffrer localement avec l''application:\n${jwsEncryptUrl}\nVous devez vous munir du mot de passe de déchiffrement qui a du vous être communiqué par l''expéditeur des fichiers.\n', 1);

-- Template PERSONAL_MESSAGE
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, '<strong>Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', 'Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 1);

-- Template GUEST_INVITATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, '<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/>', '${ownerFirstName} ${ownerLastName} vous invite à utiliser LinShare.', 1);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, 'Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul>', 'Votre compte LinShare : \n- identifiant : ${mail}  (votre adresse électronique) \n- mot de passe : ${password}', 1);

-- Template SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, '<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul>', '${firstName} ${lastName} a mis en partage ${number} fichier(s) à votre attention :\n\n${documentNamesTxt}', 1);

-- Template PASSWORD_GIVING
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, 'Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/>', 'Le mot de passe à utiliser est : ${password}', 1);

-- Template FILE_UPDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, '<strong>${firstName} ${lastName}</strong> a mis à jour le fichier partagé <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nom du nouveau fichier&nbsp;: ${fileName}</li><li>Taille du fichier&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', '${firstName} ${lastName} a mis à jour le fichier partagé ${fileOldName} : \n- nom du nouveau fichier : ${fileName}\n- taille du fichier : ${fileSize}\n- type MIME : ${mimeType}\n', 1);

-- Template GROUP_SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 13, '<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) pour le groupe ${groupName}&nbsp;:<ul>${documentNames}</ul>', '${firstName} ${lastName} a mis en partage ${number} fichier(s) pour le groupe ${groupName} :\n\n${documentNamesTxt}', 1);

-- Template GROUP_NEW_MEMBER
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 14, 'Vous appartenez maintenant au groupe ${groupName}.', 'Vous appartenez maintenant au groupe ${groupName}.', 1);

-- Template GROUP_MEMBERSHIP_STATUS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 15, 'Votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName} est ${status}.', 'Votre demande d''adhésion au groupe ${groupName} concernant ${newMemberFirstName} ${newMemberLastName} est ${status}.', 1);

-- Template GROUP_SHARE_DELETED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 16, '${firstName} ${lastName} a supprimé le fichier <strong>${documentName}</strong> partagé avec le groupe <strong>${groupName}</strong>.', '${firstName} ${lastName} a supprimé le fichier ${documentName} partagé avec le groupe ${groupName}.', 1);

-- Template SHARED_FILE_DELETED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, '<strong>${firstName} ${lastName}</strong> a supprimé le fichier partagé <strong>${documentName}</strong>.', '${firstName} ${lastName} a supprimé le fichier partagé ${documentName}.', 1);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, 'Un partage provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant cette date.', 'Un partage provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant cette date.', 1);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, 'Le partage du fichier ${documentName} provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier rapidement.', 'Le partage du fichier ${documentName} provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier rapidement.', 1);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, 'Le fichier <strong>${documentName}</strong> va expirer dans ${nbDays} jours.', 'Le fichier ${documentName} va expirer dans ${nbDays} jours.', 1);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO linshare_welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, 'Bienvenue dans LinShare, le système Open Source de partage de fichiers sécurisé.', 1);


-- LOCALE nl
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, 'LinShare : Een anonieme gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, 'LinShare : Een gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, 'LinShare : Uw LinShare account werd aangemaakt.', 2);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, 'LinShare : Uw nieuwe wachtwoord', 2);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, 'LinShare : Een gebruiker heeft te delen bestanden voor u klaargezet.', 2);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, 'LinShare : Een gebruiker heeft een gedeeld bestand bijgewerkt', 2);

-- Subject NEW_GROUP_SHARING
-- LinShare: A user deposited files in sharing for the group ${groupName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 6, 'LinShare : Een gebruiker heeft te delen bestanden klaargezet voor de groep ${groupName}', 2);

-- Subject MEMBERSHIP_REQUEST_STATUS
-- LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 7, 'LinShare : Status van uw aanvraag tot toetreding tot de groep ${groupName} en de gebruiker ${newMemberFirstName} ${newMemberLastName}', 2);

-- Subject NEW_GROUP_MEMBER
-- LinShare: You are now member of the group ${groupName}
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 8, 'LinShare : U bent nu lid van de groep ${groupName}', 2);

-- Subject GROUP_SHARING_DELETED
-- LinShare: A file shared with the group ${groupName} has been deleted.
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 9, 'LinShare : Een bestand gedeeld met de groep ${groupName} werd gewist', 2);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, 'LinShare : Een gebruiker heeft een gedeeld bestand gewist', 2);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, 'LinShare : Een share zal binnenkort gewist worden.', 2);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO linshare_mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, 'LinShare : Een bestand zal binnenkort gewist worden.', 2);

-- Mail templates
-- Template GREETINGS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, 'Hallo ${firstName} ${lastName},<br/><br/>', 'Hallo ${firstName} ${lastName},', 2);

-- Template FOOTER
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, '<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Open Source toepassing voor het beveiligd delen van bestanden', 'LinShare - http://linshare.org - Open Source toepassing voor het beveiligd delen van bestanden', 2);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, 'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', 'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, '${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', '${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template LINSHARE_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, 'U kan inloggen op dit adres&nbsp;: <a href="${url}">${url}</a><br/>', 'U kan inloggen op dit adres : ${url}', 2);

-- Template FILE_DOWNLOAD_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, 'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', 'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser :\n${url}${urlparam}', 2);

-- Template DECRYPT_URL
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, '<p>Sommige ontvangen bestanden zijn <b>versleuteld</b>. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>U moet in het bezit zijn van het <i>versleutelwachtwoord</i> dat u gekregen hebt van de persoon die u de bestanden stuurt.</p><br/>', 'Sommige ontvangen bestanden zijn versleuteld. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:\n${jwsEncryptUrl}\nU moet in het bezit zijn van het versleutelwachtwoord dat u gekregen hebt van de persoon die u de bestanden stuurt.\n', 2);

-- Template PERSONAL_MESSAGE
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, '<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', '<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 2);

-- Template GUEST_INVITATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, '<strong>${ownerFirstName} ${ownerLastName}</strong> nodigt u uit gebruik te maken van LinShare.<br/>', '${ownerFirstName} ${ownerLastName} nodigt u uit gebruik te maken van LinShare.', 2);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, 'Uw LinShare account&nbsp;:<ul><li>Identificatie&nbsp;: <code>${mail}</code> &nbsp;(uw e-mailadres)</li><li>Wachtwoord&nbsp;: <code>${password}</code></li></ul>', 'Uw LinShare account :\n- Identificatie : ${mail} (uw e-mailadres)\n- Wachtwoord : ${password}', 2);

-- Template SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, '<strong>${firstName} ${lastName}</strong> heeft ${number} te delen bestand(en) voor u klaargezet&nbsp;:<ul>${documentNames}</ul>', '${firstName} ${lastName} heeft ${number} te delen bestand(en) voor u klaargezet :\n\n${documentNamesTxt}', 2);

-- Template PASSWORD_GIVING
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, 'Het bijbehorende wachtwoord dat u moet gebruiken, is&nbsp;: <code>${password}</code><br/>', 'Het bijbehorende wachtwoord dat u moet gebruiken, is : ${password}', 2);

-- Template FILE_UPDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, '<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand bijgewerkt <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nieuwe bestandsnaam&nbsp;: ${fileName}</li><li>Grootte van het bestand&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', '${firstName} ${lastName} heeft het gedeelde bestand bijgewerkt ${fileOldName} :\n- Nieuwe bestandsnaam : ${fileName}\n- Grootte van het bestand : ${fileSize}\n- Type MIME : ${mimeType}\n', 2);

-- Template GROUP_SHARE_NOTIFICATION
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 13, '<strong>${firstName} ${lastName}</strong> heeft ${number} bestand(en) te delen aangeboden voor de groep${groupName}&nbsp;:<ul>${documentNames}</ul>', '${firstName} ${lastName} heeft ${number} bestand(en) te delen aangeboden voor de groep${groupName} :\n\n${documentNamesTxt}', 2);

-- Template GROUP_NEW_MEMBER
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 14, 'U bent nu lid van de groep ${groupName}.', 'U bent nu lid van de groep ${groupName}.', 2);

-- Template GROUP_MEMBERSHIP_STATUS
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 15, 'Uw aanvraag tot toetreding van de groep ${groupName} en de gebruiker ${newMemberFirstName} ${newMemberLastName} is ${status}.', 'Uw aanvraag tot toetreding van de groep ${groupName} en de gebruiker ${newMemberFirstName} ${newMemberLastName} is ${status}.', 2);

-- Template GROUP_SHARE_DELETED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 16, '${firstName} ${lastName} heeft het bestand <strong>${documentName}</strong> dat gedeeld werd met de groep <strong>${groupName}</strong> gewist.', '${firstName} ${lastName} heeft het bestand ${documentName} dat gedeeld werd met de groep ${groupName} gewist.', 2);

-- Template SHARED_FILE_DELETED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, '<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand <strong>${documentName}</strong> gewist.', '${firstName} ${lastName} heeft het gedeelde bestand ${documentName} gewist.', 2);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, 'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', 'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', 2);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, 'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', 'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', 2);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO linshare_mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, 'Het bestand <strong>${documentName}</strong> zal verlopen binnen ${nbDays} dagen.', 'Het bestand ${documentName} zal verlopen binnen ${nbDays} dagen.', 2);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO linshare_welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, 'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 2);




-- default domain policy
INSERT INTO linshare_domain_access_policy(id) VALUES (1);
INSERT INTO linshare_domain_access_rule(id, domain_access_rule_type, regexp, domain_id, domain_access_policy_id, rule_index) VALUES (1, 0, '', null, 1,0);
INSERT INTO linshare_domain_policy(id, identifier, domain_access_policy_id) VALUES (1, 'DefaultDomainPolicy', 1);


-- Root domain (application domain)
INSERT INTO linshare_domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id) VALUES (1, 0, 'LinShareRootDomain', 'LinShareRootDomain', true, false, 'The root application domain', 3, 'en', 0, null, 1, null, 1);



INSERT INTO linshare_ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) VALUES (1, 'baseLDAP', 'ldap://localhost:33389', 'simple', '', '');
INSERT INTO linshare_domain_pattern(domain_pattern_id, identifier, description, get_user_command, get_all_domain_users_command, auth_command, search_user_command, user_mail, user_firstname , user_lastname) VALUES (1, 'basePattern', '', 'ldap.entry("uid=" + userId + ",ou=People," + domain, "objectClass=*");', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(mail=*)(givenName=*)(sn=*))");', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(mail="+mail+")(givenName="+firstName+")(sn="+lastName+"))");', 'mail', 'givenName', 'sn');

INSERT INTO linshare_user_provider_ldap(id, differential_key, domain_pattern_id, ldap_connection_id) VALUES (1, 'dc=linpki,dc=org', 1, 1);
-- Top domain (example domain)
INSERT INTO linshare_domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 1, 1);
-- Sub domain (example domain)
INSERT INTO linshare_domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 0, 1, 1, 2, 1);
-- Guest domain (example domain)
INSERT INTO linshare_domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 2, 1);







-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
INSERT INTO linshare_user(user_id, user_type_id, domain_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (2, 0, 1, 'root@localhost.localdomain', 'Administrator', 'LinShare', 'root@localhost.localdomain', null, 3, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', '2019-01-01', 'false','false');
INSERT INTO linshare_user(user_id, user_type_id, domain_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest) VALUES (3, 0, 1, 'system', '', '', 'system@localhost', '2009-01-01', 2, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', null, 'false','false');




-- unit type : TIME(0), SIZE(1)
-- unit value : FileSizeUnit : KILO(0), MEGA(1), GIGA(2)
-- unit value : TimeUnit : DAY(0), WEEK(1), MONTH(2)
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)


-- Functionality : FILESIZE_MAX
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (1, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (2, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (1, false, 'FILESIZE_MAX', 1, 2, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (1, 1, 1);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (1, 10, 1);


-- Functionality : QUOTA_GLOBAL
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (3, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (4, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (2, false, 'QUOTA_GLOBAL', 3, 4, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (2, 1, 1);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (2, 1, 2);


-- Functionality : QUOTA_USER
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (5, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (6, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (3, false, 'QUOTA_USER', 5, 6, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (3, 1, 1);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (3, 100, 3);


-- Functionality : MIME_TYPE
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (7, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (8, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (4, true, 'MIME_TYPE', 7, 8, 1);


-- Functionality : SIGNATURE
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (9, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (10, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (5, true, 'SIGNATURE', 9, 10, 1);


-- Functionality : ENCIPHERMENT
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (11, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (12, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (6, true, 'ENCIPHERMENT', 11, 12, 1);


-- Functionality : TIME_STAMPING
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (13, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (14, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (7, false, 'TIME_STAMPING', 13, 14, 1);
INSERT INTO linshare_functionality_string(functionality_id, string_value) VALUES (7, 'http://localhost:8080/signserver/tsa?signerId=1');


-- Functionality : ANTIVIRUS
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (15, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (16, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (8, true, 'ANTIVIRUS', 15, 16, 1);


-- Functionality : CUSTOM_LOGO
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (17, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (18, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (9, false, 'CUSTOM_LOGO', 17, 18, 1);
INSERT INTO linshare_functionality_string(functionality_id, string_value) VALUES (9, 'http://localhost/images/logo.png');


-- Functionality : ACCOUNT_EXPIRATION (for Guests)
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (19, true, true, 0, true);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (20, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (10, false, 'ACCOUNT_EXPIRATION', 19, 20, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (4, 0, 2);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (10, 3, 4);


-- Functionality : FILE_EXPIRATION
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (21, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (22, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (11, false, 'FILE_EXPIRATION', 21, 22, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (5, 0, 2);
INSERT INTO linshare_functionality_unit(functionality_id, integer_value, unit_id) VALUES (11, 3, 5);


-- Functionality : SHARE_EXPIRATION
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (23, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (24, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (12, false, 'SHARE_EXPIRATION', 23, 24, 1);
INSERT INTO linshare_unit(id, unit_type, unit_value) VALUES (6, 0, 2);
INSERT INTO linshare_functionality_unit_boolean(functionality_id, integer_value, unit_id, boolean_value) VALUES (12, 3, 6, false);


-- Functionality : ANONYMOUS_URL
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (25, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (26, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (13, true, 'ANONYMOUS_URL', 25, 26, 1);


-- Functionality : GUESTS
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (27, false, false, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (28, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (14, true, 'GUESTS', 27, 28, 1);


-- Functionality : USER_CAN_UPLOAD
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (29, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (30, false, false, 2, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (15, true, 'USER_CAN_UPLOAD', 29, 30, 1);


-- Functionality : COMPLETION
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (31, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (32, true, true, 1, false);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (16, false, 'COMPLETION', 31, 32, 1);
INSERT INTO linshare_functionality_integer(functionality_id, integer_value) VALUES (16, 3);


-- Functionality : TAB_HELP
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (33, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (34, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (17, true, 'TAB_HELP', 33, 34, 1);


-- Functionality : TAB_AUDIT
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (35, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (36, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (18, true, 'TAB_AUDIT', 35, 36, 1);


-- Functionality : TAB_USER
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (37, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (38, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (19, true, 'TAB_USER', 37, 38, 1);


-- Functionality : TAB_GROUP
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (39, true, true, 1, false);
INSERT INTO linshare_policy(id, status, default_status, policy, system) VALUES (40, false, false, 1, true);
INSERT INTO linshare_functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (20, true, 'TAB_GROUP', 39, 40, 1);




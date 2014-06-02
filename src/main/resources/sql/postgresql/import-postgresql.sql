SET client_encoding = 'UTF8';
SET client_min_messages = warning;


INSERT INTO messages_configuration (messages_configuration_id) VALUES (1);


-- LOCALE en
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An unknown user has just downloaded a file you made available for sharing
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, E'An unknown user ${actorRepresentation} has just downloaded a file you made available for sharing', 0);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: A user has just downloaded a file you made available for sharing
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, E'A user ${actorRepresentation} has just downloaded a file you made available for sharing', 0);

-- Subject NEW_GUEST
-- LinShare: Your LinShare account has been successfully created
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, E'Your LinShare account has been sucessfully created', 0);

-- Subject RESET_PASSWORD
-- LinShare: Your password has been reset
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, E'Your password has been reset', 0);

-- Subject NEW_SHARING
-- LinShare: A user has just made a file available to you!
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, E'A user ${actorRepresentation} has just made a file available to you!', 0);

-- Subject NEW_SHARING_WITH_ACTOR
-- LinShare: sharing file with personal message.
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 13, E'${actorSubject} from ${actorRepresentation}', 0);


-- Subject SHARED_DOC_UPDATED
-- LinShare: A user has just modified a shared file you still have access to
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, E'A user ${actorRepresentation} has just modified a shared file you still have access to', 0);

-- Subject SHARED_DOC_DELETED
-- LinShare: A user has just deleted a shared file you had access to!
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, E'A user ${actorRepresentation} has just deleted a shared file you had access to!', 0);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A LinShare workspace is about to be deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, E'A LinShare workspace is about to be deleted', 0);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A shared file is about to be deleted!
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, E'A shared file is about to be deleted!', 0);

-- Mail templates
-- Template GREETINGS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, E'Hello ${firstName} ${lastName},', E'Hello ${firstName} ${lastName},<br/><br/>', 0);

-- Template FOOTER
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, E'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - THE Secure, Open-Source File Sharing Tool', E'LinShare - http://linshare.org - THE Secure, Open-Source File Sharing Tool', 0);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, E'An unknown user ${email} has just downloaded the following file(s) you made available via LinShare:<ul>${documentNames}</ul>', E'An unknown user ${email} has just downloaded the following file(s) you made available via LinShare:\n${documentNamesTxt}', 0);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, E'${recipientFirstName} ${recipientLastName} has just downloaded the following file(s) you made available to her/him via LinShare:<ul>${documentNames}</ul>', E'${recipientFirstName} ${recipientLastName} has just downloaded the following file(s) you made available to her/him via LinShare:\n${documentNamesTxt}', 0);

-- Template LINSHARE_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, E'To login, please go to: <a href="${url}">${url}</a><br/>', E'To login, please go to: ${url}', 0);

-- Template FILE_DOWNLOAD_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, E'To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a>', E'To download the file(s), symply click on the following link or copy/paste it into your favorite browser:\n${url}${urlparam}', 0);

-- Template DECRYPT_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, E'<p>One or more received files are <b>encrypted</b>. After download is complete, make sure to decrypt them locally by using the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You must use the <i>password</i> granted to you by the user who made the file(s) available for sharing.</p><br/>', E'One or more received files are encrypted. After download is complete, make sure to decrypt them locally by using the application:\n${jwsEncryptUrl}\nYou have to use the <i>password</i> granted to you by the user who made the file(s) available for sharing.\n', 0);

-- Template PRIVATE_MESSAGE
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, E'<strong>You have a new Private Message, from ${ownerFirstName} ${ownerLastName}, made available to you via LinShare</strong><pre>${message}</pre><hr/>', E'You have a new Private Message, from ${ownerFirstName} ${ownerLastName}, made available to you via LinShare\n\n${message}\n\n--------------------------------------------------------------', 0);

-- Template GUEST_INVITATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, E'<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use and enjoy LinShare!<br/>', E'${ownerFirstName} ${ownerLastName} invites you to use and enjoy LinShare!', 0);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, E'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul>', E'Your LinShare account:\n- Login: ${mail}  (your e-mail address)\n- Password: ${password}', 0);

-- Template SHARE_NOTIFICATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, E'<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul>', E'${firstName} ${lastName} has just shared with you ${number} file(s):\n\n${documentNamesTxt}', 0);

-- Template PASSWORD_GIVING
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, E'The password to be used is: <code>${password}</code><br/>', E'The password to be used is: ${password}', 0);

-- Template FILE_UPDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, E'<strong>${firstName} ${lastName}</strong> has just modified the following shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul>', E'${firstName} ${lastName} has just modified the following shared file ${fileOldName}:\n- New file name: ${fileName}\n- File size: ${fileSize}\n- MIME type: ${mimeType}\n', 0);

-- Template SHARED_FILE_DELETED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, E'<strong>${firstName} ${lastName}</strong> has just deleted a previously shared file <strong>${documentName}</strong>.', E'${firstName} ${lastName} has just deleted a previously shared file ${documentName}.', 0);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, E'The LinShare workspace created by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the shared files before!', E'The LinShare workspace created by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the shared files before!', 0);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, E'Your access to the shared file ${documentName}, granted by ${firstName} ${lastName}, will expire in ${nbDays} days. Remember to download it before!', E'Your access to the shared file ${documentName}, granted by ${firstName} ${lastName}, will expire in ${nbDays} days. Remember to download it before!', 0);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, E'Your access to the file <strong>${documentName}</strong> will expire in ${nbDays} days!', E'Your access to the file ${documentName} will expire in ${nbDays} days!', 0);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.
INSERT INTO welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, E'Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.', 0);


-- LOCALE fr
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, E'Un utilisateur anonyme ${actorRepresentation} a téléchargé des fichiers en partage', 1);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, E'Un utilisateur ${actorRepresentation} a téléchargé des fichiers en partage', 1);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, E'Votre compte LinShare a été créé', 1);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, E'Votre nouveau mot de passe', 1);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, E'Un utilisateur ${actorRepresentation} vous a déposé des fichiers en partage', 1);

-- Subject NEW_SHARING_WITH_ACTOR
-- LinShare: sharing file with personal message.
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 13, E'${actorSubject} de la part de ${actorRepresentation}', 1);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, E'Un utilisateur ${actorRepresentation} a mis à jour un fichier partagé', 1);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, E'Un utilisateur ${actorRepresentation} a supprimé un fichier partagé', 1);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, E'Un partage va bientôt expirer', 1);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, E'Un fichier va bientôt être supprimé', 1);

-- Mail templates
-- Template GREETINGS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, E'Bonjour ${firstName} ${lastName},<br/><br/>', E'Bonjour ${firstName} ${lastName},', 1);

-- Template FOOTER
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, E'<a href="http://www.linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Logiciel libre de partage de fichiers sécurisé', E'LinShare - http://www.linshare.org/ - Logiciel libre de partage de fichiers sécurisé', 1);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, E'L’utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>', E'L’utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare :\n${documentNamesTxt}', 1);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, E'${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>', E'${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare :\n${documentNamesTxt}', 1);

-- Template LINSHARE_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, E'Vous pouvez vous connecter à cette adresse&nbsp;: <a href="${url}">${url}</a><br/>', E'Vous pouvez vous connecter à cette adresse : ${url}', 1);

-- Template FILE_DOWNLOAD_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, E'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', E'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur :\n${url}${urlparam}', 1);

-- Template DECRYPT_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, E'<p>Certains de vos fichiers sont <strong>chiffrés</strong>. Après le téléchargement, vous devez les déchiffrer localement avec l’application&nbsp;:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir du <em>mot de passe de déchiffrement</em> qui a dû vous être communiqué par l’expéditeur des fichiers.</p>', E'Certains de vos fichiers sont chiffrés. Après le téléchargement, vous devez les déchiffrer localement avec l’application :\n${jwsEncryptUrl}\nVous devez vous munir du mot de passe de déchiffrement qui a dû vous être communiqué par l’expéditeur des fichiers.\n', 1);

-- Template PERSONAL_MESSAGE
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, E'<strong>Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', E'Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 1);

-- Template GUEST_INVITATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, E'<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/>', E'${ownerFirstName} ${ownerLastName} vous invite à utiliser LinShare.', 1);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, E'Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul>', E'Votre compte LinShare : \n- identifiant : ${mail}  (votre adresse électronique) \n- mot de passe : ${password}', 1);

-- Template SHARE_NOTIFICATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, E'<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul>', E'${firstName} ${lastName} a mis en partage ${number} fichier(s) à votre attention :\n\n${documentNamesTxt}', 1);

-- Template PASSWORD_GIVING
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, E'Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/>', E'Le mot de passe à utiliser est : ${password}', 1);

-- Template FILE_UPDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, E'<strong>${firstName} ${lastName}</strong> a mis à jour le fichier partagé <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nom du nouveau fichier&nbsp;: ${fileName}</li><li>Taille du fichier&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', E'${firstName} ${lastName} a mis à jour le fichier partagé ${fileOldName} : \n- nom du nouveau fichier : ${fileName}\n- taille du fichier : ${fileSize}\n- type MIME : ${mimeType}\n', 1);

-- Template SHARED_FILE_DELETED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, E'<strong>${firstName} ${lastName}</strong> a supprimé le fichier partagé <strong>${documentName}</strong>.', E'${firstName} ${lastName} a supprimé le fichier partagé ${documentName}.', 1);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, E'Un partage provenant de <strong>${firstName} ${lastName}</strong> va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant leur expiration.', E'Un partage provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant leur expiration.', 1);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, E'Le partage du fichier ${documentName} provenant de <strong>${firstName} ${lastName}</strong> va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier avant son expiration.', E'Le partage du fichier ${documentName} provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier avant son expiration.', 1);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, E'Le fichier <strong>${documentName}</strong> va expirer dans ${nbDays} jours.', E'Le fichier ${documentName} va expirer dans ${nbDays} jours.', 1);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, E'Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.', 1);



-- LOCALE nl
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, E'Een anonieme gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, E'Een gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, E'Uw LinShare account werd aangemaakt.', 2);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, E'Uw nieuwe wachtwoord', 2);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, E'Een gebruiker heeft te delen bestanden voor u klaargezet.', 2);

-- Subject NEW_SHARING_WITH_ACTOR
-- LinShare: sharing file with personal message.
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 13, E'${actorSubject} from ${actorRepresentation}', 2);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, E'Een gebruiker heeft een gedeeld bestand bijgewerkt', 2);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, E'Een gebruiker heeft een gedeeld bestand gewist', 2);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, E'Een share zal binnenkort gewist worden.', 2);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, E'Een bestand zal binnenkort gewist worden.', 2);
-- Mail templates
-- Template GREETINGS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, E'Hallo ${firstName} ${lastName},<br/><br/>', E'Hallo ${firstName} ${lastName},', 2);

-- Template FOOTER
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, E'<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Open Source toepassing voor het beveiligd delen van bestanden', E'LinShare - http://linshare.org - Open Source toepassing voor het beveiligd delen van bestanden', 2);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, E'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', E'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, E'${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', E'${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template LINSHARE_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, E'U kan inloggen op dit adres&nbsp;: <a href="${url}">${url}</a><br/>', E'U kan inloggen op dit adres : ${url}', 2);

-- Template FILE_DOWNLOAD_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, E'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', E'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser :\n${url}${urlparam}', 2);

-- Template DECRYPT_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, E'<p>Sommige ontvangen bestanden zijn <b>versleuteld</b>. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>U moet in het bezit zijn van het <i>versleutelwachtwoord</i> dat u gekregen hebt van de persoon die u de bestanden stuurt.</p><br/>', E'Sommige ontvangen bestanden zijn versleuteld. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:\n${jwsEncryptUrl}\nU moet in het bezit zijn van het versleutelwachtwoord dat u gekregen hebt van de persoon die u de bestanden stuurt.\n', 2);

-- Template PERSONAL_MESSAGE
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, E'<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', E'<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 2);

-- Template GUEST_INVITATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, E'<strong>${ownerFirstName} ${ownerLastName}</strong> nodigt u uit gebruik te maken van LinShare.<br/>', E'${ownerFirstName} ${ownerLastName} nodigt u uit gebruik te maken van LinShare.', 2);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, E'Uw LinShare account&nbsp;:<ul><li>Identificatie&nbsp;: <code>${mail}</code> &nbsp;(uw e-mailadres)</li><li>Wachtwoord&nbsp;: <code>${password}</code></li></ul>', E'Uw LinShare account :\n- Identificatie : ${mail} (uw e-mailadres)\n- Wachtwoord : ${password}', 2);

-- Template SHARE_NOTIFICATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, E'<strong>${firstName} ${lastName}</strong> heeft ${number} te delen bestand(en) voor u klaargezet&nbsp;:<ul>${documentNames}</ul>', E'${firstName} ${lastName} heeft ${number} te delen bestand(en) voor u klaargezet :\n\n${documentNamesTxt}', 2);

-- Template PASSWORD_GIVING
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, E'Het bijbehorende wachtwoord dat u moet gebruiken, is&nbsp;: <code>${password}</code><br/>', E'Het bijbehorende wachtwoord dat u moet gebruiken, is : ${password}', 2);

-- Template FILE_UPDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, E'<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand bijgewerkt <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nieuwe bestandsnaam&nbsp;: ${fileName}</li><li>Grootte van het bestand&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', E'${firstName} ${lastName} heeft het gedeelde bestand bijgewerkt ${fileOldName} :\n- Nieuwe bestandsnaam : ${fileName}\n- Grootte van het bestand : ${fileSize}\n- Type MIME : ${mimeType}\n', 2);

-- Template SHARED_FILE_DELETED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, E'<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand <strong>${documentName}</strong> gewist.', E'${firstName} ${lastName} heeft het gedeelde bestand ${documentName} gewist.', 2);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, E'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', E'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', 2);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, E'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', E'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', 2);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, E'Het bestand <strong>${documentName}</strong> zal verlopen binnen ${nbDays} dagen.', E'Het bestand ${documentName} zal verlopen binnen ${nbDays} dagen.', 2);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, E'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 2);




-- default domain policy
INSERT INTO domain_access_policy(id) VALUES (1);
INSERT INTO domain_access_rule(id, domain_access_rule_type, regexp, domain_id, domain_access_policy_id, rule_index) VALUES (1, 0, '', null, 1,0);
INSERT INTO domain_policy(id, identifier, domain_access_policy_id) VALUES (1, 'DefaultDomainPolicy', 1);


-- Root domain (application domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (1, 0, 'LinShareRootDomain', 'LinShareRootDomain', true, false, 'The root application domain', 3, 'en', 0, null, 1, null, 1, 0);

-- Default mime policy
INSERT INTO mime_policy(id, domain_id, uuid, name, mode, displayable, creation_date, modification_date) VALUES(1, 1, '3d6d8800-e0f7-11e3-8ec0-080027c0eef0', 'Default Mime Policy', 0, 0, now(), now());
UPDATE domain_abstract SET mime_policy_id=1;


-- system domain pattern
-- OBM domain pattern.
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
 1,
 'default-pattern-obm',
 'This is pattern the default pattern for the ldap obm structure.',
 'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
 'ldap.search(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
 true,
 'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
 'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
 100,
 100,
 10,
 10
 );
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (1, 'user_mail', 'mail', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (2, 'user_firstname', 'givenName', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (3, 'user_lastname', 'sn', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id, completion) VALUES (4, 'user_uid', 'uid', false, true, true, 1, false);

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




-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, password, destroyed, domain_id) VALUES (1, 6, 'root@localhost.localdomain', current_date,current_date, 3, 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', false, 1);
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (1, 'Administrator', 'LinShare', 'root@localhost.localdomain', false, '', false, false);

-- system account :
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (2, 7, 'system', current_date,current_date, 3, 'en', 'en', true, false, 1);


-- unit type : TIME(0), SIZE(1)
-- unit value : FileSizeUnit : KILO(0), MEGA(1), GIGA(2)
-- unit value : TimeUnit : DAY(0), WEEK(1), MONTH(2)
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)


-- Functionality : FILESIZE_MAX
INSERT INTO policy(id, status, default_status, policy, system) VALUES (1, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (2, true, true, 1, false);
-- if a functionality is system, you will not be hable see/modify its parameters
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (1, false, 'FILESIZE_MAX', 1, 2, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (1, 1, 1);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (1, 10, 1);


-- Functionality : QUOTA_GLOBAL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (3, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (4, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (2, false, 'QUOTA_GLOBAL', 3, 4, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (2, 1, 1);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (2, 1, 2);


-- Functionality : QUOTA_USER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (5, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (6, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (3, false, 'QUOTA_USER', 5, 6, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (3, 1, 1);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (3, 100, 3);


-- Functionality : MIME_TYPE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (7, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (8, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (4, true, 'MIME_TYPE', 7, 8, 1);


-- Functionality : SIGNATURE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (9, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (10, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (5, true, 'SIGNATURE', 9, 10, 1);


-- Functionality : ENCIPHERMENT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (11, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (12, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (6, true, 'ENCIPHERMENT', 11, 12, 1);


-- Functionality : TIME_STAMPING
INSERT INTO policy(id, status, default_status, policy, system) VALUES (13, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (14, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (7, false, 'TIME_STAMPING', 13, 14, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (7, 'http://localhost:8080/signserver/tsa?signerId=1');


-- Functionality : ANTIVIRUS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (15, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (16, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (8, true, 'ANTIVIRUS', 15, 16, 1);


-- Functionality : CUSTOM_LOGO
INSERT INTO policy(id, status, default_status, policy, system) VALUES (17, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (18, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (9, false, 'CUSTOM_LOGO', 17, 18, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (9, 'http://localhost/images/logo.png');


-- Functionality : ACCOUNT_EXPIRATION (for Guests)
INSERT INTO policy(id, status, default_status, policy, system) VALUES (19, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (20, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (10, false, 'ACCOUNT_EXPIRATION', 19, 20, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (4, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (10, 3, 4);


-- Functionality : FILE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (21, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (22, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (11, false, 'FILE_EXPIRATION', 21, 22, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (5, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (11, 3, 5);


-- Functionality : SHARE_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (23, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (24, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (12, false, 'SHARE_EXPIRATION', 23, 24, 1);
INSERT INTO unit(id, unit_type, unit_value) VALUES (6, 0, 2);
INSERT INTO functionality_unit_boolean(functionality_id, integer_value, unit_id, boolean_value) VALUES (12, 3, 6, false);


-- Functionality : ANONYMOUS_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (25, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (26, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (13, true, 'ANONYMOUS_URL', 25, 26, 1);


-- Functionality : GUESTS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (27, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (28, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (14, true, 'GUESTS', 27, 28, 1);


-- Functionality : USER_CAN_UPLOAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (29, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (30, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (15, true, 'USER_CAN_UPLOAD', 29, 30, 1);


-- Functionality : COMPLETION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (31, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (32, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (16, false, 'COMPLETION', 31, 32, 1);
INSERT INTO functionality_integer(functionality_id, integer_value) VALUES (16, 3);


-- Functionality : TAB_HELP
INSERT INTO policy(id, status, default_status, policy, system) VALUES (33, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (34, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (17, true, 'TAB_HELP', 33, 34, 1);


-- Functionality : TAB_AUDIT
INSERT INTO policy(id, status, default_status, policy, system) VALUES (35, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (36, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (18, true, 'TAB_AUDIT', 35, 36, 1);


-- Functionality : TAB_USER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (37, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (38, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (19, true, 'TAB_USER', 37, 38, 1);

-- Functionality : SECURED_ANONYMOUS_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (41, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (42, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (21, true, 'SECURED_ANONYMOUS_URL', 41, 42, 1);


-- Functionality : SHARE_NOTIFICATION_BEFORE_EXPIRATION
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)
INSERT INTO policy(id, status, default_status, policy, system) VALUES (43, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (44, false, false, 2, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (22, false, 'SHARE_NOTIFICATION_BEFORE_EXPIRATION', 43, 44, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (22, '2,7');

-- Functionality : TAB_THREAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (45, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (46, false, false, 1, true);
-- if a functionality is system, you will not be able see/modify its parameters
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (23, true, 'TAB_THREAD', 45, 46, 1);


-- Functionality : RESTRICTED_GUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (47, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (48, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (24, true, 'RESTRICTED_GUEST', 47, 48, 1);

-- Functionality : DOMAIN_MAIL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (49, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (50, false, false, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (25, false, 'DOMAIN_MAIL', 49, 50, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (25, 'linshare-noreply@linagora.com');

-- Functionality : TAB_LIST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (53, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (54, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (26, true, 'TAB_LIST', 53, 54, 1);

--Functionality : UPDATE_FILE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (55, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (56, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (27, true, 'UPDATE_FILE', 55, 56, 1);

-- Functionality : CREATE_THREAD_PERMISSION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (57, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (58, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (28, true, 'CREATE_THREAD_PERMISSION', 57, 58, 1);

-- Functionality : LINK_LOGO
INSERT INTO policy(id, status, default_status, policy, system) VALUES (59, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (60, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (29, false, 'LINK_LOGO', 59, 60, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (29, 'http://localhost:8080/linshare/en');

-- Functionality : NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (61, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (62, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(30, false, 'NOTIFICATION_URL', 61, 62, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (30, 'http://localhost:8080/linshare/');


-- Functionality : UPLOAD_REQUEST
INSERT INTO policy(id, status, default_status, policy, system) VALUES (63, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (64, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(31, false, 'UPLOAD_REQUEST', 63, 64, 1);

-- Functionality : UPLOAD_REQUEST__NOTIFICATION_LANGUAGE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (65, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (66, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param) VALUES(32, false, 'UPLOAD_REQUEST__NOTIFICATION_LANGUAGE', 65, 66, 1, 'UPLOAD_REQUEST', true);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (32, 'fr');




-- %{image}    <img src="cid:image.part.1@linshare.org" /><br/><br/>

INSERT INTO mail_layout (id, name,domain_abstract_id,visible,plaintext,modification_date,creation_date,uuid,layout) VALUES (1, 'Default HTML layout', 1,true,false,now(),now(),'15044750-89d1-11e3-8d50-5404a683a462',E'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n<html xmlns="http://www.w3.org/1999/xhtml">\n<head>\n<title>%{mailSubject}</title>\n<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />\n<meta http-equiv="Content-Style-Type" content="text/css" />\n<style type="text/css">\npre { margin-top: .25em; font-family: Verdana, Arial, Helvetica, sans-serif; color: blue; }\nul { margin-top: .25em; padding-left: 1.5em; }\n</style>\n</head>\n<body>\n%{image}\n%{personalMessage}\n%{greetings}\n%{body}\n <hr/>\n%{footer}\n</body>\n</html>');
INSERT INTO mail_layout (id, name,domain_abstract_id,visible,plaintext,modification_date,creation_date,uuid,layout) VALUES (2, 'Default plaintext layout', 1,true,true,now(),now(),'db044da6-89d1-11e3-b6a9-5404a683a462', E'%{personalMessage}\n\n%{greetings}\n\n%{body}\n-- \n%{footer}\n');

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

-- LinShare version
INSERT INTO version (id, version) VALUES (1, '1.6.0');

-- Sequence for hibernate
SELECT setval('hibernate_sequence', 100);





-- Alias
CREATE VIEW alias_func_list_all  AS SELECT
 functionality.id, functionality.system as sys, identifier, policy_delegation_id AS pd_id, domain_id, param, parent_identifier AS parent,
 ap.status AS ap_status, ap.default_status AS ap_default, ap.policy AS ap_policy, ap.system AS ap_sys,
 cp.status AS cp_status, cp.default_status AS cp_default, cp.policy AS cp_policy, cp.system AS cp_sys
 FROM functionality
 JOIN policy AS ap ON policy_activation_id = ap.id
 JOIN policy AS cp ON policy_configuration_id = cp.id order by identifier;

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

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = False;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = True;

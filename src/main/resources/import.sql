INSERT INTO messages_configuration (messages_configuration_id) VALUES (1);


-- LOCALE en
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An unknown user has just downloaded a file you made available for sharing
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, 'LinShare: An unknown user has just downloaded a file you made available for sharing', 0);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: A user has just downloaded a file you made available for sharing
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, 'LinShare: A user has just downloaded a file you made available for sharing', 0);

-- Subject NEW_GUEST
-- LinShare: Your LinShare account has been successfully created
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, 'LinShare: Your LinShare account has been sucessfully created', 0);

-- Subject RESET_PASSWORD
-- LinShare: Your password has been reset
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, 'LinShare: Your password has been reset', 0);

-- Subject NEW_SHARING
-- LinShare: A user has just made a file available to you!
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, 'LinShare: A user has just made a file available to you!', 0);

-- Subject SHARED_DOC_UPDATED
-- LinShare: A user has just modified a shared file you still have access to
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, 'LinShare: A user has just modified a shared file you still have access to', 0);

-- Subject SHARED_DOC_DELETED
-- LinShare: A user has just deleted a shared file you had access to!
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, 'LinShare: A user has just deleted a shared file you had access to!', 0);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A LinShare workspace is about to be deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, 'LinShare: A LinShare workspace is about to be deleted', 0);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A shared file is about to be deleted!
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, 'LinShare: A shared file is about to be deleted!', 0);

-- Mail templates
-- Template GREETINGS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, 'Hello ${firstName} ${lastName},', 'Hello ${firstName} ${lastName},<br/><br/>', 0);

-- Template FOOTER
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, '<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - THE Secure, Open-Source File Sharing Tool', 'LinShare - http://linshare.org - THE Secure, Open-Source File Sharing Tool', 0);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, 'An unknown user ${email} has just downloaded the following file(s) you made available via LinShare:<ul>${documentNames}</ul>', 'An unknown user ${email} has just downloaded the following file(s) you made available via LinShare:\n${documentNamesTxt}', 0);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, '${recipientFirstName} ${recipientLastName} has just downloaded the following file(s) you made available to her/him via LinShare:<ul>${documentNames}</ul>', '${recipientFirstName} ${recipientLastName} has just downloaded the following file(s) you made available to her/him via LinShare:\n${documentNamesTxt}', 0);

-- Template LINSHARE_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, 'To login, please go to: <a href="${url}">${url}</a><br/>', 'To login, please go to: ${url}', 0);

-- Template FILE_DOWNLOAD_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, 'To download the file(s), simply click on the following link or copy/paste it into your favorite browser: <a href="${url}${urlparam}">${url}${urlparam}</a>', 'To download the file(s), symply click on the following link or copy/paste it into your favorite browser:\n${url}${urlparam}', 0);

-- Template DECRYPT_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, '<p>One or more received files are <b>encrypted</b>. After download is complete, make sure to decrypt them locally by using the application:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>You must use the <i>password</i> granted to you by the user who made the file(s) available for sharing.</p><br/>', 'One or more received files are encrypted. After download is complete, make sure to decrypt them locally by using the application:\n${jwsEncryptUrl}\nYou have to use the <i>password</i> granted to you by the user who made the file(s) available for sharing.\n', 0);

-- Template PRIVATE_MESSAGE
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, '<strong>You have a new Private Message, from ${ownerFirstName} ${ownerLastName}, made available to you via LinShare</strong><pre>${message}</pre><hr/>', 'You have a new Private Message, from ${ownerFirstName} ${ownerLastName}, made available to you via LinShare\n\n${message}\n\n--------------------------------------------------------------', 0);

-- Template GUEST_INVITATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, '<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use and enjoy LinShare!<br/>', '${ownerFirstName} ${ownerLastName} invites you to use and enjoy LinShare!', 0);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, 'Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul>', 'Your LinShare account:\n- Login: ${mail}  (your e-mail address)\n- Password: ${password}', 0);

-- Template SHARE_NOTIFICATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, '<strong>${firstName} ${lastName}</strong> has just shared with you ${number} file(s):<ul>${documentNames}</ul>', '${firstName} ${lastName} has just shared with you ${number} file(s):\n\n${documentNamesTxt}', 0);

-- Template PASSWORD_GIVING
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, 'The password to be used is: <code>${password}</code><br/>', 'The password to be used is: ${password}', 0);

-- Template FILE_UPDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, '<strong>${firstName} ${lastName}</strong> has just modified the following shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul>', '${firstName} ${lastName} has just modified the following shared file ${fileOldName}:\n- New file name: ${fileName}\n- File size: ${fileSize}\n- MIME type: ${mimeType}\n', 0);

-- Template SHARED_FILE_DELETED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, '<strong>${firstName} ${lastName}</strong> has just deleted a previously shared file <strong>${documentName}</strong>.', '${firstName} ${lastName} has just deleted a previously shared file ${documentName}.', 0);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, 'The LinShare workspace created by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the shared files before!', 'The LinShare workspace created by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the shared files before!', 0);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, 'Your access to the shared file ${documentName}, granted by ${firstName} ${lastName}, will expire in ${nbDays} days. Remember to download it before!', 'Your access to the shared file ${documentName}, granted by ${firstName} ${lastName}, will expire in ${nbDays} days. Remember to download it before!', 0);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, 'Your access to the file <strong>${documentName}</strong> will expire in ${nbDays} days!', 'Your access to the file ${documentName} will expire in ${nbDays} days!', 0);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.
INSERT INTO welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, 'Welcome to LinShare, THE Secure, Open-Source File Sharing Tool.', 0);




-- LOCALE fr
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, 'LinShare : Un utilisateur anonyme a téléchargé des fichiers en partage', 1);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, 'LinShare : Un utilisateur a téléchargé des fichiers en partage', 1);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, 'LinShare : Votre compte LinShare a été créé', 1);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, 'LinShare : Votre nouveau mot de passe', 1);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, 'LinShare : Un utilisateur vous a déposé des fichiers en partage', 1);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, 'LinShare : Un utilisateur a mis à jour un fichier partagé', 1);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, 'LinShare : Un utilisateur a supprimé un fichier partagé', 1);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, 'LinShare : Un partage va bientôt expirer', 1);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, 'LinShare : Un fichier va bientôt être supprimé', 1);

-- Mail templates
-- Template GREETINGS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, 'Bonjour ${firstName} ${lastName},<br/><br/>', 'Bonjour ${firstName} ${lastName},', 1);

-- Template FOOTER
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, '<a href="http://www.linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Logiciel libre de partage de fichiers sécurisé', 'LinShare - http://www.linshare.org/ - Logiciel libre de partage de fichiers sécurisé', 1);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, 'L’utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>', 'L’utilisateur anonyme ${email} a téléchargé le(s) fichier(s) que vous avez mis en partage via LinShare :\n${documentNamesTxt}', 1);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, '${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare&nbsp;:<ul>${documentNames}</ul>', '${recipientFirstName} ${recipientLastName} a téléchargé le(s) fichier(s) que vous lui avez mis en partage via LinShare :\n${documentNamesTxt}', 1);

-- Template LINSHARE_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, 'Vous pouvez vous connecter à cette adresse&nbsp;: <a href="${url}">${url}</a><br/>', 'Vous pouvez vous connecter à cette adresse : ${url}', 1);

-- Template FILE_DOWNLOAD_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, 'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', 'Pour télécharger les fichiers, cliquez sur le lien ou copiez le dans votre navigateur :\n${url}${urlparam}', 1);

-- Template DECRYPT_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, '<p>Certains de vos fichiers sont <strong>chiffrés</strong>. Après le téléchargement, vous devez les déchiffrer localement avec l’application&nbsp;:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>Vous devez vous munir du <em>mot de passe de déchiffrement</em> qui a dû vous être communiqué par l’expéditeur des fichiers.</p>', 'Certains de vos fichiers sont chiffrés. Après le téléchargement, vous devez les déchiffrer localement avec l’application :\n${jwsEncryptUrl}\nVous devez vous munir du mot de passe de déchiffrement qui a dû vous être communiqué par l’expéditeur des fichiers.\n', 1);

-- Template PERSONAL_MESSAGE
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, '<strong>Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', 'Message personnel de ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 1);

-- Template GUEST_INVITATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, '<strong>${ownerFirstName} ${ownerLastName}</strong> vous invite à utiliser LinShare.<br/>', '${ownerFirstName} ${ownerLastName} vous invite à utiliser LinShare.', 1);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, 'Votre compte LinShare&nbsp;:<ul><li>Identifiant&nbsp;: <code>${mail}</code> &nbsp;(votre adresse électronique)</li><li>Mot de passe&nbsp;: <code>${password}</code></li></ul>', 'Votre compte LinShare : \n- identifiant : ${mail}  (votre adresse électronique) \n- mot de passe : ${password}', 1);

-- Template SHARE_NOTIFICATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, '<strong>${firstName} ${lastName}</strong> a mis en partage ${number} fichier(s) à votre attention&nbsp;:<ul>${documentNames}</ul>', '${firstName} ${lastName} a mis en partage ${number} fichier(s) à votre attention :\n\n${documentNamesTxt}', 1);

-- Template PASSWORD_GIVING
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, 'Le mot de passe à utiliser est&nbsp;: <code>${password}</code><br/>', 'Le mot de passe à utiliser est : ${password}', 1);

-- Template FILE_UPDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, '<strong>${firstName} ${lastName}</strong> a mis à jour le fichier partagé <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nom du nouveau fichier&nbsp;: ${fileName}</li><li>Taille du fichier&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', '${firstName} ${lastName} a mis à jour le fichier partagé ${fileOldName} : \n- nom du nouveau fichier : ${fileName}\n- taille du fichier : ${fileSize}\n- type MIME : ${mimeType}\n', 1);

-- Template SHARED_FILE_DELETED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, '<strong>${firstName} ${lastName}</strong> a supprimé le fichier partagé <strong>${documentName}</strong>.', '${firstName} ${lastName} a supprimé le fichier partagé ${documentName}.', 1);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, 'Un partage provenant de <strong>${firstName} ${lastName}</strong> va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant leur expiration.', 'Un partage provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger les fichiers avant leur expiration.', 1);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, 'Le partage du fichier ${documentName} provenant de <strong>${firstName} ${lastName}</strong> va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier avant son expiration.', 'Le partage du fichier ${documentName} provenant de ${firstName} ${lastName} va expirer dans ${nbDays} jours. Pensez à télécharger ou copier ce fichier avant son expiration.', 1);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, 'Le fichier <strong>${documentName}</strong> va expirer dans ${nbDays} jours.', 'Le fichier ${documentName} va expirer dans ${nbDays} jours.', 1);

-- Welcome texts
-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, 'Bienvenue dans LinShare, le logiciel libre de partage de fichiers sécurisé.', 1);



-- LOCALE nl
-- Mail subjects
-- Subject ANONYMOUS_DOWNLOAD
-- LinShare: An anonymous user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 0, 'LinShare : Een anonieme gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject REGISTERED_DOWNLOAD
-- LinShare: An user downloaded the file you shared
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 1, 'LinShare : Een gebruiker heeft het door u gedeelde bestand gedownload', 2);

-- Subject NEW_GUEST
-- LinShare: Your account on LinShare has been created
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 2, 'LinShare : Uw LinShare account werd aangemaakt.', 2);

-- Subject RESET_PASSWORD
-- LinShare: Your password was reset
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 3, 'LinShare : Uw nieuwe wachtwoord', 2);

-- Subject NEW_SHARING
-- LinShare: A user deposited files in sharing for you
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 4, 'LinShare : Een gebruiker heeft te delen bestanden voor u klaargezet.', 2);

-- Subject SHARED_DOC_UPDATED
-- LinShare: An user has updated a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 5, 'LinShare : Een gebruiker heeft een gedeeld bestand bijgewerkt', 2);

-- Subject SHARED_DOC_DELETED
-- LinShare: An user has deleted a shared file
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 10, 'LinShare : Een gebruiker heeft een gedeeld bestand gewist', 2);

-- Subject SHARED_DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 11, 'LinShare : Een share zal binnenkort gewist worden.', 2);

-- Subject DOC_UPCOMING_OUTDATED
-- LinShare: A sharing will be soon deleted
INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (1, 12, 'LinShare : Een bestand zal binnenkort gewist worden.', 2);

-- Mail templates
-- Template GREETINGS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 0, 'Hallo ${firstName} ${lastName},<br/><br/>', 'Hallo ${firstName} ${lastName},', 2);

-- Template FOOTER
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 1, '<a href="http://linshare.org/" title="LinShare"><strong>LinShare</strong></a> - Open Source toepassing voor het beveiligd delen van bestanden', 'LinShare - http://linshare.org - Open Source toepassing voor het beveiligd delen van bestanden', 2);

-- Template CONFIRM_DOWNLOAD_ANONYMOUS
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 2, 'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', 'Een anonieme gebruiker ${email} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template CONFIRM_DOWNLOAD_REGISTERED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 3, '${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare&nbsp;:<ul>${documentNames}</ul>', '${recipientFirstName} ${recipientLastName} heeft het/de bestand(en) gedownload die u om te delen aangeboden hebt via LinShare :\n${documentNamesTxt}', 2);

-- Template LINSHARE_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 4, 'U kan inloggen op dit adres&nbsp;: <a href="${url}">${url}</a><br/>', 'U kan inloggen op dit adres : ${url}', 2);

-- Template FILE_DOWNLOAD_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 5, 'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser&nbsp;: <a href="${url}${urlparam}">${url}${urlparam}</a>', 'Om de bestanden te downloaden, klik op de link of kopieer de link naar uw browser :\n${url}${urlparam}', 2);

-- Template DECRYPT_URL
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 6, '<p>Sommige ontvangen bestanden zijn <b>versleuteld</b>. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:<br/><a href="${jwsEncryptUrl}">${jwsEncryptUrl}</a><br/>U moet in het bezit zijn van het <i>versleutelwachtwoord</i> dat u gekregen hebt van de persoon die u de bestanden stuurt.</p><br/>', 'Sommige ontvangen bestanden zijn versleuteld. Na het downloaden moet u ze plaatselijk ontsleutelen met de toepassing:\n${jwsEncryptUrl}\nU moet in het bezit zijn van het versleutelwachtwoord dat u gekregen hebt van de persoon die u de bestanden stuurt.\n', 2);

-- Template PERSONAL_MESSAGE
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 7, '<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>', '<strong>Persoonlijke boodschap van ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------', 2);

-- Template GUEST_INVITATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 8, '<strong>${ownerFirstName} ${ownerLastName}</strong> nodigt u uit gebruik te maken van LinShare.<br/>', '${ownerFirstName} ${ownerLastName} nodigt u uit gebruik te maken van LinShare.', 2);

-- Template ACCOUNT_DESCRIPTION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 9, 'Uw LinShare account&nbsp;:<ul><li>Identificatie&nbsp;: <code>${mail}</code> &nbsp;(uw e-mailadres)</li><li>Wachtwoord&nbsp;: <code>${password}</code></li></ul>', 'Uw LinShare account :\n- Identificatie : ${mail} (uw e-mailadres)\n- Wachtwoord : ${password}', 2);

-- Template SHARE_NOTIFICATION
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 10, '<strong>${firstName} ${lastName}</strong> heeft ${number} te delen bestand(en) voor u klaargezet&nbsp;:<ul>${documentNames}</ul>', '${firstName} ${lastName} heeft ${number} te delen bestand(en) voor u klaargezet :\n\n${documentNamesTxt}', 2);

-- Template PASSWORD_GIVING
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 11, 'Het bijbehorende wachtwoord dat u moet gebruiken, is&nbsp;: <code>${password}</code><br/>', 'Het bijbehorende wachtwoord dat u moet gebruiken, is : ${password}', 2);

-- Template FILE_UPDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 12, '<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand bijgewerkt <strong>${fileOldName}</strong>&nbsp;:<ul><li>Nieuwe bestandsnaam&nbsp;: ${fileName}</li><li>Grootte van het bestand&nbsp;: ${fileSize}</li><li>Type MIME&nbsp;: <code>${mimeType}</code></li></ul>', '${firstName} ${lastName} heeft het gedeelde bestand bijgewerkt ${fileOldName} :\n- Nieuwe bestandsnaam : ${fileName}\n- Grootte van het bestand : ${fileSize}\n- Type MIME : ${mimeType}\n', 2);

-- Template SHARED_FILE_DELETED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 17, '<strong>${firstName} ${lastName}</strong> heeft het gedeelde bestand <strong>${documentName}</strong> gewist.', '${firstName} ${lastName} heeft het gedeelde bestand ${documentName} gewist.', 2);

-- Template SECURED_URL_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 18, 'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', 'Een share van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan de bestanden vóór die datum te downloaden.', 2);

-- Template SHARED_DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 19, 'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', 'Het delen van het bestand ${documentName} afkomstig van ${firstName} ${lastName} zal verlopen binnen ${nbDays} dagen. Denk eraan dit bestand te downloaden of te kopiëren.', 2);

-- Template DOC_UPCOMING_OUTDATED
INSERT INTO mail_templates (messages_configuration_id, template_id, content_html, content_txt, language_id) VALUES (1, 20, 'Het bestand <strong>${documentName}</strong> zal verlopen binnen ${nbDays} dagen.', 'Het bestand ${documentName} zal verlopen binnen ${nbDays} dagen.', 2);

-- Welcome texts

-- Welcome to LinShare, the Open Source secure files sharing system
INSERT INTO welcome_texts (messages_configuration_id, welcome_text, language_id) VALUES (1, 'Welkom bij LinShare, het Open Source-systeem om grote bestanden te delen.', 2);




-- default domain policy
INSERT INTO domain_access_policy(id) VALUES (1);
INSERT INTO domain_access_rule(id, domain_access_rule_type, regexp, domain_id, domain_access_policy_id, rule_index) VALUES (1, 0, '', null, 1,0);
INSERT INTO domain_policy(id, identifier, domain_access_policy_id) VALUES (1, 'DefaultDomainPolicy', 1);


-- Root domain (application domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id) VALUES (1, 0, 'LinShareRootDomain', 'LinShareRootDomain', true, false, 'The root application domain', 3, 'en', 0, null, 1, null, 1);



INSERT INTO ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) VALUES (1, 'baseLDAP', 'ldap://localhost:33389', 'simple', '', '');

-- system domain pattern
INSERT INTO domain_pattern(domain_pattern_id, identifier, description, auth_command, search_user_command, auto_complete_command, system) VALUES (1, 'system', '', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(mail="+mail+")(givenName="+firstName+")(sn="+lastName+"))");', 'Not Yet Implemented', true);

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (1, 'user_mail', 'mail', false, true, true, 1);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (2, 'user_firstname', 'givenName', false, true, true, 1);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (3, 'user_lastname', 'sn', false, true, true, 1);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (4, 'user_uid', 'uid', false, true, true, 1);

-- user domain pattern
INSERT INTO domain_pattern(domain_pattern_id, identifier, description, auth_command, search_user_command, auto_complete_command, system) VALUES (2, 'basePattern', '', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");', 'ldap.list("ou=People," + domain, "(&(objectClass=*)(mail="+mail+")(givenName="+firstName+")(sn="+lastName+"))");', 'Not Yet Implemented', false);

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (5, 'user_mail', 'mail', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (6, 'user_firstname', 'givenName', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (7, 'user_lastname', 'sn', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (8, 'user_uid', 'uid', false, true, true, 2);


INSERT INTO user_provider_ldap(id, differential_key, domain_pattern_id, ldap_connection_id) VALUES (1, 'dc=linpki,dc=org', 1, 1);
-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 1, 1, 2);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 0, 1, 1, 2, 1 , 3);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 2, 1, 4);




-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, password, destroyed, domain_id) VALUES (1, 6, 'root@localhost.localdomain', current_date(), current_date(), 3, 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', false, 1);
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (1, 'Administrator', 'LinShare', 'root@localhost.localdomain', false, '', false, false);

-- system account :
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, enable, destroyed, domain_id) VALUES (2, 7, 'system', current_date(), current_date(), 3, 'en', 'en', true, false, 1);





-- unit type : TIME(0), SIZE(1)
-- unit value : FileSizeUnit : KILO(0), MEGA(1), GIGA(2)
-- unit value : TimeUnit : DAY(0), WEEK(1), MONTH(2)
-- Policies : MANDATORY(0), ALLOWED(1), FORBIDDEN(2)


-- Functionality : FILESIZE_MAX
INSERT INTO policy(id, status, default_status, policy, system) VALUES (1, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (2, true, true, 1, false);
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


-- Functionality : SECURE_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (41, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (42, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (21, true, 'SECURED_ANONYMOUS_URL', 41, 42, 1);

-- Functionality : TAB_THREAD
INSERT INTO policy(id, status, default_status, policy, system) VALUES (45, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (46, false, false, 1, true);
-- if a functionality is system, you will not be hable see/modify its parameters
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

--Functionality : UPDATE_FILE
INSERT INTO policy(id, status, default_status, policy, system) VALUES (55, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (56, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (27, true, 'UPDATE_FILE', 55, 56, 1);

--Functionality : CREATE_THREAD_PERMISSION
INSERT INTO policy(id, status, default_status, policy, system) VALUES (57, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (58, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (28, true, 'CREATE_THREAD_PERMISSION', 57, 58, 1);

-- Functionality : LINK_LOGO
INSERT INTO policy(id, status, default_status, policy, system) VALUES (59, false, false, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (60, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (29, false, 'LINK_LOGO', 59, 60, 1);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (29, 'http://localhost:8080/linshare/en');

--Functionality : NOTIFICATION_URL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (61, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (62, false, false, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(30, false, 'NOTIFICATION_URL', 61, 62, 1); 
INSERT INTO functionality_string(functionality_id, string_value) VALUES (30, 'http://localhost:8080/linshare/');


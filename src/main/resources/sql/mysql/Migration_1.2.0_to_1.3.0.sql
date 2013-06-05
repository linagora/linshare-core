-- MySQL migration script : 1.2.0 to 1.3.0

-- update mail subjects
UPDATE mail_subjects SET content = '${actorRepresentation} has just downloaded a file you made available for sharing' WHERE messages_configuration_id = 1 AND subject_id = 0 AND language_id = 0;
UPDATE mail_subjects SET content = '${actorRepresentation} has just downloaded a file you made available for sharing' WHERE messages_configuration_id = 1 AND subject_id = 1 AND language_id = 0;
UPDATE mail_subjects SET content = 'Your LinShare account has been sucessfully created' WHERE messages_configuration_id = 1 AND subject_id = 2 AND language_id = 0;
UPDATE mail_subjects SET content = 'Your password has been reset' WHERE messages_configuration_id = 1 AND subject_id = 3 AND language_id = 0;
UPDATE mail_subjects SET content = '${actorRepresentation} has just made a file available to you!' WHERE messages_configuration_id = 1 AND subject_id = 4 AND language_id = 0;
UPDATE mail_subjects SET content = '${actorRepresentation} has just modified a shared file you still have access to' WHERE messages_configuration_id = 1 AND subject_id = 5 AND language_id = 0;
UPDATE mail_subjects SET content = '${actorRepresentation} has just deleted a shared file you had access to!' WHERE messages_configuration_id = 1 AND subject_id = 10 AND language_id = 0;
UPDATE mail_subjects SET content = 'A LinShare workspace is about to be deleted' WHERE messages_configuration_id = 1 AND subject_id = 11 AND language_id = 0;
UPDATE mail_subjects SET content = 'A shared file is about to be deleted!' WHERE messages_configuration_id = 1 AND subject_id = 12 AND language_id = 0;
UPDATE mail_subjects SET content = '${actorRepresentation} a téléchargé des fichiers en partag' WHERE messages_configuration_id = 1 AND subject_id = 0 AND language_id = 1;
UPDATE mail_subjects SET content = '${actorRepresentation} a téléchargé des fichiers en partag' WHERE messages_configuration_id = 1 AND subject_id = 1 AND language_id = 1;
UPDATE mail_subjects SET content = 'Votre compte LinShare a été créé' WHERE messages_configuration_id = 1 AND subject_id = 2 AND language_id = 1;
UPDATE mail_subjects SET content = 'Votre nouveau mot de pass' WHERE messages_configuration_id = 1 AND subject_id = 3 AND language_id = 1;
UPDATE mail_subjects SET content = '${actorRepresentation} vous a déposé des fichiers en partag' WHERE messages_configuration_id = 1 AND subject_id = 4 AND language_id = 1;
UPDATE mail_subjects SET content = '${actorRepresentation} a mis à jour un fichier partagé' WHERE messages_configuration_id = 1 AND subject_id = 5 AND language_id = 1;
UPDATE mail_subjects SET content = '${actorRepresentation} a supprimé un fichier partagé' WHERE messages_configuration_id = 1 AND subject_id = 10 AND language_id = 1;
UPDATE mail_subjects SET content = 'Un partage va bientôt expirer' WHERE messages_configuration_id = 1 AND subject_id = 11 AND language_id = 1;
UPDATE mail_subjects SET content = 'Un fichier va bientôt être supprimé' WHERE messages_configuration_id = 1 AND subject_id = 12 AND language_id = 1;
UPDATE mail_subjects SET content = '${actorRepresentation} gebruiker heeft het door u gedeelde bestand gedownload' WHERE messages_configuration_id = 1 AND subject_id = 0 AND language_id = 2;
UPDATE mail_subjects SET content = 'Een gebruiker heeft het door u gedeelde bestand gedownload' WHERE messages_configuration_id = 1 AND subject_id = 1 AND language_id = 2;
UPDATE mail_subjects SET content = 'Uw LinShare account werd aangemaakt.' WHERE messages_configuration_id = 1 AND subject_id = 2 AND language_id = 2;
UPDATE mail_subjects SET content = 'Uw nieuwe wachtwoord' WHERE messages_configuration_id = 1 AND subject_id = 3 AND language_id = 2;
UPDATE mail_subjects SET content = '${actorRepresentation} gebruiker heeft te delen bestanden voor u klaargezet.' WHERE messages_configuration_id = 1 AND subject_id = 4 AND language_id = 2;
UPDATE mail_subjects SET content = '${actorRepresentation} gebruiker heeft een gedeeld bestand bijgewerkt' WHERE messages_configuration_id = 1 AND subject_id = 5 AND language_id = 2;
UPDATE mail_subjects SET content = '${actorRepresentation} gebruiker heeft een gedeeld bestand gewist' WHERE messages_configuration_id = 1 AND subject_id = 10 AND language_id = 2;
UPDATE mail_subjects SET content = 'Een share zal binnenkort gewist worden.' WHERE messages_configuration_id = 1 AND subject_id = 11 AND language_id = 2;
UPDATE mail_subjects SET content = 'Een bestand zal binnenkort gewist worden.' WHERE messages_configuration_id = 1 AND subject_id = 12 AND language_id = 2;

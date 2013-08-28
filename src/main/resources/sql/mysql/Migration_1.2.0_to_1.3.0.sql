-- MySQL migration script : 1.2.0 to 1.3.0
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0
START TRANSACTION;
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

INSERT INTO version (description) VALUES ('1.3.0');

COMMIT;
SET AUTOCOMMIT=1;


-- Postgresql migration script : 1.2.0 to 1.3.0
BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

-- update mail subjects
UPDATE mail_subjects SET content = E'${actorRepresentation} has just downloaded a file you made available for sharing' WHERE messages_configuration_id = 1 AND subject_id = 0 AND language_id = 0;
UPDATE mail_subjects SET content = E'${actorRepresentation} has just downloaded a file you made available for sharing' WHERE messages_configuration_id = 1 AND subject_id = 1 AND language_id = 0;
UPDATE mail_subjects SET content = E'Your LinShare account has been sucessfully created' WHERE messages_configuration_id = 1 AND subject_id = 2 AND language_id = 0;
UPDATE mail_subjects SET content = E'Your password has been reset' WHERE messages_configuration_id = 1 AND subject_id = 3 AND language_id = 0;
UPDATE mail_subjects SET content = E'${actorRepresentation} has just made a file available to you!' WHERE messages_configuration_id = 1 AND subject_id = 4 AND language_id = 0;
UPDATE mail_subjects SET content = E'${actorRepresentation} has just modified a shared file you still have access to' WHERE messages_configuration_id = 1 AND subject_id = 5 AND language_id = 0;
UPDATE mail_subjects SET content = E'${actorRepresentation} has just deleted a shared file you had access to!' WHERE messages_configuration_id = 1 AND subject_id = 10 AND language_id = 0;
UPDATE mail_subjects SET content = E'A LinShare workspace is about to be deleted' WHERE messages_configuration_id = 1 AND subject_id = 11 AND language_id = 0;
UPDATE mail_subjects SET content = E'A shared file is about to be deleted!' WHERE messages_configuration_id = 1 AND subject_id = 12 AND language_id = 0;
UPDATE mail_subjects SET content = E'${actorRepresentation} a téléchargé des fichiers en partage' WHERE messages_configuration_id = 1 AND subject_id = 0 AND language_id = 1;
UPDATE mail_subjects SET content = E'${actorRepresentation} a téléchargé des fichiers en partage' WHERE messages_configuration_id = 1 AND subject_id = 1 AND language_id = 1;
UPDATE mail_subjects SET content = E'Votre compte LinShare a été créé' WHERE messages_configuration_id = 1 AND subject_id = 2 AND language_id = 1;
UPDATE mail_subjects SET content = E'Votre nouveau mot de passe' WHERE messages_configuration_id = 1 AND subject_id = 3 AND language_id = 1;
UPDATE mail_subjects SET content = E'${actorRepresentation} vous a déposé des fichiers en partage' WHERE messages_configuration_id = 1 AND subject_id = 4 AND language_id = 1;
UPDATE mail_subjects SET content = E'${actorRepresentation} a mis à jour un fichier partagé' WHERE messages_configuration_id = 1 AND subject_id = 5 AND language_id = 1;
UPDATE mail_subjects SET content = E'${actorRepresentation} a supprimé un fichier partagé' WHERE messages_configuration_id = 1 AND subject_id = 10 AND language_id = 1;
UPDATE mail_subjects SET content = E'Un partage va bientôt expirer' WHERE messages_configuration_id = 1 AND subject_id = 11 AND language_id = 1;
UPDATE mail_subjects SET content = E'Un fichier va bientôt être supprimé' WHERE messages_configuration_id = 1 AND subject_id = 12 AND language_id = 1;
UPDATE mail_subjects SET content = E'${actorRepresentation} gebruiker heeft het door u gedeelde bestand gedownload' WHERE messages_configuration_id = 1 AND subject_id = 0 AND language_id = 2;
UPDATE mail_subjects SET content = E'Een gebruiker heeft het door u gedeelde bestand gedownload' WHERE messages_configuration_id = 1 AND subject_id = 1 AND language_id = 2;
UPDATE mail_subjects SET content = E'Uw LinShare account werd aangemaakt.' WHERE messages_configuration_id = 1 AND subject_id = 2 AND language_id = 2;
UPDATE mail_subjects SET content = E'Uw nieuwe wachtwoord' WHERE messages_configuration_id = 1 AND subject_id = 3 AND language_id = 2;
UPDATE mail_subjects SET content = E'${actorRepresentation} gebruiker heeft te delen bestanden voor u klaargezet.' WHERE messages_configuration_id = 1 AND subject_id = 4 AND language_id = 2;
UPDATE mail_subjects SET content = E'${actorRepresentation} gebruiker heeft een gedeeld bestand bijgewerkt' WHERE messages_configuration_id = 1 AND subject_id = 5 AND language_id = 2;
UPDATE mail_subjects SET content = E'${actorRepresentation} gebruiker heeft een gedeeld bestand gewist' WHERE messages_configuration_id = 1 AND subject_id = 10 AND language_id = 2;
UPDATE mail_subjects SET content = E'Een share zal binnenkort gewist worden.' WHERE messages_configuration_id = 1 AND subject_id = 11 AND language_id = 2;
UPDATE mail_subjects SET content = E'Een bestand zal binnenkort gewist worden.' WHERE messages_configuration_id = 1 AND subject_id = 12 AND language_id = 2;

COMMIT;

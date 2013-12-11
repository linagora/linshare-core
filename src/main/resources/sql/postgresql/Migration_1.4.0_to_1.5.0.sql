-- Postgresql migration script : 1.4.0 to 1.5.0
BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

DROP FUNCTION  IF EXISTS copy_domain_abstract2mail_subjects();
CREATE OR REPLACE FUNCTION copy_domain_abstract2mail_subjects()
  RETURNS VOID AS $$
DECLARE
  rec RECORD;
BEGIN
	FOR rec IN SELECT DISTINCT(messages_configuration_id) from domain_abstract LOOP
		INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (rec.messages_configuration_id, 13, '${actorSubject} from ${actorRepresentation}', 0);
		INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (rec.messages_configuration_id, 13, '${actorSubject} de la part de ${actorRepresentation}', 1);
		INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (rec.messages_configuration_id, 13, '${actorSubject} from ${actorRepresentation}', 2);
	END LOOP;
END;
$$ language 'plpgsql';


DELETE FROM mail_subjects WHERE subject_id=13 ;
SELECT copy_domain_abstract2mail_subjects();

-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.5.0');

COMMIT;


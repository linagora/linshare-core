-- Postgresql migration script : 1.4.0 to 1.5.0

-- this script must be run with postgres user.

BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;

DROP EXTENSION IF EXISTS plpgsql;
DROP LANGUAGE IF EXISTS plpgsql;
DROP FUNCTION IF EXISTS plpgsql_call_handler();

CREATE OR REPLACE FUNCTION plpgsql_call_handler () RETURNS language_handler
    AS '$libdir/plpgsql.so', 'plpgsql_call_handler'
        LANGUAGE C;

CREATE TRUSTED PROCEDURAL LANGUAGE plpgsql HANDLER plpgsql_call_handler;
GRANT USAGE ON LANGUAGE plpgsql TO public;

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

DROP FUNCTION  IF EXISTS copy_domain_abstract2mail_subjects();
drop extension IF EXISTS plpgsql;
DROP LANGUAGE IF EXISTS plpgsql;


ALTER TABLE ldap_attribute ADD COLUMN completion bool DEFAULT true NOT NULL;
UPDATE ldap_attribute SET completion=false  WHERE field = 'user_uid';

ALTER TABLE domain_pattern ADD COLUMN auto_complete_command_on_first_and_last_name text DEFAULT 'To be define' NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN auto_complete_command_on_all_attributes text DEFAULT 'To be define' NOT NULL;

ALTER TABLE domain_pattern DROP COLUMN auto_complete_command;

ALTER TABLE domain_pattern ADD COLUMN search_page_size int4 DEFAULT 100 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN search_size_limit int4 DEFAULT 100 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN completion_page_size int4 DEFAULT 10 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN completion_size_limit int4 DEFAULT 10 NOT NULL;

delete from ldap_attribute where domain_pattern_id=1;
delete from domain_pattern where domain_pattern_id=1;


-- system domain pattern
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





-- LinShare version
INSERT INTO version (id,description) VALUES ((SELECT nextVal('hibernate_sequence')),'1.5.0');

COMMIT;


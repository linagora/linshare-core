-- MySQL migration script : 1.4.0 to 1.5.0
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;

SET AUTOCOMMIT=0
START TRANSACTION;
-- update mail subjects

DROP PROCEDURE IF EXISTS copy_domain_abstract2mail_subjects;

DELIMITER $$
 
CREATE PROCEDURE copy_domain_abstract2mail_subjects ()
BEGIN
 
    DECLARE v_finished INTEGER DEFAULT 0;
	DECLARE v_messageID bigint(8) DEFAULT "";
 
    -- declare cursor for employee email
    DEClARE message_cursor CURSOR FOR 
        SELECT distinct(messages_configuration_id) from domain_abstract ;
 
    -- declare NOT FOUND handler
    DECLARE CONTINUE HANDLER 
        FOR NOT FOUND SET v_finished = 1;
 
    OPEN message_cursor;
 
    get_message: LOOP
 
        FETCH message_cursor INTO v_messageID;
 
        IF v_finished = 1 THEN 
            LEAVE get_message;
        END IF;

        INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (v_messageID, 13, '${actorSubject} from ${actorRepresentation}', 0);
		INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (v_messageID, 13, '${actorSubject} de la part de ${actorRepresentation}', 1);
		INSERT INTO mail_subjects (messages_configuration_id, subject_id, content, language_id) VALUES (v_messageID, 13, '${actorSubject} from ${actorRepresentation}', 2);
 
    END LOOP get_message;
 
    CLOSE message_cursor;
 
END$$
 
DELIMITER ;


DELETE FROM mail_subjects WHERE subject_id=13;
call copy_domain_abstract2mail_subjects();
DROP PROCEDURE IF EXISTS copy_domain_abstract2mail_subjects;


ALTER TABLE `domain_pattern` DROP `description` ;
ALTER TABLE `domain_pattern`  ADD `description` TEXT AFTER `identifier` NOT NULL;

ALTER TABLE ldap_attribute ADD COLUMN completion bool DEFAULT true NOT NULL;
UPDATE ldap_attribute SET completion=false  WHERE field = 'user_uid';

ALTER TABLE domain_pattern ADD COLUMN auto_complete_command_on_first_and_last_name text DEFAULT 'To be define' NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN auto_complete_command_on_all_attributes text DEFAULT 'To be define' NOT NULL;

ALTER TABLE domain_pattern DROP COLUMN auto_complete_command;

ALTER TABLE domain_pattern ADD COLUMN search_page_size int4 DEFAULT 100 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN search_size_limit int4 DEFAULT 100 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN completion_page_size int4 DEFAULT 10 NOT NULL;
ALTER TABLE domain_pattern ADD COLUMN completion_size_limit int4 DEFAULT 10 NOT NULL;

DELETE FROM ldap_attribute WHERE domain_pattern_id=1;
DELETE FROM domain_pattern WHERE domain_pattern_id=1;


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
INSERT INTO version (description) VALUES ('1.5.0');

COMMIT;
SET AUTOCOMMIT=1;



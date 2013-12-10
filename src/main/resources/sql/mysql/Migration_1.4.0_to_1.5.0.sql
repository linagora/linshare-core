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


DELETE FROM mail_subjects WHERE subject_id=13 ;
call copy_domain_abstract2mail_subjects();


INSERT INTO version (description) VALUES ('1.5.0');

COMMIT;
SET AUTOCOMMIT=1;



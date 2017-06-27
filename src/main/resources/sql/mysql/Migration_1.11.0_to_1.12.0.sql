-- MySQL migration script from 1.11.0 to 1.12.0

SET default_storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;
SET AUTOCOMMIT=0;
START TRANSACTION;

DROP PROCEDURE IF EXISTS ls_prechecks;
DROP PROCEDURE IF EXISTS ls_version;
DROP PROCEDURE IF EXISTS ls_check_user_connected;

SET @version_to = '1.12.0';
SET @version_from = '1.11.0';

delimiter '$$'
CREATE PROCEDURE ls_version()
BEGIN
	INSERT INTO version (version) VALUES (@version_to);
END$$

CREATE PROCEDURE ls_prechecks()
BEGIN
	SET @start := concat('You are about to upgrade from LinShare : ', @version_from, ' to ' , @version_to);
	SET @version_history_from := (SELECT version from version ORDER BY id DESC LIMIT 1);
	SET @database_info = version();
	SET @database := concat('Mysql version :', ' ', @database_info);
	SET @error:= concat('Your database upgrade history indicates that you already upgraded to :', @version_to);
	SET @connection_id = CONNECTION_ID();

	SELECT @start AS '';
	SELECT @version_history_from AS 'Your actual version of linShare is :';
	SELECT version AS 'Your database history is :' from version ORDER BY id DESC;
	SELECT @database AS 'Your database system information is :';

	IF (@version_history_from <> @version_from) THEN
		SELECT ' ';
		SELECT concat('You must be in version : ', @version_from,' to run this script. You are actually in version: ', @version_history_from, '.') AS '__ERROR__';
		IF EXISTS (SELECT * from version where version = @version_to) THEN
			SELECT @error AS '__WARNING__';
		END IF;
		SELECT 'We are going to kill to conection in order to stop the migration script.' as ' ';
--		DIRTY: did it to stop the process cause there is no clean way to do it, expected error code: 1317.
		KILL @connection_id;
	END IF;

END$$

CREATE PROCEDURE ls_check_user_connected()
BEGIN
	SET @connection_id := CONNECTION_ID();
	SET @database := DATABASE();
	SET @user_connected := (SELECT `USER` FROM INFORMATION_SCHEMA.PROCESSLIST WHERE DB = @database);
		SELECT 'You are actually connected with the user "root", you should be connected with your LinShare database user, we are about to stop the migration script.' AS '__WARNING__';
	IF (@user_connected = 'root') THEN
--		DIRTY: did it to stop the process cause there is no clean way to do it, expected error code: 1317.
		KILL @connection_id;
	END IF;
END$$

delimiter ';'

call ls_check_user_connected();
call ls_prechecks();

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;

DROP PROCEDURE IF EXISTS ls_drop_column_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_constraint_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_index_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_primarykey_if_exists;
DROP PROCEDURE IF EXISTS ls_drop_id_if_exists_from_func_boolean;
DROP PROCEDURE IF EXISTS ls_migrate_destroyed_false;
DROP PROCEDURE IF EXISTS ls_delete_duplicated;
DROP FUNCTION IF EXISTS ls_return_last_insert;

delimiter '$$'
CREATE PROCEDURE ls_drop_column_if_exists(IN ls_table_name VARCHAR(255), IN ls_column_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE local_ls_column_name varchar(255) DEFAULT ls_column_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema = ls_database_name AND table_name = ls_table_name AND column_name = ls_column_name) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP COLUMN ', local_ls_column_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_constraint_if_exists(IN ls_table_name VARCHAR(255), IN ls_constraint_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE local_ls_constraint_name varchar(255) DEFAULT ls_constraint_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND constraint_name = ls_constraint_name AND constraint_type <> 'UNIQUE' ) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP FOREIGN KEY ', local_ls_constraint_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND constraint_name = ls_constraint_name AND constraint_type = 'UNIQUE' ) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP INDEX ', local_ls_constraint_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_index_if_exists(IN ls_table_name VARCHAR(255), IN ls_index_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE local_ls_index_name varchar(255) DEFAULT ls_index_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT * FROM information_schema.STATISTICS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND INDEX_NAME = ls_index_name) THEN
        SET @SQL := CONCAT('DROP INDEX ', local_ls_index_name, ' ON ', local_ls_table_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_primarykey_if_exists(IN ls_table_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
    IF EXISTS (SELECT NULL FROM information_schema.table_constraints WHERE constraint_type = 'PRIMARY KEY' AND table_name = ls_table_name AND table_schema = ls_database_name) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP PRIMARY KEY ', ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE PROCEDURE ls_drop_id_if_exists_from_func_boolean(IN ls_table_name VARCHAR(255), IN ls_column_name VARCHAR(255))
BEGIN
    DECLARE ls_database_name varchar(255);
    DECLARE local_ls_table_name varchar(255) DEFAULT ls_table_name;
    DECLARE _stmt VARCHAR(1024);
    SELECT DATABASE() INTO ls_database_name;
       IF EXISTS (SELECT * FROM information_schema.columns WHERE table_schema = ls_database_name AND table_name = ls_table_name AND column_name = ls_column_name) THEN
       call ls_drop_column_if_exists("functionality_boolean", "id");
       call ls_drop_constraint_if_exists("functionality_boolean", "linshare_functionality_boolean_pkey");
       SET @SQL := CONCAT('ALTER TABLE ', ls_table_name, ' ADD PRIMARY KEY(functionality_id);');
       select @SQL;
       PREPARE _stmt FROM @SQL;
       EXECUTE _stmt;
       DEALLOCATE PREPARE _stmt;
    END IF;
END$$

CREATE FUNCTION ls_return_last_insert() RETURNS bigint(8)
BEGIN
    DECLARE result bigint(8);
    INSERT INTO policy (status, default_status, policy, system) VALUES (true, true, 1, false);
    SET result = last_insert_id();
    return result;
END$$

delimiter ';'

-- Here you start your migration instructions.
ALTER TABLE account ADD COLUMN mail varchar(255);
UPDATE account AS a, users AS u SET a.mail = u.mail WHERE u.account_id = a.id;
UPDATE account AS a, thread AS t SET a.mail = a.ls_uuid WHERE a.id = t.account_id;
ALTER TABLE account MODIFY destroyed varchar(255) NULL DEFAULT NULL;
ALTER TABLE account CHANGE destroyed destroyed bigint(8);

delimiter '$$'
CREATE PROCEDURE ls_migrate_destroyed_false()
BEGIN
	BLOCK1:BEGIN
		DECLARE v_finished INTEGER DEFAULT 0;
		DECLARE destroyed_id INTEGER DEFAULT 0;
		DECLARE i INTEGER DEFAULT 1;
		DECLARE v_mail VARCHAR(255);

		-- declare destroyed cursor
		DECLARE destroyed_cursor CURSOR FOR
			SELECT mail FROM account AS a WHERE destroyed > 0 GROUP BY mail;
		-- declare NOT FOUND handler
		DECLARE CONTINUE HANDLER
		FOR NOT FOUND SET v_finished = 1;

		OPEN destroyed_cursor;
		get_destroyed: LOOP
			FETCH destroyed_cursor INTO v_mail;

			IF v_finished = 1 THEN
				LEAVE get_destroyed;
			END IF;
			BLOCK2:BEGIN
				DECLARE v_finish INTEGER DEFAULT 0;
				DECLARE update_cursor CURSOR FOR SELECT id FROM account AS a WHERE destroyed > 0 AND a.mail = v_mail ORDER BY modification_date;
				DECLARE CONTINUE HANDLER
				FOR NOT FOUND SET v_finish = 1;
				OPEN update_cursor;
				SET i := 1;
				upd_dest: LOOP
					FETCH update_cursor INTO destroyed_id;
					IF v_finish = 1 THEN
						LEAVE upd_dest;
					END IF;
					UPDATE account as A SET destroyed = i WHERE id = destroyed_id;
					SET i := i + 1;
				END LOOP upd_dest;
				CLOSE update_cursor;
			END BLOCK2;
		END LOOP get_destroyed;
		CLOSE destroyed_cursor;
	END BLOCK1;
END$$

CREATE PROCEDURE ls_delete_duplicated()
BEGIN
	BLOCK1:BEGIN
		DECLARE v_finished INTEGER DEFAULT 0;
		DECLARE v_destid INTEGER DEFAULT 0;
		DECLARE dom_id INTEGER DEFAULT 0;
		DECLARE i INTEGER DEFAULT 1;
		DECLARE v_mail VARCHAR(255);

		-- declare destroyed cursor
		DECLARE destroyed_cursor CURSOR FOR
		SELECT a.mail, a.domain_id dom
			FROM users u
			JOIN account a ON u.account_id = a.id
			LEFT JOIN entry e ON a.id = e.owner_id
			WHERE u.mail IN (
				SELECT u2.mail
					FROM users u2
					JOIN account a2 ON u2.account_id = a2.id
					WHERE a2.destroyed = 0
					GROUP BY u2.mail, a2.domain_id
					HAVING COUNT(a2.id) > 1
					)
					AND a.destroyed = 0
					GROUP BY u.account_id, u.mail, a.creation_date, a.modification_date, a.domain_id;
		-- declare NOT FOUND handler
		DECLARE CONTINUE HANDLER
		FOR NOT FOUND SET v_finished = 1;

		OPEN destroyed_cursor;

		get_destroyed: LOOP

			FETCH destroyed_cursor INTO v_mail, dom_id;
			IF v_finished = 1 THEN
			    LEAVE get_destroyed;
			END IF;
			BLOCK2:BEGIN
				DECLARE v_finish INTEGER DEFAULT 0;
				DECLARE destroyed_id INTEGER DEFAULT 0;
				DECLARE update_cursor CURSOR FOR
					SELECT id FROM account WHERE mail = v_mail and domain_id = dom_id and destroyed = 0;
				DECLARE CONTINUE HANDLER
				FOR NOT FOUND SET v_finish = 1;
				OPEN update_cursor;
				SET i := 0;
				upd_dest:LOOP
					FETCH update_cursor INTO destroyed_id;
					IF v_finish = 1 THEN
					   LEAVE upd_dest;
					END IF;
					IF i > 0 THEN
					   UPDATE account SET destroyed = i WHERE id = destroyed_id;
					END IF;
					SET i := i + 1;
				END LOOP upd_dest;
			END BLOCK2;
		END LOOP get_destroyed;
	END BLOCK1;
END$$

delimiter ';'

call ls_migrate_destroyed_false();
call ls_delete_duplicated();

ALTER TABLE users DROP COLUMN mail;
ALTER TABLE account MODIFY destroyed bigint(8) NOT NULL;
UPDATE account AS a SET mail = ls_uuid WHERE ls_uuid = 'system';
UPDATE account AS a SET mail = ls_uuid WHERE ls_uuid = 'system-account-uploadrequest';
DELETE FROM account WHERE mail IS NULL;
ALTER TABLE account MODIFY mail varchar(255) NOT NULL;
ALTER TABLE account ADD CONSTRAINT account_unique_mail_domain_destroyed UNIQUE (mail, domain_id, destroyed);

ALTER TABLE upload_request_entry ADD COLUMN upload_request_url_id bigint(8);
call ls_drop_constraint_if_exists("upload_request_entry", "fkupload_req220981");
call ls_drop_constraint_if_exists("upload_request_entry", "FKupload_req220981");
ALTER TABLE upload_request_entry DROP INDEX fkupload_req220981;

delimiter '$$'
CREATE PROCEDURE ls_update_upload_request_entry()
BEGIN
	BLOCK1:BEGIN
		DECLARE v_finished INTEGER DEFAULT 0;
		DECLARE upload_req_id INTEGER DEFAULT 0;
		DECLARE url_id INTEGER DEFAULT 0;

		-- declare upload request cursor
		DECLARE upload_cursor CURSOR FOR
			SELECT upload_request_id FROM upload_request_entry;
		-- declare NOT FOUND handler
		DECLARE CONTINUE HANDLER
		FOR NOT FOUND SET v_finished = 1;

		OPEN upload_cursor;
		get_upload: LOOP
			FETCH upload_cursor INTO upload_req_id;
			IF v_finished = 1 THEN
				LEAVE get_upload;
			END IF;
			BLOCK2:BEGIN
				DECLARE v_finish INTEGER DEFAULT 0;
				--	PAS BESOIN DE JOINTURE
				DECLARE update_cursor CURSOR FOR SELECT uu.id FROM upload_request_url AS uu WHERE uu.upload_request_id = upload_req_id LIMIT 1;
				DECLARE CONTINUE HANDLER
				FOR NOT FOUND SET v_finish = 1;
				OPEN update_cursor;
				upd_req: LOOP
					FETCH update_cursor INTO url_id;
					IF v_finish = 1 THEN
						LEAVE upd_req;
					END IF;
					UPDATE upload_request_entry SET upload_request_url_id = url_id WHERE upload_request_id = upload_req_id;
				END LOOP upd_req;
				CLOSE update_cursor;
			END BLOCK2;
		END LOOP get_upload;
		CLOSE upload_cursor;
	END BLOCK1;
END$$

delimiter ';'

call ls_update_upload_request_entry();
ALTER TABLE upload_request_entry DROP COLUMN upload_request_id;
ALTER TABLE upload_request_entry MODIFY upload_request_url_id bigint(8) NOT NULL;
ALTER TABLE upload_request_entry ADD INDEX FKupload_req220981 (upload_request_url_id), ADD CONSTRAINT FKupload_req220981 FOREIGN KEY (upload_request_url_id) REFERENCES upload_request_url (id);
ALTER TABLE upload_request ADD notified tinyint(1) DEFAULT false NOT NULL;
-- system account for upload-request:
UPDATE account set role_id = 6 WHERE id = 3;

UPDATE document SET sha256sum = NULL;

-- End of your migration instructions.

-- LinShare version
call ls_version();

-- Alias for Users
-- All users
CREATE VIEW alias_users_list_all AS
 SELECT id, first_name, last_name, a.mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
-- All active users
CREATE VIEW alias_users_list_active AS
 SELECT id, first_name, last_name, a.mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed users
CREATE VIEW alias_users_list_destroyed AS
 SELECT id, first_name, last_name, a.mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed > 0;

-- Alias for threads
-- All threads
CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
-- All active threads
CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
-- All destroyed threads
CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed > 0;

COMMIT;
SET AUTOCOMMIT=1;

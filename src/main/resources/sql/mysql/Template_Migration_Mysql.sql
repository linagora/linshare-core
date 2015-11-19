-- MySQL migration script template

SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;
SET AUTOCOMMIT=0;
START TRANSACTION;

DROP PROCEDURE IF EXISTS ls_prechecks;
DROP PROCEDURE IF EXISTS ls_version;
DROP PROCEDURE IF EXISTS ls_check_user_connected;

-- TODO: CHANGE THE VERSIONS
SET @version_to = 'CHANGE ME';
SET @version_from = 'CHANGE ME';

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
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND LOWER(constraint_name) = LOWER(ls_constraint_name) AND constraint_type <> 'UNIQUE' ) THEN
        SET @SQL := CONCAT('ALTER TABLE ', local_ls_table_name, ' DROP FOREIGN KEY ', local_ls_constraint_name , ";");
        select @SQL;
        PREPARE _stmt FROM @SQL;
        EXECUTE _stmt;
        DEALLOCATE PREPARE _stmt;
    END IF;
    IF EXISTS (SELECT * FROM information_schema.TABLE_CONSTRAINTS WHERE table_schema = ls_database_name AND table_name = ls_table_name AND LOWER(constraint_name) = LOWER(ls_constraint_name) AND constraint_type = 'UNIQUE' ) THEN
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


-- End of your migration instructions.

-- LinShare version
call ls_version();

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

COMMIT;
SET AUTOCOMMIT=1;
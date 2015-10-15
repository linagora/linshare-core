-- MySQL migration script template

SET storage_engine=INNODB;
SET NAMES UTF8 COLLATE utf8_general_ci;
SET CHARACTER SET UTF8;
SET AUTOCOMMIT=0;
START TRANSACTION;

DROP PROCEDURE IF EXISTS ls_prechecks;
DROP PROCEDURE IF EXISTS ls_version;

-- TODO: CHANGE THE VERSIONS
SET @version_to = '1.11.0';
SET @version_from = '1.10.0';

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

delimiter ';'

call ls_prechecks();

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;

-- Here you start your migration instructions.


-- fix migration 1.9 to 1.10
DROP TABLE upload_request_entry_url;

CREATE TABLE async_task (
    id                    bigint(8) NOT NULL AUTO_INCREMENT,
    owner_id              bigint(8) NOT NULL,
    actor_id              bigint(8) NOT NULL,
    domain_abstract_id    bigint(8) NOT NULL,
    uuid                  varchar(255) NOT NULL,
    task_type             varchar(255) NOT NULL,
    resource_uuid         varchar(255),
    status                varchar(255) NOT NULL,
    creation_date         datetime NOT NULL,
    start_processing_date datetime NULL,
    end_processing_date   datetime NULL,
    processing_duration   bigint(8),
    modification_date     datetime NOT NULL,
    error_code            int(4),
    error_name            varchar(255),
    error_msg             text,
    ls_size               bigint(8),
    file_name             text,
    frequency             int(4),
    transfert_duration    bigint(8),
    waiting_duration      bigint(8),
    meta_data             text,
    PRIMARY KEY (id),
    INDEX (id),
    INDEX (owner_id),
    INDEX (actor_id),
    INDEX (domain_abstract_id),
    UNIQUE INDEX (uuid)) CHARACTER SET UTF8;

ALTER TABLE async_task ADD INDEX FKasync_task548996 (domain_abstract_id), ADD CONSTRAINT FKasync_task548996 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE async_task ADD INDEX FKasync_task706276 (actor_id), ADD CONSTRAINT FKasync_task706276 FOREIGN KEY (actor_id) REFERENCES account (id);
ALTER TABLE async_task ADD INDEX FKasync_task559470 (owner_id), ADD CONSTRAINT FKasync_task559470 FOREIGN KEY (owner_id) REFERENCES account (id);

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

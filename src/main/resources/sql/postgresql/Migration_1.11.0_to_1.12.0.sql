-- Postgresql migration script from 1.11.0 to 1.12.0

BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

CREATE OR REPLACE FUNCTION ls_version() RETURNS void AS $$
BEGIN
	INSERT INTO version (id, version) VALUES ((SELECT nextVal('hibernate_sequence')),'1.12.0');
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_prechecks() RETURNS void AS $$
BEGIN
	DECLARE version_to VARCHAR := '1.12.0';
	DECLARE version_from VARCHAR := '1.11.0';
	DECLARE start VARCHAR := concat('You are about to upgrade from LinShare : ', version_from,  ' to ' , version_to);
	DECLARE version_history_from VARCHAR := (SELECT version from version ORDER BY id DESC LIMIT 1);
	DECLARE database_info VARCHAR = version();
	DECLARE error VARCHAR := concat('Your database upgrade history indicates that you already upgraded to : ', version_to);
	DECLARE connection_id INT := pg_backend_pid();
	DECLARE row record;
	BEGIN
		RAISE NOTICE '%', start;
		RAISE NOTICE 'Your actual version is: %', version_history_from;
		RAISE NOTICE 'Your databse history is :';
		FOR row IN (SELECT * FROM version ORDER BY id DESC) LOOP
			RAISE INFO '%', row.version;
		END LOOP;
		RAISE NOTICE 'Your database system information is : %', database_info;
		IF (version_from <> version_history_from) THEN
			RAISE WARNING 'You must be in version : % to run this script. You are actually in version: %', version_from, version_history_from;
			IF EXISTS (SELECT * from version where version = version_to) THEN
				RAISE WARNING '%', error;
			END IF;
			RAISE WARNING 'We are about to abort the migration script, all the following instructions will be aborted and transaction will rollback.';
			RAISE INFO 'You should expect the following error : "query has no destination for result data".';
	--		DIRTY: did it to stop the process cause there is no clean way to do it.
	--		Expected error: query has no destination for result data.
			select error;
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_check_user_connected() RETURNS void AS $$
BEGIN
	DECLARE database VARCHAR := (SELECT current_database());
	DECLARE user_connected VARCHAR = (SELECT usename FROM pg_stat_activity where datname = database);
	DECLARE error VARCHAR := ('You are actually connected with the user "postgres", you should be connected with your LinShare database user, we are about to stop the migration script.');
	BEGIN
		IF (user_connected = 'postgres') THEN
			RAISE WARNING '%', error;
		--	DIRTY: did it to stop the process cause there is no clean way to do it.
		--	Expected error: query has no destination for result data.
			SELECT '';
		END IF;
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_check_user_connected();
SELECT ls_prechecks();

SET client_min_messages = warning;

DROP VIEW IF EXISTS alias_users_list_all;
DROP VIEW IF EXISTS alias_users_list_active;
DROP VIEW IF EXISTS alias_users_list_destroyed;
DROP VIEW IF EXISTS alias_threads_list_all;
DROP VIEW IF EXISTS alias_threads_list_active;
DROP VIEW IF EXISTS alias_threads_list_destroyed;
-- -- Here your request

-- HANDLING DESTROYED
ALTER TABLE account ADD COLUMN mail varchar(255);
UPDATE account AS a SET mail = u.mail FROM users AS u WHERE a.id = u.account_id;
UPDATE account AS a SET mail = ls_uuid FROM thread AS t WHERE a.id = t.account_id;
ALTER TABLE account ALTER COLUMN destroyed DROP NOT NULL;
ALTER TABLE account ALTER COLUMN destroyed TYPE varchar(255);
UPDATE account set destroyed = '0' WHERE destroyed = 'false';
UPDATE account set destroyed = '1' WHERE destroyed = 'true';
ALTER TABLE account ALTER COLUMN destroyed TYPE BIGINT USING (trim(destroyed)::BIGINT);
CREATE OR REPLACE FUNCTION ls_migrate_destroyed_false() RETURNS void AS $$
BEGIN
	DECLARE r record;
	DECLARE t record;
	DECLARE v record;
	DECLARE i INT;
	BEGIN
		FOR r IN (SELECT id FROM domain_abstract) LOOP
			FOR t IN (SELECT mail, domain_id FROM account WHERE destroyed > 0  GROUP BY mail, domain_id) LOOP
				i := 1;
				FOR v IN (SELECT id FROM account AS a WHERE destroyed > 0 AND a.mail = t.mail ORDER BY modification_date) LOOP
					UPDATE account AS a SET destroyed = i WHERE a.id = v.id;
					i := i + 1;
				END LOOP;
			END LOOP;
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ls_delete_duplicated() RETURNS void AS $$
BEGIN
	DECLARE r record;
	DECLARE t record;
	DECLARE j INT := 0;

	BEGIN
		FOR r IN (SELECT u.account_id acc, u.mail, a.creation_date, a.modification_date, a.domain_id dom, COUNT(e.id) entries
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
					GROUP BY u.account_id, u.mail, a.creation_date, a.modification_date, a.domain_id) LOOP
			j := 0;
			FOR t IN (SELECT mail, id, domain_id, destroyed FROM account WHERE mail = r.mail and domain_id = r.dom and destroyed = 0) LOOP
				IF j > 0 THEN
					RAISE NOTICE '%', j;
					RAISE NOTICE '%', r.mail;
					UPDATE account SET destroyed = j WHERE id = t.id;
				END IF;
				j := j + 1;
			END LOOP;
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_migrate_destroyed_false();
SELECT ls_delete_duplicated();

ALTER TABLE users DROP COLUMN mail;
ALTER TABLE account ALTER COLUMN destroyed SET NOT NULL;
UPDATE account AS a SET mail = ls_uuid WHERE ls_uuid = 'system';
UPDATE account AS a SET mail = ls_uuid WHERE ls_uuid = 'system-account-uploadrequest';
ALTER TABLE account ALTER COLUMN mail SET NOT NULL;
ALTER TABLE account ADD CONSTRAINT account_unique_mail_domain_destroyed UNIQUE (mail, domain_id, destroyed);

ALTER TABLE upload_request_entry ADD COLUMN upload_request_url_id INT8;
ALTER TABLE upload_request_entry DROP CONSTRAINT FKupload_req220981;

CREATE OR REPLACE FUNCTION ls_update_upload_request_url() RETURNS void AS $$
BEGIN
	DECLARE row record;
	DECLARE t record;
	BEGIN
		FOR t IN (SELECT upload_request_id FROM upload_request_entry) LOOP
			FOR row IN (SELECT uu.id FROM upload_request_url AS uu WHERE uu.upload_request_id = t.upload_request_id LIMIT 1) LOOP
			RAISE INFO 'ROW';
			RAISE INFO '%', row.id;
				UPDATE upload_request_entry SET upload_request_url_id = row.id WHERE upload_request_id = t.upload_request_id;
			END LOOP;
		END LOOP;
	END;
END
$$ LANGUAGE plpgsql;

SELECT ls_update_upload_request_url();

ALTER TABLE upload_request_entry DROP COLUMN upload_request_id;
ALTER TABLE upload_request_entry ALTER COLUMN upload_request_url_id SET NOT NULL;
ALTER TABLE upload_request_entry ADD CONSTRAINT FKupload_req220981 FOREIGN KEY (upload_request_url_id) REFERENCES upload_request_url (id);
ALTER TABLE upload_request ADD COLUMN notified bool DEFAULT false NOT NULL;
-- system account for upload-request:
UPDATE account set role_id = 6 WHERE id = 3;

UPDATE document SET sha256sum = NULL;
-- End of your requests

-- LinShare version
SELECT ls_version();

 -- Alias for Users
 -- All users
 CREATE VIEW alias_users_list_all AS
  SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id;
 -- All active users
 CREATE VIEW alias_users_list_active AS
  SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed = 0;
 -- All destroyed users
 CREATE VIEW alias_users_list_destroyed AS
  SELECT id, first_name, last_name, mail, can_upload, restricted, expiration_date, ldap_uid, domain_id, ls_uuid, creation_date, modification_date, role_id, account_type from users as u join account as a on a.id=u.account_id where a.destroyed > 0;

 -- Alias for threads
 -- All threads
 CREATE VIEW alias_threads_list_all AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id;
 -- All active threads
 CREATE VIEW alias_threads_list_active AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed = 0;
 -- All destroyed threads
 CREATE VIEW alias_threads_list_destroyed AS SELECT a.id, name, domain_id, ls_uuid, creation_date, modification_date, enable, destroyed from thread as u join account as a on a.id=u.account_id where a.destroyed > 0;
COMMIT;

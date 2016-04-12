BEGIN;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = info;
SET default_with_oids = false;

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
-- system account for upload-request:
UPDATE account set role_id = 6 WHERE id = 3;
COMMIT;
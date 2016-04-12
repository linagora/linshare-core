ALTER TABLE upload_request_entry ADD COLUMN upload_request_url_id bigint(8);
ALTER TABLE upload_request_entry DROP FOREIGN KEY FKupload_req220981;
ALTER TABLE upload_request_entry DROP INDEX FKupload_req220981;

delimiter '$$'
CREATE PROCEDURE ls_upadte_upload_request_entry()
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

call ls_upadte_upload_request_entry();
ALTER TABLE upload_request_entry DROP COLUMN upload_request_id;
ALTER TABLE upload_request_entry MODIFY upload_request_url_id bigint(8) NOT NULL;
ALTER TABLE upload_request_entry ADD INDEX FKupload_req220981 (upload_request_url_id), ADD CONSTRAINT FKupload_req220981 FOREIGN KEY (upload_request_url_id) REFERENCES upload_request_url (id);
-- system account for upload-request:
UPDATE account set role_id = 6 WHERE id = 3;
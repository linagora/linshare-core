ALTER TABLE upload_request_entry ADD COLUMN upload_request_url_id INT8 NOT NULL;
ALTER TABLE uplaod_request_entry DROP FOREIGN KEY FKupload_req220981;
ALTER TABLE upload_request_entry DROP COLUMN upload_request_id;

ALTER TABLE upload_request_entry ADD CONSTRAINT FKupload_req220981 FOREIGN KEY (upload_request_url_id) REFERENCES upload_request_url (id);

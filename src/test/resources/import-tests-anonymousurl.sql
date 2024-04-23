INSERT INTO document (id, uuid, creation_date, type, human_mime_type, ls_size, check_mime_type, has_thumbnail, compute_thumbnail)
	VALUES (1, 'dc1', now(), 'data', 'others', 1024, false, false, false);
INSERT INTO document (id, uuid, creation_date, type, human_mime_type, ls_size, check_mime_type, has_thumbnail, compute_thumbnail)
	VALUES (2, 'dc2', now(), 'data', 'others', 1024, false, false, false);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (1, 10, now(), now(), 'Document entry 1', '', 'e1', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, human_mime_type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (1, 1, false, 'data', 'others', 1024, 'plop', false, 2);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (2, 10, now(), now(), 'Document entry 2', '', 'e2', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, human_mime_type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (2, 2, false, 'data', 'others', 1024, 'plop', false, 2);

INSERT INTO contact (id, mail)
VALUES (1, 'amy.wolsh@linshare.org');

INSERT INTO anonymous_url (id, url_path, uuid, password, contact_id)
VALUES (1, '/external/anonymous/', '1', null, 1);

-- password is secret crypted with bcrypt
INSERT INTO anonymous_url (id, url_path, uuid, password, contact_id)
VALUES (2, '/external/anonymous/', '2', '{bcrypt}$2y$10$OBrbJCVt5iqW4lOL4kUsPeaKfLEpJzPXA.aJ6zYwAu60Ny/MIxqbS', 1);

INSERT INTO share_entry_group (id,account_id,uuid,subject,notification_date,creation_date,modification_date,notified,processed,expiration_date,external_mail_locale)
VALUES (1, 10,'ee1','Test', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.671000', false, false, null, 'en');
INSERT INTO share_entry_group (id,account_id,uuid,subject,notification_date,creation_date,modification_date,notified,processed,expiration_date,external_mail_locale)
VALUES (2, 10,'ee2','Test', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.671000', false, false, null, 'en');


INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (10, 10, now(), now(), 'Anonymous share entry 1', '', '1', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
VALUES (10, 0, 1, 1, 1);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (20, 10, now(), now(), 'Anonymous share entry 2', '', '2', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
VALUES (20, 0, 1, 2, 2);


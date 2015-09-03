
SET @document_id = SELECT nextVal('h2_sequence');
SET @document_entry_id = SELECT nextVal('h2_sequence');
SET @share_entry_group_id = SELECT nextVal('h2_sequence');

-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (@share_entry_group_id, @john_do_id, '7d0ba756-ac50-4803-ba4f-c5bea7f46f5c', 'subject seg', now(), now(), now(), false, false);

-- DE
INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (@document_id, '7a2efea4-ddb0-4ae2-9a39-403779b569c7', now(), 'data', 1024, false);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (@document_entry_id, @john_do_id, now(), now(), 'doc entry name', '', '028bed54-885f-457b-8d89-32987a83ed06', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (@document_entry_id, @document_id, false, 'data', 1024, 'plop', false, 0);

-- SE
SET @share_entry_id = SELECT nextVal('h2_sequence');
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'SE test doc entry name 1', '', 'ef70587a-0f1f-4520-b9e8-61a725d05bf0', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (@share_entry_id, @document_entry_id, 0, @john_do_id, @share_entry_group_id);

-- SE
SET @share_entry_id = SELECT nextVal('h2_sequence');
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'SE test doc entry name 1', '', '00beb8bd-d125-4201-99f2-078f7698f786', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (@share_entry_id, @document_entry_id, 1, @jane_simth_id, @share_entry_group_id);

-- ASE
SET @share_entry_id = SELECT nextVal('h2_sequence');
SET @anonymous_url_id = SELECT nextVal('h2_sequence');
SET @contact_id = SELECT nextVal('h2_sequence');
INSERT INTO contact(id, mail) VALUES (@contact_id, 'no.user.yoda@linshare.org');
INSERT INTO anonymous_url (id, url_path, uuid, contact_id)
	VALUES (@anonymous_url_id, 'download', '66d9c6c9-fd65-42c9-8c39-5694df981eb8', @contact_id);
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'ASE test doc entry name 1', '', '3a2a4d4e-9939-4d12-8c72-6b4b5180cd87', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
	VALUES (@share_entry_id, 0, @document_entry_id, @anonymous_url_id, @share_entry_group_id);




SET @document_id = SELECT nextVal('h2_sequence');
SET @document_entry_id = SELECT nextVal('h2_sequence');

-- DE 2
INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (@document_id, '7a2efea4-ddb0-4ae2-9a39-403779b569c8', now(), 'data', 1024, false);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (@document_entry_id, @john_do_id, now(), now(), 'doc entry name 2', '', '028bed54-885f-457b-8d89-32987a83ed07', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (@document_entry_id, @document_id, false, 'data', 1024, 'plop', false, 0);

-- SE
SET @share_entry_id = SELECT nextVal('h2_sequence');
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'SE test doc entry name 1', '', 'ef70587a-0f1f-4520-b9e8-61a725d05bf1', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (@share_entry_id, @document_entry_id, 1, @john_do_id, @share_entry_group_id);

-- SE
SET @share_entry_id = SELECT nextVal('h2_sequence');
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'SE test doc entry name 1', '', '00beb8bd-d125-4201-99f2-078f7698f787', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (@share_entry_id, @document_entry_id, 1, @jane_simth_id, @share_entry_group_id);

-- ASE
SET @share_entry_id = SELECT nextVal('h2_sequence');
SET @anonymous_url_id = SELECT nextVal('h2_sequence');
INSERT INTO anonymous_url (id, url_path, uuid, contact_id)
	VALUES (@anonymous_url_id, 'download', '66d9c6c9-fd65-42c9-8c39-5694df981eb9', @contact_id);
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'ASE test doc entry name 1', '', '3a2a4d4e-9939-4d12-8c72-6b4b5180cd88', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
	VALUES (@share_entry_id, 1, @document_entry_id, @anonymous_url_id, @share_entry_group_id);




SET @document_id = SELECT nextVal('h2_sequence');
SET @document_entry_id = SELECT nextVal('h2_sequence');

-- DE 3
INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (@document_id, '7a2efea4-ddb0-4ae2-9a39-403779b569c9', now(), 'data', 1024, false);
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (@document_entry_id, @john_do_id, now(), now(), 'doc entry name 3', '', '028bed54-885f-457b-8d89-32987a83ed08', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (@document_entry_id, @document_id, false, 'data', 1024, 'plop', false, 0);

-- SE
SET @share_entry_id = SELECT nextVal('h2_sequence');
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'SE test doc entry name 1', '', 'ef70587a-0f1f-4520-b9e8-61a725d05bf2', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (@share_entry_id, @document_entry_id, 0, @john_do_id, @share_entry_group_id);

-- ASE
SET @share_entry_id = SELECT nextVal('h2_sequence');
SET @anonymous_url_id = SELECT nextVal('h2_sequence');
INSERT INTO anonymous_url (id, url_path, uuid, contact_id)
	VALUES (@anonymous_url_id, 'download', '66d9c6c9-fd65-42c9-8c39-5694df981eba', @contact_id);
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (@share_entry_id, @john_do_id, now(), now(), now(), 'ASE test doc entry name 1', '', '3a2a4d4e-9939-4d12-8c72-6b4b5180cd89', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
	VALUES (@share_entry_id, 0, @document_entry_id, @anonymous_url_id, @share_entry_group_id);

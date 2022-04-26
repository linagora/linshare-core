
INSERT INTO contact(id, mail) VALUES (2, 'yoda@linshare.org');
INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type, has_thumbnail, compute_thumbnail)
	VALUES (1, '92ce81a8-3e87-413b-82c6-8ae9940aae9e', now(), 'data', 1024, false, false, false);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (1, 10, now(), now(), 'DE test doc entry name 1', '', 'bfaf3fea-c64a-4ee0-bae8-b1482f1f6401', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (1, 1, false, 'data', 1024, 'plop', false, 2);
-- DE

INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed, external_mail_locale)
	VALUES (1, 10, 'c96d778e-b09b-4557-b785-ff5124bd2b8d', 'subject 1', now(), now(), now(), false, false, 'en');

INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (2, 10, now(), now(), now(), 'SE test doc entry name 1', '', 'cb1e7ba6-40db-11e5-9c46-0800271467bb', false);

INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (2, 1, 0, 11, 1);

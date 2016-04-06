-- Upload Requests close expired.
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (1, 'subject of upload request 2', 'body of upload request 1', 'b344b5ca-d9e7-4857-b959-5e86f34a91f7', DATEADD(month, -2, now()), DATEADD(month, -2, now()));
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (1, 2, 10, 1, '916a6e65-deb8-4120-b2ab-c64bfcbf4e02', 3, 31457280, 10485760, 'STATUS_ENABLED', DATEADD(month, -2, now()), DATEADD(month, -2, now()), DATEADD(month, -2, now()), '2014-08-10 00:00:00', DATEADD(month, 1, now()), null, true, true, true, 'fr', true, null);
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (2, 'subject of upload request 2', 'body of upload request 2', 'f358f600-ef45-11e5-a447-5398b7a56bf6', DATEADD(month, -1, now()), DATEADD(month, -1, now()));
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (2, 2, 10, 2, 'f3d57d10-ef45-11e5-98ad-5f9a6ac1c0ed', 3, 31457280, 10485760, 'STATUS_ENABLED', DATEADD(month, -1, now()), DATEADD(month, -1, now()), DATEADD(month, -1, now()), '2014-08-10 00:00:00', DATEADD(day, -3, now()), null, true, true, true, 'fr', true, null); 
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (3, 'subject of upload request 3', 'body of upload request 3', 'f433a43a-ef45-11e5-a5a8-cb91b7e459b2', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (3, 2, 10, 3, 'f49ca05c-ef45-11e5-9c0e-f34ce5ee3ac1', 3, 31457280, 10485760, 'STATUS_ENABLED', now(), now(), now(), '2014-08-10 00:00:00', '2014-09-10 00:00:00', null, true, true, true, 'fr', true, null); 

-- Upload requests enable created.
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (4, 'subject of upload request 4', 'body of upload request 4', 'f4f31b58-ef45-11e5-b506-c348d7a7b65a', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (4, 2, 10, 4, 'f548ac1c-ef45-11e5-a73f-4b811b25f11b', 3, 31457280, 10485760, 'STATUS_CREATED', DATEADD(day, -1, now()), DATEADD(month, -2, now()), now(), DATEADD(month, 3, now()), DATEADD(month, 3, now()), null, true, true, true, 'fr', true, null);
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (5, 'subject of upload request 5', 'body of upload request 5', '2980a264-ef46-11e5-a0c9-0b2742279c1a', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (5, 2, 10, 5, '2a378b50-ef46-11e5-98c3-673ead758c8f', 3, 31457280, 10485760, 'STATUS_CREATED', DATEADD(day, -1, now()), now(), DATEADD(month, 3, now()), DATEADD(month, 3, now()), '2014-09-10 00:00:00', null, true, true, true, 'fr', true, null);
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (6, 'subject of upload request 6', 'body of upload request 6', '394b8e98-ef46-11e5-800e-7f734472e0d0', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (6, 2, 10, 6, '39a4a7e4-ef46-11e5-b1d1-e3c21cdc7a0a', 3, 31457280, 10485760, 'STATUS_CREATED', DATEADD(day, -3, now()), now(), DATEADD(month, 3, now()), DATEADD(month, 3, now()), '2014-09-10 00:00:00', null, true, true, true, 'fr', true, null);

-- Upload requests notifier.
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (7, 'subject of upload request 4', 'body of upload request 4', 'f4j31b58-ef45-11e5-b506-c348d7a7b65a', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id, notified)
	VALUES (7, 2, 10, 7, 'f547ac1c-ef45-11e5-a73f-4b811b25f11b', 3, 31457280, 10485760, 'STATUS_ENABLED', DATEADD(day, -1, now()), DATEADD(month, -2, now()), now(), now(), DATEADD(month, 3, now()), null, true, true, true, 'fr', true, null, false);
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (8, 'subject of upload request 5', 'body of upload request 5', '29k0a264-ef46-11e5-a0c9-0b2742279c1a', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id, notified)
	VALUES (8, 2, 10, 8, '2a368b50-ef46-11e5-98c3-673ead758c8f', 3, 31457280, 10485760, 'STATUS_ENABLED', DATEADD(day, -1, now()), now(), now(), now(), DATEADD(month, 4, now()), null, true, true, true, 'fr', true, null, false);
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (9, 'subject of upload request 6', 'body of upload request 6', '39db8e98-ef46-11e5-800e-7f734472e0d0', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id, notified)
	VALUES (9, 2, 10, 9, '39a407e4-ef46-11e5-b1d1-e3c21cdc7a0a', 3, 31457280, 10485760, 'STATUS_ENABLED', DATEADD(day, -3, now()), now(), DATEADD(month, 3, now()), now(), DATEADD(month, 4, now()), null, true, true, true, 'fr', true, null, false);

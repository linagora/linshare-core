-- Contact
INSERT INTO contact (id, mail)
	VALUES (1000, 'contact1@mail.com');
INSERT INTO contact (id, mail)
	VALUES (1001, 'contact2@mail.com');
INSERT INTO contact (id, mail)
	VALUES (1002, 'contact3@mail.com');
	
-- ### Grouped Mode
	-- Upload requests .
INSERT INTO upload_request_group (id, domain_abstract_id, account_id, subject, body, uuid, creation_date, modification_date, max_file, max_deposit_size, max_file_size, activation_date, notification_date, expiry_date, can_delete, can_close, can_edit_expiry_date, locale, protected_by_password, mail_message_id, enable_notification, collective, status)
	VALUES (10, 2, 10, 'subject of upload request 4', 'body of upload request 4', 'frj31b58-ef45-11e5-b506-c348d7a7b65c', DATEADD(month, -2, now()), DATEADD(month, -2, now()), 3, 31457280, 10485760, DATEADD(month, -2, now()), DATEADD(month, -2, now()),DATEADD(month, 1, now()), true, true, true, 'fr', true, null, true, true, 'ENABLED');
INSERT INTO upload_request (id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, protected_by_password, mail_message_id, notified, enable_notification, pristine)
	VALUES (10, 10, 'f447ac1c-ef45-11e5-a73f-4b811b25f11b', 3, 31457280, 10485760, 'ENABLED', DATEADD(day, -1, now()), DATEADD(month, -2, now()), now(), now(), DATEADD(month, 3, now()), null, true, true, true, 'fr', true, null, false, true, false);

-- Upload request urls
INSERT INTO upload_request_url (id, contact_id, upload_request_id, uuid, path, password, creation_date, modification_date)
	VALUES (20, 1000, 10, 'f447ac1c-ef45-44e5-a73f-4b844b25f44b', 'upload_request', 'kITh6Jk+FiuyGQtdtaeFxvYnzug=', now(), now());
INSERT INTO upload_request_url (id, contact_id, upload_request_id, uuid, path, password, creation_date, modification_date)
	VALUES (21, 1001, 10, 'f447ac1c-ef45-44e5-a73f-4b844b25f66b', 'upload_request', 'kITh6Jk+FiuyGQtdtaeFxvYnzug=', now(), now());
INSERT INTO upload_request_url (id, contact_id, upload_request_id, uuid, path, password, creation_date, modification_date)
	VALUES (22, 1002, 10, 'f447ac1c-ef45-44e5-a73f-4b844b25f55b', 'upload_request', 'kITh6Jk+FiuyGQtdtaeFxvYnzug=', now(), now());

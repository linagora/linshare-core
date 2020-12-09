-- Upload Requests Group expired.
INSERT INTO upload_request_group (id, domain_abstract_id, account_id, subject, body, uuid, creation_date, modification_date, max_file, max_deposit_size, max_file_size, activation_date, notification_date, expiry_date, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id, enable_notification, collective, status)
	VALUES (1, 2, 10, 'subject of upload request 2', 'body of upload request 1', 'b344b5ca-d9e7-4857-b959-5e86f34a91f7', DATEADD(month, -2, now()), DATEADD(month, -2, now()), 3, 31457280, 10485760, DATEADD(month, -2, now()), DATEADD(month, -2, now()),DATEADD(month, -1, now()), true, true, true, 'fr', true, null,true,false,'ENABLED');
INSERT INTO upload_request (id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id, dirty, enable_notification)
	VALUES (1, 1, '916a6e65-deb8-4120-b2ab-c64bfcbf4e02', 3, 31457280, 10485760, 'CLOSED', DATEADD(month, -2, now()), DATEADD(month, -2, now()), DATEADD(month, -2, now()), '2014-08-10 00:00:00', DATEADD(month, 1, now()), null, true, true, true, 'fr', true, null, false, true);

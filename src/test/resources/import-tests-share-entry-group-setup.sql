-- SET client_encoding = 'UTF8';
-- SET client_min_messages = warning;
-- \set ON_ERROR_STOP


INSERT INTO contact(id, mail) VALUES (2, 'yoda@linshare.org');
INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (1, '92ce81a8-3e87-413b-82c6-8ae9940aae9e', now(), 'data', 1024, false);

INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (2, '7ea9644c-7870-4c85-abfe-7adefa7207ba', now(), 'data', 1024, false);

INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (3, '113c7cc6-3ddc-4c17-bdbf-9254ad4e0f4f', now(), 'data', 1024, false);

INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type)
	VALUES (4, 'b82ff544-41ab-11e5-9e6f-080027b8274b', now(), 'data', 1024, false);


-- Share 1 - one share was downloaded : 1/4
-- DE
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (1, 10, now(), now(), 'DE test doc entry name 1', '', 'bfaf3fea-c64a-4ee0-bae8-b1482f1f6401', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (1, 1, false, 'data', 1024, 'plop', false, 2);
-- DE
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (12, 10, now(), now(), 'DE test doc entry name 4', '', 'fd87394a-41ab-11e5-b191-080027b8274b', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (12, 4, false, 'data', 1024, 'plop', false, 1);

-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (1, 10, 'c96d778e-b09b-4557-b785-ff5124bd2b8d', 'subject 1', now(), now(), now(), false, false);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (2, 10, now(), now(), now(), 'SE test doc entry name 1', '', 'cb1e7ba6-40db-11e5-9c46-0800271467bb', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (2, 1, 0, 10, 1);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (3, 10, now(), now(), now(), 'SE test doc entry name 1', '', 'f92aaba0-40db-11e5-9698-0800271467bb', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (3, 1, 1, 11, 1);

-- ASE
INSERT INTO anonymous_url (id, url_path, uuid, contact_id) VALUES (1, 'download', '7374f362-419e-11e5-94a5-0800271467bb', 2);
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (10, 10, now(), now(), now(), 'ASE test doc entry name 1', '', '9c555037-f447-47bb-99cb-5f46a1409829', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
    VALUES (10, 0, 1, 1, 1 );

-- ASE
INSERT INTO anonymous_url (id, url_path, uuid, contact_id) VALUES (3, 'download', '35e57086-41ac-11e5-8d78-080027b8274b', 2);
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (13, 10, now(), now(), now(), 'ASE test doc entry name 4', '', '54f01fc6-41ac-11e5-b057-080027b8274b', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
    VALUES (13, 0, 12, 3, 1 );


-- Share 2 - no download : 0/2
-- DE
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (4, 10, now(), now(), 'DE test doc entry name 2', '', '64911715-ae51-4d76-bec4-b4d88c24cba0', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (4, 2, false, 'data', 1024, 'plop', false, 2);

-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (2, 10, '61eae04b-9496-4cb1-900e-eda8caac6703', 'subject 2', now(), now(), now(), false, false);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (5, 10, now(), now(), now(), 'SE test doc entry name 2', '', 'e551db6e-cce1-46bc-b446-6cc10a439dfa', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (5, 4, 0, 10, 2);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (6, 10, now(), now(), now(), 'SE test doc entry name 2', '', 'f1dcb21b-5b92-4780-8a9b-1c5a4c935177', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (6, 4, 0, 11, 2);


-- Share 3 - every thing is downloaded : 2/2
-- DE
INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (7, 10, now(), now(), 'DE test doc entry name 3', '', 'd8a572c3-4673-4bed-939b-709de62f0ebe', false);
INSERT INTO document_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail, shared)
	VALUES (7, 3, false, 'data', 1024, 'plop', false, 2);

-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (3, 10, 'c8a54434-7898-472d-93c0-b98e1b526062', 'subject 3', now(), now(), now(), false, false);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (8, 10, now(), now(), now(), 'SE test doc entry name 3', '', '6ff7b6a4-7935-425c-960e-ff92901992e9', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (8, 7, 1, 10, 3);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (9, 10, now(), now(), now(), 'SE test doc entry name 3', '', 'ff6465f2-142e-476b-b4aa-11ef9194885e', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (9, 7, 1, 11, 3);


-- Share 4 - notified : 0/0
-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (4, 10, '421a2bc5-d41c-4b83-8e94-cd87aa2964c3', 'subject 4', now(), now(), now(), true, false);


-- Share 5 - 1 SE, 1 ASE, one download : 1/2
-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (5, 10, '6588844c-3891-44bf-af14-b2b85ca47de4', 'subject 5', now(), now(), now(), false, false);

-- ASE
INSERT INTO anonymous_url (id, url_path, uuid, contact_id) VALUES (2, 'download', 'd7587b25-6545-45d9-b465-679e3fb585e4', 2);
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (11, 10, now(), now(), now(), 'ASE test doc entry name 3', '', 'a11fb104-a695-4aea-bc8e-56fb8bdf24e5', false);
INSERT INTO anonymous_share_entry (entry_id, downloaded, document_entry_id, anonymous_url_id, share_entry_group_id)
	VALUES (11, 0, 7, 2, 5);

-- SE
INSERT INTO entry (id, owner_id, creation_date, modification_date, expiration_date, name, comment, uuid, cmis_sync)
	VALUES (14, 10, now(), now(), now(), 'SE test doc entry name 3', '', '7a167dce-4277-11e5-84ee-0800271467bb', false);
INSERT INTO share_entry (entry_id, document_entry_id, downloaded, recipient_id, share_entry_group_id)
	VALUES (14, 7, 1, 11, 5);


-- Share 6 - notified : 0/0
-- SEG
INSERT INTO share_entry_group (id, account_id, uuid, subject, creation_date, modification_date, notification_date, notified, processed)
	VALUES (6, 10, '027599d8-3433-4e07-9b7c-e8be82fed4a9', 'subject 6', now(), now(), now(), false, false);


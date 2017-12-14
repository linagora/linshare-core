INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type, has_thumbnail, compute_thumbnail)
	VALUES (1, '92ce81a8-3e87-413b-82c6-8ae9940aae9e', now(), 'data', 1024, false, false, false);

INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type, has_thumbnail, compute_thumbnail)
	VALUES (2, '7ea9644c-7870-4c85-abfe-7adefa7207ba', now(), 'data', 1024, false, false, false);

INSERT INTO document (id, uuid, creation_date, type, ls_size, check_mime_type, has_thumbnail, compute_thumbnail)
	VALUES (3, '113c7cc6-3ddc-4c17-bdbf-9254ad4e0f4f', now(), 'data', 1024, false, false, false);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (1, 10, now(), now(), 'DE test doc entry name 1', '', 'bfaf3fea-c64a-4ee0-bae8-b1482f1f6401', false);
INSERT INTO thread_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail)
	VALUES (1, 1, false, 'data', 1024, 'plop', false);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (12, 10, now(), now(), 'DE test doc entry name 4', '', 'fd87394a-41ab-11e5-b191-080027b8274b', false);
INSERT INTO thread_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail)
	VALUES (12, 2, false, 'data', 1024, 'plop', false);

INSERT INTO entry (id, owner_id, creation_date, modification_date, name, comment, uuid, cmis_sync)
	VALUES (4, 10, now(), now(), 'DE test doc entry name 2', '', '64911715-ae51-4d76-bec4-b4d88c24cba0', false);
INSERT INTO thread_entry (entry_id, document_id, ciphered, type, ls_size, sha256sum, has_thumbnail)
	VALUES (4, 3, false, 'data', 1024, 'plop', false);
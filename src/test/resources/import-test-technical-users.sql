-- LinShare Users Permissions
INSERT INTO TECHNICAL_ACCOUNT_PERMISSION
    (ID, UUID, CREATION_DATE, MODIFICATION_DATE)
VALUES
    (1, 'technicalAccountPermissionUuid1', now(), now()),
    (2, 'technicalAccountPermissionUuid2', now(), now()),
    (3, 'technicalAccountPermissionUuid3', now(), now()),
    (4, 'technicalAccountPermissionUuid4', now(), now()),
    (5, 'technicalAccountPermissionUuid5', now(), now()),
    (6, 'technicalAccountPermissionUuid6', now(), now()),
    (7, 'technicalAccountPermissionUuid7', now(), now()),
    (8, 'technicalAccountPermissionUuid8', now(), now()),
    (9, 'technicalAccountPermissionUuid9', now(), now()),
    (10, 'technicalAccountPermissionUuid10', now(), now());

INSERT INTO account_permission
    (id, technical_account_permission_id, permission)
VALUES
	(1, 1, 'AUDIT_LIST'),
	(2, 3, 'SHARED_SPACE_NODE_CREATE'),
	(3, 3, 'SHARED_SPACE_PERMISSION_LIST'),
	(4, 3, 'SHARED_SPACE_PERMISSION_CREATE'),
	(5, 3, 'SHARED_SPACE_ROLE_LIST'),
	(6, 3, 'SHARED_SPACE_ROLE_GET'),
	(7, 4, 'DOCUMENT_ENTRIES_CREATE'),
	(8, 5, 'DOCUMENT_ENTRIES_LIST'),
	(9, 6, 'DOCUMENT_ENTRIES_GET'),
	(10, 6, 'DOCUMENT_ENTRIES_DELETE'),
	(11, 7, 'DOCUMENT_ENTRIES_GET'),
	(12, 8, 'DOCUMENT_ENTRIES_GET'),
	(13, 8, 'DOCUMENT_ENTRIES_UPDATE'),
	(14, 9, 'SHARE_ENTRIES_CREATE'),
	(15, 9, 'DOCUMENT_ENTRIES_GET'),
	(16, 10, 'SHARE_ENTRY_GROUPS_LIST');

--	SHARE_ENTRY_GROUPS_GET,
--	SHARE_ENTRY_GROUPS_LIST,
--	SHARE_ENTRY_GROUPS_UPDATE,
--	SHARE_ENTRY_GROUPS_DELETE,

INSERT INTO TECHNICAL_ACCOUNT_PERMISSION_DOMAIN_ABSTRACT
    (TECHNICAL_ACCOUNT_PERMISSION_ID, DOMAIN_ABSTRACT_ID)
VALUES
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 1),
    (5, 1),
    (6, 1),
    (7, 1),
    (8, 1),
    (9, 1),
    (10, 1);

-- LinShare Users
INSERT INTO account
    (id, mail, account_type, ls_uuid, technical_account_permission_id,
	creation_date, modification_date, role_id, mail_locale, external_mail_locale, cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name, last_name, can_upload, comment, restricted,
	CAN_CREATE_GUEST, inconsistent, authentication_failure_count, authentication_failure_last_date)
VALUES
	-- technical user with audit permissions
	(100, 'technical.audit@linshare.org', 4, 'technical.audit@linshare.org', 1,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'audit', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with create shared space node permission
	(102, 'technical.create.node@linshare.org', 4, 'technical.create.node@linshare.org', 3,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with no permissions
	(101, 'technical.none@linshare.org', 4, 'technical.none@linshare.org', 2,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with create document permissions
	(103, 'technical.create.document@linshare.org', 4, 'technical.create.document@linshare.org', 4,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with locked account
	(104, 'technical.locked@linshare.org', 4, 'technical.locked@linshare.org', 1,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 30, now()),
	-- technical user with list document permissions
	(105, 'technical.list.document@linshare.org', 4, 'technical.list.document@linshare.org', 5,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with delete document permissions
	(106, 'technical.delete.document@linshare.org', 4, 'technical.delete.document@linshare.org', 6,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with get document permissions
	(107, 'technical.get.document@linshare.org', 4, 'technical.get.document@linshare.org', 7,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with update document permissions
	(108, 'technical.update.document@linshare.org', 4, 'technical.update.document@linshare.org', 8,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with create share permissions
	(109, 'technical.create.share@linshare.org', 4, 'technical.create.share@linshare.org', 9,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null),
	-- technical user with create list permissions
	(110, 'technical.list.shareEntryGroup@linshare.org', 4, 'technical.list.shareEntryGroup@linshare.org', 10,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0, null);


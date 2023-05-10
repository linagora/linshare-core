-- LinShare Users Permissions
INSERT INTO TECHNICAL_ACCOUNT_PERMISSION
    (ID, UUID, CREATION_DATE, MODIFICATION_DATE)
VALUES
    (1, 'technicalAccountPermissionUuid1', now(), now()),
    (2, 'technicalAccountPermissionUuid2', now(), now());

INSERT INTO account_permission
    (id, technical_account_permission_id, permission)
VALUES
	(1, 1, 'AUDIT_LIST');

INSERT INTO TECHNICAL_ACCOUNT_PERMISSION_DOMAIN_ABSTRACT
    (TECHNICAL_ACCOUNT_PERMISSION_ID, DOMAIN_ABSTRACT_ID)
VALUES
    (1, 1),
    (2, 1);

-- LinShare Users
INSERT INTO account
    (id, mail, account_type, ls_uuid, technical_account_permission_id,
	creation_date, modification_date, role_id, mail_locale, external_mail_locale, cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name, last_name, can_upload, comment, restricted,
	CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
VALUES
	-- technical user with audit permissions
	(100, 'technical.audit@linshare.org', 2, 'technical.audit@linshare.org', 1,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'audit', 'technical', true, '', false,
	true, false, 0),
	-- technical user with no permissions
	(101, 'technical.none@linshare.org', 2, 'technical.none@linshare.org', 2,
	now(), now(), 4, 'en', 'en', 'en', true, null,
	0, 1, 'IN_USE', 'none', 'technical', true, '', false,
	true, false, 0);


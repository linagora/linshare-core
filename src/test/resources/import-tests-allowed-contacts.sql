

-- Add a restricted guest
INSERT INTO account(
	id, mail, account_type, ls_uuid,
	creation_date, modification_date, role_id,
	mail_locale, external_mail_locale,
	cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name,
	last_name, can_upload, comment, restricted,
	CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
VALUES
	(101, 'guest101@linshare.org', 3, '9fcf8335-7e0d-43a0-9474-be57eaaa76de',
	now(), now(), 0,
	'en', 'en', 'en', true, '{bcrypt}$2a$10$GX1j3pNgFcg8LfANc4w9h.oivxTErSnP/6YYpVLX.pdqTLjdF6Dfm',
	0, 4, 'IN_USE', 'Guest',
	'Test', true, '', true,
	true, false, 0);


-- Add allowed contacts
INSERT INTO allowed_contact (id,account_id,contact_id)
VALUES
 (0,101,10),
 (1,101,11);
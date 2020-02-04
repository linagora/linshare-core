
-- guest1 - expired
INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, expiration_date, inconsistent)
    VALUES (1020, 'guest1@linshare.org', 3, 'aebe1b64-39c0-11e5-9fa8-080027b8274f', '2015-07-05 16:04:43.265', '2015-07-05 16:04:43.265', 0, 'en', 'en', 'en', true, null, 8, 2, 'IN_USE', 'Guest1', 'Do', true, '', false, true, '2015-07-05 16:04:43.265', false);

-- guest2 - expired and deleted
INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, expiration_date, inconsistent)
    VALUES (1021,  'guest2@linshare.org', 3, 'aebe1b64-39c0-11e5-9fa8-080027b8274g', '2015-07-05 16:04:43.265', '2015-07-05 16:04:43.265', 0, 'en', 'en', 'en', true, null, 4, 2, 'IN_USE', 'Guest2', 'Do', true, '', false, true, '2015-07-05 16:04:43.265', false);

-- guest3 - expired and deleted and mark to purge
INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, expiration_date, inconsistent)
    VALUES (1022, 'guest3@linshare.org', 3, 'aebe1b64-39c0-11e5-9fa8-080027b8274h', '2015-07-05 16:04:43.265', '2015-07-05 16:04:43.265', 0, 'en', 'en', 'en', true, null, 1, 2, 'WAIT_FOR_PURGE', 'Guest3', 'Do', true, '', false, true, '2015-07-05 16:04:43.265', false);


-- default domain policy
INSERT INTO domain_access_policy(id, creation_date, modification_date) VALUES (100001, now(), now());
INSERT INTO domain_access_rule(id, domain_access_rule_type, domain_id, domain_access_policy_id, rule_index) VALUES (100001, 0, null, 100001,0);
INSERT INTO domain_policy(id, uuid, label, domain_access_policy_id, creation_date, modification_date) VALUES (100001, 'TestAccessPolicy0-test', 'TestAccessPolicy0-test', 100001, now(), now());


-- Root domain (application domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, purge_step, user_provider_id, domain_policy_id, parent_id, welcome_messages_id, creation_date, modification_date) VALUES (100001, 0, 'TEST_Domain-0', 'TEST_Domain-0', true, false, 'The root test application domain', 3, 'en','IN_USE', null, 100001, null, null, now(), now());
-- id : 100001

-- TESTS
INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100001, 100001, null, null, 'aa59beec-b1d4-494c-bf49-d65cc533efe7', 'aa59beec-b1d4-494c-bf49-d65cc533efe7', '2022-08-25 13:25:25.220000', '2022-08-25 13:25:26.206000', 0, 'en', 'en', true,
        5, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100002, 100001, null, null, '29bca36a-e7e0-4d90-b418-21f08c17f596', '29bca36a-e7e0-4d90-b418-21f08c17f596', '2022-08-25 13:25:34.360000', '2022-08-25 13:25:34.412000', 0, 'en', 'en', true,
        3, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100003, 100001, null, null, '7ce4de69-31e7-4908-a0b7-55b8844abf31', '7ce4de69-31e7-4908-a0b7-55b8844abf31', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.719000', 0, 'en', 'en', true,
        5, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100004, 100001, null, null, '7ce4de69-31e7-4908-a0b7-55b8844abf32', '7ce4de69-31e7-4908-a0b7-55b8844abf32', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.719000', 0, 'en', 'en', true,
        5, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100005, 100001, null, null, '7ce4de69-31e7-4908-a0b7-55b8844abf33', '7ce4de69-31e7-4908-a0b7-55b8844abf33', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.719000', 0, 'en', 'en', true,
        3, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100006, 100001, null, null, '7ce4de69-31e7-4908-a0b7-55b8844abf34', '7ce4de69-31e7-4908-a0b7-55b8844abf34', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.719000', 0, 'en', 'en', true,
        3, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100007, 100001, null, null, '7ce4de69-31e7-4908-a0b7-55b8844abf35', '7ce4de69-31e7-4908-a0b7-55b8844abf34', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.719000', 0, 'en', 'en', true,
        3, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);

INSERT INTO account (id, domain_id, guest_source_domain_id, technical_account_permission_id, ls_uuid, mail, creation_date, modification_date, role_id, mail_locale, external_mail_locale, enable,
                     account_type, password, destroyed, purge_step, cmis_locale, first_name, last_name, not_after, not_before, can_upload, comment, restricted, expiration_date, ldap_uid, can_create_guest, inconsistent, second_fa_secret, second_fa_creation_date, authentication_failure_last_date, authentication_success_last_date, authentication_failure_count)
VALUES (100008, 100001, null, null, '7ce4de69-31e7-4908-a0b7-55b8844abf36', '7ce4de69-31e7-4908-a0b7-55b8844abf36', '2022-08-25 13:25:41.671000', '2022-08-25 13:25:41.719000', 0, 'en', 'en', true,
        5, null, 0, 'IN_USE', 'ENGLISH', null, null, null, null, null, null, false, null, null, null, false, null, null, null, null, 0);



insert into moderator(id,uuid, role, creation_date, modification_date, account_id, guest_id)
values (100001, '100001', 'ADMIN', '2022-08-25 13:25:25.220000', '2022-08-25 13:25:25.220000', 100001, 100002);
insert into moderator(id,uuid, role, creation_date, modification_date, account_id, guest_id)
values (100002, '100002', 'ADMIN', '2022-08-25 13:25:25.220000', '2022-08-25 13:25:25.220000', 100005, 100007);
insert into moderator(id,uuid, role, creation_date, modification_date, account_id, guest_id)
values (100003, '100003', 'SIMPLE', '2022-08-25 13:25:25.220000', '2022-08-25 13:25:25.220000', 100006, 100007);
insert into moderator(id,uuid, role, creation_date, modification_date, account_id, guest_id)
values (100004, '100004', 'ADMIN', '2022-08-25 13:25:25.220000', '2022-08-25 13:25:25.220000', 100008, 100007);

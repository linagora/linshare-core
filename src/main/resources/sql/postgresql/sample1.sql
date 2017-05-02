BEGIN;
SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET client_min_messages = warning;
SET default_with_oids = false;
-- SET ON_ERROR_STOP = on;



-- Jeu de données de tests

INSERT INTO ldap_connection(id, uuid, label, provider_url, security_auth, security_principal, security_credentials, creation_date, modification_date)
	VALUES (1, 'a9b2058f-811f-44b7-8fe5-7a51961eb098', 'linshare-obm', 'ldap://linshare-obm2.linagora.dc1:389', 'simple', '', '', now(), now());
INSERT INTO ldap_connection(id, uuid, label, provider_url, security_auth, security_principal, security_credentials, creation_date, modification_date)
	VALUES (2, 'f5550163-8f06-4310-91c4-1e174f5c62fd', 'linshare-obm-ip', 'ldap://172.16.18.67:389', 'simple', '', '', now(), now());


-- system domain pattern
INSERT INTO ldap_pattern(
    id,
    uuid,
    pattern_type,
    label,
    description,
    auth_command,
    search_user_command,
    system,
    auto_complete_command_on_first_and_last_name,
    auto_complete_command_on_all_attributes,
    search_page_size,
    search_size_limit,
    completion_page_size,
    completion_size_limit,
    creation_date,
    modification_date)
VALUES (
    50,
    'd793ebef-b8bb-4930-bf16-27add911ec13',
    'USER_LDAP_PATTERN',
    'linshare-obm',
    'This is pattern the default pattern for the ldap obm structure.',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    false,
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
    );

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (51, 'user_mail', 'mail', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (52, 'user_firstname', 'givenName', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (53, 'user_lastname', 'sn', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (54, 'user_uid', 'uid', false, true, true, 50, false);



INSERT INTO user_provider(id, uuid, provider_type, base_dn, creation_date, modification_date, ldap_connection_id, ldap_pattern_id)
    VALUES (1, '93fd0e8b-fa4c-495d-978f-132e157c2292', 'LDAP_PROVIDER', 'ou=users,dc=int6.linshare.dev,dc=local', now(), now(), 1, 50);


-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, default_mail_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order) VALUES (2, 1, 'f4a7e5d0-4650-4a5c-a2a8-f1a563f2ff6c', 'MyDomain', true, false, 'a simple description', 0, 'en', 'en', null, 1, 1, 2);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, default_mail_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order) VALUES (3, 2, 'c3cffd8c-7bbb-4314-8dd8-80960dc596e6', 'MySubDomain', true, false, 'a simple description', 0, 'en','en', 1, 1, 2, 3);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, default_mail_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order) VALUES (4, 3, '3d3f0827-a224-445d-9fe6-b1ce313ab1b2', 'GuestDomain', true, false, 'a simple description', 0, 'en', 'en', null, 1, 2, 4);

UPDATE domain_abstract SET mailconfig_id = 1;
UPDATE domain_abstract SET mime_policy_id=1;
UPDATE domain_abstract SET welcome_messages_id = 1;



INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, password, destroyed, domain_id)
	VALUES (40, 'amy.wolsh@int6.linshare.dev', 4, 'fff38827-490a-4654-86a6-57b61611b42d', now(),now(), 4, 'en', 'en','en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', 0, 1);
INSERT INTO users(account_id, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST)
	VALUES (40, null, 'Technical Account for test', false, '', false, false);

INSERT INTO technical_account_permission (id, uuid, creation_date, modification_date) VALUES (40, 'fbba4e41-ca60-4f09-8d59-fbfe052acb82', current_timestamp(3), current_timestamp(3));


-- Users
-- amy.wolsh
INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id) VALUES (50, 'amy.wolsh@int6.linshare.dev', 2, '9a9ece25-7a0e-4d75-bb55-d4070e25e1e1', current_timestamp(3), current_timestamp(3), 0, 'fr', 'en','fr', true, 0, 3);
INSERT INTO users(account_id, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (50, 'Amy', 'Wolsh', true, '', false, true);
-- pierre mongin : 15kn60njvhdjh
-- INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id, owner_id , password ) VALUES (53, 'pmongin@ratp.fr', 3, 'fa2cab19-2cd7-44f5-96f6-418455899d3e', current_timestamp(3), current_timestamp(3), 0, 'fr', 'fr','en', true, 0, 4, 50 , 'OsFTxoUjd62imwHnaV/4zQfrJ5s=');
-- INSERT INTO users(account_id, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, expiration_date) VALUES (53, 'Pierre', 'Mongin', true, '', false, false, current_timestamp(3));

-- Upload Requests.
INSERT INTO upload_request_group (id, subject, body, uuid, creation_date, modification_date)
	VALUES (1, 'subject of upload request 1', 'body of upload request 1', 'b344b5ca-d9e7-4857-b959-5e86f34a91f7', now(), now());
INSERT INTO upload_request (id, domain_abstract_id, account_id, upload_request_group_id, uuid, max_file, max_deposit_size, max_file_size, status, activation_date, creation_date, modification_date, notification_date, expiry_date, upload_proposition_request_uuid, can_delete, can_close, can_edit_expiry_date, locale, secured, mail_message_id)
	VALUES (1, 3, 50, 1, '916a6e65-deb8-4120-b2ab-c64bfcbf4e02', 3, 31457280, 10485760, 'STATUS_ENABLED', now(), now(), now(), '2014-08-10 00:00:00', '2014-09-10 00:00:00', null, true, true, true, 'fr', true, null); 

INSERT INTO contact (id, mail) VALUES (1,'yoda@int4.linshare.dev');
-- password : 1qm6xtpyu93qp
INSERT INTO upload_request_url (id, contact_id, upload_request_id, uuid, path, password, creation_date, modification_date)
	VALUES (1, 1, 1, '90b8a0f8-af07-4052-8bb8-bc5179f64b72', 'upload_request', 'kITh6Jk+FiuyGQtdtaeFxvYnzug=', now(), now());



-- MyDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    quota_type, current_value_for_subdomains)
VALUES (
    2, '164783e8-b9d1-11e5-87e9-bfc0aac925c2', NOW(), NOW(), null,
	0, 0,
    2, 1,
	1000000000000, false,
	1000000000000,
    1000000000000, false,
    'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)


-- 'CONTAINER_QUOTA', 'USER' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (
    3, '37226d66-b9d2-11e5-b4d8-f7b730449724', NOW(), NOW(), null,
	2, 0, 0,
    2, 1,
	400000000000, false,
    400000000000,
    400000000000, false,
    10000000000, false,
    100000000000, false,
    10000000000, false,
    100000000000, false,
    'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (
    4, '6a442450-b9d2-11e5-8c67-5b2367500fc4', NOW(), NOW(), null,
	2, 0, 0,
    2, 1,
	400000000000, false,
    400000000000,
    400000000000, false,
    10000000000, false,
    400000000000, false,
    10000000000, false,
    400000000000, false,
    'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup



-- MySubDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    quota_type, current_value_for_subdomains)
VALUES (
    5, 'b69b9d1a-b9d2-11e5-aab9-e337a9ab2b58', NOW(), NOW(), null,
	0, 0,
    3, 2,
	1000000000000, false,
	1000000000000,
    1000000000000, false,
    'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)



-- 'CONTAINER_QUOTA', 'USER' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (
    6, 'f8733bd0-b9d2-11e5-a247-2b9505cfdddf', NOW(), NOW(), null,
	5, 0, 0,
    3, 2,
	400000000000, false,
    400000000000,
    400000000000, false,
    10000000000, false,
    100000000000, false,
    10000000000, false,
    100000000000, false,
    'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (
    7, '002310d0-b9d3-11e5-9413-d3f63c53e650', NOW(), NOW(), null,
	5, 0, 0,
    3, 2,
	400000000000, false,
    400000000000,
    400000000000, false,
    10000000000, false,
    400000000000, false,
    10000000000, false,
    400000000000, false,
    'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup


-- GuestDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    quota_type, current_value_for_subdomains)
VALUES (
    8, '0b866494-b9d4-11e5-be35-afca154efca0', NOW(), NOW(), null,
	0, 0,
    4, 2,
	1000000000000, false,
	1000000000000,
    1000000000000, false,
    'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)



-- 'CONTAINER_QUOTA', 'USER' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (
    9, '1515e6e2-b9d4-11e5-997e-0b5792ea886a', NOW(), NOW(), null,
	8, 0, 0,
    4, 2,
	400000000000, false,
    400000000000,
    400000000000, false,
    10000000000, false,
    100000000000, false,
    10000000000, false,
    100000000000, false,
    'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value,
    domain_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    default_max_file_size, default_max_file_size_override,
    default_account_quota, default_account_quota_override,
    max_file_size, max_file_size_override,
    account_quota, account_quota_override,
    quota_type, container_type, shared)
VALUES (
    10, '1f468522-b9d4-11e5-916d-a713a67dd225', NOW(), NOW(), null,
	8, 0, 0,
    4, 2,
	400000000000, false,
    400000000000,
    400000000000, false,
    10000000000, false,
    400000000000, false,
    10000000000, false,
    400000000000, false,
    'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup


-- Bart ACCOUNT QUOTA
INSERT INTO quota(
    id, uuid, creation_date, modification_date, batch_modification_date,
    quota_container_id, current_value, last_value,
    domain_id, account_id, domain_parent_id,
    quota, quota_override,
    quota_warning,
    default_quota, default_quota_override,
    max_file_size, max_file_size_override,
    shared, quota_type)
VALUES (
    43, '1f468522-b9d4-11e5-916d-a713a67dd226', NOW(), NOW(), null,
    6, 0, 0,
    3, 50, 2,
    100000000000, false,
    100000000000,
    100000000000, false,
    10000000000, false,
    false, 'ACCOUNT_QUOTA');

-- enable guests
UPDATE policy SET status=true where id=27;

-- enable thread tab
UPDATE policy SET status=true , system=false , default_status=true where id=45;

-- enable upload proposition web service
UPDATE policy SET status=true , policy=1 where id=98; -- fixme

-- enable upload request
UPDATE policy SET status=true , policy=1 where id=63;

COMMIT;

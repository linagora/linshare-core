-- ldap connection
-- Since we do not have an ldap embedded server that we can use in a standalone embedded mode
-- with jetty, I decided to at least define the same parameters as our ldap docker image for tests.
INSERT INTO ldap_connection(id, uuid, label, provider_url, security_auth, security_principal, security_credentials, creation_date, modification_date)
	VALUES (50, 'a9b2058f-811f-44b7-8fe5-7a51961eb098', 'baseLDAP', 'ldap://ldap:1389', 'simple', 'cn=linshare,dc=linshare,dc=org', 'linshare', now(), now());

-- user domain pattern
INSERT INTO ldap_pattern(id, uuid, pattern_type, label,
	description, auth_command, search_user_command,
	system,	auto_complete_command_on_first_and_last_name, auto_complete_command_on_all_attributes,
	search_page_size, search_size_limit, completion_page_size,
	completion_size_limit, creation_date, modification_date)
	VALUES
	(50, 'e4db2f22-2496-4b7d-b5e5-232872652c68', 'USER_LDAP_PATTERN', 'basePattern',
	'basePattern', 'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
	'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
	false, 'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
	'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
	0, 100, 0,
	10, now(), now());
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (50, 'user_mail', 'mail', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (51, 'user_firstname', 'givenName', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (52, 'user_lastname', 'sn', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (53, 'user_uid', 'uid', false, true, true, 50, false);

-- user provider
INSERT INTO user_provider(id, uuid, provider_type, base_dn, creation_date, modification_date, ldap_connection_id, ldap_pattern_id)
	VALUES (50, '93fd0e8b-fa4c-495d-978f-132e157c2292', 'LDAP_PROVIDER', 'ou=People,dc=linshare,dc=org', now(), now(), 50, 50);

INSERT INTO domain_abstract(
	id, type , uuid, label,
	enable, template, description, default_role,
	default_locale, purge_step, user_provider_id,
	domain_policy_id, parent_id, auth_show_order, mailconfig_id,
	welcome_messages_id, creation_date, modification_date)
VALUES
-- Top domain (MyDomain)
	(2, 1, 'MyDomain', 'MyDomain',
	true, false, 'a simple description', 0,
	'en','IN_USE', 50,
	1, 1, 2, null,
	1, now(), now()),
-- Sub domain (MySubDomain)
	(3, 2, 'MySubDomain', 'MySubDomain',
	true, false, 'a simple description', 0,
	'en','IN_USE', null,
	1, 2, 3, null,
	1, now(), now()),
-- Guest domain (example domain)
	(4, 3, 'GuestDomain', 'GuestDomain',
	true, false, 'a simple description', 0,
	'en','IN_USE', null,
	1, 2, 4, null,
	1, now(), now());

SET @my_domain_id = SELECT 2;
SET @my_sub_domain_id = SELECT 3;
SET @guest_domain_id = SELECT 4;


UPDATE domain_abstract SET mime_policy_id = 1;
UPDATE domain_abstract SET mailconfig_id = 1;


-- MyDomain QUOTA
INSERT INTO quota(
	id, uuid, creation_date, modification_date, batch_modification_date,
	current_value, last_value, domain_id, domain_parent_id,
	quota, quota_override, quota_warning, default_quota,
	default_quota_override, quota_type, current_value_for_subdomains)
VALUES (
	2, '164783e8-b9d1-11e5-87e9-bfc0aac925c2', NOW(), NOW(), null,
	6666666, 0, @my_domain_id , 1,
	1000000000000, false, 1000000000000, 1000000000000,
	 false, 'DOMAIN_QUOTA', 0);
UPDATE quota SET
	domain_shared_override = false,
	domain_shared = false
WHERE id = 2;
UPDATE quota SET
	default_domain_shared_override = false,
	default_domain_shared = false
WHERE id = 2;
SET @quota_my_domain_id = SELECT 2;
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)

-- 'CONTAINER_QUOTA', 'USER' for MyDomain
INSERT INTO quota(
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value, domain_id, domain_parent_id,
	quota, quota_override, quota_warning, default_quota, default_quota_override,
	default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override,
	max_file_size, max_file_size_override, account_quota, account_quota_override,
	quota_type, container_type, shared)
VALUES (
	3, '37226d66-b9d2-11e5-b4d8-f7b730449724', NOW(), NOW(), null,
	@quota_my_domain_id , 0, 0, @my_domain_id , 1,
	400000000000, false, 400000000000, 400000000000, false,
	10000000000, false, 100000000000, false,
	10000000000, false, 100000000000, false,
	'CONTAINER_QUOTA', 'USER', false);
SET @quota_on_my_domain_container_user_id = SELECT 3;
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


	-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date,
	batch_modification_date, quota_domain_id, current_value, last_value,
	domain_id, domain_parent_id, quota, quota_override,
	quota_warning, default_quota, default_quota_override, default_max_file_size,
	default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size,
	max_file_size_override, account_quota, account_quota_override, quota_type,
	container_type, shared)
VALUES ( 4, '6a442450-b9d2-11e5-8c67-5b2367500fc4', NOW(), NOW(),
	null, @quota_my_domain_id , 0, 0,
	@my_domain_id , 1, 400000000000, false,
	400000000000, 400000000000, false, 10000000000,
	false, 400000000000, false, 10000000000,
	false, 400000000000, false, 'CONTAINER_QUOTA',
	'WORK_GROUP', true);
SET @quota_on_my_domain_container_workgroup_id = SELECT 4;
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup

-- MyDomain ACCOUNT_QUOTA - John (10)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_override, max_file_size_override, maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	111, 'a2b4ed6e-4039-4f45-9a33-d906a3fd3ae8', NOW(), NOW(), NOW(),
	@quota_my_domain_id, @quota_on_my_domain_container_user_id , 0, 0,
	@my_domain_id , @john_do_id , 1, 1000000000,
	false, false, false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');
UPDATE quota SET domain_shared_override = false, domain_shared = false WHERE id = 111;
SET @quota_account_jhon_id = SELECT 111;
-- quota : 100 Go
-- max_file_size : 10 Go

-- MyDomain ACCOUNT_QUOTA - Jane (11)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_override, max_file_size_override, maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	122, '072b06d3-8f05-48fa-8aed-bf86fce7170a', NOW(), NOW(), NOW(),
	@quota_my_domain_id, @quota_on_my_domain_container_user_id, 0, 0,
	@my_domain_id, @jane_simth_id , 1, 1000000000,
	false, false, false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');
UPDATE quota SET domain_shared_override = false, domain_shared = false WHERE id = 122;
SET @quota_account_jane_id = SELECT 122;
-- quota : 100 Go
-- max_file_size : 10 Go

-- MyDomain ACCOUNT_QUOTA - Foo (12)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_override, max_file_size_override, maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	133, '123b06d3-8f05-48fa-8aed-bf86fce7170a', NOW(), NOW(), NOW(),
	@quota_my_domain_id, @quota_on_my_domain_container_user_id, 0, 0,
	@my_domain_id , @foo_bar_id , 1, 1000000000,
	false, false, false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');
UPDATE quota SET domain_shared_override = false, domain_shared = false WHERE id = 133;
SET @quota_account_foo_id = SELECT 133;
-- quota : 100 Go
-- max_file_size : 10 Go


-- MyDomain, ACCOUNT_QUOTA - workgroup (20)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_warning, max_file_size, container_type, quota_type)
VALUES
	(103, 'b87b9700-4f44-11ea-8b3e-bbbf9093ff0e', NOW(), NOW(), NOW(),
	@quota_my_domain_id, @quota_on_my_domain_container_workgroup_id , 700, 0,
	@my_domain_id , @workgroup_20_id , null, 1000,
	800, 5, null, 'ACCOUNT_QUOTA'),
-- MyDomain, ACCOUNT_QUOTA - workgroup (21)
	(104, 'bd0f0b44-4f44-11ea-ae4f-bbde575ffbd3', NOW(), NOW(), NOW(),
	@quota_my_domain_id , @quota_on_my_domain_container_workgroup_id, 500, 200,
	@my_domain_id , @workgroup_21_id , null, 1300,
	1000, 6, null, 'ACCOUNT_QUOTA');
SET @quota_workgroup_20_id = SELECT 103;
SET @quota_workgroup_21_id = SELECT 104;

-- MySubDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date,
	batch_modification_date, current_value, last_value, domain_id,
	domain_parent_id, quota, quota_override, quota_warning,
	default_quota, default_quota_override, quota_type, current_value_for_subdomains)
VALUES ( 5, 'b69b9d1a-b9d2-11e5-aab9-e337a9ab2b58', NOW(), NOW(),
	null, 0, 0, @my_sub_domain_id ,
	@my_domain_id , 1000000000000, false, 1000000000000,
	1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)
UPDATE quota SET
	domain_shared_override = false,
	domain_shared = false
WHERE id = 5;
UPDATE quota SET
	default_domain_shared_override = null,
	default_domain_shared = null
WHERE id = 5;
SET @quota_my_subdomain_id = SELECT 5;

-- 'CONTAINER_QUOTA', 'USER' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date,
	batch_modification_date, quota_domain_id, current_value, last_value,
	domain_id, domain_parent_id, quota, quota_override,
	quota_warning, default_quota, default_quota_override, default_max_file_size,
	default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size,
	max_file_size_override, account_quota, account_quota_override, quota_type,
	container_type, shared)
VALUES ( 6, 'f8733bd0-b9d2-11e5-a247-2b9505cfdddf', NOW(), NOW(),
	null, 5, 0, 0,
	@my_sub_domain_id , @my_domain_id , 400000000000, false,
	400000000000, 400000000000, false, 10000000000,
	false, 100000000000, false, 10000000000,
	false, 100000000000, false, 'CONTAINER_QUOTA',
	'USER', false);
SET @quota_onsubdomain_container_user_id = SELECT 6;
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date,
	batch_modification_date, quota_domain_id, current_value, last_value,
	domain_id, domain_parent_id, quota, quota_override,
	quota_warning, default_quota, default_quota_override, default_max_file_size,
	default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size,
	max_file_size_override, account_quota, account_quota_override, quota_type,
	container_type, shared)
VALUES ( 7, '002310d0-b9d3-11e5-9413-d3f63c53e650', NOW(), NOW(),
	null, 5, 0, 0,
	@my_sub_domain_id , @my_domain_id , 400000000000, false,
	400000000000, 400000000000, false, 10000000000,
	false, 400000000000, false, 10000000000,
	false, 400000000000, false, 'CONTAINER_QUOTA',
	'WORK_GROUP', true);
SET @quota_onsubdomain_container_workgroup_id = SELECT 7;
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup


-- GuestDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date,
	batch_modification_date, current_value, last_value, domain_id,
	domain_parent_id, quota, quota_override, quota_warning,
	default_quota, default_quota_override, quota_type, current_value_for_subdomains)
VALUES ( 8, '0b866494-b9d4-11e5-be35-afca154efca0', NOW(), NOW(),
	null, 0, 0, @guest_domain_id ,
	@my_domain_id , 1000000000000, false, 1000000000000,
	1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)
UPDATE quota SET
	domain_shared_override = false,
	domain_shared = false
WHERE id = 8;
UPDATE quota SET
	default_domain_shared_override = null,
	default_domain_shared = null
WHERE id = 8;
SET @quota_guest_domain_id = SELECT 8;


-- 'CONTAINER_QUOTA', 'USER' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, current_value, last_value, domain_id,
	domain_parent_id, quota, quota_override, quota_warning,
	default_quota, default_quota_override, default_max_file_size, default_max_file_size_override,
	default_account_quota, default_account_quota_override, max_file_size, max_file_size_override,
	account_quota, account_quota_override, quota_type, container_type, shared)
VALUES
	( 9, '1515e6e2-b9d4-11e5-997e-0b5792ea886a', NOW(), NOW(), null,
	8, 0, 0, 4,
	2, 400000000000, false, 400000000000,
	400000000000, false, 10000000000, false,
	100000000000, false, 10000000000, false,
	100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go
SET @quota_on_guest_domain_container_user_id = SELECT 9;
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date,
	batch_modification_date, quota_domain_id, current_value, last_value,
	domain_id, domain_parent_id, quota, quota_override,
	quota_warning, default_quota, default_quota_override, default_max_file_size,
	default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size,
	max_file_size_override, account_quota, account_quota_override, quota_type,
	container_type, shared)
VALUES ( 10, '1f468522-b9d4-11e5-916d-a713a67dd225', NOW(), NOW(),
	null, 8, 0, 0,
	@guest_domain_id , @my_domain_id , 400000000000, false,
	400000000000, 400000000000, false, 10000000000,
	false, 400000000000, false, 10000000000,
	false, 400000000000, false, 'CONTAINER_QUOTA',
	'WORK_GROUP', true);
SET @quota_on_guest_domain_container_workgroup_id = SELECT 9;
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup

UPDATE domain_abstract SET mailconfig_id = 1;


-- Activate all nested func of upload request func (We need to activate all functionalities to insure the tests run well)
UPDATE policy SET status=true WHERE id IN (SELECT policy_activation_id FROM functionality WHERE parent_identifier = 'UPLOAD_REQUEST');



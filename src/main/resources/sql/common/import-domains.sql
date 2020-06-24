-- default domain policy
INSERT INTO domain_access_policy(id, creation_date, modification_date) 
	VALUES (1, now(), now());
INSERT INTO domain_access_rule(id, domain_access_rule_type, domain_id, domain_access_policy_id, rule_index) 
	VALUES (1, 0, null, 1,0);
INSERT INTO domain_policy(id, uuid, label, domain_access_policy_id) 
	VALUES (1, 'DefaultDomainPolicy', 'DefaultDomainPolicy', 1);

-- Root domain (application domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, purge_step, default_mail_locale, creation_date, modification_date, user_provider_id, domain_policy_id, parent_id, auth_show_order) 
	VALUES (1, 0, 'LinShareRootDomain', 'LinShareRootDomain', true, false, 'The root application domain', 3, 'en','IN_USE', 'en', now(), now(), null, 1, null, 0);

-- Default mime policy
INSERT INTO mime_policy(id, domain_id, uuid, name, mode, displayable, creation_date, modification_date) 
	VALUES(1, 1, '3d6d8800-e0f7-11e3-8ec0-080027c0eef0', 'Default Mime Policy', 0, 0, now(), now());
UPDATE domain_abstract SET mime_policy_id=1;

-- login is e-mail address 'root@localhost.localdomain' and password is 'adminlinshare'
-- password generated from https://www.browserling.com/tools/bcrypt
INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, password, destroyed, domain_id, purge_step, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, authentication_failure_count) 
	VALUES (1, 'root@localhost.localdomain', 6, 'root@localhost.localdomain', now(),now(), 3, 'en', 'en','en', true, '{bcrypt}$2a$10$LQSvbfb2ZsCrWzPp5lj2weSZCz2fWRDBOW4k3k0UxxtdFIEquzTA6', 0, 1, 'IN_USE', 'Administrator', 'LinShare', true, '', false, false, 0);

-- system account :
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id, purge_step, can_upload, restricted, can_create_guest, authentication_failure_count) 
	VALUES (2, 'system', 7, 'system', now(),now(), 3, 'en', 'en','en', true, 0, 1, 'IN_USE', false, false, false, 0);
-- system account for upload-request:
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id, purge_step, can_upload, restricted, can_create_guest, authentication_failure_count) 
	VALUES (3,'system-account-uploadrequest', 7, 'system-account-uploadrequest', now(),now(), 3, 'en', 'en','en', true, 0, 1, 'IN_USE', false, false, false, 0);

-- system
-- OBM user ldap pattern.
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
    1,
    'cd26e59d-6d4c-41b4-a0eb-610fd42e1beb',
    'USER_LDAP_PATTERN',
    'default-pattern-obm',
    'This is pattern the default pattern for the ldap obm structure.',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
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
	VALUES (1, 'user_mail', 'mail', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (2, 'user_firstname', 'givenName', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (3, 'user_lastname', 'sn', false, true, true, 1, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (4, 'user_uid', 'uid', false, true, true, 1, false);

-- Active Directory domain pattern.
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
    2,
    'af7ceb1e-9268-4b20-af80-21fa4bd5222c',
    'USER_LDAP_PATTERN',
    'default-pattern-AD',
    'This is pattern the default pattern for the Active Directory structure.',
    'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(sAMAccountName="+login+")))");',
    'ldap.search(domain, "(&(objectClass=user)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=user)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (5, 'user_mail', 'mail', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (6, 'user_firstname', 'givenName', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (7, 'user_lastname', 'sn', false, true, true, 2, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (8, 'user_uid', 'sAMAccountName', false, true, true, 2, false);

-- OpenLdap ldap pattern.
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
    3,
    '868400c0-c12e-456a-8c3c-19e985290586',
    'USER_LDAP_PATTERN',
    'default-pattern-openldap',
    'This is pattern the default pattern for the OpenLdap structure.',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (9, 'user_mail', 'mail', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (10, 'user_firstname', 'givenName', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (11, 'user_lastname', 'sn', false, true, true, 3, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (12, 'user_uid', 'uid', false, true, true, 3, false);

-- Group ldap pattern
INSERT INTO ldap_pattern(
	id,
	uuid,
	pattern_type,
	label,
	system,
	description,
	auth_command,
	search_user_command,
	search_page_size,
	search_size_limit,
	auto_complete_command_on_first_and_last_name,
	auto_complete_command_on_all_attributes, completion_page_size,
	completion_size_limit,
	creation_date,
	modification_date,
	search_all_groups_query,
	search_group_query,
	group_prefix)
	VALUES(
	4,
	'dfaa3523-51b0-423f-bb6d-95d6ecbfcd4c',
	'GROUP_LDAP_PATTERN',
	'Ldap groups',
	true,
	'default-group-pattern',
	NULL,
	NULL,
	100,
	NULL,
	NULL,
	NULL,
	NULL,
	NULL,
	NOW(),
	NOW(),
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-*))");',
	'ldap.search(baseDn, "(&(objectClass=groupOfNames)(cn=workgroup-" + pattern + "))");',
	'workgroup-');


-- ldap attributes
INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(13, 'mail', 'member_mail', false, true, true, false, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(14, 'givenName', 'member_firstname', false, true, true, false, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(15, 'cn', 'group_name_attr', false, true, true, true, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(16, 'member', 'extended_group_member_attr', false, true, true, true, 4);

INSERT INTO ldap_attribute
(id, attribute, field, sync, system, enable, completion, ldap_pattern_id)
VALUES(17, 'sn', 'member_lastname', false, true, true, false, 4);


-- Demo ldap pattern.
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
    5,
    'a4620dfc-dc46-11e8-a098-2355f9d6585a',
    'USER_LDAP_PATTERN',
    'default-pattern-demo',
    'This is pattern the default pattern for the OpenLdap demo structure.',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");',
    true,
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");',
    'ldap.search(domain, "(&(objectClass=inetOrgPerson)(employeeType=Internal)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");',
    100,
    100,
    10,
    10,
    now(),
    now()
);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (18, 'user_mail', 'mail', false, true, true, 5, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (19, 'user_firstname', 'givenName', false, true, true, 5, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (20, 'user_lastname', 'sn', false, true, true, 5, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion)
	VALUES (21, 'user_uid', 'uid', false, true, true, 5, false);

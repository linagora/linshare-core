-- ldap connection 
INSERT INTO ldap_connection(id, uuid, label, provider_url, security_auth, security_principal, security_credentials, creation_date, modification_date) VALUES (50, 'a9b2058f-811f-44b7-8fe5-7a51961eb098', 'baseLDAP', 'ldap://localhost:33389', 'simple', null, null, now(), now());

-- user domain pattern
INSERT INTO ldap_pattern( id, uuid, pattern_type, label, description, auth_command, search_user_command, system, auto_complete_command_on_first_and_last_name, auto_complete_command_on_all_attributes, search_page_size, search_size_limit, completion_page_size, completion_size_limit, creation_date, modification_date) VALUES ( 50, 'e4db2f22-2496-4b7d-b5e5-232872652c68', 'USER_LDAP_PATTERN', 'basePattern', 'basePattern', 'ldap.search(domain, "(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(mail="+login+")(uid="+login+")))");', 'ldap.search(domain, "(&(objectClass=*)(mail="+mail+")(givenName="+first_name+")(sn="+last_name+"))");', false, 'ldap.search(domain, "(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(&(sn=" + first_name + ")(givenName=" + last_name + "))(&(sn=" + last_name + ")(givenName=" + first_name + "))))");', 'ldap.search(domain, "(&(objectClass=*)(mail=*)(givenName=*)(sn=*)(|(mail=" + pattern + ")(sn=" + pattern + ")(givenName=" + pattern + ")))");', 0, 100, 0, 10, now(), now()); 
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion) VALUES (50, 'user_mail', 'mail', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion) VALUES (51, 'user_firstname', 'givenName', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion) VALUES (52, 'user_lastname', 'sn', false, true, true, 50, true);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, ldap_pattern_id, completion) VALUES (53, 'user_uid', 'uid', false, true, true, 50, false);

-- user provider
INSERT INTO user_provider(id, uuid, provider_type, base_dn, creation_date, modification_date, ldap_connection_id, ldap_pattern_id) VALUES (50, '93fd0e8b-fa4c-495d-978f-132e157c2292', 'LDAP_PROVIDER', 'dc=linshare,dc=org', now(), now(), 50, 50);

-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, mailconfig_id) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', null, 1, 1, 2, null);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, mailconfig_id) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 50, 1, 2, 3, null);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, mailconfig_id) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', null, 1, 2, 4, null);

UPDATE domain_abstract SET mime_policy_id=1 WHERE id < 100000;
UPDATE domain_abstract SET mailconfig_id = 1;
UPDATE domain_abstract SET welcome_messages_id = 1;

-- INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, quota_container_id, current_value, last_value, domain_id, account_id, domain_parent_id, quota, quota_warning, max_file_size, container_type, quota_type) VALUES (4, '802a74ea-a30c-11e5-bcf1-5404a683a462', NOW(), NOW(), NOW(), null, null, 1096,100, null, null, null, 2300, 2000, 10, null, 'PLATFORM_QUOTA');


-- MyDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, quota_type, current_value_for_subdomains) VALUES ( 2, '164783e8-b9d1-11e5-87e9-bfc0aac925c2', NOW(), NOW(), NOW(), 0, 0, 2, 1, 1000000000000, false, 1000000000000, 1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)


-- 'CONTAINER_QUOTA', 'USER' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 3, '37226d66-b9d2-11e5-b4d8-f7b730449724', NOW(), NOW(), NOW(), 2, 0, 0, 2, 1, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 100000000000, false, 10000000000, false, 100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MyDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 4, '6a442450-b9d2-11e5-8c67-5b2367500fc4', NOW(), NOW(), NOW(), 2, 0, 0, 2, 1, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 400000000000, false, 10000000000, false, 400000000000, false, 'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup



-- MySubDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, quota_type, current_value_for_subdomains) VALUES ( 5, 'b69b9d1a-b9d2-11e5-aab9-e337a9ab2b58', NOW(), NOW(), NOW(), 0, 0, 3, 2, 1000000000000, false, 1000000000000, 1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)



-- 'CONTAINER_QUOTA', 'USER' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 6, 'f8733bd0-b9d2-11e5-a247-2b9505cfdddf', NOW(), NOW(), NOW(), 5, 0, 0, 3, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 100000000000, false, 10000000000, false, 100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for MySubDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 7, '002310d0-b9d3-11e5-9413-d3f63c53e650', NOW(), NOW(), NOW(), 5, 0, 0, 3, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 400000000000, false, 10000000000, false, 400000000000, false, 'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup


-- GuestDomain QUOTA
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, quota_type, current_value_for_subdomains) VALUES ( 8, '0b866494-b9d4-11e5-be35-afca154efca0', NOW(), NOW(), NOW(), 0, 0, 4, 2, 1000000000000, false, 1000000000000, 1000000000000, false, 'DOMAIN_QUOTA', 0);
-- quota : 1 To
-- quota_warning : 1000000000000 : 1 To
-- default_quota : 1000000000000 : 1 To (1 To per sub domain)



-- 'CONTAINER_QUOTA', 'USER' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 9, '1515e6e2-b9d4-11e5-997e-0b5792ea886a', NOW(), NOW(), NOW(), 8, 0, 0, 4, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 100000000000, false, 10000000000, false, 100000000000, false, 'CONTAINER_QUOTA', 'USER', false);
-- quota : 400000000000 : 400 Go for all users
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 100000000000 : 100 Go
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 100000000000 : 100 Go


-- 'CONTAINER_QUOTA', 'WORK_GROUP' for GuestDomain
INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, quota_domain_id, current_value, last_value, domain_id, domain_parent_id, quota, quota_override, quota_warning, default_quota, default_quota_override, default_max_file_size, default_max_file_size_override, default_account_quota, default_account_quota_override, max_file_size, max_file_size_override, account_quota, account_quota_override, quota_type, container_type, shared) VALUES ( 10, '1f468522-b9d4-11e5-916d-a713a67dd225', NOW(), NOW(), NOW(), 8, 0, 0, 4, 2, 400000000000, false, 400000000000, 400000000000, false, 10000000000, false, 400000000000, false, 10000000000, false, 400000000000, false, 'CONTAINER_QUOTA', 'WORK_GROUP', true);
-- quota : 400000000000 : 400 Go for all workgroups
-- quota_warning : 400000000000 : 400 Go
-- default_quota : 400000000000 : 400 Go
-- default_max_file_size : 10000000000  : 10 Go
-- default_account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup
-- max_file_size : 10000000000  : 10 Go
-- account_quota : 400000000000 : 400 Go, also 400 Go for one workgroup

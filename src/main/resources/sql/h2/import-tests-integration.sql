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
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, mailconfig_id, welcome_messages_id) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', null, 1, 1, 2, null, 1);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, mailconfig_id, welcome_messages_id) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 50, 1, 2, 3, null, 1);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, mailconfig_id, welcome_messages_id) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', null, 1, 2, 4, null, 1);

UPDATE domain_abstract SET mime_policy_id=1 WHERE id < 100000;
UPDATE domain_abstract SET mailconfig_id = 1;

-- enable guests
UPDATE policy SET status=true where id=27;

-- enable thread tab
UPDATE policy SET status=true , system=false , default_status=true where id=45;

-- enable upload proposition web service
UPDATE policy SET status=true , policy=1 where id=98; -- fixme

-- enable upload request
UPDATE policy SET status=true , policy=1 where id=63;



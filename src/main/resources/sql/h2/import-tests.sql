-- h2 dedicated sequence
CREATE SEQUENCE IF NOT EXISTS h2_sequence INCREMENT BY 100000 START WITH 1 CACHE 1;

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




INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) VALUES (10, 'user1@linshare.org', 2, 'aebe1b64-39c0-11e5-9fa8-080027b8274b', now(), now(), 0, 'en', 'en', 'en', true, null, 0, 2, 'IN_USE');
INSERT INTO users(account_id, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent) VALUES (10, 'John', 'Do', true, '', false, true, false);
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) VALUES (11, 'user2@linshare.org', 2, 'd896140a-39c0-11e5-b7f9-080027b8274b', now(), now(), 0, 'en', 'en', 'en', true, null, 0, 2, 'IN_USE');
INSERT INTO users(account_id, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent) VALUES (11, 'Jane', 'Simth', true, '', false, true, false);
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) VALUES (12, 'user3@linshare.org', 2, 'e524e1ba-39c0-11e5-b704-080027b8274b', now(), now(), 0, 'en', 'en', 'en', true, null, 0, 2, 'IN_USE');
INSERT INTO users(account_id, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent) VALUES (12, 'Foo', 'Bar', true, '', false, true, false);

SET @john_do_id = SELECT 10;
SET @jane_simth_id = SELECT 11;

INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) VALUES (13, 'guest@linshare.org', 3, '46455499-f703-46a2-9659-24ed0fa0d63c', now(), now(), 0, 'en', 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', 0, 4, 'IN_USE');
INSERT INTO users(account_id, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent) VALUES (13, 'Guest', 'Test', true, '', false, true, false);
SET @guest1_id = SELECT 13;


UPDATE policy SET status = true where id=27;








-- TESTS

-- default domain policy
INSERT INTO domain_access_policy(id) VALUES (100001);
INSERT INTO domain_access_rule(id, domain_access_rule_type, domain_id, domain_access_policy_id, rule_index) VALUES (100001, 0, null, 100001,0);
INSERT INTO domain_policy(id, uuid, label, domain_access_policy_id) VALUES (100001, 'TestAccessPolicy0-test', 'TestAccessPolicy0-test', 100001);


-- Root domain (application domain)
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, welcome_messages_id) VALUES (100001, 0, 'TEST_Domain-0', 'TEST_Domain-0', true, false, 'The root test application domain', 3, 'en', null, 100001, null, 1);
-- id : 100001

-- topDomainName
-- id : 100002
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, welcome_messages_id) VALUES (100002, 1, 'TEST_Domain-0-1', 'TEST_Domain-0-1 (Topdomain)', true, false, 'a simple description', 0, 'en', null, 100001, 100001, 2, 1);

-- topDomainName2
-- id : 100003
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, welcome_messages_id) VALUES (100003, 1, 'TEST_Domain-0-2', 'TEST_Domain-0-2 (Topdomain)', true, false, 'a simple description', 0, 'en', null, 100001, 100001, 3, 1);

-- subDomainName1
-- id : 100004
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, welcome_messages_id) VALUES (100004, 2, 'TEST_Domain-0-1-1', 'TEST_Domain-0-1-1 (Subdomain)', true, false, 'a simple description', 0, 'en', null, 100001, 100002, 4, 1);

-- subDomainName2
-- id : 100005
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, welcome_messages_id) VALUES (100005, 2, 'TEST_Domain-0-1-2', 'TEST_Domain-0-1-2 (Subdomain)', true, false, 'a simple description', 0, 'en', null, 100001, 100002, 5, 1);


-- Guest domain (example domain)
-- id : 100006
INSERT INTO domain_abstract(id, type , uuid, label, enable, template, description, default_role, default_locale, user_provider_id, domain_policy_id, parent_id, auth_show_order, welcome_messages_id) VALUES (100006, 3, 'guestDomainName1', 'guestDomainName1 (GuestDomain)', true, false, 'a simple description', 0, 'en', null, 100001, 100002, 6, 1);

-- Default mime policy
INSERT INTO mime_policy(id, domain_id, uuid, name, mode, displayable, version, creation_date, modification_date) VALUES(100001, 100001, 'ec51317c-086c-442a-a4bf-1afdf8774079', 'Default Mime Policy de test', 0, 0, 1, now(), now());
UPDATE domain_abstract SET mime_policy_id=1 WHERE id >= 100001;



INSERT INTO account(id, Mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step) VALUES (100001, 'root@localhost.localdomain@test', 6, 'root@localhost.localdomain@test', current_date(), current_date(), 3, 'en', 'en', 'en', true, 'JYRd2THzjEqTGYq3gjzUh2UBso8=', 0, 100001, 'IN_USE');
INSERT INTO users(account_id, First_name, Last_name, Can_upload, Comment, Restricted, CAN_CREATE_GUEST, inconsistent) VALUES (100001, 'Administrator', 'LinShare', true, '', false, false, false);


-- root domain de test
-- Functionality : TEST_TIME_STAMPING
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110013, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110014, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110007, false, 'TEST_TIME_STAMPING', 110013, 110014, 100001);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (110007, 'http://server/service');

-- Functionality : TEST_FILESIZE_MAX
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110001, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110002, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110001, false, 'TEST_FILESIZE_MAX', 110001, 110002, 100001);
-- Size : MEGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (110001, 1, 1);
-- Value : 200
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (110001, 200, 110001);


-- Functionality : TEST_QUOTA_GLOBAL
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110003, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110004, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110002, true, 'TEST_QUOTA_GLOBAL', 110003, 110004, 100001);
-- Size : GIGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (110002, 1, 2);
-- Value : 1
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (110002, 1, 110002);


-- Functionality : TEST_QUOTA_USER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110005, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110006, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110003, false, 'TEST_QUOTA_USER', 110005, 110006, 100001);
-- Size : GIGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (110003, 1, 1);
-- Value : 500
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (110003, 500, 110003);


-- Functionality : GUESTS
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110027, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110028, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110014, true, 'GUESTS', 110027, 110028, 100001);


-- Functionality : TEST_FUNC1
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110059, true, true, 2, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110060, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110029, false, 'TEST_FUNC1', 110059, 110060, 100001);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (110029, 'blabla');

-- Functionality : TEST_FUNC2
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110061, true, true, 2, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110062, true, true, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES(110030, false, 'TEST_FUNC2', 110061, 110062, 100001); 
INSERT INTO functionality_string(functionality_id, string_value) VALUES (110030, 'blabla');


-- Functionality : TEST_FUNC3
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110049, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110050, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110025, false, 'TEST_FUNC3', 110049, 110050, 100001);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (110025, 'blabla');


-- Functionality : TEST_FUNC4
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110017, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110018, true, true, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110009, false, 'TEST_FUNC4', 110017, 110018, 100001);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (110009, 'blabla');


-- Functionality : TEST_FUNC5
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110025, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (110026, false, false, 1, true);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (110013, true, 'TEST_FUNC5', 110025, 110026, 100001);
INSERT INTO functionality_string(functionality_id, string_value) VALUES (110013, 'blabla');










-- topDomainName 1
-- Functionality : TEST_FILESIZE_MAX
INSERT INTO policy(id, status, default_status, policy, system) VALUES (111001, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (111002, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (111001, false, 'TEST_FILESIZE_MAX', 111001, 111002, 100002);
-- Size : MEGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (111001, 1, 1);
-- Value : 200
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (111001, 100, 111001);


-- Functionality : TEST_QUOTA_USER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (111005, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (111006, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (111003, false, 'TEST_QUOTA_USER', 111005, 111006, 100002);
-- Size : GIGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (111003, 1, 1);
-- Value : 500
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (111003, 250, 111003);






-- subDomainName 1
-- Functionality : TEST_FILESIZE_MAX
INSERT INTO policy(id, status, default_status, policy, system) VALUES (112001, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (112002, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (112001, false, 'TEST_FILESIZE_MAX', 112001, 112002, 100004);
-- Size : MEGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (112001, 1, 1);
-- Value : 200
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (112001, 50, 112001);

-- subDomainName 2
-- Functionality : TEST_QUOTA_USER
INSERT INTO policy(id, status, default_status, policy, system) VALUES (113005, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (113006, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id) VALUES (113003, false, 'TEST_QUOTA_USER', 113005, 113006, 100005);
-- Size : GIGA
INSERT INTO unit(id, unit_type, unit_value) VALUES (113003, 1, 1);
-- Value : 500
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) VALUES (113003, 125, 113003);

UPDATE domain_abstract SET mailconfig_id = 1;

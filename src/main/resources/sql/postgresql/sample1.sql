-- Jeu de données de tests

INSERT INTO ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) VALUES (1, 'linshare-obm', 'ldap://linshare-obm.linshare.team.services.par.lng:389', 'simple', '', '');

INSERT INTO domain_pattern(domain_pattern_id, identifier, description, auth_command, search_user_command, auto_complete_command, system) VALUES (2, 'linshare-obm', '', 'ldap.list(domain, "(&(objectClass=obmUser)(mail="+login+")(givenName=*)(sn=*))");', 'ldap.list(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+firstName+")(sn="+lastName+"))");', 'Not Yet Implemented', false);

INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (5, 'user_mail', 'mail', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (6, 'user_firstname', 'givenName', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (7, 'user_lastname', 'sn', false, true, true, 2);
INSERT INTO ldap_attribute(id, field, attribute, sync, system, enable, domain_pattern_id) VALUES (8, 'user_uid', 'uid', false, true, true, 2);






INSERT INTO user_provider_ldap(id, differential_key, domain_pattern_id, ldap_connection_id) VALUES (1, 'ou=users,dc=int1.linshare.dev,dc=local', 2, 1);


-- Top domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (2, 1, 'MyDomain', 'MyDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 1, 1, 2);
-- Sub domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (3, 2, 'MySubDomain', 'MySubDomain', true, false, 'a simple description', 0, 'en', 0, 1, 1, 2, 1 , 3);
-- Guest domain (example domain)
INSERT INTO domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (4, 3, 'GuestDomain', 'GuestDomain', true, false, 'a simple description', 0, 'en', 0, null, 1, 2, 1, 4);







-- Users
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, enable, destroyed, domain_id) VALUES (50, 2, '9a9ece25-7a0e-4d75-bb55-d4070e25e1e1', current_date,current_date, 0, 'fr', true, false, 3);
INSERT INTO users(account_id, First_name, Last_name, Mail, Can_upload, Comment, Restricted, CAN_CREATE_GUEST) VALUES (50, 'Bart', 'Simpson', 'bart.simpson@int1.linshare.dev', true, '', false, true);


-- Thread
INSERT INTO account(id, account_type, ls_uuid, creation_date, modification_date, role_id, locale, enable, destroyed, domain_id) VALUES (51, 5, '9806de10-ed0b-11e1-877a-5404a6202d2c', current_date,current_date, 0, 'fr', true, false, 1);
INSERT INTO thread (account_id, name) VALUES (51, 'cours des comptes');


--Thread members
INSERT INTO thread_member (id, thread_id, admin, can_upload, creation_date, modification_date, user_id) VALUES (1, 51, true, true, current_date,current_date, 50); 


-- Tags
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (1, 50, 'Réponse', false, true, null,0);
INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (2, 50, 'Demande', false, true, null,0);

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (3, 50, 'Projets', false, true, true,1);
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (1, 3, 'RATP'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (2, 3, 'Ministère de l''intérieur'); 

INSERT INTO tag (id, account_id, name, system, visible, not_null, tag_type) VALUES (4, 50, 'Phases', false, true, true,1);
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (3, 4, 'Instruction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (4, 4, 'Contradiction'); 
INSERT INTO tag_enum_value (id, tag_id, value) VALUES (5, 4, 'Recommandation'); 




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



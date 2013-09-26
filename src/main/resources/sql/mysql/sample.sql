INSERT INTO `linshare_ldap_connection` VALUES (2,'linshare-obm','ldap://linshare-obm.linagora.dc1:389','simple','','');

INSERT INTO linshare_domain_pattern(domain_pattern_id, identifier, description, auth_command, search_user_command, get_user_command, get_all_domain_users_command, user_mail, user_firstname, user_lastname) 
	VALUES (2, 'linshare-obm', 'linshare-obm', 'ldap.list(domain, "(&(objectClass=obmUser)(mail="+login+")(givenName=*)(sn=*))");', 
		'ldap.list(domain, "(&(objectClass=obmUser)(mail="+mail+")(givenName="+firstName+")(sn="+lastName+"))");', 
		'', '',
		'mail', 
		'givenName',
		'sn');

INSERT INTO linshare_user_provider_ldap(id, differential_key, domain_pattern_id, ldap_connection_id) 
	VALUES (2, 'ou=users,dc=int1.linshare.dev,dc=local', 2, 2);

INSERT INTO linshare_domain_abstract(id, type , identifier, label, enable, template, description, default_role, default_locale, used_space, 
	user_provider_id, domain_policy_id, parent_id, messages_configuration_id) 
	VALUES (4, 1, 'MyTopDomain', 'MyTopDomain', true, false, 'a simple description', 0, 'en', 0, 2, 1, 1, 1 );

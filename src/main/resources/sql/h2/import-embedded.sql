-- ldap connection
-- Since we do not have an ldap embedded server that we can use in a standalone embedded mode
-- with jetty, I decided to at least define the same parameters as our ldap docker image for tests.

update ldap_connection set security_principal = 'cn=linshare,dc=linshare,dc=org', security_credentials = 'linshare' where uuid = 'a9b2058f-811f-44b7-8fe5-7a51961eb098';

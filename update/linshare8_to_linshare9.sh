#!/bin/bash

echo "-------------------"
echo "LDAP configuration:"
echo "-------------------"
read -p "Please enter your ldap url (was ldap.auth.url): " ldap_url
read -p  "Please enter your ldap base (was ldap.auth.base): " ldap_base
read -p  "Please enter your ldap base DN (was ldap.auth.baseDN): " ldap_baseDN
read -p  "Please enter your ldap mail attribute (was ldap.auth.attribute): " ldap_auth_attribute
echo ""
echo "Please enter your ldap credentials (leave userDN and password empty for anonymous connection to LDAP):"
read -p  "   userDN (was ldap.auth.userDN): " userDN
read -p  "   password (was ldap.auth.password): " password

#echo "-------------------"
#echo "Superadmin configuration:"
#echo "-------------------"
#echo "Please enter the superadmin password: "
#read -s superadmin_pass
#echo "Please confirm the superadmin password: "
#read -s superadmin_pass_confirm

#while [ "$superadmin_pass" != "$superadmin_pass_confirm" ]
#do
#echo ""
#echo "The password does not correspond to the confirmation !"
#echo "Please enter the superadmin password: "
#read -s superadmin_pass
#echo "Please confirm the superadmin password: "
#read -s superadmin_pass_confirm
#done
echo "-------------------"

hashpass=`echo -ne "$(echo -n $superadmin_pass | sha1sum | cut -f1 -d" " | sed -e 's/\(.\{2\}\)/\\\x\1/g')" | base64`

echo "Please insert this lines into your database after the import.sql script :"
echo "-------------------"

echo "INSERT INTO linshare_ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) VALUES (1, 'baseLDAP', '$ldap_url', 'simple', '$userDN', '$password');"

echo "INSERT INTO linshare_domain_pattern(domain_pattern_id, identifier, description, get_user_command, get_all_domain_users_command, auth_command, search_user_command, get_user_result) VALUES (1, 'basePattern', '', 'ldap.entry(\"uid=\" + userId + \",$ldap_baseDN,\" + domain, \"objectClass=*\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)($ldap_auth_attribute=*)(givenName=*)(sn=*))\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|($ldap_auth_attribute=\"+login+\")(uid=\"+login+\")))\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)($ldap_auth_attribute=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");', '$ldap_auth_attribute givenName sn');"

echo "INSERT INTO linshare_domain(domain_id, identifier, differential_key, domain_pattern_id, ldap_connection_id, parameter_id) VALUES (1, 'baseDomain', '$ldap_base', 1, 1, 1);"

#echo "INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)   VALUES (4, 0, 'root@localhost.localdomain', '', '', 'root@localhost.localdomain', '2009-01-01', 3, '$hashpass', '2019-01-01', 'false','false');"


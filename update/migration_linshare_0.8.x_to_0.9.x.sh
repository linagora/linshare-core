#!/bin/bash
# Script Revision : $Revision$
# Script Url : $HeadURL$
# Script Date : $Date$

#################################################
# GLOBAL VARIABLES
#################################################
# These variables are set for example
ldap_url="ldap://localhost:389"
ldap_base="o=groups,dc=nodomain,dc=com"
ldap_baseDN="ou=linshare"
ldap_auth_attribute="mail"
userDN="cn=admin,dc=com"
password="password"
superadmin_pass="adminlinshare"
domain_identifier="myDomain"
sgbd=1


#################################################
# FUNCTIONS
#################################################
function initGlobalVariables()
{
	echo "-------------------"
	echo "Domain configuration:"
	echo "-------------------"
	read -p "Please enter your domain name (unique) '[0-9a-zA-Z]{6,10}' : " domain_identifier
	
	echo "-------------------"
	echo "Sgbd configuration:"
	echo "-------------------"
	read -p "Please enter your sgbd type Postgresql = 1 , Mysql = 2 : " sgbd

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
	
	echo "-------------------"
	echo "Superadmin configuration:"
	echo "-------------------"
	echo "Please enter the superadmin password: "
	read -s superadmin_pass
	echo "Please confirm the superadmin password: "
	read -s superadmin_pass_confirm
	
	while [ "$superadmin_pass" != "$superadmin_pass_confirm" ]
	do
	echo ""
	echo "The password does not correspond to the confirmation !"
	echo "Please enter the superadmin password: "
	read -s superadmin_pass
	echo "Please confirm the superadmin password: "
	read -s superadmin_pass_confirm
	done
	echo "-------------------"
	
}

function printGlobalVariables ()
{
	echo -e "\nYour parameters : \n"
	
	echo "domain name : " $domain_identifier
	echo "ldap url : " $ldap_url
	echo "ldap base : " $ldap_base
	echo "ldap base DN : " $ldap_baseDN
	echo "ldap mail attribute : " $ldap_auth_attribute
	echo "ldap credentials :" 
	echo "   userDN : " $userDN
	echo "   password : " $password

	case $sgbd in 
	2)
		echo -e "Sgbd : Mysql"
	;;
	1)
		echo -e "Sgbd : Postgresql"
	;;
	*)
		echo "ERROR Sgbd $sgbd is not supported !"
	;;
	esac

	echo
}

function encode_admin_pwd ()
{
	hashpass=`echo -ne "$(echo -n $superadmin_pass | sha1sum | cut -f1 -d" " | sed -e 's/\(.\{2\}\)/\\\x\1/g')" | base64`
}


## Ldap Connexion

function create_ldap_entry_mysql ()
{
	echo "INSERT INTO linshare_ldap_connection(identifier, provider_url, security_auth, security_principal, security_credentials) 
		VALUES ('${domain_identifier}LDAP', '$ldap_url', 'simple', '$userDN', '$password');"
}

function create_ldap_entry_postgresql ()
{

	echo "INSERT INTO linshare_ldap_connection(ldap_connection_id, identifier, provider_url, security_auth, security_principal, security_credentials) 
	VALUES ((SELECT nextVal('hibernate_sequence')), '${domain_identifier}LDAP', '$ldap_url', 'simple', '$userDN', '$password');"
}

function get_ldap_entry_id ()
{
	echo "SELECT ldap_connection_id from linshare_ldap_connection where identifier='${domain_identifier}LDAP'"
}



## Domain parameters

function create_domain_parameter_mysql ()
{
	echo "INSERT INTO linshare_parameter (identifier, file_size_max, user_available_size, global_quota, global_used_quota, active_global_quota, active_mimetype, active_signature, active_encipherment, active_doc_time_stamp, user_expiry_time, user_expiry_time_unit_id, custom_logo_url, default_expiry_time, delete_doc_expiry_time, default_expiry_time_unit_id, default_file_expiry_time, default_file_expiry_time_unit_id, closed_domain, restricted_domain, domain_with_guests, guest_can_create_other, messages_configuration_id) SELECT '${domain_identifier}Param',file_size_max, user_available_size, global_quota, global_used_quota, active_global_quota, active_mimetype, active_signature, active_encipherment, active_doc_time_stamp, user_expiry_time, user_expiry_time_unit_id, custom_logo_url, default_expiry_time, delete_doc_expiry_time, default_expiry_time_unit_id, default_file_expiry_time, default_file_expiry_time_unit_id, closed_domain, restricted_domain, domain_with_guests, guest_can_create_other, messages_configuration_id from linshare_parameter where identifier='baseParam';"

}

function create_domain_parameter_postgresql ()
{
	echo "INSERT INTO linshare_parameter (parameter_id, identifier, file_size_max, user_available_size, global_quota, global_used_quota, active_global_quota, active_mimetype, active_signature, active_encipherment, active_doc_time_stamp, user_expiry_time, user_expiry_time_unit_id, custom_logo_url, default_expiry_time, delete_doc_expiry_time, default_expiry_time_unit_id, default_file_expiry_time, default_file_expiry_time_unit_id, closed_domain, restricted_domain, domain_with_guests, guest_can_create_other, messages_configuration_id) SELECT (SELECT nextVal('hibernate_sequence')), '${domain_identifier}Param',file_size_max, user_available_size, global_quota, global_used_quota, active_global_quota, active_mimetype, active_signature, active_encipherment, active_doc_time_stamp, user_expiry_time, user_expiry_time_unit_id, custom_logo_url, default_expiry_time, delete_doc_expiry_time, default_expiry_time_unit_id, default_file_expiry_time, default_file_expiry_time_unit_id, closed_domain, restricted_domain, domain_with_guests, guest_can_create_other, messages_configuration_id from linshare_parameter where identifier='baseParam';"
}

function get_domain_parameter_id ()
{
	echo "SELECT parameter_id from linshare_parameter where identifier='${domain_identifier}Param'"
}




## Domain pattern

function create_domain_pattern_mysql ()
{
	echo "INSERT INTO linshare_domain_pattern(identifier, description, get_user_command, get_all_domain_users_command, auth_command, search_user_command, get_user_result) 
	VALUES ('${domain_identifier}Pattern', '', 'ldap.entry(\"uid=\" + userId + \",$ldap_baseDN,\" + domain, \"objectClass=*\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)($ldap_auth_attribute=*)(givenName=*)(sn=*))\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|($ldap_auth_attribute=\"+login+\")(uid=\"+login+\")))\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)($ldap_auth_attribute=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");', '$ldap_auth_attribute givenName sn');"

}

function create_domain_pattern_postgresql ()
{
	echo "INSERT INTO linshare_domain_pattern(domain_pattern_id, identifier, description, get_user_command, get_all_domain_users_command, auth_command, search_user_command, get_user_result) 
	VALUES ((SELECT nextVal('hibernate_sequence')), '${domain_identifier}Pattern', '', 'ldap.entry(\"uid=\" + userId + \",$ldap_baseDN,\" + domain, \"objectClass=*\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)($ldap_auth_attribute=*)(givenName=*)(sn=*))\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)(givenName=*)(sn=*)(|($ldap_auth_attribute=\"+login+\")(uid=\"+login+\")))\");', 'ldap.list(\"$ldap_baseDN,\" + domain, \"(&(objectClass=*)($ldap_auth_attribute=\"+mail+\")(givenName=\"+firstName+\")(sn=\"+lastName+\"))\");', '$ldap_auth_attribute givenName sn');"
}

function get_domain_pattern_id ()
{
	echo "SELECT domain_pattern_id from linshare_domain_pattern where identifier='${domain_identifier}Pattern'"
}



## Domain 

function create_domain_mysql ()
{
	echo "INSERT INTO linshare_domain(identifier, differential_key, domain_pattern_id, ldap_connection_id, parameter_id) 
		SELECT '${domain_identifier}', '$ldap_base',
		(`get_domain_pattern_id`),
		(`get_ldap_entry_id`),
		(`get_domain_parameter_id`);"
}

function create_domain_postgresql ()
{
	echo "INSERT INTO linshare_domain(domain_id, identifier, differential_key, domain_pattern_id, ldap_connection_id, parameter_id) 
		SELECT (SELECT nextVal('hibernate_sequence')), '${domain_identifier}', '$ldap_base',
		(`get_domain_pattern_id`),
		(`get_ldap_entry_id`),
		(`get_domain_parameter_id`);"
}

function get_domain_id ()
{
	echo "SELECT domain_id from linshare_domain where identifier='${domain_identifier}'"
}


## Users

function insert_super_admin_user_mysql ()
{
	echo "

INSERT INTO linshare_user(user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)
        SELECT  0, 'root@localhost.localdomain', 'Administrator', 'LinShare', 'root@localhost.localdomain', '2009-01-01', 3, '$hashpass', '2019-01-01', false,false from linshare_user
 WHERE NOT EXISTS
        (
                SELECT 0 from linshare_user where login ='root@localhost.localdomain' 
			OR mail ='root@localhost.localdomain'
        );"

}


function insert_super_admin_user_postgresql ()
{
	echo "INSERT INTO linshare_user(user_id, user_type_id, login, first_name, last_name, mail, creation_date, role_id, password, expiry_date, can_upload, can_create_guest)
        SELECT  (SELECT nextVal('hibernate_sequence')), 0, 'root@localhost.localdomain', 'Administrator', 'LinShare', 'root@localhost.localdomain', '2009-01-01', 3, '$hashpass', '2019-01-01', false,false
        WHERE NOT EXISTS
        (
                SELECT 0 from linshare_user where login ='root@localhost.localdomain' 
			OR mail ='root@localhost.localdomain'
        );"
}

function update_domain_users ()
{
	# After the migration, you have to set the id domain to all the existing users.
	echo "UPDATE linshare_user SET domain_id=(`get_domain_id`) WHERE role_id = 0 or role_id = 1;"
}

################################################
# MAIN
################################################
echo -e "\nThis script is designed to convert a 0.8.x LinShare config to a 0.9.x LinShare config. 
In the 0.8.x version, ldap configuration was stored in a file as properties.
In the 0.9.x version, you can now store many configurations in the database.

You have to import the Migration* sql script before running this script. It will generate all the sql queries you need.

Migration Scripts are : 
1) Migration_v08_v09.sql
2) Migration_v09-3_v09-4.sql if you will use a linshare version >= 0.9.4 \n"

initGlobalVariables
encode_admin_pwd
printGlobalVariables
read -p "Continue ?"


case $sgbd in 
	2)
		echo -e "\n\nPlease insert this lines into your mysql database after the migration sql script :"
		echo -e "-------------------\n"
		create_ldap_entry_mysql
		create_domain_parameter_mysql
		create_domain_pattern_mysql
		create_domain_mysql
	 	update_domain_users
		insert_super_admin_user_mysql
	;;
	1)
		echo -e "\n\nPlease insert this lines into your postgresql database after the migration sql script :"
		echo -e "-------------------\n"
		create_ldap_entry_postgresql
		create_domain_parameter_postgresql
		create_domain_pattern_postgresql
		create_domain_postgresql
		update_domain_users
		insert_super_admin_user_postgresql
	;;
	*)
		echo "ERROR Sgbd $sgbd is not supported !"
	;;
esac


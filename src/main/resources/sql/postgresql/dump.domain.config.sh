#!/bin/bash
set -e
#set -x


if [ "$1" == "-h" ] ; then
	echo -e "This is script allow you to dump LinShare domain configuration (ldap connection, domain pattern and domain).\n"
	echo -e "First argument is the database name : 
	default : linshare\n"
	echo
	exit 0
fi

if [ ! -z $1 ] ; then
	l_database="$1"
else
	l_database="linshare"
fi


g_dumpfile="${l_database}.configuration.sql"

if [ -f "${g_dumpfile}" ] ; then 
	echo "WARN: dump file already exists : ${g_dumpfile}"
	echo "Press enter to continue ? (Ctrl C to stop)"
	read
else
	echo "INFO dumping data to ${g_dumpfile}."
fi


pg_dump -U linshare -a --insert --column-inserts --attribute-inserts \
--table ldap_connection \
--table domain_pattern \
--table ldap_attribute \
--table user_provider_ldap \
--table domain_abstract \
${l_database} -f ${g_dumpfile}
if [ $? -ne 0 ] ; then
	echo
	echo "ERROR: dump failed !"
else
	echo
	echo "INFO : dump done."
fi


cp ${g_dumpfile}{,.bak}
sed -i -e '/INSERT INTO domain_pattern (domain_pattern_id, identifier, description, auth_command, search_user_command, system, auto_complete_command) VALUES (1,/ d' ${g_dumpfile}

sed -i -e '/INSERT INTO domain_abstract (id, type, identifier, label, enable, template, description, default_role, default_locale, used_space, user_provider_id, domain_policy_id, parent_id, messages_configuration_id, auth_show_order) VALUES (1,/ d' ${g_dumpfile}

sed -i -e '/INSERT INTO ldap_attribute (id, domain_pattern_id, field, attribute, sync, system, enable) VALUES (.*, 1,/ d' ${g_dumpfile}
echo "INFO : removing default datas."


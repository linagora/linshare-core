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

if [ -f dump.linshare.configuration.sql ] ; then 
	echo "WARN: dump file already exists : dump.linshare.configuration.sql"
	echo "Press enter to continue ? (Ctrl C to stop)"
	read
else
	echo "INFO dumping data to dump.linshare.configuration.sql."
fi

pg_dump -U linshare -a --insert --column-inserts --attribute-inserts \
--table ldap_connection \
--table domain_pattern \
--table ldap_attribute \
--table user_provider_ldap \
--table domain_abstract \
${l_database} -f dump.linshare.configuration.sql 
if [ $? -ne 0 ] ; then
	echo
	echo "ERROR: dump failed !"
else
	echo
	echo "INFO : dump done."
fi


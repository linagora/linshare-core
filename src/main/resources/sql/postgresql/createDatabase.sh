#!/bin/bash
set -e 
#set -x

if [ "$1" == "-h" ] ; then
	echo -e "This is script allow you to create all the databases for LinShare.\n"
	echo -e "First argument is the database name : 
	default : linshare\n"
	echo -e "Second argument is the locale :
	default : fr_FR.UTF-8
	an alias for en_US.UTF-8 is available : \"en\""
	echo
	exit 0
fi

if [ ! -z $1 ] ; then
	l_database="$1"
else
	l_database="linshare"
fi

if [ ! -z $2 ] ; then
	if [ "$2" == "en" ] ; then
		l_locale="en_US.UTF-8"
	else
		l_locale="$2"
	fi
else
	l_locale="fr_FR.UTF-8"
fi


echo "
DROP DATABASE IF EXISTS ${l_database};
DROP DATABASE IF EXISTS ${l_database}_data;

CREATE USER linshare WITH PASSWORD 'linshare';

CREATE DATABASE ${l_database}
  WITH OWNER = linshare
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = '${l_locale}'
       LC_CTYPE = '${l_locale}'
       CONNECTION LIMIT = -1;
GRANT ALL ON DATABASE ${l_database} TO linshare;

CREATE DATABASE ${l_database}_data
  WITH OWNER = linshare
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = '${l_locale}'
       LC_CTYPE = '${l_locale}'
       CONNECTION LIMIT = -1;

GRANT ALL ON DATABASE ${l_database}_data TO linshare;
"





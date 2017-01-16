#!/bin/bash
set -x
set -e

g_import_src=../src/main/resources/sql/postgresql/import-postgresql.sql
g_import_new=../src/main/resources/sql/postgresql/import-postgresql.sql.new
g_host=127.0.0.1
g_port=5432
g_database=linshare

if [ -f update_mail_config.cfg ] ; then
    source makeone.cfg
fi

echo "############ Config #########"
echo "host : $g_host"
echo "port : $g_port"
echo "database : $g_database"
echo "#############################"

sed -r -e '/-- ###BEGIN-PART-1###/,/###END-PART-1###/ !d' ${g_import_src} > ${g_import_new}

pg_dump -h $g_host -p $g_port -U linshare -t mail_layout -t mail_footer -t mail_content -t mail_config -t mail_content_lang -t mail_footer_lang  -a --inserts --attribute-inserts  $g_database -f mails.sql 
echo "-- ###BEGIN-PART-2###" >> ${g_import_new}
grep -v "^-- "  mails.sql | grep -v "^--$" | grep -v "^SET" >> ${g_import_new}
echo "-- ###END-PART-2###" >> ${g_import_new}

sed -r -e '/-- ###BEGIN-PART-3###/,/###END-PART-3###/ !d' ${g_import_src} >> ${g_import_new}
sed -i -r -e "s/'2017-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]{3,6}'/now()/g" ${g_import_new}

mv -v ${g_import_new} ${g_import_src}

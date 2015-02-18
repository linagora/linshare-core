#!/bin/sh

die () {
    echo >&2 "$@"
    echo "usage: visualparadigmtomysql input_file"
    exit 1
}

if [ "$#" -eq 1 ] 
then
    if [ -f $1 ]
    then
        cp $1 output.sql
    else
        die "file $1 doesn't exist"
    fi
else
    die "1 argument required, $# provided"
fi

# 
sed -i -e "1i\SET statement_timeout = 0;\nSET client_encoding = 'UTF8';\nSET client_min_messages = warning;\nSET default_with_oids = false;\n" output.sql
# correcting wrong types
sed -i -e 's/timestamp(29)/timestamp/g' output.sql
sed -i -e 's/BIGSERIAL/int8/g' output.sql
sed -i -e 's/CREATE INDEX mailing_list_uuid/CREATE INDEX mailing_list_index/g' output.sql
sed -i -e 's/CREATE INDEX mailing_list_contact_uuid/CREATE INDEX mailing_list_contact_index/g' output.sql

# cleanup
#sed -i -e 's/ $//g' output.sql
sed -i -e 's///g' output.sql
sed -i -e 's/ $//g' output.sql

echo "Done : output.sql"

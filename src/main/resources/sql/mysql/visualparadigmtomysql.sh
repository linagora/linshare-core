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

# set engine
echo "1i\nSET storage_engine=INNODB;\n.\nwq" | ex -s output.sql

# correcting wrong types
sed -i -e 's/bigint(20)/bigint(8)/g' output.sql
sed -i -e 's/int(11)/int(4)/g' output.sql
sed -i -e 's/timestamp NOT NULL,/datetime NOT NULL,/g' output.sql
sed -i -e 's/timestamp NULL,/datetime NULL,/g' output.sql
sed -i -e 's/last_use   datetime NOT NULL/last_use   timestamp NOT NULL/g' output.sql

# escaping keywords
sed -i -e 's/write/`write`/g' output.sql
sed -i -e 's/ ENGINE=InnoDB//g' output.sql


# To be fixed
sed -i -e 's/version text NOT NULL UNIQUE/version varchar(255) NOT NULL UNIQUE/' output.sql

# cleanup
sed -i -e 's///g' output.sql
sed -i -e 's/ $//g' output.sql

echo "Done : output.sql"

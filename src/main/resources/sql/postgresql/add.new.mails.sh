#!/bin/bash
#set -x
set -e

l_offset=$1
l_count=$2

function usage ()
{
    echo
    echo "Usage: $0 <offset> <count>"
    echo
    echo "first param : offset (the sql id of the last mail content)"
    echo "  select id from mail_content order by id desc limit 1;"
    echo
    echo "second param : count (number of new email to generate)"
    echo
    exit 1
}

if [ ! -f /usr/bin/uuid ] ; then
    echo
    echo "Missing uuid command. please install uuid tool."
    echo
    exit 1
fi

if [ -z "${l_offset}" ] ; then
    usage
fi
if [ -z "${l_count}" ] ; then
    usage
fi

if [ ${l_offset} -lt 0 ] ; then
    usage
fi
if [ ${l_count} -le 0 ] ; then
    usage
fi


for i in $(seq 1 ${l_count})
do
    l_id=$(expr ${i} + ${l_offset})
    l_uuid=$(uuid)
    echo "INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,messages_vietnamese,modification_date,readonly,subject,uuid,visible)
        VALUES ('',NOW(),'',1,${l_id},${l_id},'','','','',NOW(),true,'','${l_uuid}',true);"

        # language : 4
        l_id_mclang=${l_id}
        for l_lang in $(seq 0 3)
        do
            l_uuid=$(uuid)
            echo "INSERT INTO mail_content_lang (id,language,mail_content_id,mail_config_id,mail_content_type,readonly,uuid) VALUES
            (${l_id_mclang},${l_lang},${l_id},1,${l_id},true,'${l_uuid}');"
            l_id_mclang=$(expr ${l_id_mclang} + 100)
        done
done

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
    echo "INSERT INTO mail_content (id, domain_abstract_id, description, visible, mail_content_type, subject, body, uuid, creation_date, modification_date, readonly, messages_french, messages_english)
        VALUES (${l_id}, 1, '', true, ${l_id}, '', '<!DOCTYPE html><html
        xmlns:th="http://www.thymeleaf.org"><body>layout</body></html>',
        '${l_uuid}', now(), now(), true, NULL,
        NULL);"

        # language : 2
        l_id_mclang=${l_id}
        for l_lang in $(seq 0 1)
        do
            l_uuid=$(uuid)
            echo "INSERT INTO mail_content_lang (id, language, mail_content_id,
            mail_config_id, mail_content_type, uuid, readonly) VALUES
            (${l_id_mclang}, ${l_lang},
            ${l_id}, 1, ${l_id}, '${l_uuid}', true);"
            l_id_mclang=$(expr ${l_id_mclang} + 100)
        done
done

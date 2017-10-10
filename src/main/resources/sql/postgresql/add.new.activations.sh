#!/bin/bash
#set -x
set -e

l_offset=$1
l_ma_policies_id=$2
l_count=$3

function usage ()
{
    echo
    echo "Usage: $0 <offset> <count>"
    echo
    echo "first param : offset (the sql id of the last mail activation)"
    echo "  select id from mail_activation order by id desc limit 1;"
    echo
    echo "second param : policy_id (the sql id of the last policy)"
    echo "  select id from policy order by id desc limit 1;"
    echo
    echo "third param : count (number of new mail activation to generate)"
    echo
    exit 1
}


if [ -z "${l_offset}" ] ; then
    usage
fi
if [ -z "${l_count}" ] ; then
    usage
fi
if [ -z "${l_ma_policies_id}" ] ; then
    usage
fi

if [ ${l_offset} -lt 0 ] ; then
    usage
fi
if [ ${l_count} -le 0 ] ; then
    usage
fi
if [ ${l_ma_policies_id} -le 0 ] ; then
    usage
fi


echo "BEGIN;"
for i in $(seq 1 ${l_count})
do
    l_id=$(expr ${i} + ${l_offset})

    l_id_p1=$(expr ${l_ma_policies_id} + 1)
    l_id_p2=$(expr ${l_ma_policies_id} + 2)
    l_id_p3=$(expr ${l_ma_policies_id} + 3)
    l_uuid=$(uuid)

    echo "INSERT INTO policy(id, status, default_status, policy, system) VALUES (${l_id_p1}, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (${l_id_p2}, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (${l_id_p3}, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(${l_id}, false, '${l_uuid}', ${l_id_p1}, ${l_id_p2}, ${l_id_p3}, 1, true);
"
    l_ma_policies_id=$(expr ${l_ma_policies_id} + 3)
done
echo "COMMIT;"

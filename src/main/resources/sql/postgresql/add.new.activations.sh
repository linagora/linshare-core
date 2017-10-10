#!/bin/bash
#set -x
set -e

l_offset=$1
l_ma_policies_id=$2
l_names=$@

function usage ()
{
    echo
    echo "Usage: $0 <offset> <policy_id> <names>"
    echo
    echo "first param : offset (the sql id of the last mail activation)"
    echo "  select id from mail_activation order by id desc limit 1;"
    echo
    echo "second param : policy_id (the sql id of the last policy)"
    echo "  select id from policy order by id desc limit 1;"
    echo
    echo "third param : a list of mail activation identifiers"
    echo
    echo "ex: ./add.new.activations.sh 16 230 UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT UPLOAD_REQUEST_ACTIVATED_FOR_OWNER"
    echo
    exit 1
}


if [ -z "${l_offset}" ] ; then
    usage
fi
if [ -z "${l_ma_policies_id}" ] ; then
    usage
fi

shift
shift
l_names=$@
if [ -z "${l_names}" ] ; then
    usage
fi

if [ ${l_offset} -lt 0 ] ; then
    usage
fi
if [ ${l_ma_policies_id} -le 0 ] ; then
    usage
fi


echo "BEGIN;"
#for i in $(seq 1 ${l_count})
i=0
for l_name in ${l_names}
do
    i=$(expr ${i} + 1)
    l_id=$(expr ${i} + ${l_offset})

    l_id_p1=$(expr ${l_ma_policies_id} + 1)
    l_id_p2=$(expr ${l_ma_policies_id} + 2)
    l_id_p3=$(expr ${l_ma_policies_id} + 3)

    echo "INSERT INTO policy(id, status, default_status, policy, system) VALUES (${l_id_p1}, true, true, 0, true);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (${l_id_p2}, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) VALUES (${l_id_p3}, false, false, 2, true);
INSERT INTO mail_activation(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, enable)
 VALUES(${l_id}, false, '${l_name}', ${l_id_p1}, ${l_id_p2}, ${l_id_p3}, 1, true);
"
    l_ma_policies_id=$(expr ${l_ma_policies_id} + 3)
done
echo "COMMIT;"

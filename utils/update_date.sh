#!/bin/sh

l_path=$1
	echo ${l_path}

usage() {
	echo "Usage : license.sh PATH"
    echo "Add missing license in java source file recursively from PATH"
	exit 0
}

if [ -z "$l_path" ]; then
    usage
fi
if [ ! -d "$l_path" ]; then
    usage
fi

set -o verbose

yearto='2020'

# some legacy license use - instead of –
find ${l_path} -name "*.*" | xargs -i sed -Ei 's/\(C\) (20[0-9]{2})–(20[0-9]{2}) LINAGORA/(C) \1-\2 LINAGORA/g' {}

# Match date like 2015
find ${l_path} -name "*.*" | xargs -i sed -Ei 's/\(C\) (20[0-9]{2}) LINAGORA/(C) \1-'${yearto}' LINAGORA/g' {}

# Match date like 2010-2015 
find ${l_path} -name "*.*" | xargs -i sed -Ei 's/\(C\) (20[0-9]{2}-)(20[0-9]{2}) LINAGORA/(C) \1'${yearto}' LINAGORA/g' {}

# Match deeper date like 2009–2015
find ${l_path} -name "*.*" | xargs -i sed -Ei 's/(20[0-9]{2}–)(20[0-9]{2})/\1'${yearto}'/g' {}

# get original date for a file :
# git rev-list --reverse  HEAD AuthRole.java |head -n1 | xargs git  show -q --format="%ci" | grep -Eo "20[0-9]{2}(-[0-1][1-9]){2}" | grep -Eo "20[0-9]{2}"


find ${l_path} -name "*.*" | xargs -i sed -Ei 's/'${yearto}'-'${yearto}'/'${yearto}'/g' {}

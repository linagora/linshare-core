#!/bin/sh

path=$1

usage() {
	echo "Usage : license.sh PATH"
    echo "Add missing license in java source file recursively from PATH"
	exit 0
}

if [ -z "$path" ]; then
    usage
fi
if [ ! -d "$path" ]; then
    usage
fi

yearfrom='2014'
yearto='2015'

# some legacy license use - instead of –
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i -r 's/\(C\) (20[0-9]{2})–(20[0-9]{2}) LINAGORA/(C) \1-\2 LINAGORA/g'

# Match date like 2015
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i -r 's/\(C\) (20[0-9]{2}) LINAGORA/(C) \1-'${yearto}' LINAGORA/g'

# Match date like 2010-2015
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i -r 's/\(C\) (20[0-9]{2}-)(20[0-9]{2}) LINAGORA/(C) \1'${yearto}' LINAGORA/g'


# get original date for a file :
# git rev-list --reverse  HEAD AuthRole.java |head -n1 | xargs git  show -q --format="%ci" | grep -Eo "20[0-9]{2}(-[0-1][1-9]){2}" | grep -Eo "20[0-9]{2}"

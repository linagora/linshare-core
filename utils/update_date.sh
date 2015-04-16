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

grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i 's/2009-'${yearfrom}' LINAGORA/'${yearto}' LINAGORA/g' {}
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i 's/2009–'${yearfrom}'/2009–'${yearto}'/g' {}
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i 's/(C) '${yearfrom}' LINAGORA/(C) '${yearto}' LINAGORA/g' {}
# some legacy license use - instead of –
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i 's/2009-'${yearfrom}'/2009–'${yearto}'/g' {}

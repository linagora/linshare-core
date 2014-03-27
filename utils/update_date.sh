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

grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i 's/2013 LINAGORA/2014 LINAGORA/g' {}
grep -l "LinShare" --include "*.java" --include "*.properties" --include "*.tml" -R ${path} 2>/dev/null | xargs -i \
    sed -i 's/2009-2013/2009-2014/g' {}

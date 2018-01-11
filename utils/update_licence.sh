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

set -o verbose

find ${path} -name "*.java" | xargs -i sed -Ei "s/2009–2015|2009-2016|2009-2017/2009–2018/g" {}
#!/bin/sh
#
# Copyright (C) 2007-2023 - LINAGORA
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#


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

yearto='2022'

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

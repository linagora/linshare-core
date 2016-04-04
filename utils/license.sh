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

find ${path} -name "*.java" | xargs grep -L "Linagora" 2>/dev/null | xargs -i sed -i \
    "1i/*\n\
 * LinShare is an open source filesharing software, part of the LinPKI software\n\
 * suite, developed by Linagora.\n\
 * \n\
 * Copyright (C) 2016 LINAGORA\n\
 * \n\
 * This program is free software: you can redistribute it and/or modify it under\n\
 * the terms of the GNU Affero General Public License as published by the Free\n\
 * Software Foundation, either version 3 of the License, or (at your option) any\n\
 * later version, provided you comply with the Additional Terms applicable for\n\
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General\n\
 * Public License, subsections (b), (c), and (e), pursuant to which you must\n\
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top\n\
 * of the interface window, the display of the “You are using the Open Source\n\
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to\n\
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the\n\
 * e-mails sent with the Program, (ii) retain all hypertext links between\n\
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)\n\
 * refrain from infringing Linagora intellectual property rights over its\n\
 * trademarks and commercial brands. Other Additional Terms apply, see\n\
 * <http://www.linagora.com/licenses/> for more details.\n\
 * \n\
 * This program is distributed in the hope that it will be useful, but WITHOUT\n\
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS\n\
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more\n\
 * details.\n\
 * \n\
 * You should have received a copy of the GNU Affero General Public License and\n\
 * its applicable Additional Terms for LinShare along with this program. If not,\n\
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License\n\
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms\n\
 * applicable to LinShare software.\n\
 */\n\
" {}

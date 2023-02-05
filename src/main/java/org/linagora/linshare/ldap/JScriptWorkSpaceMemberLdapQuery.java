/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.ldap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;

import com.google.common.collect.Sets;

public class JScriptWorkSpaceMemberLdapQuery extends JScriptLdapQuery<LdapWorkSpaceMemberObject> {

	public JScriptWorkSpaceMemberLdapQuery(LqlRequestCtx ctx, String baseDn, GroupLdapPattern ldapPattern, IDnList dnList, Class<?> clazz)
			throws NamingException, IOException {
		super(ctx, baseDn, dnList, ldapPattern, clazz);
	}

	public Set<LdapWorkSpaceMemberObject> searchAllGroupMember(LdapGroupObject lgo) throws NamingException {
		Map<String, LdapAttribute> ldapDbAttributes = filterAttrByPrefix("member_");
		if (lgo == null || lgo.getMembers() == null) {
			return Sets.newHashSet();
		}
		return dnListToObjectList(lgo.getMembers(), ldapDbAttributes);
	}

}

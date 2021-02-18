/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.ldap;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;

public class JScriptGroupLdapQuery extends JScriptLdapQuery<LdapGroupObject> {

	public JScriptGroupLdapQuery(LqlRequestCtx ctx, String baseDn, GroupLdapPattern ldapPattern, IDnList dnList, Class<?> clazz)
			throws NamingException, IOException {
		super(ctx, baseDn, dnList, ldapPattern, clazz);
	}

	public Set<LdapGroupObject> searchAllGroups() throws NamingException {
		String  command = ((GroupLdapPattern)ldapPattern).getSearchAllGroupsQuery();
		if (logger.isDebugEnabled()) {
			logLqlQuery(command, "");
		}
		// searching ldap directory with pattern
		List<String> dnResultList = this.evaluate(command);
		Map<String, LdapAttribute> ldapDbAttributes = filterAttrByPrefix("group_");
		return dnListToObjectList(dnResultList, ldapDbAttributes);
	}

	public Set<LdapGroupObject> searchGroups(String pattern) throws NamingException {
		String  command = ((GroupLdapPattern)ldapPattern).getSearchGroupQuery();
		pattern = addExpansionCharacters(pattern);
		lqlctx.getVariables().put("pattern", pattern);
		if (logger.isDebugEnabled()) {
			logLqlQuery(command, pattern);
		}
		// searching ldap directory with pattern
		List<String> dnResultList = this.evaluate(command);
		Map<String, LdapAttribute> ldapDbAttributes = filterAttrByPrefix("group_");
		return dnListToObjectList(dnResultList, ldapDbAttributes);
	}

	public LdapGroupObject loadDnMembers(String groupExternalId) throws NamingException {
		Map<String, LdapAttribute> ldapDbAttributes = filterAttrByPartialString("group_");
		LdapGroupObject object = dnToObject(groupExternalId, ldapDbAttributes);
		return object;
	}

}

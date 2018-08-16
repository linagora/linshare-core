/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPGroupQueryService;
import org.linagora.linshare.ldap.JScriptGroupLdapQuery;
import org.linagora.linshare.ldap.JScriptGroupMemberLdapQuery;
import org.linagora.linshare.ldap.LdapGroupMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.LinShareDnList;
import org.linagora.linshare.ldap.Role;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class LDAPGroupQueryServiceImpl extends LDAPQueryServiceImpl implements LDAPGroupQueryService {

	@Override
	public Set<LdapGroupObject> listGroups(LdapConnection ldapConnection, String baseDn, GroupLdapPattern pattern)
			throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("baseDn", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(pattern.getSearchPageSize(), 0);
		List<LdapGroupObject> list = null;
		try {
			JScriptGroupLdapQuery query = new JScriptGroupLdapQuery(lqlctx, baseDn, pattern, dnList,
					LdapGroupObject.class);
			list = query.searchAllGroups();
		} finally {
			ldapContext.close();
		}
		Function<LdapGroupObject, LdapGroupObject> convert = new Function<LdapGroupObject, LdapGroupObject>() {
			@Override
			public LdapGroupObject apply(LdapGroupObject lgo) {
				lgo.setPrefix(pattern.getGroupPrefix());
				lgo.setRole(Role.READER);
				return lgo.removePrefix();
			}
		};
		return Sets.newHashSet(Lists.transform(list, convert));
	}

	@Override
	public Set<LdapGroupMemberObject> listMembers(LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern pattern, LdapGroupObject group) throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, null).getReadOnlyContext();
		List<LdapGroupMemberObject> res = null;
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("baseDn", baseDn);
		vars.put("logger", logger);
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(pattern.getSearchPageSize(), 0);
		try {
			JScriptGroupLdapQuery query = new JScriptGroupLdapQuery(lqlctx, baseDn, pattern, dnList,
					LdapGroupObject.class);
			LdapGroupObject lgo = query.loadMembers(group);
			JScriptGroupMemberLdapQuery queryMember = new JScriptGroupMemberLdapQuery(lqlctx, baseDn, pattern, dnList,
					LdapGroupMemberObject.class);
			res = queryMember.searchAllGroupMember(lgo.getMembers());
		} finally {
			ldapContext.close();
		}
		Function<LdapGroupMemberObject, LdapGroupMemberObject> convert = new Function<LdapGroupMemberObject, LdapGroupMemberObject>() {
			@Override
			public LdapGroupMemberObject apply(LdapGroupMemberObject lgm) {
				lgm.setRole(group.getRole());
				return lgm;
			}
		};
		return Sets.newHashSet(Lists.transform(res, convert));
	}

}

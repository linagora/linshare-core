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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.linagora.linshare.core.domain.constants.NodeType;
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
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

public class LDAPGroupQueryServiceImpl extends LDAPQueryServiceImpl implements LDAPGroupQueryService {

	@Override
	public Set<LdapGroupObject> listGroups(LdapConnection ldapConnection, String baseDn, GroupLdapPattern groupPattern)
			throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("baseDn", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(groupPattern.getSearchPageSize(), 0);
		Set<LdapGroupObject> list = null;
		try {
			JScriptGroupLdapQuery query = groupQuery(baseDn, groupPattern, lqlctx, dnList);
			list = query.searchAllGroups();
		} finally {
			ldapContext.close();
		}
		return convert(groupPattern, list);
	}

	@Override
	public Set<LdapGroupObject> searchGroups(LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, String pattern) throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("baseDn", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(groupPattern.getSearchPageSize(), 0);
		Set<LdapGroupObject> list = null;
		try {
			JScriptGroupLdapQuery query = groupQuery(baseDn, groupPattern, lqlctx, dnList);
			list = query.searchGroups(pattern);
		} finally {
			ldapContext.close();
		}
		return convert(groupPattern, list);
	}

	@Override
	public Set<LdapGroupMemberObject> listMembers(LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, LdapGroupObject group)
			throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, null).getReadOnlyContext();
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("baseDn", baseDn);
		vars.put("logger", logger);
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(groupPattern.getSearchPageSize(), 0);
		Set<LdapGroupMemberObject> res = Sets.newHashSet();
		try {
			JScriptGroupLdapQuery groupQuery = groupQuery(baseDn, groupPattern, lqlctx, dnList);
			JScriptGroupMemberLdapQuery memberQuery = memberQuery(baseDn, groupPattern, lqlctx, dnList);
			// load writer members
			if (groupQuery.isDnExist(group.getWritersDn())) {
				res.addAll(convert(Role.WRITER, getMembers(group.getWritersDn(), groupQuery, memberQuery)));
			}
			// load contributor members
			if (groupQuery.isDnExist(group.getContributorsDn())) {
				res.addAll(convert(Role.CONTRIBUTOR, getMembers(group.getContributorsDn(), groupQuery, memberQuery)));
			}
			// load read only members
			res.addAll(convert(Role.READER, getMembers(group.getExternalId(), groupQuery, memberQuery)));
		} finally {
			ldapContext.close();
		}
		return res;
	}

	private Set<LdapGroupMemberObject> getMembers(String externalId, JScriptGroupLdapQuery groupQuery,
			JScriptGroupMemberLdapQuery memberQuery) throws NamingException {
		LdapGroupObject lgo = groupQuery.loadDnMembers(externalId);
		Set<LdapGroupMemberObject> list = memberQuery.searchAllGroupMember(lgo);
		return list;
	}

	private JScriptGroupLdapQuery groupQuery(String baseDn, GroupLdapPattern groupPattern, LqlRequestCtx lqlctx,
			IDnList dnList) throws NamingException, IOException {
		JScriptGroupLdapQuery query = new JScriptGroupLdapQuery(lqlctx, baseDn, groupPattern, dnList, LdapGroupObject.class);
		return query;
	}

	private JScriptGroupMemberLdapQuery memberQuery(String baseDn, GroupLdapPattern groupPattern, LqlRequestCtx lqlctx,
			IDnList dnList) throws NamingException, IOException {
		JScriptGroupMemberLdapQuery queryMember = new JScriptGroupMemberLdapQuery(lqlctx, baseDn, groupPattern, dnList,
				LdapGroupMemberObject.class);
		return queryMember;
	}

	private Set<LdapGroupMemberObject> convert(Role role, Set<LdapGroupMemberObject> list) {
		Function<LdapGroupMemberObject, LdapGroupMemberObject> convert = new Function<LdapGroupMemberObject, LdapGroupMemberObject>() {
			@Override
			public LdapGroupMemberObject apply(LdapGroupMemberObject lgm) {
				lgm.setRole(role);
				return lgm;
			}
		};
		FluentIterable<LdapGroupMemberObject> transform = FluentIterable.from(list).transform(convert);
		return transform.toSet();
	}

	private Set<LdapGroupObject> convert(GroupLdapPattern groupPattern, Set<LdapGroupObject> list) {
		Function<LdapGroupObject, LdapGroupObject> convert = new Function<LdapGroupObject, LdapGroupObject>() {
			@Override
			public LdapGroupObject apply(LdapGroupObject lgo) {
				lgo.setPrefix(groupPattern.getGroupPrefix());
				lgo.setRoles(Map.of(NodeType.WORK_GROUP, Role.READER));
				return lgo.removePrefix();
			}
		};
		FluentIterable<LdapGroupObject> transform = FluentIterable.from(list).transform(convert);
		return transform.toSet();
	}
}

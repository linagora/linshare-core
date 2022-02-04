/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPWorkSpaceQueryService;
import org.linagora.linshare.ldap.JScriptWorkSpaceMemberLdapQuery;
import org.linagora.linshare.ldap.JScriptGroupLdapQuery;
import org.linagora.linshare.ldap.LdapWorkSpaceMemberObject;
import org.linagora.linshare.ldap.LdapGroupObject;
import org.linagora.linshare.ldap.LinShareDnList;
import org.linagora.linshare.ldap.Role;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

public class LDAPWorkSpaceQueryServiceImpl extends LDAPGroupQueryServiceImpl implements LDAPWorkSpaceQueryService{

	@Override
	public Set<LdapWorkSpaceMemberObject> listWorkSpaceMembers(LdapConnection ldapConnection, String baseDn,
			GroupLdapPattern groupPattern, LdapGroupObject group)
			throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, null).getReadOnlyContext();
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("baseDn", baseDn);
		vars.put("logger", logger);
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(groupPattern.getSearchPageSize(), 0);
		Set<LdapWorkSpaceMemberObject> res = Sets.newHashSet();
		try {
			JScriptGroupLdapQuery groupQuery = groupQuery(baseDn, groupPattern, lqlctx, dnList);
			JScriptWorkSpaceMemberLdapQuery memberQuery = memberQuery(baseDn, groupPattern, lqlctx, dnList);
			// load writer members and WorkSpace role is not defined
			if (groupQuery.isDnExist(group.getWritersDn()) && !groupQuery.isDnExist(group.getWorkSpaceWritersDn())) {
				res.addAll(convert(Role.WORK_SPACE_READER, Role.WRITER, getMembers(group.getWritersDn(), groupQuery, memberQuery)));
			}
			// load contributor members and WorkSpace role is not defined
			if (groupQuery.isDnExist(group.getContributorsDn()) && !groupQuery.isDnExist(group.getWorkSpaceWritersDn())) {
				res.addAll(convert(Role.WORK_SPACE_READER, Role.CONTRIBUTOR, getMembers(group.getContributorsDn(), groupQuery, memberQuery)));
			}
			// load WorkSpace_writer role and workGroup role is not defined
			if ((!(groupQuery.isDnExist(group.getWritersDn())) || (groupQuery.isDnExist(group.getContributorsDn()))) && groupQuery.isDnExist(group.getWorkSpaceWritersDn())) {
				res.addAll(convert(Role.WORK_SPACE_WRITER, Role.READER, getMembers(group.getWorkSpaceWritersDn(), groupQuery, memberQuery)));
			}
			// load writer members and WorkSpace role is defined
			if (groupQuery.isDnExist(group.getWritersDn()) && groupQuery.isDnExist(group.getWorkSpaceWritersDn())) {
				res.addAll(convert(Role.WORK_SPACE_WRITER, Role.WRITER, getMembers(group.getWritersDn(), groupQuery, memberQuery)));
			}
			// load contributor members and WorkSpace role is defined
			if (groupQuery.isDnExist(group.getContributorsDn()) && groupQuery.isDnExist(group.getWorkSpaceWritersDn())) {
				res.addAll(convert(Role.WORK_SPACE_WRITER, Role.CONTRIBUTOR, getMembers(group.getContributorsDn(), groupQuery, memberQuery)));
			}
			// load read only members
			res.addAll(convert(Role.WORK_SPACE_READER, Role.READER, getMembers(group.getExternalId(), groupQuery, memberQuery)));
		} finally {
			ldapContext.close();
		}
		return res;
	}

	private Set<LdapWorkSpaceMemberObject> getMembers(String externalId, JScriptGroupLdapQuery groupQuery,
			JScriptWorkSpaceMemberLdapQuery memberQuery) throws NamingException {
		LdapGroupObject lgo = groupQuery.loadDnMembers(externalId);
		Set<LdapWorkSpaceMemberObject> list = memberQuery.searchAllGroupMember(lgo);
		return list;
	}

	private JScriptGroupLdapQuery groupQuery(String baseDn, GroupLdapPattern groupPattern, LqlRequestCtx lqlctx,
			IDnList dnList) throws NamingException, IOException {
		JScriptGroupLdapQuery query = new JScriptGroupLdapQuery(lqlctx, baseDn, groupPattern, dnList, LdapGroupObject.class);
		return query;
	}

	private JScriptWorkSpaceMemberLdapQuery memberQuery(String baseDn, GroupLdapPattern groupPattern, LqlRequestCtx lqlctx,
			IDnList dnList) throws NamingException, IOException {
		JScriptWorkSpaceMemberLdapQuery queryMember = new JScriptWorkSpaceMemberLdapQuery(lqlctx, baseDn, groupPattern, dnList,
				LdapWorkSpaceMemberObject.class);
		return queryMember;
	}

	private Set<LdapWorkSpaceMemberObject> convert(Role role, Role nestedRole, Set<LdapWorkSpaceMemberObject> list) {
		Function<LdapWorkSpaceMemberObject, LdapWorkSpaceMemberObject> convert = new Function<LdapWorkSpaceMemberObject, LdapWorkSpaceMemberObject>() {
			@Override
			public LdapWorkSpaceMemberObject apply(LdapWorkSpaceMemberObject lgm) {
				lgm.setRole(role);
				lgm.setNestedRole(nestedRole);
				return lgm;
			}
		};
		FluentIterable<LdapWorkSpaceMemberObject> transform = FluentIterable.from(list).transform(convert);
		return transform.toSet();
	}
}

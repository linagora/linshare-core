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
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPUserQueryService;
import org.linagora.linshare.ldap.JScriptUserLdapQuery;
import org.linagora.linshare.ldap.LinShareDnList;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPUserQueryServiceImpl extends LDAPQueryServiceImpl implements LDAPUserQueryService {

	private static final Logger logger = LoggerFactory.getLogger(LDAPUserQueryServiceImpl.class);

	public LDAPUserQueryServiceImpl() {
		super();
	}

	@Override
	public User searchForAuth(LdapConnection ldapConnection, String baseDn,
			UserLdapPattern domainPattern, String userLogin)
			throws NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getSearchPageSize(), domainPattern.getSearchSizeLimit());

		logger.debug("LDAPQueryServiceImpl.authUser: baseDn: '" + baseDn + "' , login : '" + userLogin + "'");
		User user = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			user = query.searchForAuth(ldapConnection, userLogin);
		} finally {
			ldapContext.close();
		}
		return user;
	}

	@Override
	public User auth(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String userLogin, String userPasswd) throws NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getSearchPageSize(), domainPattern.getSearchSizeLimit());

		logger.debug("LDAPQueryServiceImpl.authUser: baseDn: '" + baseDn + "' , login : '" + userLogin + "'");
		User user = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			// this coulds throw BadCredentialsException.
			user = query.auth(ldapConnection, userLogin, userPasswd);
		} finally {
			ldapContext.close();
		}
		return user;
	}

	@Override
	public User getUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String mail) throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getSearchPageSize(), domainPattern.getSearchSizeLimit());

		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDn + "' , motif (mail) : '" + mail + "'");
		User user = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			user = query.findUser(mail);
		} finally {
			ldapContext.close();
		}
		return user;
	}

	@Override
	public List<User> searchUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String mail, String first_name, String last_name) throws BusinessException,
			NamingException, IOException {

		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getSearchPageSize(), domainPattern.getSearchSizeLimit());

		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDn + "' , motif (mail) : '" + mail + "'");
		List<User> list = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			list = query.searchUser(mail, first_name, last_name);
		} finally {
			ldapContext.close();
		}
		return list;
	}

	@Override
	public List<User> completeUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String pattern) throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getCompletionPageSize(), domainPattern.getCompletionSizeLimit());

		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDn + "' , motif (pattern) : '" + pattern + "'");
		List<User> list = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			list = query.complete(pattern);
		} finally {
			ldapContext.close();
		}
		return list;
	}

	@Override
	public List<User> completeUser(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String first_name, String last_name) throws BusinessException, NamingException,
			IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getCompletionPageSize(), domainPattern.getCompletionSizeLimit());

		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDn + "' , motif (firstName lastName) : '" + first_name + "' et '" + last_name + "'");
		List<User> list = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			list = query.complete(first_name, last_name);
		} finally {
			ldapContext.close();
		}
		return list;
	}

	@Override
	public Boolean isUserExist(LdapConnection ldapConnection, String baseDn, UserLdapPattern domainPattern, String mail) throws BusinessException, NamingException, IOException {
		LdapContext ldapContext = (LdapContext) getLdapContext(ldapConnection, baseDn).getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDn);
		vars.put("logger", logger);

		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPattern.getCompletionPageSize(), domainPattern.getCompletionSizeLimit());

		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDn + "' , motif (mail) : '" + mail + "'");
		Boolean userExist = null;
		try {
			JScriptUserLdapQuery query = new JScriptUserLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			userExist = query.isUserExist(mail);
		} finally {
			ldapContext.close();
		}
		return userExist;
	}
}
/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.linagora.linshare.ldap.JScriptLdapQuery;
import org.linagora.linshare.ldap.LinShareDnList;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;

public class LDAPQueryServiceImpl implements LDAPQueryService {

	private static final Logger logger = LoggerFactory.getLogger(LDAPQueryServiceImpl.class);

	public LDAPQueryServiceImpl() {
		super();
	}

	private ContextSource getLdapContext(LdapConnection ldapConnection, String baseDn) {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapConnection.getProviderUrl());
		ldapContextSource.setBase(baseDn);
		String userDn = ldapConnection.getSecurityPrincipal();
		String password = ldapConnection.getSecurityCredentials();
		if (userDn != null && password != null) {
			ldapContextSource.setUserDn(userDn);
			ldapContextSource.setPassword(password);
		}

		try {
			ldapContextSource.afterPropertiesSet();
			return ldapContextSource;
		} catch (Exception e) {
			logger.error("Can not set ldap context");
			return null;
		}
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
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
			JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPattern, dnList);
			userExist = query.isUserExist(mail);
		} finally {
			ldapContext.close();
		}
		return userExist;
	}
}
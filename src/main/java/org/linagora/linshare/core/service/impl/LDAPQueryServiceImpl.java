/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.linagora.linshare.ldap.JScriptLdapQuery;
import org.linid.dm.authorization.lql.JScriptEvaluator;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.DnList;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
//import org.linagora.linshare.ldap.JScriptEvaluator;
//import org.linagora.linshare.ldap.JScriptLdapQuery;

public class LDAPQueryServiceImpl implements LDAPQueryService {
	
	private static final Logger logger = LoggerFactory.getLogger(LDAPQueryServiceImpl.class);
	
	private DomainPattern domainPatternHack;
	
//	public LDAPQueryServiceImpl(IDnList dnList) {
//		super();
//		this.dnList = dnList;
//	}
	
	
	public LDAPQueryServiceImpl() {
		super();
		this.dnList = new DnList();
		/* HACK */
		Map<String, LdapAttribute>  attributes = new HashMap<String, LdapAttribute>();
		attributes.put(DomainPattern.USER_MAIL, new LdapAttribute(DomainPattern.USER_MAIL, "mail"));
		attributes.put(DomainPattern.USER_FIRST_NAME, new LdapAttribute(DomainPattern.USER_FIRST_NAME, "givenName"));
		attributes.put(DomainPattern.USER_LAST_NAME, new LdapAttribute(DomainPattern.USER_LAST_NAME, "sn"));
		attributes.put(DomainPattern.USER_UID, new LdapAttribute(DomainPattern.USER_UID, "uid"));
		
		this.domainPatternHack= new DomainPattern("testPattern", "testPattern", 
				" ", 
				" ", 
				"ldap.search(domain, \"(&(objectClass=obmUser)(givenName=*)(sn=*)(mail=\"+login+\"))\");", // auth command
				"ldap.search(domain, \"(&(objectClass=obmUser)(mail=\"+mail+\")(givenName=*)(sn=*))\");", // search command 
				attributes,
				"ldap.search(domain, \"(&(objectClass=obmUser)(|(mail=\"+pattern+\")(givenName=\"+pattern+\")(sn=\"+pattern+\"))(mail=*)(givenName=*)(sn=*))\");", // auto complete command
				false);
	}
	
	/** The local LDAP facade used to evaluate LQL requests */
	private ContextSource ldapContext;

	/** Local LDAP cache */
	private IDnList dnList; 
	
	private ContextSource getLdapContext() {
        return ldapContext;
	}

	public void setLdapContext(ContextSource ldapContext) {
		this.ldapContext = ldapContext;
	}
	
	public void setDnList(IDnList dnList) {
		this.dnList = dnList;
	}
	
	
			
	@Override
	public User auth(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String userId, String userPasswd) throws BusinessException, NamingException, IOException {
		List<User> searchUser = this.searchUser(ldapConnection, baseDn, domainPatternHack, userId);
		return searchUser.get(0);
	}

	@Override
	public List<User> searchUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String mail, String firstName, String lastName) throws BusinessException, NamingException,
			IOException {
		return this.searchUser(ldapConnection, baseDn, domainPatternHack, mail);
	}

	@Override
	public List<User> searchUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String mail) throws BusinessException, NamingException,
			IOException {
		
		LdapContext ldapContext = (LdapContext)getLdapContext().getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("login", mail);
		vars.put("domain", baseDn);
		
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		
		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDn + "' , motif (mail) : '" + mail + "'");
		JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDn, domainPatternHack, dnList);
		return query.searchUserFred(mail);
	}

	@Override
	public List<User> completeByMail(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String mail) throws BusinessException, NamingException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
}

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
import org.linagora.linshare.ldap.LinShareDnList;
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
	
	private String baseDnHack;
	
//	public LDAPQueryServiceImpl(IDnList dnList) {
//		super();
//		this.dnList = dnList;
//	}
	
	
	public LDAPQueryServiceImpl() {
		super();
//		this.dnList = new LinShareDnList();
//		this.dnList = new DnList();
		/* HACK */
//		initObm1();
		initInsee();
		
		
	}
	
	private void initObm1() {
		Map<String, LdapAttribute>  attributes = new HashMap<String, LdapAttribute>();
		attributes.put(DomainPattern.USER_MAIL, new LdapAttribute(DomainPattern.USER_MAIL, "mail"));
		attributes.put(DomainPattern.USER_FIRST_NAME, new LdapAttribute(DomainPattern.USER_FIRST_NAME, "givenName"));
		attributes.put(DomainPattern.USER_LAST_NAME, new LdapAttribute(DomainPattern.USER_LAST_NAME, "sn"));
		attributes.put(DomainPattern.USER_UID, new LdapAttribute(DomainPattern.USER_UID, "uid"));

		
//		complete on mail
		String a ="(|(mail=pattern)(first=pattern)(last=pattern))";
//		complete on first and/or last)
		// pattern{1,2} = pattern.split(' ')
//		String b ="(&(mail=*)(|(first=pattern1)(last=pattern2))(|(first=pattern2)(last=pattern1)))";
		String b ="(&(mail=*)(|(first=pattern1)(last=pattern2))(|(first=pattern2)(last=pattern1)))";

		
		
		String autocomplete1 = "ldap.search(domain, \"(&(objectClass=obmUser)(|(mail=\"+pattern+\")(givenName=\"+pattern+\")(sn=\"+pattern+\"))(mail=*)(givenName=*)(sn=*))\");";
//		"(&(objectClass=inetOrgPerson)(mail=${pattern1})(|(&(givenName=${pattern2})(sn=${pattern3}))(&(givenName=${pattern3})(sn=${pattern2}))))"
		String autocomplete2="ldap.search(domain, \"&(objectClass=inetOrgPerson)(mail=\" + mail + \")(|(&(givenName=\" + first_or_last_name1 + \")(sn=\" + first_or_last_name2 + \"))(&(givenName=\" + first_or_last_name2 + \")(sn=\" + first_or_last_name1 + \"))))\")";
		this.domainPatternHack= new DomainPattern("testPattern", "testPattern", 
				" ", 
				" ", 
				"ldap.search(domain, \"(&(objectClass=obmUser)(givenName=*)(sn=*)(mail=\"+login+\"))\");", // auth command
				"ldap.search(domain, \"(&(objectClass=obmUser)(mail=\"+mail+\")(givenName=*)(sn=*))\");", // search command 
				attributes,
				autocomplete2,// auto complete command
				false);
		
		baseDnHack = "ou=users,dc=int1.linshare.dev,dc=local";
	}
	
	private void initInsee() {
		Map<String, LdapAttribute>  attributes = new HashMap<String, LdapAttribute>();
		attributes.put(DomainPattern.USER_MAIL, new LdapAttribute(DomainPattern.USER_MAIL, "mail"));
		attributes.put(DomainPattern.USER_FIRST_NAME, new LdapAttribute(DomainPattern.USER_FIRST_NAME, "sn"));
		attributes.put(DomainPattern.USER_LAST_NAME, new LdapAttribute(DomainPattern.USER_LAST_NAME, "cn"));
		attributes.put(DomainPattern.USER_UID, new LdapAttribute(DomainPattern.USER_UID, "uid"));
		
		String autocomplete1 = null;
		String autocomplete2 = null;
		
		autocomplete1="ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=\" + mail_pattern + \")" +
				"(|(&(sn=\" + first_or_last_name1 + \")(cn=\" + first_or_last_name2 + \"))(&(sn=\" + first_or_last_name2 + \")(cn=\" + first_or_last_name1 + \"))))\")";
		
		autocomplete1="ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=*)(cn=*)(sn=*)" +
				"(|(&(sn=\" + first_or_last_name1 + \")(cn=\" + first_or_last_name2 + \"))" + // "first_name last_name"
				"  (&(sn=\" + first_or_last_name2 + \")(cn=\" + first_or_last_name1 + \"))" + // "last_name first_name"
				"  (|(mail=\" + mail_pattern + \")(sn=\" + first_or_last_name1 + \"))" +	  // "mail first_name
				"))\")";
		
		autocomplete1="ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=*)(cn=*)(sn=*)" +
				"(|(&(sn=\" + first_or_last_name1 + \")(cn=\" + first_or_last_name2 + \"))" + // "first_name last_name"
				"  (&(sn=\" + first_or_last_name2 + \")(cn=\" + first_or_last_name1 + \"))" + // "last_name first_name"
				"  (|(mail=\" + mail_pattern + \")(sn=\" + first_or_last_name1 + \"))" +	  // "mail first_name
				"))\")";
		
		autocomplete1="ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=*)(cn=*)(sn=*)" +
					"(|" +
						"(mail=\" + pattern + \")" +
						"(sn=\" + pattern + \")" +
						"(cn=\" + pattern + \")" +
					")" +
				")\")";
		
		autocomplete2="ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=*)(cn=*)(sn=*)" +
				"(|" +
					"(&(sn=\" + first_name + \")(cn=\" + last_name + \"))" + // "first_name last_name"
					"(&(sn=\" + last_name + \")(cn=\" + first_name + \"))" + // "last_name first_name"
				"))\")";
		
		
		this.domainPatternHack= new DomainPattern("testPattern", "testPattern", 
				" ", 
				" ", 
				"ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=\"+login+\"))\");", // auth command
				"ldap.search(domain, \"(&(objectClass=inetOrgPerson)(mail=\"+mail+\"))\");", // search command
				500,
				2000,
				attributes,
//				"ldap.search(domain, \"(&(objectClass=obmUser)(|(mail=\"+pattern+\")(cn=\"+pattern+\")(sn=\"+pattern+\"))(mail=*)(cn=*)(sn=*))\");", // auto complete command
				autocomplete1,
				autocomplete2,
				20,
				20,
				false);
		
		baseDnHack = "ou=People,o=insee,c=fr";
	}
	
	/** The local LDAP facade used to evaluate LQL requests */
	private ContextSource ldapContext;

	/** Local LDAP cache */
//	private IDnList dnList; 
	
	private ContextSource getLdapContext() {
        return ldapContext;
	}

	public void setLdapContext(ContextSource ldapContext) {
		this.ldapContext = ldapContext;
	}
	
//	public void setDnList(IDnList dnList) {
//		this.dnList = dnList;
//	}
	
			
	@Override
	public User auth(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String userId, String userPasswd) throws BusinessException, NamingException, IOException {
		List<User> searchUser = this.searchUser(ldapConnection, baseDnHack, domainPatternHack, userId);
		return searchUser.get(0);
	}

	@Override
	public List<User> searchUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String mail, String firstName, String lastName) throws BusinessException, NamingException,
			IOException {
		return this.searchUser(ldapConnection, baseDnHack, domainPatternHack, mail);
	}

	@Override
	public List<User> searchUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String mail) throws BusinessException, NamingException,
			IOException {
		
		LdapContext ldapContext = (LdapContext)getLdapContext().getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("login", mail);
		vars.put("domain", baseDnHack);
		
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPatternHack.getSearchPageSize(), domainPatternHack.getSearchSizeLimit());
		
		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDnHack + "' , motif (mail) : '" + mail + "'");
		JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDnHack, domainPatternHack, dnList);
		return query.searchUserFred(mail);
	}

	@Override
	public List<User> completeUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String pattern) throws BusinessException,
			NamingException, IOException {
		LdapContext ldapContext = (LdapContext)getLdapContext().getReadOnlyContext();

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDnHack);
		vars.put("login", pattern);
		
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPatternHack.getCompletionPageSize(), domainPatternHack.getCompletionSizeLimit());
		
		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDnHack + "' , motif (pattern) : '" + pattern + "'");
		JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDnHack, domainPatternHack, dnList);
		return query.complete(pattern);
	}
	
	@Override
	public List<User> completeUser(LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern, String first_name, String last_name) throws BusinessException,
	NamingException, IOException {
		LdapContext ldapContext = (LdapContext)getLdapContext().getReadOnlyContext();
		
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("domain", baseDnHack);
		
		LqlRequestCtx lqlctx = new LqlRequestCtx(ldapContext, vars, true);
		IDnList dnList = new LinShareDnList(domainPatternHack.getCompletionPageSize(), domainPatternHack.getCompletionSizeLimit());
		
		logger.debug("LDAPQueryServiceImpl.searchUser: baseDn: '" + baseDnHack + "' , motif (firstName lastName) : '" + first_name + "' et '" + last_name + "'");
		JScriptLdapQuery query = new JScriptLdapQuery(lqlctx, baseDnHack, domainPatternHack, dnList);
		return query.complete(first_name, last_name);
	}
	
	
}

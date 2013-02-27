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
package org.linagora.linshare.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;

import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameNotFoundException;

public class JScriptLdapQuery {
	
	/** 	Attributes 		**/
	
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(JScriptEvaluator.class);
	private static final String LOG_INFO_KEY_PARSE = "Keys parsed which will be used in LDAP query are:";
	
	// Java to JavaScript Translator object
	private JScriptEvaluator jScriptEvaluator;
	
	// Ladp Jndi Service
	private JndiServices ldapJndiService;
	
	private LDAPConnection ldapConnection;
	
	private String baseDn;
	
	private DomainPattern domainPattern;

	
	/**
	 * @param jScriptEvaluator
	 * @param ldapJndiService
	 * @param ldapConnection
	 * @param baseDn
	 * @param domainPattern
	 */
	public JScriptLdapQuery(JScriptEvaluator jScriptEvaluator, JndiServices ldapJndiService, LDAPConnection ldapConnection,	String baseDn, DomainPattern domainPattern) throws NamingException, IOException {
		super();
		this.jScriptEvaluator = jScriptEvaluator;
		this.ldapJndiService = ldapJndiService;
		this.ldapConnection = ldapConnection;
		this.baseDn = baseDn;
		this.domainPattern = domainPattern;
		
		if(ldapJndiService == null) {
			try {
				this.ldapJndiService = new JndiServices(ldapConnection.toLdapProperties());
			} catch (NamingException e) {
				logger.error("Error while connecting to {}", ldapConnection.getProviderUrl());
				logger.error(e.toString());
				if (!(e instanceof ServiceUnavailableException)) {
					logger.debug(e.getMessage());
				}
				throw e;
			} catch (IOException e) {
				logger.error("Error while connecting to {}", ldapConnection.getProviderUrl());
				logger.error(e.toString());
				logger.debug(e.getMessage());
				throw e;
			}
		} else {
			this.ldapJndiService = ldapJndiService;	
		}
		this.jScriptEvaluator.setJndiService(this.ldapJndiService);
	}
	
	public JScriptLdapQuery(JScriptEvaluator jScriptEvaluator, LDAPConnection ldapConnection, String baseDn, DomainPattern domainPattern) throws NamingException, IOException {
			this(jScriptEvaluator, null, ldapConnection, baseDn, domainPattern);
	}

	/** 	Methods 	**/
	

	/**
	 * This method allow to search a user from part of his mail or/and first name or/and last name 
	 * @param mail
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public List<User> searchUser(String mail, String firstName,	String lastName) {
		
		String command = domainPattern.getSearchUserCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mail", addExpansionCharacters(mail));
		params.put("firstName", addExpansionCharacters(firstName));
		params.put("lastName", addExpansionCharacters(lastName));
		params.put("domain", baseDn);
		List<String> uidList = jScriptEvaluator.evalToStringList(command, params);
		if(uidList == null) {
			logger.error("searchUser:The uidList is null.");
		}
        return dnListToUsersList(uidList);
	}
		
	
	/**
	 * Ldap Authentification method
	 * @param login
	 * @param userPasswd
	 * @return
	 * @throws NamingException
	 */
	public User auth(String login, String userPasswd) throws NamingException {
	
		String command = domainPattern.getAuthCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login", login);
		params.put("domain", baseDn);
		List<String> retList = jScriptEvaluator.evalToStringList(command, params);
		
		
		if (retList == null || retList.size() < 1) {
			throw new NameNotFoundException("No user found for login: "+login);
		} else if (retList.size() > 1) {
			logger.error("The authentification query had returned more than one user !!!");
			return null;
		}
		
		
		String dn = retList.get(0);
		if(ldapJndiService.auth(userPasswd, dn)) {
			return dnToUser(dn);
		}
		return null;
	}
		
	/**
	 * This method is designed to add expansion characters to the input string. 
	 * @param string : any string
	 * @return 
	 */
	private String addExpansionCharacters(String string) {
		if (string==null || string.length()<1){
			string="*";
		} else {
			string = "*" + string + "*";
		}
		return string;
	}

	
	/**
	 * Create an user list from an dn list 
	 * @param dnList
	 * @return
	 */
	private List<User> dnListToUsersList(List<String> dnList) {
		List<User> users = new ArrayList<User>();
		for (String string : dnList) {
			User user = dnToUser(string);
			if(user != null) {
				users.add(user);
			}
		}
		return users;
	}

	/**
	 * This method retrieve user informations from user ldap dn and build an user object.  
	 * @param keys : Ldap user attribute list
	 * @param string : Distinguish Name
	 * @return
	 */
	private User dnToUser(String dn) {
		String unitCommand = "ldap.entry(dn,\"objectClass=*\");";
		Map<String, Object> unitParams = new HashMap<String, Object>();
		unitParams.put("dn", dn);
		Map<String, List<String>> retMap = jScriptEvaluator.evalToEntryMap(unitCommand, unitParams);
		if(retMap == null) {
			logger.error("dnToUser: retMap is null");
			return null;
		}
		return mapToUser(retMap);
	}

	/**
	 * This method is designed to build a User object from a Ldap entry result.
	 * @param retMap
	 * @param keys
	 * @return
	 */
	private User mapToUser(Map<String, List<String>> retMap) {
		String mail = retMap.get(getUserMail()).get(0);
    	String firstName = retMap.get(getUserFirstName()).get(0);
        String lastName = retMap.get(getUserLastName()).get(0);
        String ldapUid = retMap.get(getLdapUid()).get(0);
        User user = new Internal(firstName, lastName, mail, ldapUid);
		return user;
	}

	private Object getLdapUid() {
		return domainPattern.getAttribute(DomainPattern.USER_UID).trim().toLowerCase();
	}

	private String getUserMail() {
		return domainPattern.getAttribute(DomainPattern.USER_MAIL).trim().toLowerCase();
	}
	
	private String getUserFirstName(){
		return domainPattern.getAttribute(DomainPattern.USER_FIRST_NAME).trim().toLowerCase();
	}
	
	private String getUserLastName(){
		return domainPattern.getAttribute(DomainPattern.USER_LAST_NAME).trim().toLowerCase();
	}

	
	@Override
	protected void finalize() throws Throwable {
		jScriptEvaluator.setJndiService(null);
		jScriptEvaluator=null;
		ldapJndiService = null;
		super.finalize();
	}
}

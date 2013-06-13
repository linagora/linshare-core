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
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.User;
import org.linid.dm.authorization.lql.JScriptEvaluator;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameNotFoundException;

public class JScriptLdapQuery {
	
	/** 	Attributes 		**/
	
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(JScriptLdapQuery.class);
	
	// Java to JavaScript Translator object
	private JScriptEvaluator evaluator;
	
	private String baseDn;
	
	private DomainPattern domainPattern;
	
	private LqlRequestCtx lqlctx;

	private IDnList dnList;
	
	/**
	 * @param jScriptEvaluator
	 * @param ldapJndiService
	 * @param ldapConnection
	 * @param baseDn
	 * @param domainPattern
	 */
	public JScriptLdapQuery(LqlRequestCtx ctx, String baseDn, DomainPattern domainPattern, IDnList dnList) throws NamingException, IOException {
		super();
		this.lqlctx = ctx;
		this.evaluator = JScriptEvaluator.getInstance(ctx.getLdapCtx(), dnList);
		this.baseDn = baseDn;
		this.domainPattern = domainPattern;
	}
	/** 	Methods 	**/
	

	public List<String> evaluate(String lqlExpression, Map<String, Object> attributes) throws NamingException {
		try {
			JScriptEvaluator evaluator = JScriptEvaluator.getInstance(lqlctx.getLdapCtx(), dnList);
//			return evaluator.evalToStringList(lqlExpression, lqlctx.getVariables());
			return evaluator.evalToStringList(lqlExpression, attributes);
		} catch (IOException e) {
			try {
				lqlctx.renewLdapCtx();
			} catch (NamingException e1) {
				return null;
			}
			return evaluate(lqlExpression, attributes);
		}
	}
	
	/**
	 * 
	 * @param pattern : could be first name, surname, or mail fragment.
	 * @return
	 * @throws NamingException
	 */
	public List<User> complete(String pattern) throws NamingException {
		
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("pattern", addExpansionCharacters(pattern));
		vars.put("domain", baseDn);
		
		
		// searching ldap directory with pattern 
		List<String> results = this.evaluate(domainPattern.getAutoCompleteCommand(), vars);

		

		// converting resulting dn to User object 
		Map<String, LdapAttribute> attributes = domainPattern.getAttributes();
		List<User> users = new ArrayList<User>();
		for (String string : results) {
			logger.info("FREd: " + string);
			String dn = string +","+ baseDn;
			users.add(dnToUserFred(lqlctx, dnList, dn, attributes));
		}
		return users;
	}
	
	
	/**
	 * 
	 * @param mail : mail fragment
	 * @return
	 * @throws NamingException
	 */
	public List<User> searchUserFred(String mail) throws NamingException {

		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("mail", addExpansionCharacters(mail));
		vars.put("domain", baseDn);
		
		List<String> results = this.evaluate(domainPattern.getSearchUserCommand(), vars);
		
		Map<String, LdapAttribute> attributes = domainPattern.getAttributes();
		List<User> users = new ArrayList<User>();
		for (String string : results) {
			logger.info("FREd: " + string);
			String dn = string +","+ baseDn;
			User user = dnToUserFred(lqlctx, dnList, dn, attributes);
			if(user != null)	users.add(user);
		}
		return users;
	}


	
	private User dnToUserFred(LqlRequestCtx ctx, IDnList dnList, String dn, Map<String, LdapAttribute> attributes) {
		JScriptEvaluator evaluator;
		try {
			// getting evaluator with cache for dn: we don't get dn here, just attributes. no need.
			evaluator = JScriptEvaluator.getInstance(ctx.getLdapCtx(), null);
			
			Map<String, Object> unitParams = new HashMap<String, Object>();
			unitParams.put("dn", dn);
			
			User user = new Internal();;
			for (String key : attributes.keySet()) {
				logger.info("Key = " + key);
			    LdapAttribute attr = attributes.get(key);
			    String unitCommand = "ldap.attribute(dn, \"" + attr.getAttribute() + "\");";
			    logger.info("unitCommand : " + unitCommand);
			
			    List<String> stringList = evaluator.evalToStringList(unitCommand, unitParams);
			    for (String string : stringList) {
			    	logger.info("value of attribute : " + attr.getAttribute() + " : " + string);
			    }

			    if(key == "user_lastname") {
			    	user.setLastName(stringList.get(0));
			    } else if(key == "user_firstname") {
			    	user.setFirstName(stringList.get(0));
			    } else if(key == "user_mail") {
			    	user.setMail(stringList.get(0));
			    } else if(key == "user_uid") {
			    	user.setLdapUid(stringList.get(0));
			    }
			}
			return user;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
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
		List<String> uidList = evaluator.evalToStringList(command, params);
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
		List<String> retList = evaluator.evalToStringList(command, params);
		
		
		if (retList == null || retList.size() < 1) {
			throw new NameNotFoundException("No user found for login: "+login);
		} else if (retList.size() > 1) {
			logger.error("The authentification query had returned more than one user !!!");
			return null;
		}
		
		//TODO FRED
//		String dn = retList.get(0);
//		if(ldapJndiService.auth(userPasswd, dn)) {
//			return dnToUser(dn);
//		}
		return null;
	}
		
	/**
	 * This method is designed to add expansion characters to the input string. 
	 * @param string : any string
	 * @return 
	 */
	private String addExpansionCharacters(String string) {
		if (string==null || string.length()<1){
			string="";
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
		// Config map.
		Map<String, Object> unitParams = new HashMap<String, Object>();
		unitParams.put("dn", dn);
		// result map : attribute => values
		Map<String, List<String>> retMap = new HashMap<String, List<String>>();
		
		for (String key : domainPattern.getAttributes().keySet()) {
			String attr = domainPattern.getAttributes().get(key).getAttribute();
			String unitCommand = "ldap.attribute(dn, \"" + attr + "\");";
			logger.debug("unitCommand : " + unitCommand);
			List<String> attrValues = evaluator.evalToStringList(unitCommand, unitParams);
			if(logger.isDebugEnabled()) logger.debug("Attribute values : " + String.valueOf(attrValues));
			retMap.put(key, attrValues);
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
        String ldapUid = null;
        List<String> uids = retMap.get(getLdapUid());
        if (uids != null && ! uids.isEmpty())		ldapUid = uids.get(0);
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
}

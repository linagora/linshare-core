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
package org.linagora.linshare.ldap;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linid.dm.authorization.lql.JScriptEvaluator;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.LdapUserSearch;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class JScriptLdapQuery {

	/** Attributes **/

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(JScriptLdapQuery.class);
	
	private JScriptEvaluator evaluator;
	
	private String baseDn;

	private UserLdapPattern domainPattern;

	private LqlRequestCtx lqlctx;

	private IDnList dnList;

	private BeanInfo beanInfo;

	private Pattern cleaner;

	/**
	 * @param jScriptEvaluator
	 * @param ldapJndiService
	 * @param ldapConnection
	 * @param baseDn
	 * @param domainPattern
	 */
	public JScriptLdapQuery(LqlRequestCtx ctx, String baseDn, UserLdapPattern domainPattern, IDnList dnList) throws NamingException, IOException {
		super();
		this.lqlctx = ctx;
		this.evaluator = JScriptEvaluator.getInstance(ctx.getLdapCtx(), dnList);
		this.baseDn = baseDn;
		this.domainPattern = domainPattern;
		this.cleaner = Pattern.compile("[;,!|*()&]");

		try {
			this.beanInfo = Introspector.getBeanInfo(Internal.class);
		} catch (IntrospectionException e) {
			logger.error("Introspection of Internal user class impossible.");
			logger.debug("message : " + e.getMessage());
		}
	}

	public String cleanLdapInputPattern(String pattern) {
		return cleaner.matcher(pattern).replaceAll("");
	}

	/** Methods **/

	public List<String> evaluate(String lqlExpression) throws NamingException {
		try {
			Date date_before = new Date();
			evaluator = JScriptEvaluator.getInstance(lqlctx.getLdapCtx(), dnList);
			List<String> evalToStringList = evaluator.evalToStringList(lqlExpression, lqlctx.getVariables());
			if (logger.isDebugEnabled()) {
				Date date_after = new Date();
				logger.debug("diff : " + String.valueOf(date_after.getTime() - date_before.getTime()));
			}
			return evalToStringList;
		} catch (IOException e) {
			try {
				lqlctx.renewLdapCtx();
			} catch (NamingException e1) {
				return null;
			}
			return evaluate(lqlExpression);
		}
	}

	private void logLqlQuery(String command, String pattern) {
		if (logger.isDebugEnabled()) {
			logger.debug("lql command " + command);
			logger.debug("pattern: " + pattern);
			String cmd = command.replaceAll("\"[ ]*[+][ ]*pattern[ ]*[+][ ]*\"", pattern);
			cmd = cmd.replaceAll("\"[ ]*[+][ ]*mail[ ]*[+][ ]*\"", pattern);
			logger.debug("ldap filter : " + cmd);
		}
	}

	private void logLqlQuery(String command, String mail, String first_name, String last_name) {
		if (logger.isDebugEnabled()) {
			logger.debug("lql command " + command);
			logger.debug("first_name: " + first_name);
			logger.debug("last_name: " + last_name);
			String cmd = command.replaceAll("\"[ ]*[+][ ]*last_name[ ]*[+][ ]*\"", last_name);
			cmd = cmd.replaceAll("\"[ ]*[+][ ]*first_name[ ]*[+][ ]*\"", first_name);
			if (mail != null) {
				cmd = cmd.replaceAll("\"[ ]*[+][ ]*mail[ ]*[+][ ]*\"", mail);
			}
			logger.debug("ldap filter : " + cmd);
		}
	}

	private void logLqlQuery(String command, String first_name, String last_name) {
		logLqlQuery(command, null, first_name, last_name);
	}

	/**
	 * 
	 * @param pattern
	 *            : could be first name, last name, or mail fragment.
	 * @return
	 * @throws NamingException
	 */
	public List<User> complete(String pattern) throws NamingException {

		// Getting lql expression for completion
		String command = domainPattern.getAutoCompleteCommandOnAllAttributes();
		pattern = addExpansionCharacters(pattern);

		// Setting lql query parameters
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("pattern", pattern);
		if (logger.isDebugEnabled())
			logLqlQuery(command, pattern);

		// searching ldap directory with pattern
		List<String> dnResultList = this.evaluate(command);

		// Building user list from dn (getting needed attributes)
		return dnListToUsersList(dnResultList, true);
	}

	/**
	 * Looking for a user using first name and last name. Only for auto-complete.
	 * @param first_name
	 * @param last_name
	 * @return
	 * @throws NamingException
	 */
	public List<User> complete(String first_name, String last_name) throws NamingException {

		// Getting lql expression for completion
		String command = domainPattern.getAutoCompleteCommandOnFirstAndLastName();
		first_name = addExpansionCharacters(first_name);
		last_name = addExpansionCharacters(last_name);

		// Setting lql query parameters
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("first_name", first_name);
		vars.put("last_name", last_name);
		logLqlQuery(command, first_name, last_name);

		// searching ldap directory with pattern
		List<String> dnResultList = this.evaluate(command);

		return dnListToUsersList(dnResultList, true);
	}

	/**
	 * This function build user list from input dn list
	 * @param dnResultList
	 *            : // list of dn without baseDn used by the previous search.
	 * @param completionMode
	 *            : completion mode return a user object with only mail,
	 *            first name, and last name set. Otherwise all defined attributes
	 *            will be search and set (mail, firstname, lastname, uid, ...)
	 * @return List of user
	 */
	private List<User> dnListToUsersList(List<String> dnResultList, boolean completionMode) {
		ControlContext controlContext = initControlContext(completionMode);

		// converting resulting dn to User object
		List<User> users = new ArrayList<User>();
		for (String dn : dnResultList) {
			logger.debug("current dn: " + dn);
			Date date_before = new Date();
			User user = null;
			try {
				user = dnToUser(dn, controlContext.getLdapDbAttributes(), controlContext.getSearchControls());
			} catch (NamingException e) {
				logger.error(e.getMessage());
				logger.debug(e.toString());
			}
			Date date_after = new Date();
			logger.debug("fin dnToUser : " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}

	/**
	 * This function build user from input dn
	 * @param dn
	 * @param completionMode
	 * @return
	 * @throws NamingException
	 */
	private User dnToUser(String dn, boolean completionMode) throws NamingException {
		ControlContext controlContext = initControlContext(completionMode);
		return dnToUser(dn, controlContext.getLdapDbAttributes(), controlContext.getSearchControls());
	}

	private ControlContext initControlContext(boolean completionMode) {
		// Initialization of bean introspection
		if (beanInfo == null) {
			logger.error("Introspection of Internal user class impossible. Bean inspector is not initialised.");
			return null;
		}

		// Get only ldap attributes needed for completion
		Map<String, LdapAttribute> ldapDbAttributes;
		if (completionMode) {
			// Get only ldap attributes needed for completion
			ldapDbAttributes = getLdapDbAttributeForCompletion();
		} else {
			// Get only ldap attributes
			ldapDbAttributes = getLdapDbAttribute();
		}

		// String list of ldap attributes
		Collection<String> ldapAttrList = getLdapAttrList(ldapDbAttributes);

		// ldapContext ldapCtx, String base, String filter, int scope)
		SearchControls scs = new SearchControls();
		scs.setSearchScope(SearchControls.OBJECT_SCOPE);

		// Attributes to retrieve from ldap.
		logger.debug("ldap attributes to retrieve : " + ldapAttrList.toString());
		scs.setReturningAttributes(ldapAttrList.toArray(new String[ldapAttrList.size()]));
		return new ControlContext(ldapDbAttributes, scs);
	}

	private User dnToUser(String dn, Map<String, LdapAttribute> ldapDbAttributes, SearchControls scs) throws NamingException {
		// returned value
		User user = new Internal();
		NamingEnumeration<SearchResult> results = lqlctx.getLdapCtx().search(dn, "(objectclass=*)", scs);
		Integer cpt = new Integer(0);
		while (results != null && results.hasMore()) {
			cpt += 1;
			SearchResult entry = (SearchResult) results.next();
			logger.debug("processing result : " + cpt);

			// Handle the entry's response controls (if any)
			if (entry instanceof HasControls) {
				Control[] controls = ((HasControls) entry).getControls();
				if (logger.isDebugEnabled()) {
					logger.debug("entry name has controls " + controls.toString());
				}
			}

			// setting ldap attributes to user object.
			for (String dbAttrKey : ldapDbAttributes.keySet()) {
				LdapAttribute dbAttr = ldapDbAttributes.get(dbAttrKey);
				String ldapAttrName = dbAttr.getAttribute();
				Attribute ldapAttr = entry.getAttributes().get(ldapAttrName);
				if (logger.isDebugEnabled()) {
					logger.debug("field = " + dbAttrKey + ", ldap attribute = " + ldapAttrName);
				}
				boolean isNull = false;
				String value = null;
				try {
					// ldapAttr and value can be null. ldapAttr.get() can raise
					// NPE.
					value = (String) ldapAttr.get();
					if (logger.isDebugEnabled()) {
						String size = null;
						if(ldapAttr != null)	size = String.valueOf(ldapAttr.size());
						logger.debug("count of attribute values for : '" + ldapAttrName + "' :" + size);
					}
				} catch (NullPointerException e) {
					isNull = true;
				}

				if (value == null)
					isNull = true;

				if (isNull) {
					if (dbAttr.getSystem()) {
						logger.error("Can not convert dn : '" + dn +"' to an user object.");
						logger.error("The field '" + dbAttrKey + "' (ldap attribute : '" + ldapAttrName + "') must exist in your ldap directory, it is required by the system.");
						return null;
					} else {
						if (logger.isDebugEnabled())
							logger.debug("The field '" + dbAttrKey + "' (ldap attribute : '" + ldapAttrName + "') is null.");
						continue;
					}
				} else {
					logger.debug("value : " + value);
					// updating user property with current attribute value
					if (!setUserAttribute(user, dbAttrKey, value)) {
						logger.error("Can not convert dn : '" + dn +"' to an user object.");
						logger.error("Can not set the field '" + dbAttrKey + "' (ldap attribute : '" + ldapAttrName + "') with value : " + value);
						return null;
					}
				}
			}
		}
		return user;
	}

	/**
	 * Convert database LDAP attributes map to a attribute name list.
	 * 
	 * @param ldapDbAttributes
	 *            : map of database LDAP attributes
	 * @return List of attribute names.
	 */
	private Collection<String> getLdapAttrList(Map<String, LdapAttribute> ldapDbAttributes) {
		Collection<String> ldapAttrList = Maps.transformValues(ldapDbAttributes, new Function<LdapAttribute, String>() {
			public String apply(LdapAttribute input) {
				return input.getAttribute();
			}
		}).values();
		return ldapAttrList;
	}

	/**
	 * Filtering database LDAP attributes map to get only attributes needed for
	 * completion.
	 * 
	 * @return
	 */
	private Map<String, LdapAttribute> getLdapDbAttributeForCompletion() {
		Map<String, LdapAttribute> dbAttributes = domainPattern.getAttributes();
		Predicate<LdapAttribute> completionFilter = new Predicate<LdapAttribute>() {
			public boolean apply(LdapAttribute attr) {
				if (attr.getEnable()) {
					// Is attribute needed for completion ?
					return attr.getCompletion();
				}
				return false;
			}
		};

		Map<String, LdapAttribute> filterValues = Maps.filterValues(dbAttributes, completionFilter);
		return filterValues;
	}

	/**
	 * Filtering database LDAP attributes map to get only attributes needed for
	 * build a user.
	 * 
	 * @return
	 */
	private Map<String, LdapAttribute> getLdapDbAttribute() {
		Map<String, LdapAttribute> dbAttributes = domainPattern.getAttributes();
		Predicate<LdapAttribute> completionFilter = new Predicate<LdapAttribute>() {
			public boolean apply(LdapAttribute attr) {
				return attr.getEnable();
			}
		};

		Map<String, LdapAttribute> filterValues = Maps.filterValues(dbAttributes, completionFilter);
		return filterValues;
	}

	private boolean setUserAttribute(User user, String attr_key, String curValue) {
		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			Method userSetter = pd.getWriteMethod();
			String method = UserLdapPattern.USER_METHOD_MAPPING.get(attr_key);
			if (userSetter != null && method.equals(userSetter.getName())) {
				try {
					userSetter.invoke(user, curValue);
					return true;
				} catch (Exception e) {
					logger.error("Introspection : can not call method '" + userSetter.getName() + "' on User object.");
					logger.debug("message : " + e.getMessage());
					break;
				}
			}
		}
		return false;
	}

	/**
	 * This method allow to search a user from part of his mail or/and first
	 * name or/and last name
	 * 
	 * @param mail
	 * @param first_name
	 * @param last_name
	 * @return
	 * @throws NamingException
	 */
	public List<User> searchUser(String mail, String first_name, String last_name) throws NamingException {

		// Getting lql expression for completion
		String command = domainPattern.getSearchUserCommand();
		mail = addExpansionCharacters(mail);
		first_name = addExpansionCharacters(first_name);
		last_name = addExpansionCharacters(last_name);

		// Setting lql query parameters
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("mail", mail);
		vars.put("first_name", first_name);
		vars.put("last_name", last_name);
		if (logger.isDebugEnabled())
			logLqlQuery(command, mail, first_name, last_name);

		// searching ldap directory with pattern
		List<String> dnResultList = this.evaluate(command);

		return dnListToUsersList(dnResultList, false);
	}

	/**
	 * This method allow to find a user from his mail (entire mail, not a
	 * fragment).
	 * 
	 * @param mail
	 * @return a user
	 * @throws NamingException
	 */
	public User findUser(String mail) throws NamingException {

		// Getting lql expression for completion
		String command = domainPattern.getSearchUserCommand();
		if (mail == null || mail.length() < 1) {
			return null;
		}
		String first_name = "*";
		String last_name = "*";

		// Setting lql query parameters
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("mail", cleanLdapInputPattern(mail));
		vars.put("first_name", first_name);
		vars.put("last_name", last_name);
		if (logger.isDebugEnabled())
			logLqlQuery(command, mail, first_name, last_name);

		// searching ldap directory with pattern
		List<String> dnResultList = this.evaluate(command);

		if (dnResultList.size() == 1) {
			return dnToUser(dnResultList.get(0), false);
		} else if (dnResultList.size() > 1) {
			logger.error("mail must be unique ! " + mail);
		}
		return null;
	}

	/**
	 * test if a user exists using his mail. (entire mail, not a fragment)
	 * 
	 * @param mail
	 * @return
	 * @throws NamingException
	 */
	public Boolean isUserExist(String mail) throws NamingException {

		if (mail == null || mail.length() < 1) {
			return false;
		}
		String first_name = "*";
		String last_name = "*";

		// Getting lql expression for completion
		String command = domainPattern.getSearchUserCommand();

		// Setting lql query parameters
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("mail", cleanLdapInputPattern(mail));
		vars.put("first_name", first_name);
		vars.put("last_name", last_name);
		if (logger.isDebugEnabled())
			logLqlQuery(command, mail, first_name, last_name);

		// searching LDAP directory with pattern
		List<String> dnResultList = this.evaluate(command);
		if (dnResultList != null && !dnResultList.isEmpty()) {
			if (dnResultList.size() == 1) {
				return true;
			}
			logger.error("Multiple results found for mail : " + mail);
		}
		return false;
	}

	/**
	 * Ldap Authentification method
	 * 
	 * @param login
	 * @param userPasswd
	 * @return
	 * @throws NamingException
	 */
	public User auth(LdapConnection ldapConnection, String login, String userPasswd) throws NamingException {

		String command = domainPattern.getAuthCommand();
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("login", cleanLdapInputPattern(login));
		if (logger.isDebugEnabled())
			logLqlQuery(command, login);

		// searching ldap directory with pattern
		// InvalidSearchFilterException
		List<String> dnResultList = this.evaluate(command);

		if (dnResultList == null || dnResultList.size() < 1) {
			throw new NameNotFoundException("No user found for login: " + login);
		} else if (dnResultList.size() > 1) {
			logger.error("The authentification query had returned more than one user !!!");
			return null;
		}

		logger.debug("One ldap entry found, trying to make a bind to check user's password.");
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapConnection.getProviderUrl());
		String securityPrincipal = ldapConnection.getSecurityPrincipal();
		if (securityPrincipal != null) {
			ldapContextSource.setUserDn(securityPrincipal);
		}
		String securityCredentials = ldapConnection.getSecurityCredentials();
		if (securityCredentials != null) {
			ldapContextSource.setPassword(securityCredentials);
		}
		String userDn = dnResultList.get(0);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDn, userPasswd);

		BindAuthenticator authenticator = new BindAuthenticator(ldapContextSource);
		String localBaseDn = userDn + "," + baseDn;
		String searchFilter = "(objectClass=*)";
		logger.debug("looking for ldap entry with dn : " + localBaseDn + " and ldap filter : " + searchFilter);
		LdapUserSearch userSearch = new LinShareFilterBasedLdapUserSearch(localBaseDn, searchFilter, ldapContextSource);
		authenticator.setUserSearch(userSearch);
		try {
			ldapContextSource.afterPropertiesSet();
			authenticator.authenticate(authentication);
		} catch (BadCredentialsException e) {
			logger.debug("auth failed : BadCredentialsException(" + userDn + ")");
			throw e;
		} catch (Exception e) {
			logger.error("auth failed for unexpected exception: " + e.getMessage(), e);
			throw e;
		}
		return dnToUser(userDn, false);
	}

	/**
	 * search an user for Ldap Authentification process.
	 * 
	 * @param login
	 * @param userPasswd
	 * @return
	 * @throws NamingException
	 */
	public User searchForAuth(LdapConnection ldapConnection, String login) throws NamingException {

		String command = domainPattern.getAuthCommand();
		Map<String, Object> vars = lqlctx.getVariables();
		vars.put("login", cleanLdapInputPattern(login));
		if (logger.isDebugEnabled())
			logLqlQuery(command, login);

		// searching ldap directory with pattern
		// InvalidSearchFilterException
		List<String> dnResultList = this.evaluate(command);

		if (dnResultList == null || dnResultList.size() < 1) {
			return null;
		} else if (dnResultList.size() > 1) {
			logger.error("The authentification query had returned more than one user !!!");
			return null;
		}

		String userDn = dnResultList.get(0);
		return dnToUser(userDn, false);
	}
	
	
	/**
	 * This method is designed to add expansion characters to the input string.
	 * 
	 * @param string
	 *            : any string
	 * @return
	 */
	private String addExpansionCharacters(String string) {
		if (string == null || string.length() < 1) {
			string = "*";
		} else {
			string = cleanLdapInputPattern(string);
			string = "*" + string.trim() + "*";
		}
		return string;
	}
}

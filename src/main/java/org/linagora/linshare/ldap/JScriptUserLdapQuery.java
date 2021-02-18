/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

import org.linagora.linshare.core.domain.entities.Internal;
import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.LdapUserSearch;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class JScriptUserLdapQuery extends JScriptLdapQuery<User> {

	public JScriptUserLdapQuery(LqlRequestCtx ctx, String baseDn, UserLdapPattern domainPattern, IDnList dnList) throws NamingException, IOException {
		super(ctx, baseDn, dnList, domainPattern, Internal.class);
	}

	protected void logLqlQuery(String command, String mail, String first_name, String last_name) {
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

	protected void logLqlQuery(String command, String first_name, String last_name) {
		logLqlQuery(command, null, first_name, last_name);
	}

	/**
	 * 
	 * @param pattern
	 *            : could be first name, last name, or mail fragment.
	 * @return List<User>
	 * @throws NamingException
	 */
	public List<User> complete(String pattern) throws NamingException {

		// Getting lql expression for completion
		String command = ((UserLdapPattern)ldapPattern).getAutoCompleteCommandOnAllAttributes();
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
	 * @return List<User>
	 * @throws NamingException
	 */
	public List<User> complete(String first_name, String last_name) throws NamingException {

		// Getting lql expression for completion
		String command = ((UserLdapPattern)ldapPattern).getAutoCompleteCommandOnFirstAndLastName();
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
	 * @return List<User> List of user
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
				user = dnToObject(dn, controlContext.getLdapDbAttributes(), controlContext.getSearchControls());
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
	 * @return User
	 * @throws NamingException
	 */
	private User dnToUser(String dn, boolean completionMode) throws NamingException {
		ControlContext controlContext = initControlContext(completionMode);
		return dnToObject(dn, controlContext.getLdapDbAttributes(), controlContext.getSearchControls());
	}

	private ControlContext initControlContext(boolean completionMode) {
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

	/**
	 * Filtering database LDAP attributes map to get only attributes needed for
	 * completion.
	 * 
	 * @return
	 */
	private Map<String, LdapAttribute> getLdapDbAttributeForCompletion() {
		Map<String, LdapAttribute> dbAttributes = ((UserLdapPattern)ldapPattern).getAttributes();
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
	 * @return Map<String, LdapAttribute>
	 */
	private Map<String, LdapAttribute> getLdapDbAttribute() {
		Map<String, LdapAttribute> dbAttributes = ((UserLdapPattern)ldapPattern).getAttributes();
		Predicate<LdapAttribute> completionFilter = new Predicate<LdapAttribute>() {
			public boolean apply(LdapAttribute attr) {
				return attr.getEnable();
			}
		};

		Map<String, LdapAttribute> filterValues = Maps.filterValues(dbAttributes, completionFilter);
		return filterValues;
	}

	/**
	 * This method allow to search a user from part of his mail or/and first
	 * name or/and last name
	 * 
	 * @param mail
	 * @param first_name
	 * @param last_name
	 * @return List<User>
	 * @throws NamingException
	 */
	public List<User> searchUser(String mail, String first_name, String last_name) throws NamingException {

		// Getting lql expression for completion
		String command = ((UserLdapPattern)ldapPattern).getSearchUserCommand();
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
		String command = ((UserLdapPattern)ldapPattern).getSearchUserCommand();
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
	 * @return Boolean
	 * @throws NamingException
	 */
	public Boolean isUserExist(String mail) throws NamingException {

		if (mail == null || mail.length() < 1) {
			return false;
		}
		String first_name = "*";
		String last_name = "*";

		// Getting lql expression for completion
		String command = ((UserLdapPattern)ldapPattern).getSearchUserCommand();

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
	 * @return User
	 * @throws NamingException
	 */
	public User auth(LdapConnection ldapConnection, String login, String userPasswd) throws NamingException {

		String command = ((UserLdapPattern)ldapPattern).getAuthCommand();
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
	 * @param ldapConnection
	 * @param login
	 * @return User
	 * @throws NamingException
	 */
	public User searchForAuth(LdapConnection ldapConnection, String login) throws NamingException {

		String command = ((UserLdapPattern)ldapPattern).getAuthCommand();
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

}

/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;

import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.LdapPattern;
import org.linid.dm.authorization.lql.JScriptEvaluator;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class JScriptLdapQuery<T extends Object> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected JScriptEvaluator evaluator;

	protected LqlRequestCtx lqlctx;

	protected IDnList dnList;

	protected String baseDn;

	protected Pattern cleaner;

	protected Class<?> clazz;

	protected LdapPattern ldapPattern;

	public JScriptLdapQuery(LqlRequestCtx ctx, String baseDn, IDnList dnList, LdapPattern ldapPattern, Class<?> clazz) throws NamingException, IOException {
		super();
		this.lqlctx = ctx;
		this.evaluator = JScriptEvaluator.getInstance(ctx.getLdapCtx(), dnList);
		this.cleaner = Pattern.compile("[;,!|*()&]");
		this.clazz = clazz;
		this.baseDn = baseDn;
		this.ldapPattern = ldapPattern;
	}

	public String cleanLdapInputPattern(String pattern) {
		return cleaner.matcher(pattern).replaceAll("");
	}

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

	protected void logLqlQuery(String command, String pattern) {
		if (logger.isDebugEnabled()) {
			logger.debug("lql command " + command);
			logger.debug("pattern: " + pattern);
			String cmd = command.replaceAll("\"[ ]*[+][ ]*pattern[ ]*[+][ ]*\"", pattern);
			cmd = cmd.replaceAll("\"[ ]*[+][ ]*mail[ ]*[+][ ]*\"", pattern);
			logger.debug("ldap filter : " + cmd);
		}
	}

	/**
	 * This method is designed to add expansion characters to the input string.
	 * 
	 * @param string
	 *            : any string
	 * @return String
	 */
	protected String addExpansionCharacters(String string) {
		if (string == null || string.length() < 1) {
			string = "*";
		} else {
			string = cleanLdapInputPattern(string);
			string = "*" + string.trim() + "*";
		}
		return string;
	}

	/**
	 * Convert database LDAP attributes map to a attribute name list.
	 * 
	 * @param ldapDbAttributes
	 *            : map of database LDAP attributes
	 * @return List of attribute names.
	 */
	protected Collection<String> getLdapAttrList(Map<String, LdapAttribute> ldapDbAttributes) {
		Collection<String> ldapAttrList = Maps.transformValues(ldapDbAttributes, new Function<LdapAttribute, String>() {
			public String apply(LdapAttribute input) {
				return input.getAttribute();
			}
		}).values();
		return ldapAttrList;
	}

	protected Map<String, LdapAttribute> filterAttrByPrefix(String prefix) {
		Map<String, LdapAttribute> dbAttributes = ldapPattern.getAttributes();
		Predicate<LdapAttribute> filter = new Predicate<LdapAttribute>() {
			public boolean apply(LdapAttribute attr) {
				if (attr.getEnable()) {
					return attr.getField().startsWith(prefix);
				}
				return false;
			}
		};
		Map<String, LdapAttribute> filterValues = Maps.filterValues(dbAttributes, filter);
		return filterValues;
	}

	protected Map<String, LdapAttribute> filterAttrByPartialString(String contain) {
		Map<String, LdapAttribute> dbAttributes = ldapPattern.getAttributes();
		Predicate<LdapAttribute> filter = new Predicate<LdapAttribute>() {
			public boolean apply(LdapAttribute attr) {
				if (attr.getEnable()) {
					return attr.getField().contains(contain);
				}
				return false;
			}
		};
		Map<String, LdapAttribute> filterValues = Maps.filterValues(dbAttributes, filter);
		return filterValues;
	}

	protected ControlContext initControlContext(Map<String, LdapAttribute> ldapDbAttributes) {
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
	 * 
	 * @param dnResultList list of dn without baseDn used by the previous search.
	 * @param ldapDbAttributes ldap search attributes (mail, firstname, lastname)
	 * @return List of User
	 */
	protected Set<T> dnListToObjectList(List<String> dnResultList, Map<String, LdapAttribute> ldapDbAttributes) {
		// converting resulting dn to User object
		Set<T> users = Sets.newHashSet();
		for (String dn : dnResultList) {
			logger.debug("current dn: " + dn);
			Date date_before = new Date();
			T obj = null;
			try {
				obj = dnToObject(dn, ldapDbAttributes);
			} catch (NamingException e) {
				logger.error(e.getMessage());
				logger.debug(e.toString());
			}
			Date date_after = new Date();
			logger.trace("fin dnToObject: " + String.valueOf(date_after.getTime() - date_before.getTime()) + " milliseconds.");
			if (obj != null) {
				users.add(obj);
			}
		}
		return users;
	}

	protected T dnToObject(String dn, Map<String, LdapAttribute> ldapDbAttributes) throws NamingException {
		ControlContext controlContext = initControlContext(ldapDbAttributes);
		T obj = null;
		try {
			obj = dnToObject(dn, controlContext.getLdapDbAttributes(), controlContext.getSearchControls());
		} catch (NamingException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return obj;
	}
	// TODO whole refactoring needed to support multiple values per attribute
	@SuppressWarnings("unchecked")
	protected T dnToObject(String dn, Map<String, LdapAttribute> ldapDbAttributes, SearchControls scs) throws NamingException {
		T obj = null;
		try {
			Constructor<?> ctor = clazz.getConstructor();
			obj = (T) ctor.newInstance();
			logger.trace("newInstance : " + obj.toString());
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		// If T contains a method name "LdapPattern.DN", the current full dn will be
		// store into the current object.
		setUserAttribute(obj, LdapPattern.DN, dn + "," + baseDn);
		NamingEnumeration<SearchResult> results = lqlctx.getLdapCtx().search(dn, "(objectclass=*)", scs);
		Integer cpt = Integer.valueOf(0);
		while (results != null && results.hasMore()) {
			cpt += 1;
			SearchResult entry = (SearchResult) results.next();
			logger.trace("processing result : " + cpt);

			// Handle the entry's response controls (if any)
			if (entry instanceof HasControls) {
				Control[] controls = ((HasControls) entry).getControls();
				if (logger.isTraceEnabled()) {
					logger.trace("entry name has controls " + controls.toString());
				}
			}

			// setting ldap attributes to user object.
			for (String dbAttrKey : ldapDbAttributes.keySet()) {
				LdapAttribute dbAttr = ldapDbAttributes.get(dbAttrKey);
				String ldapAttrName = dbAttr.getAttribute();
				Attribute ldapAttr = entry.getAttributes().get(ldapAttrName);
				if (logger.isTraceEnabled()) {
					logger.trace("field = " + dbAttrKey + ", ldap attribute = " + ldapAttrName);
				}
				boolean isNull = false;
				String value = null;
				try {
					// ldapAttr and value can be null. ldapAttr.get() can raise
					// NPE.
					value = (String) ldapAttr.get();
					if (logger.isTraceEnabled()) {
						if (ldapAttr != null) {
							String size = null;
							size = String.valueOf(ldapAttr.size());
							logger.trace("count of attribute values for : '" + ldapAttrName + "' :" + size);
						}
					}
				} catch (NullPointerException e) {
					isNull = true;
				}

				if (value == null)
					isNull = true;

				if (isNull) {
					if (dbAttr.getSystem()) {
						logger.error("Can not convert dn : '" + dn +"' to an object.");
						logger.error("The field '" + dbAttrKey + "' (ldap attribute : '" + ldapAttrName + "') must exist in your ldap directory, it is required by the system.");
						return null;
					} else {
						if (logger.isTraceEnabled())
							logger.trace("The field '" + dbAttrKey + "' (ldap attribute : '" + ldapAttrName + "') is null.");
						continue;
					}
				} else {
					NamingEnumeration<?> all = ldapAttr.getAll();
					while (all.hasMoreElements()) {
						Object element = all.nextElement();
						logger.trace("element : " + element);
						// we are just skipping some attrute's values if they are null.
						if (element != null) {
							value = (String) element;
							// updating user property with current attribute value
							if (!setUserAttribute(obj, dbAttrKey, value)) {
								logger.error("Can not convert dn : '" + dn +"' to an user object.");
								logger.error("Can not set the field '" + dbAttrKey + "' (ldap attribute : '" + ldapAttrName + "') with value : " + value);
								return null;
							}
						}
					}
				}
			}
		}
		logger.debug("newInstance : " + obj.toString());
		return obj;
	}

	protected boolean setUserAttribute(Object user, String attr_key, String curValue) {
		String methodName = ldapPattern.getMethodsMapping().get(attr_key);
		if (methodName == null) {
			logger.trace("Skipped. Method not found for attr_key: " + attr_key);
			return false;
		}
		try {
			Method method = user.getClass().getMethod(methodName, String.class);
			method.invoke(user, curValue);
			return true;
		} catch (Exception e) {
			logger.error("Introspection : can not call method '" + methodName + "' on current object.");
			logger.error("message : " + e.getMessage());
		}
		return false;
	}

	public Boolean isDnExist(String dn) {
		return isDnExist(dn, "*");
	}

	public Boolean isDnExist(String dn, String objectclass) {
		String dnFragment = dn.split(",")[0];
		dn = dn.substring(dnFragment.length() + 1);
		SearchControls scs = new SearchControls();
		scs.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		String[] attrs = { dnFragment.split("=")[0], };
		scs.setReturningAttributes(attrs);
		try {
			String filter = "(&(objectclass=" + objectclass + ")(" + dnFragment + "))";
			NamingEnumeration<SearchResult> results = lqlctx.getLdapCtx().search(dn, filter, scs);
			boolean res = results.hasMoreElements();
			return res;
		} catch (NamingException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

}

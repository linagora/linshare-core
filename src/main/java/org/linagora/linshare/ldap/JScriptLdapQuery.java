/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import org.linagora.linshare.core.domain.entities.LdapAttribute;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linid.dm.authorization.lql.JScriptEvaluator;
import org.linid.dm.authorization.lql.LqlRequestCtx;
import org.linid.dm.authorization.lql.dnlist.IDnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JScriptLdapQuery<T extends Object> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected JScriptEvaluator evaluator;

	protected LqlRequestCtx lqlctx;

	protected IDnList dnList;

	protected String baseDn;

	protected BeanInfo beanInfo;

	protected Pattern cleaner;

	protected Class<?> clazz;

	public JScriptLdapQuery(LqlRequestCtx ctx, String baseDn, IDnList dnList, Class<?> clazz) throws NamingException, IOException {
		super();
		this.lqlctx = ctx;
		this.evaluator = JScriptEvaluator.getInstance(ctx.getLdapCtx(), dnList);
		this.cleaner = Pattern.compile("[;,!|*()&]");
		this.clazz = clazz;
		this.baseDn = baseDn;
		try {
			this.beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			logger.error("Introspection of Internal user class impossible.");
			logger.debug("message : " + e.getMessage());
		}
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

	@SuppressWarnings("unchecked")
	protected T dnToObject(String dn, Map<String, LdapAttribute> ldapDbAttributes, SearchControls scs) throws NamingException {
		T user = null;
		try {
			Constructor<?> ctor = clazz.getConstructor();
			user = (T) ctor.newInstance();
			logger.debug("newInstance : " + user.toString());
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
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
		logger.debug("newInstance : " + user.toString());
		return user;
	}

	protected boolean setUserAttribute(Object user, String attr_key, String curValue) {
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
}

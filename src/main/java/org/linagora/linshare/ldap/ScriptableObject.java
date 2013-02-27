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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.mozilla.javascript.NativeJavaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on rhino, this class is able to understand your LQL requests.
 * @author Sebastien Bahloul &lt;seb@lsc-project.org&gt;
 */
public class ScriptableObject {

	private static final Logger LOGGER =
					LoggerFactory.getLogger(ScriptableObject.class);

	@SuppressWarnings("unchecked")
	public List<String> wrap(String methodName, final Object a, final Object b,
					boolean listable) throws NamingException {
		Method method = null;

		try {
			List<String> aList = getList(a);
			List<String> bList = getList(b);

			if (listable) {
				method = this.getClass().getDeclaredMethod(methodName, List.class, List.class);

				return (List<String>) method.invoke(this, aList, bList);
			} else {
				if ((aList == null) || (bList == null)) {
					return null;
				}

				method = this.getClass().getDeclaredMethod(methodName, String.class, String.class);

				List<String> results = new ArrayList<String>();

				for (String aValue : aList) {
					for (String bValue : bList) {
						List<String> res = (List<String>) method.invoke(this, aValue, bValue);

						if (res != null) {
							results.addAll(res);
						}
					}
				}

				return results;
			}
		} catch (SecurityException e) {
			LOGGER.error("Programmatic error", e);
		} catch (NoSuchMethodException e) {
			LOGGER.error("Programmatic error", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Programmatic error", e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Programmatic error", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("Programmatic error", e);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<String>> wrapMap(String methodName, final Object a, final Object b) throws NamingException {
		Method method = null;

		try {
			List<String> aList = getList(a);
			List<String> bList = getList(b);

			if ((aList == null) || (bList == null)) {
				return null;
			}
			method = this.getClass().getDeclaredMethod(methodName, String.class, String.class);

			Map<String, List<String>> results = new HashMap<String, List<String>>();

			for (String aValue : aList) {
				for (String bValue : bList) {
					Map<String, List<String>> res = (Map<String, List<String>>) method.invoke(this, aValue, bValue);
					if (res != null) {
						results.putAll(res);
					}
				}
			}

			return results;
		} catch (SecurityException e) {
			LOGGER.error("Programmatic error", e);
		} catch (NoSuchMethodException e) {
			LOGGER.error("Programmatic error", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Programmatic error", e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Programmatic error", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("Programmatic error", e);
		}

		return null;
	}

	/**
	 * Convert objects to Strings list
	 *
	 * @param a the original object
	 *
	 * @return the strings list
	 */
	@SuppressWarnings("unchecked")
	public List<String> getList(final Object a) {
		List<String> aList = null;

		if (a == null) {
			return null;
		} else if (String.class.isAssignableFrom(a.getClass())) {
			aList = new ArrayList<String>();
			aList.add((String) a);

			return aList;
		} else if (List.class.isAssignableFrom(a.getClass())) {
			aList = (List<String>) a;

			return aList;
		} else if (NativeJavaObject.class.isAssignableFrom(a.getClass())) {
			aList = getList(((NativeJavaObject) a).unwrap());

			return aList;
		}

		return null;
	}

	public List<String> wrapList(String methodName, final Object a, final Object b)
					throws NamingException {
		return wrap(methodName, a, b, true);
	}

	public List<String> wrapString(String methodName, final Object a, final Object b)
					throws NamingException {
		return wrap(methodName, a, b, false);
	}
}

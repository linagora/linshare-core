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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <P>Simple interface to some methods to access an LDAP directory.</P>
 * 
 * <P>This class is available as "ldap" or "srcLdap" in several JavaScript 
 * enabled parameters in the LSC configuration. The methods allow you to
 * interact with an LDAP directory.</P>
 * 
 * <P>Based on Rhino (JavaScript interpreter), this class is able to understand your LQL requests.</P>
 * 
 * <P>All methods in the class use methods from {@link JndiServices}.</P>
 * 
 * @author Sebastien Bahloul &lt;seb@lsc-project.org&gt;
 */
public class ScriptableJndiServices extends ScriptableObject {
	
	protected final Logger logger = LoggerFactory.getLogger(ScriptableJndiServices.class);
	

	/** Local jndi instance. Used to connect to the right directory. */
	private JndiServices jndiServices;

	/**
	 * <P>Default constructor.</P>
	 *
	 * <P>Default directory properties are based on destination.</P>
	 */
	public ScriptableJndiServices() {
		// jndiServices = JndiServices.getDstInstance();
	}

	/**
	 * <P>Default jndiServices setter.</P>
	 * 
	 * @param jndiServices the new value
	 */
	public final void setJndiServices(JndiServices jndiServices) {
		this.jndiServices = jndiServices;
	}

	/**
	 * <P>Performs a search with subtree scope on a given base DN with a given filter.</P>
	 * 
	 * @param base The base DN to search from.
	 * @param filter The LDAP filter to use.
	 * @return List<String> List of DNs returned by the search.
	 * @throws NamingException
	 */
	public final List<String> search(final Object base, final Object filter) throws NamingException {
		return wrapString("_search", base, filter);
	}

	protected List<String> _search(final String base, final String filter)
					throws NamingException {
		return jndiServices.getDnList(base, filter, SearchControls.SUBTREE_SCOPE);
	}

	/**
	 * <P>Performs a search with one level scope on a given base DN with a given filter.</P>
	 * 
	 * @param base The base DN to search from.
	 * @param filter The LDAP filter to use.
	 * @return List<String> List of DNs returned by the search.
	 * @throws NamingException
	 */
	public final List<String> list(final Object base, final Object filter) throws NamingException {
		return wrapString("_list", base, filter);
	}

	protected final List<String> _list(final String base, final String filter) throws NamingException {
		return jndiServices.getDnList(base, filter, SearchControls.ONELEVEL_SCOPE);
	}

	/**
	 * <P>Performs a search with base level scope on a given base DN with a given filter.</P>
	 * 
	 * @param base The base DN to search from.
	 * @param filter The LDAP filter to use.
	 * @return List<String> List of DNs returned by the search.
	 * @throws NamingException
	 */
	public final List<String> read(final Object base, final Object filter) throws NamingException {
		return wrapString("_read", base, filter);
	}

	protected List<String> _read(final String base, final String filter) throws NamingException {
		return jndiServices.getDnList(base, filter, SearchControls.OBJECT_SCOPE);
	}

	/**
	 * <P>Tests if an entry exists with the given DN and if it matches a given LDAP filter.</P>
	 * 
	 * @param dn The DN of the entry to check.
	 * @param filter The LDAP filter to check on the above DN.
	 * @return List<String> List containing the DN if it exists and matches the filter, or null otherwise.
	 * @throws NamingException
	 */
	public final List<String> exists(final Object dn, final Object filter) throws NamingException {
		return wrapString("_exists", dn, filter);
	}

	/**
	 * <P>Tests if an entry exists with the given DN.</P>
	 * 
	 * @param dn The DN of the entry to check.
	 * @return List<String> List containing the DN if it exists and matches the filter, or null otherwise.
	 * @throws NamingException
	 */
	public final List<String> exists(final Object dn) throws NamingException {
		return wrapString("_exists", dn, JndiServices.DEFAULT_FILTER);
	}
	
	protected List<String> _exists(final String dn, final String filter) throws NamingException {
		if (jndiServices.exists(dn, filter)) {
			List<String> c = new ArrayList<String>();
			c.add(dn);

			return c;
		}
		return null;
	}

	/**
	 * <P>Performs a union on two Lists of Strings.</P>
	 * 
	 * @param a List of Strings
	 * @param b List of Strings
	 * @return List<String> List of Strings containing all elements from a and b.
	 * @throws NamingException
	 */
	public final List<String> or(final Object a, final Object b) throws NamingException {
		return wrapList("_or", a, b);
	}

	protected final List<String> _or(final List<String> a, final List<String> b) throws NamingException {
		List<String> c = new ArrayList<String>();
		c.addAll(a);
		c.addAll(b);

		return c;
	}

	/**
	 * <P>Reads the entry given by a DN, and returns the values of the given attribute.</P>
	 * 
	 * @param base The DN of the entry to read.
	 * @param attrName The name of the attribute to read.
	 * @return List<String> List of values of the attribute, as Strings.
	 * @throws NamingException
	 */
	public final List<String> attribute(final Object base, final Object attrName) throws NamingException {
		return wrapString("_attr", base, attrName);
	}

	@SuppressWarnings({"unchecked"})
	protected List<String> _attr(final String base, final String attrName) throws NamingException {
		SearchResult sr = jndiServices.readEntry(base, "objectClass=*", false);

		if ((sr != null) && (sr.getAttributes() != null) && (sr.getAttributes().get(attrName) != null)) {
			return (List<String>) Collections.list(sr.getAttributes().get(attrName).getAll());
		}
		return null;
	}

	/**
	 * <P>Performs an intersection on two Lists of Strings.</P>
	 * 
	 * @param a List of Strings
	 * @param b List of Strings
	 * @return List<String> List of Strings containing elements that are in both a and b.
	 * @throws NamingException
	 */
	public final List<String> and(final Object a, final Object b) throws NamingException {
		return wrapList("_and", a, b);
	}

	protected List<String> _and(final List<String> aList, final List<String> bList)
					throws NamingException {
		List<String> cList = new ArrayList<String>();

		if (aList.size() < bList.size()) {
			for (String tmp : aList) {
				if (bList.contains(tmp)) {
					cList.add(tmp);
				}
			}
		} else {
			for (String tmp : bList) {
				if (aList.contains(tmp)) {
					cList.add(tmp);
				}
			}
		}

		return cList;
	}

	/**
	 * <P>Removes all elements of a List of Strings b from a List of Strings a.</P>
	 * 
	 * @param a List of Strings
	 * @param b List of Strings
	 * @return List<String> List of Strings containing all elements from a not in b.
	 * @throws NamingException
	 */
	public final List<String> retain(final Object a, final Object b) throws NamingException {
		return wrapList("_retain", a, b);
	}

	protected List<String> _retain(final List<String> aList, final List<String> bList)
					throws NamingException {
		List<String> cList = new ArrayList<String>();
		for (String aValue : aList) {
			if (!bList.contains(aValue)) {
				cList.add(aValue);
			}
		}

		return cList;
	}

	/**
	 * <P>Returns the parent DN on the n-th level of a given DN, in a List of Strings.</P>
	 * 
	 * <P>For example, given ("uid=1234,ou=People,dc=normation,dc=com", 2), 
	 * returns "dc=normation,dc=com" (in a List of Strings).</P>
	 * 
	 * <P>As a special case, if the requested level is 0, the result is a List of the given
	 * DN and all it's parent DNs until the context DN. In the above example, this List
	 * would be ["uid=1234,ou=People,dc=normation,dc=com",
	 * "ou=People,dc=normation,dc=com", "dc=normation,dc=com"], assuming that the
	 * context DN is "dc=normation,dc=com".</P>
	 * 
	 * <P>This method returns null if a negative level is given.</P>
	 * 
	 * @param dn The DN whose parent we want.
	 * @param level The number of levels to go up, or 0 to return all parent DNs.
	 * @return List<String> List containing the parent DN, or all parent DNs if level is 0, or null if level is negative.
	 * @throws NamingException
	 */
	public final List<String> sup(final Object dn, final Object level) throws NamingException {
		return wrapString("_sup", dn, level);
	}

	protected List<String> _sup(final String dn, final String level)
					throws NamingException {
		int levelValue = Integer.parseInt(level);

		return jndiServices.sup(dn, levelValue);
	}

	/**
	 * <P>Returns a List containing the given DN and all parent DNs that 
	 * exist and match a given LDAP filter.</P>
	 * 
	 * <P>This method returns the same result as sup(dn, 0), with validation
	 * that each object exists and matches the given filter.</P>
	 * 
	 * @param dn The DN whose parents we want.
	 * @param filter The LDAP filter to check.
	 * @return List<String> List of DNs as Strings that are this entry's DN, or it's parents DN,
	 * 						that exist and match the given filter.
	 * @throws NamingException
	 */
	public final List<String> fsup(final Object dn, final Object filter)
					throws NamingException {
		return wrapString("_fsup", dn, filter);
	}

	protected List<String> _fsup(final String dn, final String filter)
					throws NamingException {
		List<String> cList = new ArrayList<String>();
		List<String> dns = jndiServices.sup(dn, 0);

		if (dns == null) {
			return null;
		}

		for (String aDn : dns) {
			if (jndiServices.exists(aDn, filter)) {
				cList.add(aDn);

				return cList;
			}
		}

		return cList;
	}
	
	public final Map<String, List<String>> entry(final Object dn, final Object filter) throws NamingException {
		return wrapMap("_entry", dn, filter);
	}

	protected Map<String, List<String>> _entry(final String dn, final String filter) throws NamingException {
		Map<String, List<String>> entry = jndiServices.entry(dn, filter);
		if(entry == null) {
			logger.error("jndiServices.entry(dn, filter) is null");
			entry = new HashMap<String, List<String>>();
		}
		return entry;
	}

	
	/**
	 * <P>Get the context DN configured for this instance.</P>
	 * 
	 * @return The context DN as a String.
	 */
	public String getContextDn() {
		return jndiServices.getContextDn();
	}
}

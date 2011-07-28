/*
 ****************************************************************************
 * Ldap Synchronization Connector provides tools to synchronize
 * electronic identities from a list of data sources including
 * any database with a JDBC connector, another LDAP directory,
 * flat files...
 *
 *                  ==LICENSE NOTICE==
 *
 * Copyright (c) 2008 - 2011 LSC Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of the LSC Project nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *                  ==LICENSE NOTICE==
 *
 *               (c) 2008 - 2011 LSC Project
 *         Sebastien Bahloul <seb@lsc-project.org>
 *         Thomas Chemineau <thomas@lsc-project.org>
 *         Jonathan Clarke <jon@lsc-project.org>
 *         Remy-Christophe Schermesser <rcs@lsc-project.org>
 ****************************************************************************
 */
package org.linagora.linShare.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPURL;

/**
 * General LDAP services wrapper.
 *
 * This class is designed to manage all the needed operations to the directory
 *
 * @author Sebastien Bahloul &lt;seb@lsc-project.org&gt;
 * @author Jonathan Clarke &lt;jon@lsc-project.org&gt;
 */
public final class JndiServices {

	/** Default LDAP filter. */
	public static final String DEFAULT_FILTER = "objectClass=*";

	private static final Logger LOGGER = LoggerFactory.getLogger(JndiServices.class);

	/** the ldap ctx. */
	private LdapContext ctx;

	/** TLSResponse in case we use StartTLS */
	private StartTlsResponse tlsResponse;

	/** The context base dn. */
	private DN contextDn;

	private LDAPURL namingContext;
	
	//	/** Attribute name to sort on. */
	//	private String sortedBy;
	/**
	 * Initiate the object and the connection according to the properties.
	 *
	 * @param connProps the connection properties to use to instantiate
	 * connection
	 * @throws NamingException thrown if a directory error is encountered
	 * @throws IOException thrown if an error occurs negotiating StartTLS operation
	 */
	public JndiServices(final Properties connProps) throws NamingException, IOException {

		// log new connection with it's details
		logConnectingTo(connProps);

		/* should we negotiate TLS? */
		if (Boolean.parseBoolean((String) connProps.get("java.naming.tls"))) {
			/* if we're going to do TLS, we mustn't BIND before the STARTTLS operation
			 * so we remove credentials from the properties to stop JNDI from binding */
			/* duplicate properties to avoid changing them (they are used as a cache key in getInstance() */
			Properties localConnProps = new Properties();
			localConnProps.putAll(connProps);
			String jndiContextAuthentication = localConnProps.getProperty(Context.SECURITY_AUTHENTICATION);
			String jndiContextPrincipal = localConnProps.getProperty(Context.SECURITY_PRINCIPAL);
			String jndiContextCredentials = localConnProps.getProperty(Context.SECURITY_CREDENTIALS);
			localConnProps.remove(Context.SECURITY_AUTHENTICATION);
			localConnProps.remove(Context.SECURITY_PRINCIPAL);
			localConnProps.remove(Context.SECURITY_CREDENTIALS);

			/* open the connection */
			ctx = new InitialLdapContext(localConnProps, null);

			/* initiate the STARTTLS extended operation */
			try {
				tlsResponse = (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
				tlsResponse.negotiate();
			} catch (IOException e) {
				LOGGER.error("Error starting TLS encryption on connection to {}", localConnProps.getProperty(Context.PROVIDER_URL));
				LOGGER.debug(e.toString(), e);
				throw e;
			} catch (NamingException e) {
				LOGGER.error("Error starting TLS encryption on connection to {}", localConnProps.getProperty(Context.PROVIDER_URL));
				LOGGER.debug(e.toString(), e);
				throw e;
			}

			/* now we add the credentials back to the context, to BIND once TLS is started */
			ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, jndiContextAuthentication);
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, jndiContextPrincipal);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, jndiContextCredentials);

		} else {
			/* don't start TLS, just connect normally (this can be on ldap:// or ldaps://) */
			ctx = new InitialLdapContext(connProps, null);
		}

		/* get LDAP naming context */
		try {
			namingContext = new LDAPURL((String) ctx.getEnvironment().get(Context.PROVIDER_URL));
		} catch (LDAPException e) {
			LOGGER.error(e.toString());
			LOGGER.debug(e.toString(), e);
			throw new NamingException(e.getMessage());
		}

		/* handle options */
		contextDn = namingContext.getBaseDN() != null ? namingContext.getBaseDN() : null;
	}

	private void logConnectingTo(Properties connProps) {
		if (LOGGER.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Connecting to LDAP server ");
			sb.append(connProps.getProperty(Context.PROVIDER_URL));

			// log identity used to connect
			if (connProps.getProperty(Context.SECURITY_AUTHENTICATION) == null || connProps.getProperty(Context.SECURITY_AUTHENTICATION).equals("none")) {
				sb.append(" anonymously");
			} else {
				sb.append(" as ");
				sb.append(connProps.getProperty(Context.SECURITY_PRINCIPAL));
			}

			// using TLS ?
			if (Boolean.parseBoolean((String) connProps.get("java.naming.tls"))) {
				sb.append(" with STARTTLS extended operation");
			}

			LOGGER.info(sb.toString());
		}
	}

	/**
	 * Search for an entry.
	 *
	 * This method is a simple LDAP search operation with SUBTREE search
	 * control
	 *
	 * @param base
	 *                the base of the search operation
	 * @param filter
	 *                the filter of the search operation
	 * @return the entry or null if not found
	 * @throws NamingException
	 *                 thrown if something goes wrong
	 */
	public SearchResult getEntry(final String base, final String filter) throws NamingException {
		SearchControls sc = new SearchControls();
		return getEntry(base, filter, sc);
	}

	/**
	 * Search for an entry.
	 *
	 * This method is a simple LDAP search operation with SUBTREE search
	 * control
	 *
	 * @param base the base of the search operation
	 * @param filter  the filter of the search operation
	 * @param sc the search controls
	 * @return the entry or null if not found
	 * @throws NamingException
	 *                 thrown if something goes wrong
	 */
	public SearchResult getEntry(final String base, final String filter,
					final SearchControls sc) throws NamingException {
		return getEntry(base, filter, sc, SearchControls.SUBTREE_SCOPE);
	}

	/**
	 * Search for an entry.
	 *
	 * This method is a simple LDAP search operation with SUBTREE search
	 * control
	 *
	 * @param base the base of the search operation
	 * @param filter  the filter of the search operation
	 * @param sc the search controls
	 * @param scope the search scope to use
	 * @return the entry or null if not found
	 * @throws SizeLimitExceededException
	 * 					thrown if more than one entry is returned by the search
	 * @throws NamingException
	 *                 thrown if something goes wrong
	 */
	public SearchResult getEntry(final String base, final String filter,
					final SearchControls sc, final int scope) throws NamingException {
		//sanity checks
		String searchBase = base == null ? "" : base;
		String searchFilter = filter == null ? DEFAULT_FILTER : filter;

		NamingEnumeration<SearchResult> ne = null;
		try {
			sc.setSearchScope(scope);
			String rewrittenBase = null;
			if (contextDn != null && searchBase.toLowerCase().endsWith(contextDn.toString().toLowerCase())) {
				if (!searchBase.equalsIgnoreCase(contextDn.toString())) {
				rewrittenBase = searchBase.substring(0, searchBase.toLowerCase().lastIndexOf(contextDn.toString().toLowerCase()) - 1);
			} else {
					rewrittenBase = "";
				}
			} else {
				rewrittenBase = searchBase;
			}
			
			ne = ctx.search(rewrittenBase, searchFilter, sc);
		} catch (CommunicationException ce) {
			LOGGER.error("ComunicationException : reconnexion to ,ldap directory");
			ctx.reconnect(ctx.getConnectControls());
			return getEntry(base, filter, sc, scope);
		} catch (NamingException nex) {
			LOGGER.error("Error while looking for {} in {}: {}",
							new Object[] { searchFilter, searchBase, nex });
			throw nex;
		}
		
		SearchResult sr = null;
		if (ne.hasMoreElements()) {
			sr = (SearchResult) ne.nextElement();
			if (ne.hasMoreElements()) {
				LOGGER.error("Too many entries returned (base: \"{}\", filter: \"{}\")",
								searchBase, searchFilter);
				throw new SizeLimitExceededException("Too many entries returned (base: \"" + searchBase + "\", filter: \"" + searchFilter + "\")");
			} else {
				return sr;
			}
		} else {
			// try hasMore method to throw exceptions if there are any and we didn't get our entry
			ne.hasMore();
		}
		return sr;
	}

	/**
	 * Check if the entry with the specified distinguish name exists (or
	 * not).
	 *
	 * @param dn the entry's distinguish name
	 * @param filter look at the dn according this filter
	 * @return entry existence (or false if something goes wrong)
	 */
	public boolean exists(final String dn, final String filter) {
		try {
			return (readEntry(dn, filter, true) != null);
		} catch (NamingException e) {
			LOGGER.error(e.toString());
			LOGGER.debug(e.toString(), e);
		}
		return false;
	}

	/**
	 * Check if the entry with the specified distinguish name exists (or
	 * not).
	 *
	 * @param dn the entry's distinguish name
	 * @return entry existence (or false if something goes wrong)
	 */
	public boolean exists(final String dn) {
		return exists(dn, DEFAULT_FILTER);
	}

	/**
	 * Search for an entry.
	 *
	 * This method is a simple LDAP search operation with BASE search scope
	 *
	 * @param base
	 *                the base of the search operation
	 * @param allowError
	 *                log error if not found or not
	 * @return the entry or null if not found
	 * @throws NamingException
	 *                 thrown if something goes wrong
	 */
	public SearchResult readEntry(final String base, final boolean allowError) throws NamingException {
		return readEntry(base, DEFAULT_FILTER, allowError);
	}

	public SearchResult readEntry(final String base, final String filter, final boolean allowError)
					throws NamingException {
		SearchControls sc = new SearchControls();
		return readEntry(base, filter, allowError, sc);
	}

	public String rewriteBase(final String base) {
		String rewrittenBase = null;
		if (contextDn.toString().length()>0 && base.toLowerCase().endsWith(contextDn.toString().toLowerCase())) {
			if (!base.equalsIgnoreCase(contextDn.toString())) {
			rewrittenBase = base.substring(0, base.toLowerCase().lastIndexOf(contextDn.toString().toLowerCase()) - 1);
		} else {
				rewrittenBase = "";
			}
		} else {
			rewrittenBase = base;
		}
		return rewrittenBase;
	}

	public SearchResult readEntry(final String base, final String filter,
					final boolean allowError, final SearchControls sc) throws NamingException {
		NamingEnumeration<SearchResult> ne = null;
		sc.setSearchScope(SearchControls.OBJECT_SCOPE);
		try {
			ne = ctx.search(rewriteBase(base), filter, sc);
		} catch (CommunicationException ce) {
			ctx.reconnect(ctx.getConnectControls());
			return readEntry(base, filter, allowError, sc);
		} catch (NamingException nex) {
			if (!allowError) {
				LOGGER.error("Error while reading entry {}: {}", base, nex);
				LOGGER.debug(nex.toString(), nex);
			}
			return null;
		}

		SearchResult sr = null;
		if (ne.hasMore()) {
			sr = (SearchResult) ne.next();
			if (ne.hasMore()) {
				LOGGER.error("Too many entries returned (base: \"{}\")", base);
			} else {
				return sr;
			}
		}
		return sr;
	}
	
	public boolean auth(final String password, final String dn) throws NamingException {
		LdapContext bindContext = this.getContext();
		Properties authProps = new Properties();
		String oldAuth = (String) bindContext.getEnvironment().get(Context.SECURITY_AUTHENTICATION);
		boolean ret = false;
		if (oldAuth == null || oldAuth.equals("none")) {
			authProps.put(Context.SECURITY_AUTHENTICATION, "none");
		} else {
			String oldPrincipal = (String) bindContext.getEnvironment().get(Context.SECURITY_PRINCIPAL);
			String oldCredentials = (String) bindContext.getEnvironment().get(Context.SECURITY_CREDENTIALS);
			authProps.put(Context.SECURITY_AUTHENTICATION, "simple");
			if (oldPrincipal != null) {
				authProps.put(Context.SECURITY_PRINCIPAL, oldPrincipal);
			}
			if (oldCredentials != null) {
				authProps.put(Context.SECURITY_CREDENTIALS, oldCredentials);
			}
		}
		try {
			bindContext.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
			bindContext.addToEnvironment(Context.SECURITY_PRINCIPAL, dn);
			bindContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			bindContext.reconnect(bindContext.getConnectControls());
			ret = true;
		} catch (Exception e) {
			ret = false;
		} finally {
            bindContext.removeFromEnvironment(Context.SECURITY_AUTHENTICATION);
            bindContext.removeFromEnvironment(Context.SECURITY_PRINCIPAL);
            bindContext.removeFromEnvironment(Context.SECURITY_CREDENTIALS);
            for (Entry<Object, Object> propertyEntry : authProps.entrySet()) {
            	bindContext.addToEnvironment(propertyEntry.getKey().toString(), propertyEntry.getValue());
            }
            bindContext.reconnect(bindContext.getConnectControls());

		}
		return ret;
    }

	/**
	 * Search for a list of DN.
	 *
	 * This method is a simple LDAP search operation which is attended to
	 * return a list of the entries DN
	 *
	 * @param base
	 *                the base of the search operation
	 * @param filter
	 *                the filter of the search operation
	 * @param scope
	 *                the scope of the search operation
	 * @return the dn of each entry that is returned by the directory
	 * @throws NamingException
	 *                 thrown if something goes wrong
	 */
	public List<String> getDnList(final String base, final String filter,
					final int scope) throws NamingException {
		NamingEnumeration<SearchResult> ne = null;
		List<String> iist = new ArrayList<String>();
		try {
			SearchControls sc = new SearchControls();
			sc.setDerefLinkFlag(false);
			sc.setReturningAttributes(new String[]{"1.1"});
			sc.setSearchScope(scope);
			sc.setReturningObjFlag(true);
			
			ne = ctx.search(base, filter, sc);
			
			String completedBaseDn = "";
			if (base.length() > 0) {
				completedBaseDn = "," + base;
			}
			while (ne.hasMoreElements()) {
				iist.add(((SearchResult) ne.next()).getName() + completedBaseDn);
			}
		} catch (CommunicationException ce) {
			ctx.reconnect(ctx.getConnectControls());
			return getDnList(base, filter, scope);
		} catch (NamingException e) {
			LOGGER.error(e.toString());
			LOGGER.debug(e.toString(), e);
			throw e;
		}
		
		return iist;
	}

	/**
	 * Return the modificationItems in the javax.naming.directory.Attributes
	 * format.
	 *
	 * @param modificationItems
	 *                the modification items list
	 * @param forgetEmpty
	 *                if specified, empty attributes will not be converted
	 * @return the formatted attributes
	 */
	private Attributes getAttributes(final List<ModificationItem> modificationItems,
					final boolean forgetEmpty) {
		Attributes attrs = new BasicAttributes();
		for (ModificationItem mi : modificationItems) {
			if (!(forgetEmpty && mi.getAttribute().size() == 0)) {
				attrs.put(mi.getAttribute());
			}
		}
		return attrs;
	}

	/**
	 * Return the LDAP schema.
	 *
	 * @param attrsToReturn
	 *                list of attribute names to return (or null for all
	 *                'standard' attributes)
	 * @return the map of name => attribute
	 * @throws NamingException
	 *                 thrown if something goes wrong (bad
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getSchema(final String[] attrsToReturn) throws NamingException {
		Map<String, List<String>> attrsResult = new HashMap<String, List<String>>();

		// connect to directory
		Hashtable props = ctx.getEnvironment();
		String baseUrl = (String) props.get(Context.PROVIDER_URL);
		baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));
		props.put(Context.PROVIDER_URL, baseUrl);
		DirContext schemaCtx = new InitialLdapContext(props, null);

		// find schema entry
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.OBJECT_SCOPE);
		sc.setReturningAttributes(new String[]{"subschemaSubentry"});

		NamingEnumeration<SearchResult> schemaDnSR = schemaCtx.search("", "(objectclass=*)", sc);

		SearchResult sr = null;
		Attribute subschemaSubentry = null;
		String subschemaSubentryDN = null;

		if (schemaDnSR.hasMore()) {
			sr = schemaDnSR.next();
		}
		if (sr != null) {
			subschemaSubentry = sr.getAttributes().get("subschemaSubentry");
		}
		if (subschemaSubentry != null && subschemaSubentry.size() > 0) {
			subschemaSubentryDN = (String) subschemaSubentry.get();
		}

		if (subschemaSubentryDN != null) {
			// get schema attributes from subschemaSubentryDN
			Attributes schemaAttrs = schemaCtx.getAttributes(
							subschemaSubentryDN, attrsToReturn != null ? attrsToReturn : new String[]{"*", "+"});

			if (schemaAttrs != null) {
				for(String attr: attrsToReturn) {
					Attribute schemaAttr = schemaAttrs.get(attr);
					if (schemaAttr != null) {
						attrsResult.put(schemaAttr.getID(), (List<String>) Collections.list(schemaAttr.getAll()));
					}
				}
			}
		}

		return attrsResult;
	}

	public List<String> sup(String dn, int level) throws NamingException {
		int ncLevel = (new LdapName(contextDn.toString())).size();

		LdapName lName = new LdapName(dn);
		List<String> cList = new ArrayList<String>();
		if (level > 0) {
			if (lName.size() > level) {
				for (int i = 0; i < level; i++) {
					lName.remove(lName.size() - 1);
				}
				cList.add(lName.toString());
			}
		} else if (level == 0) {
			cList.add(lName.toString());
			int size = lName.size();
			for (int i = 0; i < size - 1 && i < size - ncLevel; i++) {
				lName.remove(lName.size() - 1);
				cList.add(lName.toString());
			}
		} else {
			return null;
		}
		return cList;
	}

	/**
	 * Search for a list of attribute values
	 *
	 * This method is a simple LDAP search operation which is attended to
	 * return a list of the attribute values in all returned entries
	 *
	 * @param base the base of the search operation
	 * @param filter the filter of the search operation
	 * @param scope the scope of the search operation
	 * @param attrsNames table of attribute names to get
	 * @return Map of DNs of all entries that are returned by the directory with an associated map of attribute names and values (never null)
	 * @throws NamingException thrown if something goes wrong
	 */
	public Map<String, List<String>> entry(final String dn, final String filter)
					throws NamingException {

		Map<String, List<String>>  entry = new HashMap<String, List<String>>();
		
		SearchResult sr = this.readEntry(dn, filter, false);
		NamingEnumeration<? extends Attribute> ne = sr.getAttributes().getAll();
		while(ne.hasMore()) {
			List<String> valuesList = new ArrayList<String>();
			Attribute attribute = ne.next();
			NamingEnumeration<?> values = attribute.getAll();
			while(values.hasMore()) {
				Object value = values.next();
				if(value instanceof String) {
					valuesList.add((String)value);
//				} else {
//					LOGGER.info("DEBUG:Attribute " + attribute.getID() + " for entry " + dn + " contains non literal values (binary, ...) that are not handled !");
				}
			}
			entry.put(attribute.getID().toLowerCase(), valuesList);
		}
		
		return entry;
	}

	/**
	 * @return the contextDn
	 */
	public String getContextDn() {
		return contextDn.toString();
	}

	/**
	 * Close connection before this object is deleted by the garbage collector.
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// Close the TLS connection (revert back to the underlying LDAP association)
		if (tlsResponse != null) {
			tlsResponse.close();
		}

		// Close the connection to the LDAP server
		ctx.close();

		super.finalize();
	}

	/**
	 * Get the JNDI context.
	 * @return The LDAP context object in use by this class.
	 */
	public LdapContext getContext() {
		return ctx;
	}
}

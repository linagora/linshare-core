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
import java.util.Hashtable;
import java.util.List;

import javax.naming.CommunicationException;
import javax.naming.LimitExceededException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;

import org.linid.dm.authorization.lql.dnlist.DnList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinShareDnList extends DnList {

	final static protected Logger logger = LoggerFactory.getLogger(LinShareDnList.class);

	protected int searchPageSize;

	protected int searchSizeLimit;

	public LinShareDnList() {
		super();
		this.searchPageSize = 500;
		this.searchSizeLimit = 500;
	}

	public LinShareDnList(int searchPageSize, int searchSizeLimit) {
		super();
		this.searchPageSize = searchPageSize;
		this.searchSizeLimit = searchSizeLimit;
	}

	@Override
	public List<String> getDnList(LdapContext ldapCtx, String base, String filter, int scope) throws NamingException {
		if (searchPageSize == 0) {
			return getDnListNoPagination(ldapCtx, base, filter, scope);
		} else {
			return getDnListWithPagination(ldapCtx, base, filter, scope);
		}
	}

	private List<String> getDnListWithPagination(LdapContext ldapCtx, String base, String filter, int scope) throws NamingException {
		List<String> cList = new ArrayList<String>();
		SearchControls scs = new SearchControls();
		scs.setSearchScope(scope);
		// ManageReferralControl ?
//		scs.setDerefLinkFlag(false);
//		sc.setDerefLinkFlag(false);
//		sc.setReturningAttributes(new String[]{"1.1"});
//		sc.setSearchScope(scope);
//		sc.setReturningObjFlag(true);
		
		try {
			// Setting pagination control.
			ldapCtx.setRequestControls(new Control[] { new PagedResultsControl(searchPageSize, Control.CRITICAL) });
			
			// No attributes will be return. Official LDAP syntax
			scs.setReturningAttributes(new String [] { "1.1" }); 
			
			NamingEnumeration<SearchResult> results = ldapCtx.search(base, filter, scs);
			while (results != null && results.hasMore()) {
				SearchResult entry = (SearchResult) results.next();
				if(logger.isDebugEnabled()) {
					logger.debug("entry name : " + entry.getName());
				}

				// Handle the entry's response controls (if any)
				if (entry instanceof HasControls) {
					Control[] controls = ((HasControls) entry).getControls();
					if(logger.isDebugEnabled()) {
						logger.debug("entry name has controls " + controls.toString());
					}
				}

				// String dn = results.next().getName();
				String dn = entry.getName();
				if (base.length() > 0) {
					dn += "," + base;
				}
				cList.add(dn);
			}
		} catch (IOException e1) {
			logger.error("Can not set pagination control");
		} catch (NameNotFoundException nnfe) {
			logger.info("While evaluating getDnList, " + base + " seems to be inexistent !");
		} catch (CommunicationException e) {
			Hashtable<?, ?> env = ldapCtx.getEnvironment();
			Control[] controls = ldapCtx.getConnectControls();
			ldapCtx.close();
			ldapCtx = new InitialLdapContext(env, controls);
		}
		return cList;
	}

	private List<String> getDnListNoPagination(LdapContext ldapCtx, String base, String filter, int scope)
			throws NamingException {
        List<String> cList = new ArrayList<String>();
    	SearchControls scs = new SearchControls();
   		scs.setSearchScope(scope);
   		try {
   	   		NamingEnumeration<SearchResult> ne = ldapCtx.search(base, filter, scs);
   	        while(ne.hasMore()) {
   	        	String dn = ne.next().getName();
   	        	if(base.length() > 0) {
   	        		dn += "," + base;
   	        	}
   	        	cList.add(dn);
   	        }
   		} catch (NameNotFoundException nnfe) {
   			logger.info("While evaluating getDnList, " + base + " seems to be inexistent !");
   		} catch (LimitExceededException e) {
   			logger.error("Limit exceeded : " + e.getMessage());
   		} catch (CommunicationException e) {
   			Hashtable<?, ?> env = ldapCtx.getEnvironment();
   			Control[] controls = ldapCtx.getConnectControls();
   			ldapCtx.close();
   			ldapCtx = new InitialLdapContext(env, controls);
   		}
   		return cList;
	}

}

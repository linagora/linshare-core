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

		try {
			// Setting pagination control.
			ldapCtx.setRequestControls(new Control[] { new PagedResultsControl(searchPageSize, Control.CRITICAL) });
			
			NamingEnumeration<SearchResult> results = ldapCtx.search(base, filter, scs);
			while (results != null && results.hasMore()) {
				SearchResult entry = (SearchResult) results.next();
				logger.debug("entry name : " + entry.getName());
				logger.debug("entry attributes : " + entry.getAttributes());

				// Handle the entry's response controls (if any)
				if (entry instanceof HasControls) {
					Control[] controls = ((HasControls) entry).getControls();
					logger.debug("entry name has controls " + controls.toString());
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

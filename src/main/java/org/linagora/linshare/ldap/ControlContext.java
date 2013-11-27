package org.linagora.linshare.ldap;

import java.util.Map;

import javax.naming.directory.SearchControls;

import org.linagora.linshare.core.domain.entities.LdapAttribute;

public class ControlContext {

	protected Map<String, LdapAttribute> ldapDbAttributes;
	
	protected SearchControls searchControls;

	public ControlContext(Map<String, LdapAttribute> ldapDbAttributes, SearchControls searchControls) {
		super();
		this.ldapDbAttributes = ldapDbAttributes;
		this.searchControls = searchControls;
	}

	public Map<String, LdapAttribute> getLdapDbAttributes() {
		return ldapDbAttributes;
	}

	public void setLdapDbAttributes(Map<String, LdapAttribute> ldapDbAttributes) {
		this.ldapDbAttributes = ldapDbAttributes;
	}

	public SearchControls getSearchControls() {
		return searchControls;
	}

	public void setSearchControls(SearchControls searchControls) {
		this.searchControls = searchControls;
	}
}

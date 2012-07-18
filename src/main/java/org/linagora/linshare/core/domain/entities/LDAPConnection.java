package org.linagora.linshare.core.domain.entities;

import java.util.Properties;

import javax.naming.Context;

import org.linagora.linshare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linshare.core.utils.MySSLSocketFactory;

public class LDAPConnection {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	private final String identifier;
	private String providerUrl;
	private String securityAuth;
	private String securityPrincipal;
	private String securityCredentials;
	
	public LDAPConnection(LDAPConnectionVo ldapConn) {
		this.identifier = ldapConn.getIdentifier();
		this.providerUrl = ldapConn.getProviderUrl();
		this.securityAuth = ldapConn.getSecurityAuth();
		this.securityPrincipal = ldapConn.getSecurityPrincipal();
		this.securityCredentials = ldapConn.getSecurityCredentials();
	}
	
	public long getPersistenceId() {
		return persistenceId;
	}
	
	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}
	
	protected LDAPConnection() {
		this.identifier = null;
	}
	
	public LDAPConnection(String identifier, String providerUrl, String securityAuth) {
		this.identifier = identifier;
		this.providerUrl = providerUrl;
		this.securityAuth = securityAuth;
		this.securityCredentials = null;
		this.securityPrincipal = null;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public String getProviderUrl() {
		return providerUrl;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}

	public String getSecurityAuth() {
		return securityAuth;
	}

	public void setSecurityAuth(String securityAuth) {
		this.securityAuth = securityAuth;
	}

	public String getSecurityPrincipal() {
		return securityPrincipal;
	}

	public void setSecurityPrincipal(String securityPrincipal) {
		this.securityPrincipal = securityPrincipal;
	}

	public String getSecurityCredentials() {
		return securityCredentials;
	}

	public void setSecurityCredentials(String securityCredentials) {
		this.securityCredentials = securityCredentials;
	}

	public Properties toLdapProperties() {
		Properties ldapProperties = new Properties();
		ldapProperties.put(Context.PROVIDER_URL, this.providerUrl);
		ldapProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapProperties.put(Context.SECURITY_AUTHENTICATION, this.securityAuth);
		if (this.securityPrincipal != null) {
			ldapProperties.put(Context.SECURITY_PRINCIPAL, this.securityPrincipal);
		}
		if (this.securityCredentials != null) {
			ldapProperties.put(Context.SECURITY_CREDENTIALS, this.securityCredentials);
		}
		if (this.providerUrl.contains("ldaps://")) {
			ldapProperties.put(Context.SECURITY_PROTOCOL, "ssl");
			ldapProperties.put("java.naming.ldap.factory.socket", "org.linagora.linShare.core.utils.MySSLSocketFactory");
		}
		return ldapProperties;
	}

	@Override
	public String toString() {
		return "LDAPConnection : " + identifier + " : " + providerUrl;
	}

}

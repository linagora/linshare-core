package org.linagora.linShare.core.domain.vo;

import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linShare.core.domain.entities.LDAPConnection;

public class LDAPConnectionVo {

	private String identifier;
	private String providerUrl;
	private String securityAuth;
	private String securityPrincipal;
	private String securityCredentials;
	
	public LDAPConnectionVo() {
	}
	
	public LDAPConnectionVo(LDAPConnection ldapConn) {
		this.identifier = ldapConn.getIdentifier();
		this.providerUrl = ldapConn.getProviderUrl();
		this.securityAuth = ldapConn.getSecurityAuth();
		this.securityPrincipal = ldapConn.getSecurityPrincipal();
		this.securityCredentials = ldapConn.getSecurityCredentials();
	}
	
	public LDAPConnectionVo(String identifier, String providerUrl, String securityAuth) {
		this.identifier = identifier;
		this.providerUrl = providerUrl;
		this.securityAuth = securityAuth;
		this.securityCredentials = null;
		this.securityPrincipal = null;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Validate("required")
	public String getIdentifier() {
		return identifier;
	}

	@Validate("required")
	public String getProviderUrl() {
		return providerUrl;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}

	@Validate("required")
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
	
	@Override
	public String toString() {
		return identifier;
	}

}

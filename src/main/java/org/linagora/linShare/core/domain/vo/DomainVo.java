package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.entities.Domain;

public class DomainVo {

	private final String identifier;
	private String differentialKey;
	private DomainPatternVo pattern;
	private LDAPConnectionVo ldapConnection;
	private ParameterVo parameterVo;

	public DomainVo(Domain domain) {
		this.identifier = domain.getIdentifier();
		this.differentialKey = domain.getDifferentialKey();
		this.pattern = new DomainPatternVo(domain.getPattern());
		this.ldapConnection = new LDAPConnectionVo(domain.getLdapConnection());
		this.parameterVo = null;
	}

	public DomainVo(String identifier, String differentialKey,
			DomainPatternVo pattern, LDAPConnectionVo ldapConn,
			ParameterVo parameterVo) {
		this.identifier = identifier;
		this.differentialKey = differentialKey;
		this.pattern = pattern;
		this.ldapConnection = ldapConn;
		this.parameterVo = parameterVo;
	}

	public String getDifferentialKey() {
		return differentialKey;
	}

	public void setDifferentialKey(String differentialKey) {
		this.differentialKey = differentialKey;
	}

	public DomainPatternVo getPattern() {
		return pattern;
	}

	public void setPattern(DomainPatternVo pattern) {
		this.pattern = pattern;
	}

	public LDAPConnectionVo getLdapConnection() {
		return ldapConnection;
	}

	public void setLdapConnection(LDAPConnectionVo ldapConnection) {
		this.ldapConnection = ldapConnection;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public void setParameterVo(ParameterVo parameterVo) {
		this.parameterVo = parameterVo;
	}
	
	public ParameterVo getParameterVo() {
		return parameterVo;
	}
	
	@Override
	public String toString() {
		return identifier;
	}

}

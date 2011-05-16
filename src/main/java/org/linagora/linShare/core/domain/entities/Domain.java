package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.vo.ParameterVo;


public class Domain {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	private final String identifier;
	private String differentialKey;
	private DomainPattern pattern;
	private LDAPConnection ldapConnection;
	private Parameter parameter;

	public Domain(String identifier, String differentialKey,
			DomainPattern pattern, LDAPConnection ldapConn,
			Parameter parameter) {
		this.identifier = identifier;
		this.differentialKey = differentialKey;
		this.pattern = pattern;
		this.ldapConnection = ldapConn;
		this.parameter = parameter;
	}
	
	public long getPersistenceId() {
		return persistenceId;
	}
	
	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}
	
	protected Domain() {
		this.identifier = null;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getDifferentialKey() {
		return differentialKey;
	}

	public void setDifferentialKey(String differentialKey) {
		this.differentialKey = differentialKey;
	}

	public DomainPattern getPattern() {
		return pattern;
	}

	public void setPattern(DomainPattern pattern) {
		this.pattern = pattern;
	}

	public LDAPConnection getLdapConnection() {
		return ldapConnection;
	}

	public void setLdapConnection(LDAPConnection ldapConnection) {
		this.ldapConnection = ldapConnection;
	}
	
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public Parameter getParameter() {
		return parameter;
	}

}

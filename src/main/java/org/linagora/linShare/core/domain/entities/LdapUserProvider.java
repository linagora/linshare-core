
package org.linagora.linShare.core.domain.entities;

public class LdapUserProvider {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;
	
	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}
	
	private String baseDn;

	private LDAPConnection ldapconnexion;

	private DomainPattern pattern;

	public LdapUserProvider() {
	}
	

	public LdapUserProvider(String baseDn, LDAPConnection ldapconnexion,
			DomainPattern pattern) {
		super();
		this.baseDn = baseDn;
		this.ldapconnexion = ldapconnexion;
		this.pattern = pattern;
	}

	public DomainPattern getPattern() {
		return pattern;
	}

	public void setPattern(DomainPattern pattern) {
		this.pattern = pattern;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public void setLdapconnexion(LDAPConnection ldapconnexion) {
		this.ldapconnexion = ldapconnexion;
	}

	public LDAPConnection getLdapconnexion() {
		return ldapconnexion;
	}

	@Override
	public String toString() {
		return "LdapUserProvider : " + baseDn + " : " + ldapconnexion.getIdentifier() + " : " + pattern.getIdentifier();
	}
}

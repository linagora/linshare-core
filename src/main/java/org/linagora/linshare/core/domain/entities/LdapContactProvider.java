package org.linagora.linshare.core.domain.entities;

public class LdapContactProvider extends ContactProvider {

	private String baseDn;

	private ContactLdapPattern ldapPattern;

	private LdapConnection ldapConnexion;

	public LdapContactProvider(String baseDn, ContactLdapPattern ldapPattern,
			LdapConnection ldapConnexion) {
		super();
		this.baseDn = baseDn;
		this.ldapPattern = ldapPattern;
		this.ldapConnexion = ldapConnexion;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public ContactLdapPattern getLdapPattern() {
		return ldapPattern;
	}

	public void setLdapPattern(ContactLdapPattern ldapPattern) {
		this.ldapPattern = ldapPattern;
	}

	public LdapConnection getLdapConnexion() {
		return ldapConnexion;
	}

	public void setLdapConnexion(LdapConnection ldapConnexion) {
		this.ldapConnexion = ldapConnexion;
	}

	@Override
	public String toString() {
		return "LdapContactProvider [baseDn=" + baseDn + ", ldapPattern="
				+ ldapPattern.getUuid() + ", ldapConnexion=" + ldapConnexion.getUuid() + "]";
	}
}

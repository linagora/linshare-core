package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;

public abstract class Account {

	protected long id;
	
	protected String lsUid;
	
	protected Date creationDate;
	
	protected Date modificationDate;
	
	protected Role role = Role.SIMPLE;
	
	protected String locale;
	
	protected boolean enable;
	
	protected String password;
	
	protected boolean destroyed;
	
	protected AbstractDomain domain;
	
	protected Account owner;
	
	private Set<Entry> entries = new HashSet<Entry>();
	
	protected Set<ShareEntry> shareEntries = new HashSet<ShareEntry>();
	
	protected Set<Signature> signatures = new  HashSet<Signature>();
	
	public Account() {
		setCreationDate(new Date());
		setModificationDate(new Date());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLsUid() {
		return lsUid;
	}

	public void setLsUid(String lsUid) {
		this.lsUid = lsUid;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}
	
	public String getDomainId() {
		return ( (this.domain == null) ? null : this.domain.getIdentifier() );
	}
	
    public Set<Entry> getEntries() {
		return entries;
	}

	public void setEntries(Set<Entry> entries) {
		this.entries = entries;
	}

	public Set<ShareEntry> getShareEntries() {
		return shareEntries;
	}

	public void setShareEntries(Set<ShareEntry> shareEntries) {
		this.shareEntries = shareEntries;
	}
	
	public Set<Signature> getSignatures() {
		return signatures;
	}

	public void setSignatures(Set<Signature> signatures) {
		this.signatures = signatures;
	}

	public abstract AccountType getAccountType();
	
	public abstract String getAccountReprentation();

}

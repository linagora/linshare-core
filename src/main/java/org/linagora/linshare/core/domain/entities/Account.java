package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;

public abstract class Account {

	protected long id;
	
	protected String lsUuid;
	
	protected Date creationDate;
	
	protected Date modificationDate;
	
	protected Role role = Role.SIMPLE;
	
	protected String locale;
	
	protected String externalMailLocale;
	
	protected boolean enable;
	
	protected String password;
	
	protected boolean destroyed;
	
	protected AbstractDomain domain;
	
	protected Account owner;
	
	protected Set<Entry> entries = new HashSet<Entry>();
	
	protected Set<ShareEntry> shareEntries = new HashSet<ShareEntry>();
	
	protected Set<Signature> signatures = new  HashSet<Signature>();
	
	protected Set<Tag> tags = new  HashSet<Tag>();
	
	protected Set<TagFilter> tagFilters = new  HashSet<TagFilter>();
	
	protected Set<View> views = new  HashSet<View>();
	
	public Account() {
		setCreationDate(new Date());
		setModificationDate(new Date());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lsUuid == null) ? 0 : lsUuid.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (lsUuid == null) {
			if (other.lsUuid != null)
				return false;
		} else if (!lsUuid.equals(other.lsUuid))
			return false;
		return true;
	}
	

	public boolean isSuperAdmin() {
		if(this.getRole().equals(Role.SUPERADMIN)) {
			return true;
		}
		return false;
	}
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLsUuid() {
		return lsUuid;
	}

	public void setLsUuid(String lsUuid) {
		this.lsUuid = lsUuid;
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

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Set<TagFilter> getTagFilters() {
		return tagFilters;
	}

	public void setTagFilters(Set<TagFilter> tagFilters) {
		this.tagFilters = tagFilters;
	}

	
	public abstract AccountType getAccountType();

	
	public abstract String getAccountReprentation();


	public String getExternalMailLocale() {
		//TODO : add ihm for external mail locale value. For now, using user locale.
//		return externalMailLocale;
		return locale;
	}


	public void setExternalMailLocale(String externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public Set<View> getViews() {
		return views;
	}

	public void setViews(Set<View> views) {
		this.views = views;
	}
	
}

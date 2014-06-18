/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;

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
		return this.getRole().equals(Role.SUPERADMIN);
	}
	
	public boolean isAdmin() {
		return this.getRole().equals(Role.ADMIN);
	}

	public boolean isSystemAccount() {
		 return this.getRole().equals(Role.SYSTEM);
	}

	public boolean isTechnicalAccount() {
		 return this.getRole().equals(Role.SYSTEM);
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
}

/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AccountPurgeStepEnum;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;

public abstract class Account {

	protected long id;

	protected String lsUuid;

	protected String mail;

	protected Date creationDate;

	protected Date modificationDate;

	protected Role role = Role.SIMPLE;

	protected SupportedLanguage locale;

	protected Language externalMailLocale;

	protected String cmisLocale;

	protected boolean enable;

	protected String password;

	protected long destroyed;

	protected AccountPurgeStepEnum purgeStep = AccountPurgeStepEnum.IN_USE;

	protected AbstractDomain domain;

	protected Account owner;

	protected Set<Entry> entries = new HashSet<Entry>();

	protected Set<ShareEntry> shareEntries = new HashSet<ShareEntry>();

	protected Set<Signature> signatures = new HashSet<Signature>();

	protected TechnicalAccountPermission permission;

	protected Set<UploadRequestGroup> uploadRequestGroups;

	protected Set<PasswordHistory> passwordHistories;

	protected Date secondFACreationDate;

	protected String secondFASecret;

	protected Date authenticationFailureLastDate;

	protected Date authenticationSuccessLastDate;

	protected Integer authenticationFailureCount;

	public Account() {
		setCreationDate(new Date());
		setModificationDate(new Date());
		authenticationFailureCount = 0;
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

	public SupportedLanguage getLocale() {
		return locale;
	}

	public void setLocale(SupportedLanguage locale) {
		this.locale = locale;
	}

	public void setBusinessLocale(SupportedLanguage locale) {
		if (locale != null) {
			this.locale = locale;
		}
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

	public long getDestroyed() {
		return destroyed;
	}

	public void setDestroyed(long destroyed) {
		this.destroyed = destroyed;
	}

	public AccountPurgeStepEnum getPurgeStep() {
		return purgeStep;
	}

	public void setPurgeStep(AccountPurgeStepEnum purgeStep) {
		this.purgeStep = purgeStep;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	public String getDomainId() {
		return ((this.domain == null) ? null : this.domain.getUuid());
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

	public abstract String getAccountRepresentation();

	public abstract String getFullName();

	public abstract ContainerQuotaType getContainerQuotaType();

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public Locale getJavaExternalMailLocale() {
		Locale locale = Locale.ENGLISH;
		switch (getExternalMailLocale()) {
		case FRENCH:
			locale = Locale.FRANCE;
			break;
		default:
			break;
		}
		return locale;
	}

	public void setExternalMailLocale(Language externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public void setBusinessExternalMailLocale(Language externalMailLocale) {
		if (externalMailLocale != null) {
			this.externalMailLocale = externalMailLocale;
		}
	}

	public TechnicalAccountPermission getPermission() {
		return permission;
	}

	public void setPermission(TechnicalAccountPermission permission) {
		this.permission = permission;
	}

	public Set<UploadRequestGroup> getUploadRequestGroups() {
		return uploadRequestGroups;
	}

	public void setUploadRequestGroups(Set<UploadRequestGroup> uploadRequestGroups) {
		this.uploadRequestGroups = uploadRequestGroups;
	}

	public String getCmisLocale() {
		return cmisLocale;
	}

	public void setCmisLocale(String cmisLocale) {
		this.cmisLocale = cmisLocale;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Role Helpers
	 */

	public boolean hasAllRights() {
		return hasSuperAdminRole() || hasSystemAccountRole();
	}

	public boolean hasSuperAdminRole() {
		return this.getRole().equals(Role.SUPERADMIN);
	}

	public boolean hasAdminRole() {
		return this.getRole().equals(Role.ADMIN);
	}

	public boolean hasSimpleRole() {
		return this.getRole().equals(Role.SIMPLE);
	}

	public boolean hasSystemAccountRole() {
		return this.getRole().equals(Role.SYSTEM);
	}

	public boolean hasDelegationRole() {
		return this.getRole().equals(Role.DELEGATION);
	}

	public boolean hasUploadRequestRole() {
		return this.getRole().equals(Role.UPLOAD_REQUEST);
	}

	public boolean hasSafeRole() {
		return this.getRole().equals(Role.SAFE);
	}

	/**
	 * Account type Helpers
	 */

	public boolean isUser() {
		return isGuest() || isInternal() || isRoot();
	}

	public boolean isGuest() {
		return this.getAccountType().equals(AccountType.GUEST);
	}

	public boolean isWorkGroup() {
		return this.getAccountType().equals(AccountType.THREAD);
	}

	public boolean isRoot() {
		return this.getAccountType().equals(AccountType.ROOT);
	}

	public boolean isInternal() {
		return this.getAccountType().equals(AccountType.INTERNAL);
	}

	@Override
	public String toString() {
		return getAccountRepresentation();
	}

	public Date getSecondFACreationDate() {
		return secondFACreationDate;
	}

	public void setSecondFACreationDate(Date secondFACreationDate) {
		this.secondFACreationDate = secondFACreationDate;
	}

	public String getSecondFASecret() {
		return secondFASecret;
	}

	public void setSecondFASecret(String secondFASecret) {
		this.secondFASecret = secondFASecret;
	}

	public boolean isUsing2FA() {
		return this.secondFASecret != null;
	}

	public Set<PasswordHistory> getPasswordHistories() {
		return passwordHistories;
	}

	public void setPasswordHistories(Set<PasswordHistory> passwordHistories) {
		this.passwordHistories = passwordHistories;
	}

	public Date getAuthenticationFailureLastDate() {
		return authenticationFailureLastDate;
	}

	public void setAuthenticationFailureLastDate(Date authenticationFailureLastDate) {
		this.authenticationFailureLastDate = authenticationFailureLastDate;
	}

	public Integer getAuthenticationFailureCount() {
		return authenticationFailureCount;
	}

	public void setAuthenticationFailureCount(Integer authenticationFailureCount) {
		this.authenticationFailureCount = authenticationFailureCount;
	}

	public Date getAuthenticationSuccessLastDate() {
		return authenticationSuccessLastDate;
	}

	public void setAuthenticationSuccessLastDate(Date authenticationSuccessLastDate) {
		this.authenticationSuccessLastDate = authenticationSuccessLastDate;
	}
}

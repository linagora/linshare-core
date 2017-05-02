/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

public abstract class AbstractDomain {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	protected String uuid;

	protected String label;

	protected SupportedLanguage defaultTapestryLocale;

	protected Language externalMailLocale;

	protected Role defaultRole;

	protected String description;

	protected boolean template;

	protected boolean enable;

	protected DomainPolicy policy;

	protected Set<DomainAccessRule> domainAccessRules;

	protected Set<Functionality> functionalities;

	protected AbstractDomain parentDomain;

	protected Set<AbstractDomain> subdomain;

	protected List<ShareExpiryRule> shareExpiryRules;

	protected Long authShowOrder;

	//mail configurations
	private MailConfig currentMailConfiguration;
	private Set<MailLayout> mailLayouts;
	private Set<MailFooter> mailFooters;
	private Set<MailConfig> mailConfigs;
	private Set<MailContent> mailContents;

	private MimePolicy mimePolicy;

	private Set<MimePolicy> mimePolicies;

	private Set<UploadPropositionFilter> uploadPropositionFilters;

	private Set<UploadProposition> uploadPropositions;

	protected UserProvider userProvider;

	protected Set<ContactProvider> contactProvider;

	protected WelcomeMessages currentWelcomeMessage;

	protected Set<WelcomeMessages> welcomeMessages;

	protected AbstractDomain() {
		this.uuid = null;
	}

	protected AbstractDomain(String label) {
		this.label = label;
		this.description = "";
		this.functionalities = new HashSet<Functionality>();
		this.domainAccessRules = new HashSet<DomainAccessRule>();
		this.parentDomain = null;
		this.subdomain = new HashSet<AbstractDomain>();
		this.defaultRole = Role.SIMPLE;
		this.defaultTapestryLocale = SupportedLanguage.ENGLISH;
		this.externalMailLocale = Language.ENGLISH;
		this.enable = true;
		this.template = false;
		this.shareExpiryRules = new ArrayList<ShareExpiryRule>();
		this.policy = null;
		this.authShowOrder = new Long(1);
		this.mailLayouts = new HashSet<MailLayout>();
		this.mailFooters = new HashSet<MailFooter>();
		this.mailContents = new HashSet<MailContent>();
		this.mailConfigs = new HashSet<MailConfig>();
		this.mimePolicies = new HashSet<MimePolicy>();
		this.currentWelcomeMessage = null;
		this.uuid = UUID.randomUUID().toString();
	}

	public AbstractDomain(DomainDto domainDto, AbstractDomain parent) {
		this.uuid = domainDto.getIdentifier();
		this.label = domainDto.getLabel();
		this.description = domainDto.getDescription();
		this.functionalities = new HashSet<Functionality>();
		this.domainAccessRules = new HashSet<DomainAccessRule>();
		this.parentDomain = parent;
		this.enable = true;
		this.template = false;
		this.subdomain = new HashSet<AbstractDomain>();
		this.defaultRole = Role.valueOf(domainDto.getUserRole());
		this.defaultTapestryLocale = domainDto.getLanguage();
		this.externalMailLocale = domainDto.getExternalMailLocale();
		this.authShowOrder = domainDto.getAuthShowOrder();
//		TODO this.mimePolicy = new MimePolicy();
		if (description == null) {
			this.description = "";
		}
	}

	public void updateDomainWith(AbstractDomain d) {
		this.label = d.getLabel();
		this.description = d.getDescription();
		this.defaultRole = d.getDefaultRole();
		this.defaultTapestryLocale = d.getDefaultTapestryLocale();
		this.externalMailLocale = d.getExternalMailLocale();
		this.enable = d.isEnable();
		this.authShowOrder = d.getAuthShowOrder();
		this.currentWelcomeMessage = d.getCurrentWelcomeMessage();
		if (description == null) {
			this.description = "";
		}
	}

	public SupportedLanguage getDefaultTapestryLocale() {
		return defaultTapestryLocale;
	}

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(Language externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public Role getDefaultRole() {
		return defaultRole;
	}

	public String getDescription() {
		return description;
	}

	public Set<Functionality> getFunctionalities() {
		return functionalities;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getPersistenceId() {
		return persistenceId;
	}

	public boolean isEnable() {
		return enable;
	}

	public boolean isTemplate() {
		return template;
	}

	public void setDefaultTapestryLocale(SupportedLanguage defaultTapestryLocale) {
		this.defaultTapestryLocale = defaultTapestryLocale;
	}

	public void setDefaultRole(Role defaultRole) {
		this.defaultRole = defaultRole;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setFunctionalities(Set<Functionality> functionalities) {
		this.functionalities = functionalities;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return "Domain [uuid=" + uuid + ", label=" + label + "]";
	}

	public AbstractDomain getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(AbstractDomain parentDomain) {
		this.parentDomain = parentDomain;
	}

	public Set<AbstractDomain> getSubdomain() {
		return subdomain;
	}

	public void addSubdomain(AbstractDomain subdomain) {
		this.subdomain.add(subdomain);
	}

	public void addFunctionality(Functionality functionality) {
		this.functionalities.add(functionality);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public UserProvider getUserProvider() {
		return userProvider;
	}

	public void setUserProvider(UserProvider userProvider) {
		this.userProvider = userProvider;
	}

	public DomainPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(DomainPolicy policy) {
		this.policy = policy;
	}

	public Set<DomainAccessRule> getDomainAccessRules() {
		return domainAccessRules;
	}

	public void setDomainAccessRules(Set<DomainAccessRule> domainAccessRules) {
		this.domainAccessRules = domainAccessRules;
	}

	public List<ShareExpiryRule> getShareExpiryRules() {
		return shareExpiryRules;
	}

	public void setShareExpiryRules(List<ShareExpiryRule> shareExpiryRules) {
		this.shareExpiryRules = shareExpiryRules;
	}

	public abstract DomainType getDomainType();

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null)
			return false;
		AbstractDomain d = (AbstractDomain) arg0;
		return this.getUuid().equals(d.getUuid());
	}

	@Override
	public int hashCode() {
		return this.getUuid().hashCode();
	}

	public Long getAuthShowOrder() {
		return authShowOrder;
	}

	public void setAuthShowOrder(Long authShowOrder) {
		this.authShowOrder = authShowOrder;
	}

	public boolean isManagedBy(Account account) {
		if(account.hasSuperAdminRole()) {
			return true;
		}
		if (account.hasAdminRole()) {
			if (this.uuid.equals(account.getDomainId())) {
				// You have the right to manage your own domain
				return true;
			} else {
				// Checking if a parent domain is managed by the current actor
				return checkIfManagedByParent(this, account.getDomainId());
			}
		}
		return false;
	}

	public boolean isRootDomain() {
		return false;
	}

	public boolean isGuestDomain() {
		return false;
	}

	private boolean checkIfManagedByParent(AbstractDomain domain, String accountDomainId) {
		AbstractDomain d = domain.getParentDomain();
		if (d != null) {
			if (d.getUuid().equals(accountDomainId)) {
				return true;
			} else {
				return checkIfManagedByParent(d, accountDomainId);
			}
		}
		return false;
	}

	public MailConfig getCurrentMailConfiguration() {
		return currentMailConfiguration;
	}

	public void setCurrentMailConfiguration(MailConfig currentMailConfiguration) {
		this.currentMailConfiguration = currentMailConfiguration;
	}

	public Set<MailLayout> getMailLayouts() {
		return mailLayouts;
	}

	public void setMailLayouts(Set<MailLayout> mailLayouts) {
		this.mailLayouts = mailLayouts;
	}

	public Set<MailFooter> getMailFooters() {
		return mailFooters;
	}

	public void setMailFooters(Set<MailFooter> mailFooters) {
		this.mailFooters = mailFooters;
	}

	public Set<MailConfig> getMailConfigs() {
		return mailConfigs;
	}

	public void setMailConfigs(Set<MailConfig> mailConfigs) {
		this.mailConfigs = mailConfigs;
	}

	public Set<MailContent> getMailContents() {
		return mailContents;
	}

	public void setMailContents(Set<MailContent> mailContents) {
		this.mailContents = mailContents;
	}

	public MimePolicy getMimePolicy() {
		return mimePolicy;
	}

	public void setMimePolicy(MimePolicy mimePolicy) {
		this.mimePolicy = mimePolicy;
	}

	public Set<MimePolicy> getMimePolicies() {
		return mimePolicies;
	}

	public void setMimePolicies(Set<MimePolicy> mimePolicies) {
		this.mimePolicies = mimePolicies;
	}

	public Set<UploadPropositionFilter> getUploadPropositionFilters() {
		return uploadPropositionFilters;
	}

	public void setUploadPropositionFilters(
			Set<UploadPropositionFilter> uploadPropositionFilters) {
		this.uploadPropositionFilters = uploadPropositionFilters;
	}

	public Set<UploadProposition> getUploadPropositions() {
		return uploadPropositions;
	}

	public void setUploadPropositions(Set<UploadProposition> uploadPropositions) {
		this.uploadPropositions = uploadPropositions;
	}

	public Set<ContactProvider> getContactProvider() {
		return contactProvider;
	}

	public void setContactProvider(Set<ContactProvider> contactProvider) {
		this.contactProvider = contactProvider;
	}

	public WelcomeMessages getCurrentWelcomeMessage() {
		return currentWelcomeMessage;
	}

	public void setCurrentWelcomeMessages(WelcomeMessages currentCustomisation) {
		this.currentWelcomeMessage = currentCustomisation;
	}

	public Set<WelcomeMessages> getWelcomeMessages() {
		return welcomeMessages;
	}

	public void setWelcomeMessages(Set<WelcomeMessages> customisations) {
		this.welcomeMessages = customisations;
	}
}

/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPWorkSpaceProviderDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPGroupProviderDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.WelcomeMessagesDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;

// we need to ignore route property. It was added my Restangular (Javascript IHM)

@Deprecated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"route"})
@XmlRootElement(name = "Domain")
@Schema(name = "Domain", description = "A domain contains ")
public class DomainDto {

	@Schema(description = "Identifier")
	private String identifier;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "Type")
	private String type;

	@Schema(description = "UserRole")
	private String userRole;

	@Schema(description = "Language")
	private SupportedLanguage language;

	@Schema(description = "Language")
	private Language externalMailLocale;

	@Schema(description = "Policy")
	private DomainPolicyDto policy;

	@Schema(description = "Providers")
	private List<LDAPUserProviderDto> providers = new ArrayList<LDAPUserProviderDto>();

	@Schema(description = "workSpaceProviders")
	private List<LDAPWorkSpaceProviderDto> workSpaceProviders = new ArrayList<LDAPWorkSpaceProviderDto>();

	@Schema(description = "groupProviders")
	private List<LDAPGroupProviderDto> groupProviders = new ArrayList<LDAPGroupProviderDto>();

	@Schema(description = "Children")
	private List<DomainDto> children = new ArrayList<DomainDto>();

	@Schema(description = "Parent")
	private String parent;

	@Schema(description = "AuthShowOrder")
	private Long authShowOrder;

	@Schema(description = "MimePolicyUuid")
	private String mimePolicyUuid;

	@Schema(description = "MailConfigUuid")
	private String mailConfigUuid;

	@Schema(description = "currentWelcomeMessage")
	private WelcomeMessagesDto currentWelcomeMessage;

	@Schema(description = "Quota uuid")
	private String quota;

	protected DomainDto(final AbstractDomain domain, boolean light) {
		this.identifier = domain.getUuid();
		this.label = domain.getLabel();
		this.type = domain.getDomainType().toString();
		if (domain.getMimePolicy() != null) {
			mimePolicyUuid = domain.getMimePolicy().getUuid();
		}
		if (domain.getCurrentWelcomeMessage() != null) {
			if (domain.getCurrentMailConfiguration() != null) {
				mailConfigUuid = domain.getCurrentMailConfiguration().getUuid();
			}
		}
		if (domain.getCurrentWelcomeMessage() != null) {
			this.currentWelcomeMessage = new WelcomeMessagesDto(domain.getCurrentWelcomeMessage(), false);
		}
		if (!light) {
			this.description = domain.getDescription();
			this.language = domain.getDefaultTapestryLocale();
			this.externalMailLocale = domain.getExternalMailLocale();
			this.userRole = domain.getDefaultRole().toString();
			if (domain.getPolicy() != null)
				this.policy = new DomainPolicyDto(domain.getPolicy());
			this.authShowOrder = domain.getAuthShowOrder();
			if (domain.getUserProvider() != null) {
				LDAPUserProviderDto ldapUserProviderDto = domain.getUserProvider().toLDAPUserProviderDto();
				if (ldapUserProviderDto != null) {
					this.providers.add(ldapUserProviderDto);
				}
			}
			if (domain.getParentDomain() != null) {
				this.parent = domain.getParentDomain().getUuid();
			}
			if (domain.getGroupProvider() != null) {
				this.groupProviders.add(domain.getGroupProvider().toLDAPGroupProviderDto());
			}
			if (domain.getWorkSpaceProvider() != null) {
				this.workSpaceProviders.add(domain.getWorkSpaceProvider().toLDAPWorkSpaceProviderDto());
			}
		}
	}

	public DomainDto() {
		super();
	}

	public static DomainDto getSimple(final AbstractDomain domain) {
		return new DomainDto(domain, true);
	}

	public static DomainDto getFull(final AbstractDomain domain) {
		return new DomainDto(domain, false);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String uuid) {
		this.identifier = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public SupportedLanguage getLanguage() {
		return language;
	}

	public void setLanguage(SupportedLanguage language) {
		this.language = language;
	}

	public Language getExternalMailLocale() {
		return externalMailLocale;
	}

	public void setExternalMailLocale(Language externalMailLocale) {
		this.externalMailLocale = externalMailLocale;
	}

	public DomainPolicyDto getPolicy() {
		return policy;
	}

	public void setPolicy(DomainPolicyDto policy) {
		this.policy = policy;
	}

	public List<LDAPUserProviderDto> getProviders() {
		return providers;
	}

	public void setProviders(List<LDAPUserProviderDto> providers) {
		this.providers = providers;
	}

	public List<DomainDto> getChildren() {
		return children;
	}

	public void setChildren(List<DomainDto> children) {
		this.children = children;
	}

	public void addChild(DomainDto child) {
		if (this.children == null) {
			this.children = Lists.newArrayList();
		}
		this.children.add(child);
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Long getAuthShowOrder() {
		return authShowOrder;
	}

	public void setAuthShowOrder(Long authShowOrder) {
		this.authShowOrder = authShowOrder;
	}

	public String getMimePolicyUuid() {
		return mimePolicyUuid;
	}

	public void setMimePolicyUuid(String mimePolicyUuid) {
		this.mimePolicyUuid = mimePolicyUuid;
	}

	public String getMailConfigUuid() {
		return mailConfigUuid;
	}

	public void setMailConfigUuid(String mailConfigUuid) {
		this.mailConfigUuid = mailConfigUuid;
	}

	public void setCurrentWelcomeMessage (WelcomeMessagesDto welcomeMessageDto) {
		this.currentWelcomeMessage = welcomeMessageDto;
	}

	public WelcomeMessagesDto getCurrentWelcomeMessage() {
		return currentWelcomeMessage;
	}

	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public List<LDAPGroupProviderDto> getGroupProviders() {
		return groupProviders;
	}

	public void setGroupProviders(List<LDAPGroupProviderDto> groupProviders) {
		this.groupProviders = groupProviders;
	}

	public List<LDAPWorkSpaceProviderDto> getWorkSpaceProviders() {
		return workSpaceProviders;
	}

	public void setWorkSpaceProviders(List<LDAPWorkSpaceProviderDto> workSpaceProviders) {
		this.workSpaceProviders = workSpaceProviders;
	}
}

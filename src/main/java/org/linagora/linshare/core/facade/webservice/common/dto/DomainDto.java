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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPUserProviderDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.WelcomeMessagesDto;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

// we need to ignore route property. It was added my Restangular (Javascript IHM)

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties({"route"})
@XmlRootElement(name = "Domain")
@ApiModel(value = "Domain", description = "A domain contains ")
public class DomainDto {

	@ApiModelProperty(value = "Identifier")
	private String identifier;

	@ApiModelProperty(value = "Label")
	private String label;

	@ApiModelProperty(value = "Description")
	private String description;

	@ApiModelProperty(value = "Type")
	private String type;

	@ApiModelProperty(value = "UserRole")
	private String userRole;

	@ApiModelProperty(value = "Language")
	private SupportedLanguage language;

	@ApiModelProperty(value = "Language")
	private Language externalMailLocale;

	@ApiModelProperty(value = "Policy")
	private DomainPolicyDto policy;

	@ApiModelProperty(value = "Providers")
	private List<LDAPUserProviderDto> providers = new ArrayList<LDAPUserProviderDto>();

	@ApiModelProperty(value = "Children")
	private List<DomainDto> children = new ArrayList<DomainDto>();

	@ApiModelProperty(value = "Parent")
	private String parent;

	@ApiModelProperty(value = "AuthShowOrder")
	private Long authShowOrder;

	@ApiModelProperty(value = "MimePolicyUuid")
	private String mimePolicyUuid;

	@ApiModelProperty(value = "MailConfigUuid")
	private String mailConfigUuid;

	@ApiModelProperty(value = "currentWelcomeMessage")
	private WelcomeMessagesDto currentWelcomeMessage;

	@ApiModelProperty(value = "Quota uuid")
	private String quota;

	protected DomainDto(final AbstractDomain domain, boolean light,
			boolean recursive) {
		this.identifier = domain.getUuid();
		this.label = domain.getLabel();
		this.type = domain.getDomainType().toString();
		mimePolicyUuid = domain.getMimePolicy().getUuid();
		mailConfigUuid = domain.getCurrentMailConfiguration().getUuid();
		this.currentWelcomeMessage = new WelcomeMessagesDto(domain.getCurrentWelcomeMessage(), false);
		if (!light) {
			this.description = domain.getDescription();
			this.language = domain.getDefaultTapestryLocale();
			this.externalMailLocale = domain.getExternalMailLocale();
			this.userRole = domain.getDefaultRole().toString();
			this.policy = new DomainPolicyDto(domain.getPolicy());
			this.authShowOrder = domain.getAuthShowOrder();
			if (domain.getUserProvider() != null) {
				this.providers.add(domain.getUserProvider().toLDAPUserProviderDto());
			}
			if (domain.getParentDomain() != null) {
				this.parent = domain.getParentDomain().getUuid();
			}
		}
		if (recursive) {
			for (AbstractDomain child : domain.getSubdomain()) {
				DomainDto childDto = new DomainDto(child, light, recursive);
				this.children.add(childDto);
				childDto.parent = this.identifier;
			}
		}
	}

	public DomainDto() {
		super();
	}

	public static DomainDto getSimple(final AbstractDomain domain) {
		return new DomainDto(domain, true, false);
	}

	public static DomainDto getFull(final AbstractDomain domain) {
		return new DomainDto(domain, false, false);
	}

	public static DomainDto getSimpleTree(final AbstractDomain domain) {
		return new DomainDto(domain, true, true);
	}

	public static DomainDto getFullTree(final AbstractDomain domain) {
		return new DomainDto(domain, false, true);
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
}

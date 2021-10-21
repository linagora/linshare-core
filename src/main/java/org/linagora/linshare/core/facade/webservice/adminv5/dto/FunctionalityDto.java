/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "Functionality",
	description = "Functionalities are used to configure the application"
)
public class FunctionalityDto implements Comparable<FunctionalityDto> {

	@Schema(name = "Domain", description = "A LinShare's domain")
	public class DomainDto {
	
		@Schema(description = "Domain's uuid")
		private String uuid;
	
		@Schema(description = "Domain's name")
		private String name;
	
		public DomainDto() {
			super();
		}
	
		public DomainDto(AbstractDomain domain) {
			this.setUuid(domain.getUuid());
			this.setName(domain.getLabel());
		}
	
		public String getUuid() {
			return uuid;
		}
	
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
	
		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}
	}

	@Schema(description = "Identifier")
	protected String identifier;

	@Schema(description = "Type")
	protected FunctionalityType type;

	@Schema(description = "Identifier of the parent functionality")
	protected String parentIdentifier;

	@Schema(description = "Indicate if this policy should be displayed or hidden")
	protected boolean hidden;

	@Schema(description = "Indicate if this policy should be displayed in read only mode")
	protected boolean readonly;

	@Schema(description = "Domain")
	protected DomainDto domain;

	@Schema(description = "Activation policy", name = "activationPolicy")
	protected PolicyDto activationPolicy;

	@Schema(description = "Configuration policy", name = "configurationPolicy")
	protected PolicyDto configurationPolicy;

	@Schema(description = "Delegation policy", name = "delegationPolicy")
	protected PolicyDto delegationPolicy;

	@Schema(description = "parameter")
	protected ParameterDto<?> parameter;

	public FunctionalityDto() {
		super();
	}

	public FunctionalityDto(Functionality f) {
		super();
		this.domain = new DomainDto(f.getDomain());
		this.identifier = f.getIdentifier();
		this.type = f.getType();
		this.parentIdentifier = f.getParentIdentifier();
		// FIXME
		this.hidden = !f.getDisplayable();
		this.readonly= false;
		AbstractFunctionality ancestorFunc = f.getAncestorFunc();
		if (ancestorFunc != null) {
			// Activation policy
			this.activationPolicy = new PolicyDto(f.getActivationPolicy(), ancestorFunc.getActivationPolicy());
			// Configuration policy
			this.configurationPolicy = new PolicyDto(f.getConfigurationPolicy(), ancestorFunc.getConfigurationPolicy());
			// Delegation policy
			if (f.getDelegationPolicy() != null) {
				this.delegationPolicy = new PolicyDto(f.getDelegationPolicy(), ancestorFunc.getDelegationPolicy());
			}
		} else {
			this.activationPolicy = new PolicyDto(f.getActivationPolicy());
			this.configurationPolicy = new PolicyDto(f.getConfigurationPolicy());
			// Delegation policy
			if (f.getDelegationPolicy() != null) {
				this.delegationPolicy = new PolicyDto(f.getDelegationPolicy());
			}
		}
		this.parameter = f.getParameter();
	}

	@Override
	public int compareTo(FunctionalityDto o) {
		return this.identifier.compareTo(o.getIdentifier());
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public DomainDto getDomain() {
		return domain;
	}

	public void setDomain(DomainDto domain) {
		this.domain = domain;
	}

	public PolicyDto getActivationPolicy() {
		return activationPolicy;
	}

	public void setActivationPolicy(PolicyDto activationPolicy) {
		this.activationPolicy = activationPolicy;
	}

	public PolicyDto getConfigurationPolicy() {
		return configurationPolicy;
	}

	public void setConfigurationPolicy(PolicyDto configurationPolicy) {
		this.configurationPolicy = configurationPolicy;
	}

	public PolicyDto getDelegationPolicy() {
		return delegationPolicy;
	}

	public void setDelegationPolicy(PolicyDto delegationPolicy) {
		this.delegationPolicy = delegationPolicy;
	}

	public FunctionalityType getType() {
		return type;
	}

	public void setType(FunctionalityType type) {
		this.type = type;
	}

	public ParameterDto<?> getParameter() {
		return parameter;
	}

	public void setParameter(ParameterDto<?> parameter) {
		this.parameter = parameter;
	}

	/**
	 * Transformers
	 */
	public static Function<Functionality, FunctionalityDto> toDto() {
		return new Function<Functionality, FunctionalityDto>() {
			@Override
			public FunctionalityDto apply(Functionality func) {
				return new FunctionalityDto(func);
			}
		};
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("identifier", identifier)
				.add("type", type)
				.add("parentIdentifier", parentIdentifier)
				.add("hidden", hidden)
				.add("readonly", readonly)
				.add("domain", domain)
				.add("activationPolicy", activationPolicy)
				.add("configurationPolicy", configurationPolicy)
				.add("delegationPolicy", delegationPolicy)
				.add("parameter", parameter)
				.toString();
	}
}

/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.facade.webservice.common.dto.PolicyDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties({"name"})
@XmlRootElement(name = "Functionality")
@Schema(name = "Functionality", description = "Functionalities are used to configure the application")
public class MailActivationAdminDto  implements Comparable<MailActivationAdminDto> {

	@Schema(description = "Identifier")
	protected String identifier;

	@XmlTransient
	@Schema(description = "Name")
	protected String name;

	@Schema(description = "Domain")
	protected String domain;

	@Schema(description = "ActivationPolicy")
	protected PolicyDto activationPolicy;

	@Schema(description = "ConfigurationPolicy")
	protected PolicyDto configurationPolicy;

	@Schema(description = "DelegationPolicy")
	protected PolicyDto delegationPolicy;
	
	@Schema(description = "enable")
	protected Boolean enable;

	@Schema(description = "displayable")
	protected boolean displayable;

	public MailActivationAdminDto(MailActivation m) {
		super();
		this.identifier = m.getIdentifier();
		this.name = m.getIdentifier();
		this.domain = m.getDomain().getUuid();
		this.activationPolicy = new PolicyDto(m.getActivationPolicy());
		this.configurationPolicy = new PolicyDto(m.getConfigurationPolicy());
		this.delegationPolicy = new PolicyDto(m.getDelegationPolicy());
		this.displayable = m.getDisplayable();
		this.enable = m.isEnable();
	}

	public MailActivationAdminDto() {
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
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

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "MailActivationAdminDto [identifier=" + identifier + ", name="
				+ name + ", domain=" + domain + "]";
	}

	@Override
	public int compareTo(MailActivationAdminDto o) {
		return this.identifier.compareTo(o.getIdentifier());
	}

	/**
	 * Transformers
	 */
	public static Function<MailActivation, MailActivationAdminDto> toDto() {
		return new Function<MailActivation, MailActivationAdminDto>() {
			@Override
			public MailActivationAdminDto apply(MailActivation arg0) {
				return new MailActivationAdminDto(arg0);
			}
		};
	}
}

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

package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.facade.webservice.common.dto.PolicyDto;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties({"name"})
@XmlRootElement(name = "Functionality")
@ApiModel(value = "Functionality", description = "Functionalities are used to configure the application")
public class MailActivationAdminDto  implements Comparable<MailActivationAdminDto> {

	@ApiModelProperty(value = "Identifier")
	protected String identifier;

	@XmlTransient
	@ApiModelProperty(value = "Name")
	protected String name;

	@ApiModelProperty(value = "Domain")
	protected String domain;

	@ApiModelProperty(value = "ActivationPolicy")
	protected PolicyDto activationPolicy;

	@ApiModelProperty(value = "ConfigurationPolicy")
	protected PolicyDto configurationPolicy;

	@ApiModelProperty(value = "DelegationPolicy")
	protected PolicyDto delegationPolicy;
	
	@ApiModelProperty(value = "enable")
	protected Boolean enable;

	@ApiModelProperty(value = "displayable")
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

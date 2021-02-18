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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.entities.AbstractFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PolicyDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.media.Schema;

// This field is added to the JSON object by the angular interface for internationalization. 
@JsonIgnoreProperties({"name"})
@XmlRootElement(name = "Functionality")
@Schema(name = "Functionality", description = "Functionalities are used to configure the application")
public class FunctionalityAdminDto implements Comparable<FunctionalityAdminDto> {

	@Schema(description = "Identifier")
	protected String identifier;

	@XmlTransient
	@Schema(description = "Name")
	private String name;

	@Schema(description = "Domain")
	protected String domain;

	@Schema(description = "Type")
	protected String type;

	@Schema(description = "ActivationPolicy")
	protected PolicyDto activationPolicy;

	@Schema(description = "ConfigurationPolicy")
	protected PolicyDto configurationPolicy;

	@Schema(description = "DelegationPolicy")
	protected PolicyDto delegationPolicy;

	// This field is designed to indicate if the parent functionality allow you to update the parameters.
	@Schema(description = "ParentAllowParametersUpdate")
	protected boolean parentAllowParametersUpdate;

	@Schema(description = "Parameters")
	protected List<ParameterDto> parameters;

	@Schema(description = "parentIdentifier")
	protected String parentIdentifier;

	@Schema(description = "functionalities")
	protected List<FunctionalityAdminDto> functionalities;

	@Schema(description = "displayable")
	protected boolean displayable;

	protected Boolean system;

	public FunctionalityAdminDto() {
		super();
	}

	public FunctionalityAdminDto(Integer version, Functionality f) {
		super();
		// System returned only for development purpose.
		this.system = f.isSystem();
		this.domain = f.getDomain().getUuid();
		this.identifier = f.getIdentifier();
		// Activation policy
		this.activationPolicy = new PolicyDto(f.getActivationPolicy());
		// Configuration policy
		this.configurationPolicy = new PolicyDto(f.getConfigurationPolicy());
		// Delegation policy
		if (f.getDelegationPolicy() != null) {
			this.delegationPolicy = new PolicyDto(f.getDelegationPolicy());
		}
		// Parameters
		this.parentAllowParametersUpdate = f.getParentAllowParametersUpdate();
		this.parameters = f.getParameters(version);
		this.type = f.getType().toString();
		this.parentIdentifier = f.getParentIdentifier();
		this.displayable = true;
		this.displayable = f.getDisplayable();
		functionalities = new ArrayList<FunctionalityAdminDto>();
		for (AbstractFunctionality child : f.getChildren()) {
			functionalities.add(new FunctionalityAdminDto(version, (Functionality)child));
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public boolean isParentAllowParametersUpdate() {
		return parentAllowParametersUpdate;
	}

	public void setParentAllowParametersUpdate(boolean parentAllowParametersUpdate) {
		this.parentAllowParametersUpdate = parentAllowParametersUpdate;
	}

	public List<ParameterDto> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParameterDto> parameters) {
		this.parameters = parameters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public List<FunctionalityAdminDto> getFunctionalities() {
		return Ordering.natural().immutableSortedCopy(functionalities);
	}

	public void setFunctionalities(List<FunctionalityAdminDto> functionalities) {
		this.functionalities = functionalities;
	}

	public void addFunctionalities(FunctionalityAdminDto functionality) {
		if (this.functionalities == null) {
			this.functionalities = Lists.newArrayList();
		}
		this.functionalities.add(functionality);
	}

	public boolean isDisplayable() {
		return displayable;
	}

	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
	}

	@Override
	public int compareTo(FunctionalityAdminDto o) {
		return this.identifier.compareTo(o.getIdentifier());
	}

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	@Override
	public String toString() {
		return "FunctionalityDto [identifier=" + identifier + ", domain="
				+ domain + "]";
	}

	/**
	 * Transformers
	 */
	public static Function<Functionality, FunctionalityAdminDto> toDto(Integer version) {
		return new Function<Functionality, FunctionalityAdminDto>() {
			@Override
			public FunctionalityAdminDto apply(Functionality arg0) {
				return new FunctionalityAdminDto(version, arg0);
			}
		};
	}
}

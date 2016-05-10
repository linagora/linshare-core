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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.linagora.linshare.core.domain.entities.AbstractFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.PolicyDto;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

// This field is added to the JSON object by the angular interface for internationalization. 
@JsonIgnoreProperties({"name"})
@XmlRootElement(name = "Functionality")
@ApiModel(value = "Functionality", description = "Functionalities are used to configure the application")
public class FunctionalityAdminDto implements Comparable<FunctionalityAdminDto> {

	@ApiModelProperty(value = "Identifier")
	protected String identifier;

	@XmlTransient
	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "Domain")
	protected String domain;

	@ApiModelProperty(value = "Type")
	protected String type;

	@ApiModelProperty(value = "ActivationPolicy")
	protected PolicyDto activationPolicy;

	@ApiModelProperty(value = "ConfigurationPolicy")
	protected PolicyDto configurationPolicy;

	@ApiModelProperty(value = "DelegationPolicy")
	protected PolicyDto delegationPolicy;

	// This field is designed to indicate if the parent functionality allow you to update the parameters.
	@ApiModelProperty(value = "ParentAllowParametersUpdate")
	protected boolean parentAllowParametersUpdate;

	@ApiModelProperty(value = "Parameters")
	protected List<ParameterDto> parameters;

	@ApiModelProperty(value = "parentIdentifier")
	protected String parentIdentifier;

	@ApiModelProperty(value = "functionalities")
	protected List<FunctionalityAdminDto> functionalities;

	@ApiModelProperty(value = "displayable")
	protected boolean displayable;

	protected Boolean system;

	public FunctionalityAdminDto() {
		super();
	}

	public FunctionalityAdminDto(Functionality f) {
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
		this.parameters = f.getParameters();
		this.type = f.getType().toString();
		this.parentIdentifier = f.getParentIdentifier();
		this.displayable = true;
		this.displayable = f.getDisplayable();
		functionalities = new ArrayList<FunctionalityAdminDto>();
		for (AbstractFunctionality child : f.getChildren()) {
			functionalities.add(new FunctionalityAdminDto((Functionality)child));
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
	public static Function<Functionality, FunctionalityAdminDto> toDto() {
		return new Function<Functionality, FunctionalityAdminDto>() {
			@Override
			public FunctionalityAdminDto apply(Functionality arg0) {
				return new FunctionalityAdminDto(arg0);
			}
		};
	}
}

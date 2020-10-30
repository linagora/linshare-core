/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;


public class Functionality extends AbstractFunctionality {

	/**
	 * True if the current entity is a parameter also known as sub functionality
	 */
	protected boolean param;

	/**
	 * If the current entity is a parameter, it must have a parent.
	 */
	private String parentIdentifier;

	private Date creationDate;

	private Date modificationDate;

	public Functionality() {
		super();
	}

	public Functionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain) {
		super(identifier, system, activationPolicy, configurationPolicy, domain);
	}

	public Functionality(FunctionalityNames fn, boolean b, Policy policy,
			Policy policy2, AbstractDomain domain) {
		this(fn.toString(), b, policy, policy2, domain);
	}

	public FunctionalityType getType() {
		return FunctionalityType.DEFAULT;
	}

	public List<ParameterDto> getParameters(Integer version) {
		return new ArrayList<ParameterDto>();
	}

	@Override
	public String toString() {
		return "Functionality : " + identifier + "(" + domain.getUuid() + ")";
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

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public Policy getDelegationPolicy() {
		return delegationPolicy;
	}

	public void setDelegationPolicy(Policy delegationPolicy) {
		this.delegationPolicy = delegationPolicy;
	}

	public boolean isParam() {
		return param;
	}

	public void setParam(boolean param) {
		this.param = param;
	}

	public void updateFunctionalityValuesOnlyFromDto(FunctionalityAdminDto functionality) {
		// no data in this class.
	}

	/**
	 * Need to be override by every subclasses.
	 * @param enable TODO
	 * @return FunctionalityDto
	 */
	protected FunctionalityDto getUserDto(boolean enable, Integer version) {
		return new FunctionalityDto();
	}

	/**
	 * Call it to transform your entity to a DTO.
	 * @return FunctionalityDto
	 */
	public FunctionalityDto toUserDto(Integer version) {
		boolean enable = activationPolicy.getStatus();
		FunctionalityDto f = getUserDto(enable, version);
		f.setIdentifier(identifier);
		f.setCanOverride(null);
		f.setEnable(enable);
		if (f.isEnable()) {
			if (delegationPolicy != null) {
				f.setCanOverride(delegationPolicy.getStatus());
			}
		}
		return f;
	}
}

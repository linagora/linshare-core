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

	public List<ParameterDto> getParameters() {
		return new ArrayList<ParameterDto>();
	}

	@Override
	public String toString() {
		return "Functionality : " + identifier + "(" + domain.getUuid() + ")";
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
	 * @return
	 */
	protected FunctionalityDto getUserDto(boolean enable) {
		return new FunctionalityDto();
	}

	/**
	 * Call it to transform your entity to a DTO.
	 * @return
	 */
	public FunctionalityDto toUserDto() {
		boolean enable = activationPolicy.getStatus();
		FunctionalityDto f = getUserDto(enable);
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

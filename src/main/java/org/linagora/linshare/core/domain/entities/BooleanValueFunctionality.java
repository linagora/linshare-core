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
package org.linagora.linshare.core.domain.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.BooleanParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedBooleanParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityBooleanDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.utils.Version;

public class BooleanValueFunctionality extends OneValueFunctionality<Boolean> {

	public BooleanValueFunctionality() {
		super();
	}

	public BooleanValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Boolean value) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.BOOLEAN;
	}

	@Override
	public boolean businessEquals(AbstractFunctionality obj, boolean checkPolicies) {
		if (super.businessEquals(obj, checkPolicies)) {
			BooleanValueFunctionality o = (BooleanValueFunctionality) obj;
			if (value.equals(o.getValue())) {
				logger.debug("BooleanValueFunctionality : " + this.toString() + " is equal to BooleanValueFunctionality "
						+ obj.toString());
				return true;
			}
		}
		logger.debug("BooleanValueFunctionality : " + this.toString() + " is not equal to BooleanValueFunctionality "
				+ obj.toString());
		return false;
	}

	@Override
	public void updateFunctionalityFrom(AbstractFunctionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}

	@Override
	public void updateFunctionalityValuesOnlyFrom(AbstractFunctionality functionality) {
		BooleanValueFunctionality f = (BooleanValueFunctionality) functionality;
		this.value = f.getValue();
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(Version version, FunctionalityAdminDto functionality) {
		List<ParameterDto> parameters = functionality.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			this.value = parameterDto.getBool();
		}
	}

	@Override
	public List<ParameterDto> getParameters(Version version) {
		List<ParameterDto> res = new ArrayList<ParameterDto>();
		res.add(new ParameterDto(this.getValue()));
		return res;
	}

	@Override
	protected FunctionalityDto getUserDto(boolean enable, Version version) {
		FunctionalityBooleanDto f = new FunctionalityBooleanDto();
		if (enable) {
			f.setValue(value);
		}
		return f;
	}

	/**
	 * Check activation policy, delegation policy if exists and user value if
	 * defined
	 * 
	 * @param userValue
	 *            : false, true, or null if user have not defined a value.
	 * @return true or false
	 */
	@Override
	public Boolean getFinalValue(Boolean userValue) {
		boolean result = getValue();
		if (getActivationPolicy().getStatus()) {
			if (getDelegationPolicy() != null && getDelegationPolicy().getStatus()) {
				if (userValue != null) {
					result = userValue;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	@Override
	public org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> getParameter() {
		// there is no default value for functionality parameters. sad.
		boolean parentValue = this.value;
		if (this.ancestorFunc != null) {
			parentValue = ((BooleanValueFunctionality)this.ancestorFunc).getValue();
		}
		NestedBooleanParameterDto defaut = new NestedBooleanParameterDto(this.value, parentValue);
		return new BooleanParameterDto(
			this.system,
			!this.getParentAllowParametersUpdate(),
			defaut);
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(
			org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> param) {
		Validate.isTrue(param.getType().equals("BOOLEAN"), "Wrong parameter type");
		NestedBooleanParameterDto dto = (NestedBooleanParameterDto) param.getDefaut();
		this.setValue(dto.getValue());
	}
}

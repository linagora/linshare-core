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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.LanguageParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedLanguageParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityEnumLangDto;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.Lists;

public class LanguageEnumValueFunctionality extends OneValueFunctionality<Language> {

	public LanguageEnumValueFunctionality() {
		super();
	}

	public LanguageEnumValueFunctionality(String identifier, boolean system, Policy activationPolicy, Policy configurationPolicy, AbstractDomain domain, Language value){
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.ENUM_LANG;
	}

	@Override
	public boolean businessEquals(AbstractFunctionality obj, boolean checkPolicies) {
		if (super.businessEquals(obj, checkPolicies)) {
			LanguageEnumValueFunctionality o = (LanguageEnumValueFunctionality) obj;
			if (value.equals(o.getValue())) {
				logger.debug("EnumValueFunctionality : " + this.toString() + " is equal to EnumValueFunctionality "
						+ obj.toString());
				return true;
			}
		}
		logger.debug("EnumValueFunctionality : " + this.toString() + " is not equal to EnumValueFunctionality "
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
		LanguageEnumValueFunctionality f = (LanguageEnumValueFunctionality) functionality;
		this.value = f.getValue();
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(Version version, FunctionalityAdminDto functionality) {
		List<ParameterDto> parameters = functionality.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			this.value = Language.fromTapestryLocale(parameterDto.getString());
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
		FunctionalityEnumLangDto f = new FunctionalityEnumLangDto();
		if (enable) {
			f.setValue(value);
			f.setUnits(Arrays.asList(Language.values()));
		}
		return f;
	}

	@Override
	public org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> getParameter() {
		// there is no default value for functionality parameters. sad.
		Language parentValue = this.value;
		if (this.ancestorFunc != null) {
			parentValue = ((LanguageEnumValueFunctionality)this.ancestorFunc).getValue();
		}
		NestedLanguageParameterDto defaut = new NestedLanguageParameterDto(this.value, parentValue, Lists.newArrayList(Language.values()));
		return new LanguageParameterDto(
			this.system,
			!this.getParentAllowParametersUpdate(),
			defaut);
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(
			org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> param) {
		Validate.isTrue(param.getType().equals("LANGUAGE"), "Wrong parameter type");
		NestedLanguageParameterDto dto = (NestedLanguageParameterDto) param.getDefaut();
		this.setValue(dto.getValue());
	}
}

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
import java.util.Arrays;
import java.util.List;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityEnumLangDto;

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
	public void updateFunctionalityValuesOnlyFromDto(Integer version, FunctionalityAdminDto functionality) {
		List<ParameterDto> parameters = functionality.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			this.value = Language.fromTapestryLocale(parameterDto.getString());
		}
	}

	@Override
	public List<ParameterDto> getParameters(Integer version) {
		List<ParameterDto> res = new ArrayList<ParameterDto>();
		res.add(new ParameterDto(this.getValue()));
		return res;
	}

	@Override
	protected FunctionalityDto getUserDto(boolean enable, Integer version) {
		FunctionalityEnumLangDto f = new FunctionalityEnumLangDto();
		if (enable) {
			f.setValue(value);
			f.setUnits(Arrays.asList(Language.values()));
		}
		return f;
	}
}

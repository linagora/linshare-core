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
import java.util.List;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalitySizeDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityTimeDto;

public class UnitValueFunctionality extends OneValueFunctionality<Integer> {

	protected Unit<?> unit;

	protected Integer maxValue;

	public UnitValueFunctionality() {
		super();
	}

	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
	}
	
	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit, Integer maxValue) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
		this.maxValue = maxValue;
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT;
	}

	public Unit<?> getUnit() {
		return unit;
	}

	public void setUnit(Unit<?> unit) {
		this.unit = unit;
	}

	@Override
	public boolean businessEquals(AbstractFunctionality obj, boolean checkPolicies) {
		if (super.businessEquals(obj, checkPolicies)) {
			UnitValueFunctionality o = (UnitValueFunctionality) obj;
			if (value.equals(o.getValue()) && maxValue.equals(o.getMaxValue())) {
				if (unit.businessEquals(o.getUnit())) {
					logger.debug("UnitValueFunctionality : " + this.toString() + " is equal to UnitValueFunctionality "
							+ obj.toString());
					return true;
				}
			}
		}
		logger.debug("UnitValueFunctionality : " + this.toString() + " is not equal to UnitValueFunctionality "
				+ obj.toString());
		return false;
	}

	public Object clone() {
		UnitValueFunctionality func = null;
		func = (UnitValueFunctionality) super.clone();
		func.unit = (Unit<?>) unit.clone();
		return func;
	}

	@Override
	public void updateFunctionalityFrom(AbstractFunctionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}

	@Override
	public void updateFunctionalityValuesOnlyFrom(AbstractFunctionality functionality) {
		UnitValueFunctionality f = (UnitValueFunctionality) functionality;
		this.value = f.getValue();
		this.maxValue = f.getMaxValue();
		this.unit.updateUnitFrom(f.getUnit());
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(FunctionalityAdminDto functionalityDto) {
		List<ParameterDto> parameters = functionalityDto.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			updateFunctionality(parameterDto.getType(), parameterDto);
		}
	}

	protected void updateFunctionality(String type, ParameterDto parameterDto) {
		this.value = parameterDto.getInteger();
		this.maxValue = parameterDto.getMaxInteger();
		String unit = parameterDto.getString().trim().toUpperCase();
		if (type.equals(FunctionalityType.UNIT_SIZE.toString())) {
			FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
			sizeUnit.setUnitValue(FileSizeUnit.valueOf(unit));
		} else if (type.equals(FunctionalityType.UNIT_TIME.toString())) {
			TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
			timeUnit.setUnitValue(TimeUnit.valueOf(unit));
		}
	}

	@Override
	public List<ParameterDto> getParameters(Integer version) {
		List<ParameterDto> res = new ArrayList<ParameterDto>();
		String unitType = null;
		String currentUnit = null;
		List<String> units = new ArrayList<String>();
		
		if (getUnit() instanceof FileSizeUnitClass) {
			FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
			unitType = FunctionalityType.UNIT_SIZE.toString();
			currentUnit = sizeUnit.getUnitValue().toString();
			for (FileSizeUnit val : FileSizeUnit.values()) {
				units.add(val.toString());
			}
		} else if (getUnit() instanceof TimeUnitClass) {
			TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
			unitType = FunctionalityType.UNIT_TIME.toString();
			currentUnit = timeUnit.getUnitValue().toString();
			for (TimeUnit val : TimeUnit.values()) {
				units.add(val.toString());
			}
		}
		ParameterDto parameterDto = new ParameterDto(unitType, units, currentUnit, this.getValue());
		if (version >= 4) {
			parameterDto.setMaxInteger(this.getMaxValue());
		}
		res.add(parameterDto);
		return res;
	}

	@Override
	protected FunctionalityDto getUserDto(boolean enable, Integer version) {
		if (getUnit() instanceof FileSizeUnitClass) {
			FunctionalitySizeDto f = new FunctionalitySizeDto();
			if (enable) {
				if (version >= 4) {
					f.setMaxValue(maxValue);
				}
				FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
				f.setUnit(sizeUnit.getUnitValue().toString());
				f.setValue(value);
			}
			return f;
		} else if (getUnit() instanceof TimeUnitClass) {
			FunctionalityTimeDto f = new FunctionalityTimeDto();
			if (enable) {
				if (version >= 4) {
					f.setMaxValue(maxValue);
				}
				TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
				f.setUnit(timeUnit.getUnitValue().toString());
				f.setValue(value);
			}
			return f;
		}
		return null;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

}

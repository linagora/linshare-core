/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.util.List;

import javassist.expr.Instanceof;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.vo.FunctionalityVo;
import org.linagora.linshare.core.domain.vo.SizeValueFunctionalityVo;
import org.linagora.linshare.core.domain.vo.TimeValueFunctionalityVo;
import org.linagora.linshare.webservice.dto.ParameterDto;

public class UnitValueFunctionality extends OneValueFunctionality<Integer> {

	protected Unit<?> unit;

	public UnitValueFunctionality() {
		super();
	}

	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
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
	public boolean businessEquals(Functionality obj, boolean checkPolicies) {
		if (super.businessEquals(obj, checkPolicies)) {
			UnitValueFunctionality o = (UnitValueFunctionality) obj;
			if (value.equals(o.getValue())) {
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
	public void updateFunctionalityFrom(Functionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}

	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		UnitValueFunctionality f = (UnitValueFunctionality) functionality;
		this.value = f.getValue();
		this.unit.updateUnitFrom(f.getUnit());
	}

	@Override
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {

		if (functionality.getType().equals(FunctionalityType.UNIT_SIZE)) {
			SizeValueFunctionalityVo f = (SizeValueFunctionalityVo) functionality;
			if (f.getSize() != null) {
				this.value = f.getSize();
				FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
				sizeUnit.setUnitValue(f.getUnit());
			}

		} else if (functionality.getType().equals(FunctionalityType.UNIT_TIME)) {
			TimeValueFunctionalityVo f = (TimeValueFunctionalityVo) functionality;
			if (f.getTime() != null) {
				this.value = f.getTime();
				TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
				timeUnit.setUnitValue(f.getUnit());
			}
		} else if (functionality.getType().equals(FunctionalityType.UNIT_BOOLEAN_TIME)) {
			TimeValueFunctionalityVo f = (TimeValueFunctionalityVo) functionality;
			if (f.getTime() != null) {
				this.value = f.getTime();
				TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
				timeUnit.setUnitValue(f.getUnit());
			}
		}
	}

	@Override
	public List<ParameterDto> getParameters() {
		List<ParameterDto> res = super.getParameters();
		ParameterDto parameterDto = null;
		if (getUnit() instanceof FileSizeUnitClass) {
			FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
			String unitType = FunctionalityType.UNIT_SIZE.toString();
			String unit = sizeUnit.getUnitValue().toString();
			parameterDto = new ParameterDto(unitType, unit, this.getValue());

		} else if (getUnit() instanceof TimeUnitClass) {
			TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
			String unitType = FunctionalityType.UNIT_TIME.toString();
			String unit = timeUnit.getUnitValue().toString();
			parameterDto = new ParameterDto(unitType, unit, this.getValue());
		}
		res.add(parameterDto);
		return res;
	}
}

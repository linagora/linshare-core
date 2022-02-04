/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.IntegerDefaultAndMaximumParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.IntegerDefaultParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.IntegerMaximumParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.UnlimitedParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedIntegerParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityIntegerDto;

public class IntegerValueFunctionality extends OneValueFunctionality<Integer> {

	protected Integer maxValue;

	protected Boolean valueUsed;

	protected Boolean maxValueUsed;

	protected Boolean unlimited;

	protected Boolean unlimitedUsed;

	public IntegerValueFunctionality() {
		super();
	}

	public IntegerValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Integer maxValue, Boolean valueUsed,
			Boolean maxValueUsed) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.maxValue = maxValue;
		this.valueUsed = valueUsed;
		this.maxValueUsed = maxValueUsed;
	}
	
	@Override
	public FunctionalityType getType() {
		return FunctionalityType.INTEGER;
	}

	private boolean strictBusinessEquals(IntegerValueFunctionality integerFunc) {
		if (this.getValueUsed()) {
			if (value == null) {
				if(integerFunc.getValue() != null) {
					return false;
				}
			} else {
				if(!value.equals(integerFunc.getValue())) {
					return false;
				}
			}
		}
		if (this.getMaxValueUsed()) {
			if (maxValue == null) {
				if(integerFunc.getMaxValue() != null) {
					return false;
				}
			} else {
				if(!maxValue.equals(integerFunc.getMaxValue())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean businessEquals(AbstractFunctionality obj, boolean checkPolicies) {
		if (super.businessEquals(obj, checkPolicies)) {
			IntegerValueFunctionality o = (IntegerValueFunctionality) obj;
			if (strictBusinessEquals(o)) {
				logger.debug("IntegerValueFunctionality : " + this.toString()
						+ " is equal to IntegerValueFunctionality " + obj.toString());
				return true;
			}
		}
		logger.debug("IntegerValueFunctionality : " + this.toString() + " is not equal to IntegerValueFunctionality "
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
		IntegerValueFunctionality f = (IntegerValueFunctionality)functionality;
		if (this.getValueUsed()) {
			this.value = f.getValue();
		}
		if (this.getMaxValueUsed()) {
			this.maxValue = f.getMaxValue();
		}
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(Integer version, FunctionalityAdminDto functionality) {
		List<ParameterDto> parameters = functionality.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			if (this.getValueUsed()) {
				this.value = parameterDto.getInteger();
			}
			if (version >= 4 && this.getMaxValueUsed()) {
				if (this.getUnlimitedUsed()) {
					if(parameterDto.getMaxInteger() == -1) {
						this.setUnlimited(true);
					} else {
						this.setUnlimited(false);
						this.maxValue = parameterDto.getMaxInteger();
					}
				} else {
					this.maxValue = parameterDto.getMaxInteger();
				}
			}
		}
	}

	@Override
	public List<ParameterDto> getParameters(Integer version) {
		List<ParameterDto> res = new ArrayList<ParameterDto>();
		ParameterDto parameterDto = new ParameterDto();
		parameterDto.setType("INTEGER");
		if (this.getValueUsed()) {
			parameterDto.setInteger(this.getValue());
		}
		if (version >= 4) {
			if (this.getMaxValueUsed()) {
				parameterDto.setMaxValueUsed(this.getMaxValueUsed());
				if (this.getUnlimitedUsed()) {
					if(this.getUnlimited()) {
						parameterDto.setMaxInteger(-1);
					} else {
						parameterDto.setMaxInteger(this.getMaxValue());
					}
				} else {
					parameterDto.setMaxInteger(this.getMaxValue());
				}
			}
			parameterDto.setDefaultValueUsed(this.getValueUsed());
		}
		res.add(parameterDto);
		return res;
	}

	@Override
	protected FunctionalityDto getUserDto(boolean enable, Integer version) {
		FunctionalityIntegerDto f = new FunctionalityIntegerDto();
		if (enable) {
			if (version >= 4 && this.getMaxValueUsed()) {
				f.setMaxValue(maxValue);
			}
			if (this.getValueUsed()) {
				f.setValue(value);
			}
		}
		return f;
	}

	@Override
	public Integer getValue() {
		if (!this.getValueUsed()) {
			throw new BusinessException(BusinessErrorCode.FUNCTIONALITY_DEFAULT_VALUE_NOT_AVAILABLE,
					"You cannot access the default value of the functionality \"" + this.getIdentifier() + "\"");
		}
		return super.getValue();
	}

	public Integer getMaxValue() {
		if (!this.getMaxValueUsed()) {
			throw new BusinessException(BusinessErrorCode.FUNCTIONALITY_MAX_VALUE_NOT_AVAILABLE,
					"You cannot access the max value of the functionality \"" + this.getIdentifier() + "\"");
		}
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public Boolean getValueUsed() {
		return valueUsed;
	}

	public void setValueUsed(Boolean valueUsed) {
		this.valueUsed = valueUsed;
	}

	public Boolean getMaxValueUsed() {
		return maxValueUsed;
	}

	public void setMaxValueUsed(Boolean maxValueUsed) {
		this.maxValueUsed = maxValueUsed;
	}

	@Override
	public org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> getParameter() {
		Optional<NestedParameterDto<Integer>> defaut = Optional.empty();
		Optional<NestedParameterDto<Integer>> maximum = Optional.empty();
		UnlimitedParameterDto unlimited = null;
		if (this.valueUsed) {
			// there is no default value for functionality parameters. sad.
			Integer parentValue = this.value;
			if (this.ancestorFunc != null) {
				parentValue = ((IntegerValueFunctionality)this.ancestorFunc).getValue();
			}
			defaut = Optional.of(new NestedIntegerParameterDto(this.value, parentValue));
		}
		if (this.maxValueUsed) {
			Integer parentValue = this.getMaxValue();
			if (this.ancestorFunc != null) {
				parentValue = ((IntegerValueFunctionality)this.ancestorFunc).getMaxValue();
			}
			maximum = Optional.of(new NestedIntegerParameterDto(this.maxValue, parentValue));
			if (this.unlimitedUsed) {
				Boolean parentUnlimitedValue = this.unlimited;
				if (this.ancestorFunc != null) {
					parentUnlimitedValue = ((IntegerValueFunctionality)this.ancestorFunc).getUnlimited();
				}
				unlimited = new UnlimitedParameterDto(this.unlimited, parentUnlimitedValue);
			} else {
				unlimited = new UnlimitedParameterDto();
			}
		}
		if (defaut.isPresent() && maximum.isPresent()) {
			return new IntegerDefaultAndMaximumParameterDto(this.system, !this.getParentAllowParametersUpdate(), defaut, maximum, unlimited);
		} else if (defaut.isPresent()) {
			return new IntegerDefaultParameterDto(this.system, !this.getParentAllowParametersUpdate(), defaut);
		} else {
			return new IntegerMaximumParameterDto(this.system, !this.getParentAllowParametersUpdate(), maximum, unlimited);
		}
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(
			org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> param) {
		switch (param.getType()) {
			case "INTEGER_DEFAULT":
			case "INTEGER_MAX":
			case "INTEGER_ALL":
				if (this.valueUsed) {
					Validate.notNull(param.getDefaut(), "Default object value must be set");
					NestedIntegerParameterDto dto = (NestedIntegerParameterDto) param.getDefaut();
					Validate.notNull(dto.getValue(), "Default value must be set");
					this.setValue(dto.getValue());
				}
				if (this.maxValueUsed) {
					Validate.notNull(param.getMaximum(), "Maximum object value must be set");
					NestedIntegerParameterDto dto = (NestedIntegerParameterDto) param.getMaximum();
					Validate.notNull(dto.getValue(), "Maximum value must be set");
					this.setMaxValue(dto.getValue());
					if (this.unlimitedUsed) {
						Validate.notNull(param.getUnlimited(), "Unlimited must be set");
						Validate.notNull(param.getUnlimited().getValue(), "Unlimited value must be set");
						this.setUnlimited(param.getUnlimited().getValue());
					}
				}
				break;
			default:
				throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Wrong parameter type: " + param.getType());
			}
	}

	public Boolean getUnlimited() {
		return unlimited;
	}

	public void setUnlimited(Boolean unlimited) {
		this.unlimited = unlimited;
	}

	public Boolean getUnlimitedUsed() {
		return unlimitedUsed;
	}

	public void setUnlimitedUsed(Boolean unlimitedUsed) {
		this.unlimitedUsed = unlimitedUsed;
	}
}

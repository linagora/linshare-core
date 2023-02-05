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
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.FileSizeUnitDefaultAndMaximumParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.FileSizeUnitDefaultParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.FileSizeUnitMaximumParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.TimeUnitDefaultAndMaximumParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.TimeUnitDefaultParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.TimeUnitMaximumParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.UnlimitedParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedFileSizeParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedTimeParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedUnitParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalitySizeDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityTimeDto;
import org.linagora.linshare.utils.Version;

public class UnitValueFunctionality extends OneValueFunctionality<Integer> {

	protected Unit<?> unit;

	protected Unit<?> maxUnit;

	protected Integer maxValue;

	protected Boolean valueUsed;

	protected Boolean maxValueUsed;

	protected Boolean unlimited;

	protected Boolean unlimitedUsed;

	public UnitValueFunctionality() {
		super();
	}

	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
		this.valueUsed = true;
		this.maxValueUsed = false;
		this.unlimited = false;
		this.unlimitedUsed = false;
	}
	
	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit, Unit<?> maxUnit, Integer maxValue, boolean valueUsed, boolean maxValueUsed) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
		this.maxValue = maxValue;
		this.maxUnit = maxUnit;
		this.valueUsed = valueUsed;
		this.maxValueUsed = maxValueUsed;
		this.unlimited = false;
		this.unlimitedUsed = false;
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT;
	}

	public Unit<?> getUnit() {
		return unit;
	}
	
	public Unit<?> getMaxUnit() {
		return maxUnit;
	}

	public void setMaxUnit(Unit<?> maxUnit) {
		this.maxUnit = maxUnit;
	}

	public void setUnit(Unit<?> unit) {
		this.unit = unit;
	}

	private boolean strictBusinessEquals(UnitValueFunctionality unitFunc) {
		if (valueUsed) {
			if (value == null) {
				if(unitFunc.getValue() != null) {
					return false;
				}
			} else {
				if(!value.equals(unitFunc.getValue())) {
					return false;
				}
			}
			if (unit == null) {
				if(unitFunc.getUnit() != null) {
					return false;
				}
			} else {
				if(!unit.businessEquals(unitFunc.getUnit())) {
					return false;
				}
			}
		}
		if(maxValueUsed) {
			if (maxValue == null) {
				if(unitFunc.getMaxValue() != null) {
					return false;
				}
			} else {
				if(!maxValue.equals(unitFunc.getMaxValue())) {
					return false;
				}
			}
			if (maxUnit == null) {
				if(unitFunc.getMaxUnit() != null) {
					return false;
				}
			} else {
				if(!maxUnit.businessEquals(unitFunc.getMaxUnit())) {
					return false;
				}
			}
			if (unlimited == null) {
				if(unitFunc.getUnlimited() != null) {
					return false;
				}
			} else {
				if(!unlimited.equals(unitFunc.getUnlimited())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean businessEquals(AbstractFunctionality obj, boolean checkPolicies) {
		if (super.businessEquals(obj, checkPolicies)) {
			UnitValueFunctionality o = (UnitValueFunctionality) obj;
			if (this.strictBusinessEquals(o)) {
				logger.debug("UnitValueFunctionality : " + this.toString() + " is equal to UnitValueFunctionality "
						+ obj.toString());
				return true;
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
		func.maxUnit = (Unit<?>) maxUnit.clone();
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
		if (this.getValueUsed()) {
			this.value = f.getValue();
			this.unit.updateUnitFrom(f.getUnit());
		}
		if (this.getMaxValueUsed()) {
			this.maxValue = f.getMaxValue();
			this.maxUnit.updateUnitFrom(f.getMaxUnit());
			this.unlimited = f.getUnlimited();
		}
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(Version version, FunctionalityAdminDto functionalityDto) {
		List<ParameterDto> parameters = functionalityDto.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			updateFunctionality(version, parameterDto.getType(), parameterDto);
		}
	}

	protected void updateFunctionality(Version version, String type, ParameterDto parameterDto) {
		if (this.getValueUsed()) {
			this.value = parameterDto.getInteger();
			String unit = parameterDto.getString().trim().toUpperCase();
			if (type.equals(FunctionalityType.UNIT_SIZE.toString())) {
				FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
				sizeUnit.setUnitValue(FileSizeUnit.valueOf(unit));
			} else if (type.equals(FunctionalityType.UNIT_TIME.toString())) {
				TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
				timeUnit.setUnitValue(TimeUnit.valueOf(unit));
			}
		}
		if (version.isGreaterThanOrEquals(Version.V4)) {
			if (this.getMaxValueUsed()) {
				if (!this.getUnlimitedUsed() && parameterDto.getMaxInteger() == -1) {
					logger.error("This functionality does not support unlimited value using -1 as max value: %s",
							this.identifier);
					throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT,
							"This functionality does not support unlimited value using -1 as max value: " + this.identifier);
				}
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
				String unitMax = parameterDto.getMaxString().trim().toUpperCase();
				if (type.equals(FunctionalityType.UNIT_SIZE.toString())) {
					FileSizeUnitClass sizeMaxUnit = (FileSizeUnitClass) getMaxUnit();
					sizeMaxUnit.setUnitValue(FileSizeUnit.valueOf(unitMax));
				} else if (type.equals(FunctionalityType.UNIT_TIME.toString())) {
					TimeUnitClass timeMaxUnit = (TimeUnitClass) getMaxUnit();
					timeMaxUnit.setUnitValue(TimeUnit.valueOf(unitMax));
				}
			}
		}
	}

	@Override
	public List<ParameterDto> getParameters(Version version) {
		List<ParameterDto> res = new ArrayList<ParameterDto>();
		String unitType = null;
		String currentUnit = null;
		String maxCurrentUnit = null;
		List<String> units = new ArrayList<String>();
		if (getUnit() instanceof FileSizeUnitClass && getMaxUnit() instanceof FileSizeUnitClass) {
			FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
			FileSizeUnitClass sizeMaxUnit = (FileSizeUnitClass) getMaxUnit();
			unitType = FunctionalityType.UNIT_SIZE.toString();
			currentUnit = sizeUnit.getUnitValue().toString();
			maxCurrentUnit = sizeMaxUnit.getUnitValue().toString();
			for (FileSizeUnit val : FileSizeUnit.values()) {
				units.add(val.toString());
			}
		} else if (getUnit() instanceof TimeUnitClass && getMaxUnit() instanceof TimeUnitClass) {
			TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
			TimeUnitClass maxTimeUnit = (TimeUnitClass) getMaxUnit();
			unitType = FunctionalityType.UNIT_TIME.toString();
			currentUnit = timeUnit.getUnitValue().toString();
			maxCurrentUnit = maxTimeUnit.getUnitValue().toString();
			for (TimeUnit val : TimeUnit.values()) {
				units.add(val.toString());
			}
		}
		ParameterDto parameterDto = new ParameterDto(unitType, units);
		if (version.isGreaterThanOrEquals(Version.V4)) {
			if (this.getValueUsed()) {
				parameterDto.setString(currentUnit);
				parameterDto.setInteger(this.getValue());
			}
			if (this.getMaxValueUsed()) {
				parameterDto.setMaxString(maxCurrentUnit);
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
			parameterDto.setMaxValueUsed(this.getMaxValueUsed());
		} else {
			//In lower versions, for compatibility purpose, the functionality MaxValue is rendered in value field
			if (FunctionalityNames.WORK_GROUP__DOWNLOAD_ARCHIVE.toString().equals(this.getIdentifier())) {
				parameterDto.setInteger(this.getMaxValue());
				parameterDto.setString(maxCurrentUnit);
			} else {
				parameterDto.setInteger(this.getValue());
				parameterDto.setString(currentUnit);
			}
		}
		res.add(parameterDto);
		return res;
	}

	@Override
	protected FunctionalityDto getUserDto(boolean enable, Version version) {
		if (getUnit() instanceof FileSizeUnitClass && getMaxUnit() instanceof FileSizeUnitClass) {
			FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
			FileSizeUnitClass sizeMaxUnit = (FileSizeUnitClass) getMaxUnit();
			FunctionalitySizeDto f = new FunctionalitySizeDto();
			if (enable) {
				if (version.isGreaterThanOrEquals(Version.V4)) {
					f.setUnlimited(false);
					if (this.getMaxValueUsed()) {
						f.setUnlimited(this.unlimited);
						f.setMaxValue(maxValue);
						f.setMaxUnit(sizeMaxUnit.getUnitValue().toString());
					}
				}
				if (this.getValueUsed()) {
					f.setUnit(sizeUnit.getUnitValue().toString());
					f.setValue(value);
				}
			}
			return f;
		} else if (getUnit() instanceof TimeUnitClass && getMaxUnit() instanceof TimeUnitClass) {
			FunctionalityTimeDto f = new FunctionalityTimeDto();
			TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
			TimeUnitClass maxTimeUnit = (TimeUnitClass) getMaxUnit();
			if (enable) {
				if (version.isGreaterThanOrEquals(Version.V4)) {
					f.setUnlimited(false);
					if (this.getMaxValueUsed()) {
						f.setUnlimited(this.unlimited);
						f.setMaxValue(maxValue);
						f.setMaxUnit(maxTimeUnit.getUnitValue().toString());
					}
				}
				if (this.getValueUsed()) {
					f.setUnit(timeUnit.getUnitValue().toString());
					f.setValue(value);
				}
			}
			return f;
		}
		return null;
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
		Optional<NestedUnitParameterDto<?>> defaut = Optional.empty();
		Optional<NestedUnitParameterDto<?>> maximum = Optional.empty();
		UnlimitedParameterDto unlimited = null;
		if (getUnit() instanceof FileSizeUnitClass && getMaxUnit() instanceof FileSizeUnitClass) {
			if (this.maxValueUsed) {
				// there is no default value for functionality parameters. sad.
				Integer parentValue = this.getMaxValue();
				FileSizeUnitClass maxUnit = (FileSizeUnitClass) getMaxUnit();
				FileSizeUnit parentUnit = maxUnit.getUnitValue();
				if (this.ancestorFunc != null) {
					parentValue = ((UnitValueFunctionality)this.ancestorFunc).getMaxValue();
					Unit<?> ancestorMaxUnit = ((UnitValueFunctionality)this.ancestorFunc).getMaxUnit();
					FileSizeUnitClass parentUnitClass = (FileSizeUnitClass)ancestorMaxUnit;
					parentUnit = parentUnitClass.getUnitValue();
				}
				maximum = Optional.of(new NestedFileSizeParameterDto(
						this.maxValue,
						parentValue,
						maxUnit.getUnitValue(),
						parentUnit,
						FileSizeUnit.strValues()
						));
				if (this.unlimitedUsed) {
					Boolean parentUnlimitedValue = this.unlimited;
					if (this.ancestorFunc != null) {
						parentUnlimitedValue = ((UnitValueFunctionality)this.ancestorFunc).getUnlimited();
					}
					unlimited = new UnlimitedParameterDto(this.unlimited, parentUnlimitedValue);
				} else {
					unlimited = new UnlimitedParameterDto();
				}
			}
			if (this.valueUsed) {
				// there is no default value for functionality parameters. sad.
				Integer parentValue = this.value;
				FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
				FileSizeUnit parentUnit = sizeUnit.getUnitValue();
				if (this.ancestorFunc != null) {
					parentValue = ((UnitValueFunctionality)this.ancestorFunc).getValue();
					Unit<?> ancestorUnit = ((UnitValueFunctionality) this.ancestorFunc).getUnit();
					FileSizeUnitClass parentSizeUnit = (FileSizeUnitClass) ancestorUnit;
					parentUnit = parentSizeUnit.getUnitValue();
				}
				defaut = Optional.of(new NestedFileSizeParameterDto(
						this.value,
						parentValue,
						sizeUnit.getUnitValue(),
						parentUnit,
						FileSizeUnit.strValues()
						));
			}
			if (defaut.isPresent() && maximum.isPresent()) {
				return new FileSizeUnitDefaultAndMaximumParameterDto(this.system, !this.getParentAllowParametersUpdate(), defaut, maximum, unlimited);
			} else if (defaut.isPresent()) {
				return new FileSizeUnitDefaultParameterDto(this.system, !this.getParentAllowParametersUpdate(), defaut);
			} else {
				return new FileSizeUnitMaximumParameterDto(this.system, !this.getParentAllowParametersUpdate(), maximum, unlimited);
			}
		}
		if (getUnit() instanceof TimeUnitClass && getMaxUnit() instanceof TimeUnitClass) {
			if (this.maxValueUsed) {
				// there is no default value for functionality parameters. sad.
				Integer parentValue = this.getMaxValue();
				TimeUnitClass maxUnit = (TimeUnitClass) getMaxUnit();
				TimeUnit parentUnit = maxUnit.getUnitValue();
				if (this.ancestorFunc != null) {
					parentValue = ((UnitValueFunctionality)this.ancestorFunc).getMaxValue();
					Unit<?> ancestorMaxUnit = ((UnitValueFunctionality)this.ancestorFunc).getMaxUnit();
					TimeUnitClass parentUnitClass = (TimeUnitClass) ancestorMaxUnit;
					parentUnit = parentUnitClass.getUnitValue();
				}
				maximum = Optional.of(new NestedTimeParameterDto(
						this.maxValue,
						parentValue,
						maxUnit.getUnitValue(),
						parentUnit,
						TimeUnit.strValues()
						));
				if (this.unlimitedUsed) {
					Boolean parentUnlimitedValue = this.unlimited;
					if (this.ancestorFunc != null) {
						parentUnlimitedValue = ((UnitValueFunctionality)this.ancestorFunc).getUnlimited();
					}
					unlimited = new UnlimitedParameterDto(this.unlimited, parentUnlimitedValue);
				} else {
					unlimited = new UnlimitedParameterDto();
				}
			}
			if (this.valueUsed) {
				// there is no default value for functionality parameters. sad.
				Integer parentValue = this.value;
				TimeUnitClass sizeUnit = (TimeUnitClass) getUnit();
				TimeUnit parentUnit = sizeUnit.getUnitValue();
				if (this.ancestorFunc != null) {
					parentValue = ((UnitValueFunctionality)this.ancestorFunc).getValue();
					Unit<?> ancestorUnit = ((UnitValueFunctionality) this.ancestorFunc).getUnit();
					TimeUnitClass parentSizeUnit = (TimeUnitClass) ancestorUnit;
					parentUnit = parentSizeUnit.getUnitValue();
				}
				defaut = Optional.of(new NestedTimeParameterDto(
						this.value,
						parentValue,
						sizeUnit.getUnitValue(),
						parentUnit,
						TimeUnit.strValues()
				));
			}
			if (defaut.isPresent() && maximum.isPresent()) {
				return new TimeUnitDefaultAndMaximumParameterDto(this.system, !this.getParentAllowParametersUpdate(), defaut, maximum, unlimited);
			} else if (defaut.isPresent()) {
				return new TimeUnitDefaultParameterDto(this.system, !this.getParentAllowParametersUpdate(), defaut);
			} else {
				return new TimeUnitMaximumParameterDto(this.system, !this.getParentAllowParametersUpdate(), maximum, unlimited);
			}
		}
		throw new TechnicalException("unsupported type.");
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(
			org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<?> param) {
		switch (param.getType()) {
			case "UNIT_SIZE_DEFAULT":
			case "UNIT_SIZE_MAX":
			case "UNIT_SIZE_ALL":
				if (this.valueUsed) {
					Validate.notNull(param.getDefaut(), "Default object value must be set");
					NestedFileSizeParameterDto dto = (NestedFileSizeParameterDto) param.getDefaut();
					Validate.notNull(dto.getValue(), "Default value must be set");
					this.setValue(dto.getValue());
					FileSizeUnitClass sizeUnit = (FileSizeUnitClass) this.getUnit();
					sizeUnit.setUnitValue(dto.getUnit());
				}
				if (this.maxValueUsed) {
					Validate.notNull(param.getMaximum(), "Maximum object value must be set");
					NestedFileSizeParameterDto dto = (NestedFileSizeParameterDto) param.getMaximum();
					Validate.notNull(dto.getValue(), "Maximum value must be set");
					this.setMaxValue(dto.getValue());
					FileSizeUnitClass sizeMaxUnit = (FileSizeUnitClass) getMaxUnit();
					sizeMaxUnit.setUnitValue(dto.getUnit());
					if (this.unlimitedUsed) {
						Validate.notNull(param.getUnlimited(), "Unlimited must be set");
						Validate.notNull(param.getUnlimited().getValue(), "Unlimited value must be set");
						this.setUnlimited(param.getUnlimited().getValue());
					}
				}
				break;
			case "UNIT_TIME_DEFAULT":
			case "UNIT_TIME_MAX":
			case "UNIT_TIME_ALL":
				if (this.valueUsed) {
					Validate.notNull(param.getDefaut(), "Default object value must be set");
					NestedTimeParameterDto dto = (NestedTimeParameterDto) param.getDefaut();
					Validate.notNull(dto.getValue(), "Default value must be set");
					this.setValue(dto.getValue());
					TimeUnitClass timeUnit = (TimeUnitClass) getUnit();
					timeUnit.setUnitValue(dto.getUnit());
				}
				if (this.maxValueUsed) {
					Validate.notNull(param.getMaximum(), "Maximum object value must be set");
					NestedTimeParameterDto dto = (NestedTimeParameterDto) param.getMaximum();
					Validate.notNull(dto.getValue(), "Maximum value must be set");
					if (FunctionalityNames.GUESTS__EXPIRATION.toString().equals(this.getIdentifier()) && dto.getValue() == -1) {
						logger.error("GUEST__EXPIRATION max_value can not be updated to unlimited");
						throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT,"GUESTS__EXPIRATION max_value can not be updated to unlimited");
					}
					this.setMaxValue(dto.getValue());
					TimeUnitClass timeUnit = (TimeUnitClass) getMaxUnit();
					timeUnit.setUnitValue(dto.getUnit());
					if (this.unlimitedUsed) {
						Validate.notNull(param.getUnlimited(), "Unlimited must be set");
						Validate.notNull(param.getUnlimited().getValue(), "Unlimited value must be set");
						this.setUnlimited(param.getUnlimited().getValue());
					}
				}
				break;
			default:
				throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Wrong parameter type");
		}
	}

	@Override
	public String toString() {
		return "UnitValueFunctionality [unit=" + unit + ", maxUnit=" + maxUnit + ", value=" + value + ", maxValue=" + maxValue
				+ ", valueUsed=" + valueUsed + ", maxValueUsed=" + maxValueUsed
				+ ", unlimited=" + unlimited
				+ ", unlimitedUsed=" + unlimitedUsed
				+ ", domain=" + domain + "]";
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

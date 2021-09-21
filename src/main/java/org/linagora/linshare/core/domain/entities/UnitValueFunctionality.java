/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.NestedUnitParameterDto;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.UnlimitedParameterDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalitySizeDto;
import org.linagora.linshare.core.facade.webservice.user.dto.FunctionalityTimeDto;

import com.google.common.collect.Lists;

public class UnitValueFunctionality extends OneValueFunctionality<Integer> {

	protected Unit<?> unit;

	protected Unit<?> maxUnit;

	protected Integer maxValue;

	protected Boolean valueUsed;

	protected Boolean maxValueUsed;

	// FIXME: To be handle by the database.
	public static List<String> unlimitedFunctionalityNames = Lists.newArrayList(
			FunctionalityNames.SHARE_EXPIRATION.toString(),
			FunctionalityNames.WORK_GROUP__DOWNLOAD_ARCHIVE.toString(),
			FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE.toString(),
			FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_FILE_SIZE.toString(),
			FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_FILE_COUNT.toString(),
			FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION.toString(),
			FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION.toString(),
			FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION.toString()
	);

	public UnitValueFunctionality() {
		super();
	}

	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
		this.valueUsed = true;
		this.maxValueUsed = false;
	}
	
	public UnitValueFunctionality(String identifier, boolean system, Policy activationPolicy,
			Policy configurationPolicy, AbstractDomain domain, Integer value, Unit<?> unit, Unit<?> maxUnit, Integer maxValue, boolean valueUsed, boolean maxValueUsed) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
		this.maxValue = maxValue;
		this.maxUnit = maxUnit;
		this.valueUsed = valueUsed;
		this.maxValueUsed = maxValueUsed;
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
		}
	}

	@Override
	public void updateFunctionalityValuesOnlyFromDto(Integer version, FunctionalityAdminDto functionalityDto) {
		List<ParameterDto> parameters = functionalityDto.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			ParameterDto parameterDto = parameters.get(0);
			updateFunctionality(version, parameterDto.getType(), parameterDto);
		}
	}

	protected void updateFunctionality(Integer version, String type, ParameterDto parameterDto) {
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
		if (this.getMaxValueUsed()) {
			if (FunctionalityNames.GUESTS__EXPIRATION.toString().equals(this.getIdentifier()) && parameterDto.getMaxInteger() == -1) {
				logger.error("GUEST__EXPIRATION max_value can not be updated to unlimited");
				throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT,"GUESTS__EXPIRATION max_value can not be updated to unlimited");
			} 
			this.maxValue = parameterDto.getMaxInteger();
			String unitMax = null;
			if (version >= 4) {
				unitMax = parameterDto.getMaxString().trim().toUpperCase();
			}
			if (type.equals(FunctionalityType.UNIT_SIZE.toString())) {
				FileSizeUnitClass sizeMaxUnit = (FileSizeUnitClass) getMaxUnit();
				if (version >= 4) {
					sizeMaxUnit.setUnitValue(FileSizeUnit.valueOf(unitMax));
				}
			} else if (type.equals(FunctionalityType.UNIT_TIME.toString())) {
				TimeUnitClass timeMaxUnit = (TimeUnitClass) getMaxUnit();
				if (version >= 4) {
					timeMaxUnit.setUnitValue(TimeUnit.valueOf(unitMax));
				}
			}
		}
	}

	@Override
	public List<ParameterDto> getParameters(Integer version) {
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
		if (version >= 4) {
			if (this.getValueUsed()) {
				parameterDto.setString(currentUnit);
				parameterDto.setInteger(this.getValue());
			}
			if (this.getMaxValueUsed()) {
				parameterDto.setMaxInteger(this.getMaxValue());
				parameterDto.setMaxString(maxCurrentUnit);
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
	protected FunctionalityDto getUserDto(boolean enable, Integer version) {
		if (getUnit() instanceof FileSizeUnitClass && getMaxUnit() instanceof FileSizeUnitClass) {
			FileSizeUnitClass sizeUnit = (FileSizeUnitClass) getUnit();
			FileSizeUnitClass sizeMaxUnit = (FileSizeUnitClass) getMaxUnit();
			FunctionalitySizeDto f = new FunctionalitySizeDto();
			if (enable) {
				if (version >= 4) {
					if (this.getMaxValueUsed()) {
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
				if (version >= 4) {
					if (this.getMaxValueUsed()) {
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
		NestedUnitParameterDto<?> defaut = null;
		NestedUnitParameterDto<?> maximum = null;
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
					FileSizeUnitClass parentUnitClass = (FileSizeUnitClass) ancestorMaxUnit.getUnitValue();
					parentUnit = parentUnitClass.getUnitValue();
				}
				maximum = new NestedUnitParameterDto<FileSizeUnit>(
						this.maxValue,
						parentValue,
						maxUnit.getUnitValue(),
						parentUnit,
						FileSizeUnit.strValues()
						);
				// FIXME: To be handle by the database.
				if (unlimitedFunctionalityNames.contains(this.identifier)) {
					unlimited = new UnlimitedParameterDto(maxValue == -1, parentValue == -1);
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
					FileSizeUnitClass parentSizeUnit = (FileSizeUnitClass) ancestorUnit.getUnitValue();
					parentUnit = parentSizeUnit.getUnitValue();
				}
				defaut = new NestedUnitParameterDto<FileSizeUnit>(
						this.value,
						parentValue,
						sizeUnit.getUnitValue(),
						parentUnit,
						FileSizeUnit.strValues()
						);
			}
		} else if (getUnit() instanceof TimeUnitClass && getMaxUnit() instanceof TimeUnitClass) {
			if (this.maxValueUsed) {
				// there is no default value for functionality parameters. sad.
				Integer parentValue = this.getMaxValue();
				TimeUnitClass maxUnit = (TimeUnitClass) getMaxUnit();
				TimeUnit parentUnit = maxUnit.getUnitValue();
				if (this.ancestorFunc != null) {
					parentValue = ((UnitValueFunctionality)this.ancestorFunc).getMaxValue();
					Unit<?> ancestorMaxUnit = ((UnitValueFunctionality)this.ancestorFunc).getMaxUnit();
					TimeUnitClass parentUnitClass = (TimeUnitClass) ancestorMaxUnit.getUnitValue();
					parentUnit = parentUnitClass.getUnitValue();
				}
				maximum = new NestedUnitParameterDto<TimeUnit>(
						this.maxValue,
						parentValue,
						maxUnit.getUnitValue(),
						parentUnit,
						TimeUnit.strValues()
						);
				// FIXME: To be handle by the database.
				if (unlimitedFunctionalityNames.contains(this.identifier)) {
					unlimited = new UnlimitedParameterDto(maxValue == -1, parentValue == -1);
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
					TimeUnitClass parentSizeUnit = (TimeUnitClass) ancestorUnit.getUnitValue();
					parentUnit = parentSizeUnit.getUnitValue();
				}
				defaut = new NestedUnitParameterDto<TimeUnit>(
						this.value,
						parentValue,
						sizeUnit.getUnitValue(),
						parentUnit,
						TimeUnit.strValues()
						);
			}
		}
		return new org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.ParameterDto<Integer>(
			this.system,
			!this.getParentAllowParametersUpdate(),
			defaut,
			maximum,
			unlimited
		);
	}

	@Override
	public String toString() {
		return "UnitValueFunctionality [unit=" + unit + ", maxUnit=" + maxUnit + ", maxValue=" + maxValue
				+ ", valueUsed=" + valueUsed + ", maxValueUsed=" + maxValueUsed + ", domain=" + domain + "]";
	}

}

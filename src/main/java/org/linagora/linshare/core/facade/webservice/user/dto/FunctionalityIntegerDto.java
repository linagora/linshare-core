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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "FunctionalityInteger")
@Schema(description = "Dto representing an Integer functionality type")
public class FunctionalityIntegerDto extends FunctionalityDto {

	@Schema(description = "default value parameter")
	protected Integer value;

	@Schema(description = "maximum value parameter")
	protected Integer maxValue;

	@Schema(description = "Flag to use default value parameter")
	protected Boolean valueUsed;

	@Schema(description = "Flag to use maximum value parameter")
	protected Boolean maxValueUsed;

	public FunctionalityIntegerDto() {
		super();
	}

	public FunctionalityIntegerDto(String identifier, boolean enable, boolean canOverride, Integer value,
			Integer maxValue, Boolean valueUsed, Boolean maxValueUsed) {
		super(identifier, enable, canOverride);
		this.value = value;
		this.maxValue = maxValue;
		this.valueUsed = valueUsed;
		this.maxValueUsed = maxValueUsed;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getMaxValue() {
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

}

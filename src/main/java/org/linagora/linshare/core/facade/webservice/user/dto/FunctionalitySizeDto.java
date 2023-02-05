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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement(name = "FunctionalitySize")
public class FunctionalitySizeDto extends FunctionalityDto {

	protected Integer value;

	protected Integer maxValue;

	protected String unit;

	protected String maxUnit;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	protected Boolean unlimited;

	protected List<String> units = new ArrayList<String>();

	public FunctionalitySizeDto(FileSizeUnit... units) {
		super();
		for (FileSizeUnit sizeUnit : units) {
			this.units.add(sizeUnit.toString());
		}
	}

	public FunctionalitySizeDto() {
		super();
		this.units.add(FileSizeUnit.KILO.toString());
		this.units.add(FileSizeUnit.MEGA.toString());
		this.units.add(FileSizeUnit.GIGA.toString());
	}

	public FunctionalitySizeDto(Integer value, String unit, List<String> units) {
		super();
		this.value = value;
		this.unit = unit;
		this.units = units;
	}

	public Integer getValue() {
		return value;
	}

	@XmlElement
	public Long getRawSize() {
		if (getUnit() != null) {
			FileSizeUnit unit = FileSizeUnit.valueOf(getUnit());
			return unit.getSiSize(value);
		}
		return null;
	}

	@XmlElement
	public Long getMaxRawSize() {
		if (getMaxUnit() != null) {
			FileSizeUnit unit = FileSizeUnit.valueOf(getMaxUnit());
			return unit.getSiSize(maxValue);
		}
		return null;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMaxUnit() {
		return maxUnit;
	}

	public void setMaxUnit(String maxUnit) {
		this.maxUnit = maxUnit;
	}

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer defaultValue) {
		this.maxValue = defaultValue;
	}

	public Boolean isUnlimited() {
		return unlimited;
	}

	public void setUnlimited(Boolean unlimited) {
		this.unlimited = unlimited;
	}

}

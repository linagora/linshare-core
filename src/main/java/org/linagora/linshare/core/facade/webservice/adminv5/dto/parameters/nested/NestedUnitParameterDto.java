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
package org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "NestedUnitParameter",
	description = "A Time Unit NestedParameter",
	hidden = true
)
public abstract class NestedUnitParameterDto<U> extends NestedParameterDto<Integer> {

	protected U unit;

	protected U parentUnit;

	protected List<String> units;

	public NestedUnitParameterDto() {
		super();
	}

	public NestedUnitParameterDto(Integer value, Integer parentValue, U unit, U parentUnit, List<String> units) {
		super(value, parentValue);
		this.unit = unit;
		this.parentUnit = parentUnit;
		this.units = units;
	}

	public U getUnit() {
		return unit;
	}

	public void setUnit(U unit) {
		this.unit = unit;
	}

	public U getParentUnit() {
		return parentUnit;
	}

	public void setParentUnit(U parentUnit) {
		this.parentUnit = parentUnit;
	}

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("value", value)
				.add("parentValue", parentValue)
				.add("isOverriden", isOverriden())
				.add("unit", unit)
				.add("parentUnit", parentUnit)
				.add("units", units)
				.toString();
	}
}

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
package org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters;

import java.util.Optional;

import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedUnitParameterDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name = "TimeUnitMaximumParameter",
		description = "A parameter supporting maximum time unit value"
)
public class TimeUnitMaximumParameterDto extends ParameterDto<Integer> {

	public TimeUnitMaximumParameterDto(boolean hidden, boolean readonly,
			Optional<NestedUnitParameterDto<?>> maximum, UnlimitedParameterDto unlimited) {
		super(hidden, readonly, null, maximum.get(), unlimited);
	}

	@Override
	public String toString() {
		return "TimeUnitMaximumParameterDto [hidden=" + hidden + ", readonly=" + readonly + ", maximum=" + maximum
				+ ", unlimited=" + unlimited + "]";
	}
}

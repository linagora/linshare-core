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

import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedParameterDto;

import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
		name = "IntegerMaximumParameter",
		description = "A parameter supporting maximum integer value"
)
public class IntegerMaximumParameterDto extends ParameterDto<Integer> {

	public IntegerMaximumParameterDto() {
		super();
	}

	public IntegerMaximumParameterDto(boolean hidden, boolean readonly,
			Optional<NestedParameterDto<Integer>> maximum,
			UnlimitedParameterDto unlimited) {
		super();
		this.hidden = hidden;
		this.readonly = readonly;
		this.maximum = maximum.get();
		this.unlimited = unlimited;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("hidden", hidden)
				.add("readonly", readonly)
				.add("maximum", maximum)
				.add("unlimited", unlimited)
				.toString();
	}
}

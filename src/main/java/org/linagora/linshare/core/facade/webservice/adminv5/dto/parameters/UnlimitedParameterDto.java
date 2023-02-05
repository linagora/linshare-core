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

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(name = "Unlimited")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnlimitedParameterDto {

	@Schema(description = "True if unlimited is supported.",
			accessMode = AccessMode.READ_ONLY)
	protected Boolean supported = false;

	@Schema(description = "The current value")
	protected Boolean value;

	@Schema(description = "The current value of my domain ancestor (parent domain)",
			accessMode = AccessMode.READ_ONLY)
	protected Boolean parentValue;

	public UnlimitedParameterDto() {
		super();
	}

	public UnlimitedParameterDto(Boolean value, Boolean parentValue) {
		super();
		this.supported = true;
		this.value = value;
		this.parentValue = parentValue;
	}

	public Boolean getSupported() {
		return supported;
	}

	public void setSupported(Boolean supported) {
		this.supported = supported;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	public Boolean getParentValue() {
		return parentValue;
	}

	public void setParentValue(Boolean parentValue) {
		this.parentValue = parentValue;
	}

	@Override
	public String toString() {
		return "UnlimitedParameterDto [supported=" + supported + ", value=" + value + ", parentValue=" + parentValue
				+ "]";
	}

}

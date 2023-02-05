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
package org.linagora.linshare.webservice.support;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiVersion", description = "Supported API versions.")
public class ApiVersionDto {

	protected double version;

	public ApiVersionDto() {
		super();
	}

	public ApiVersionDto(double version) {
		super();
		this.version = version;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(version);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiVersionDto other = (ApiVersionDto) obj;
		if (Double.doubleToLongBits(version) != Double.doubleToLongBits(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApiVersion [version=" + version + "]";
	}
}

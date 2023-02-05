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

import java.util.List;

import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Api", description = "Supported API.")
public class ApiDto {

	protected ApiName name;

	protected List<ApiVersionDto> versions = Lists.newArrayList();

	public ApiDto(ApiName name, ApiVersionDto ... versions) {
		super();
		this.name = name;
		for (ApiVersionDto version : versions){
			this.versions.add(version);
		}
	}

	public ApiDto() {
		super();
	}

	public ApiName getName() {
		return name;
	}

	public List<ApiVersionDto> getVersions() {
		return versions;
	}

	@Override
	public String toString() {
		return "API [name=" + name + "]";
	}
}

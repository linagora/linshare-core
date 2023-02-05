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

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public interface SupportedApiVersionConstants {

	Map<ApiName, ApiDto> API = ImmutableMap.of(
			ApiName.USER, new ApiDto(ApiName.USER,
				new ApiVersionDto(5.0),
				new ApiVersionDto(4.0),
				new ApiVersionDto(2.0),
				new ApiVersionDto(1.0)
			),
			ApiName.ADMIN, new ApiDto(ApiName.ADMIN,
				new ApiVersionDto(5.0),
				new ApiVersionDto(4.0),
				new ApiVersionDto(1.0)
			),
			ApiName.DELEGATION, new ApiDto(ApiName.DELEGATION,
				new ApiVersionDto(2.0)
			),
			ApiName.UPLOADREQUEST, new ApiDto(ApiName.UPLOADREQUEST,
				new ApiVersionDto(4.0)
			)
	);
}

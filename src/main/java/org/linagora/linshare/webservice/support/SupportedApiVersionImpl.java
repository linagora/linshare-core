/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.webservice.support;

import static org.linagora.linshare.webservice.support.SupportedApiVersionConstants.API;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.collect.ImmutableMap;

@Produces(MediaType.APPLICATION_JSON)
public class SupportedApiVersionImpl implements SupportedApiVersion {

	@GET
	@Path("/api-versions")
	@Override
	public Map<ApiName, ApiDto> findAll(
			@QueryParam(value = "type") ApiName type) {
		if (type != null) {
			return ImmutableMap.of(type, API.get(type));
		}
		return API;
	}

	@GET
	@Path("/api-versions/{version}")
	@Override
	public ApiDto find(
			@QueryParam(value = "type") ApiName type,
			@PathParam("version") Double version) {
		Validate.notNull(type, "Missing type");
		Validate.notNull(version, "Missing version");
		ApiDto api = API.get(type);
		for (ApiVersionDto apiVersion : api.getVersions()) {
			if (apiVersion.getVersion().equals(version)) {
				return api;
			}
		}
		throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "invalid version");
	}
}

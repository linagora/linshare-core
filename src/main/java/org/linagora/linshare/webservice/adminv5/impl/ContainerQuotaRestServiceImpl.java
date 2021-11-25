/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.ContainerQuotaFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ContainerQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.ContainerQuotaRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
@Path("/domains/{domainUuid}/quotas/{quotaUuid}/containers")
public class ContainerQuotaRestServiceImpl extends WebserviceBase implements ContainerQuotaRestService {

	private ContainerQuotaFacade facade;

	public ContainerQuotaRestServiceImpl(ContainerQuotaFacade facade) {
		this.facade = facade;
	}

	@Path("/")
	@GET
	@Operation(summary = "find container quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContainerQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<ContainerQuotaDto> findAll(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("quotaUuid") String quotaUuid,
			@Parameter(description = "Compute real time quota value. Carefull it could be time consuming.", required = false)
				@QueryParam("containerType") ContainerQuotaType type) throws BusinessException {
		return facade.findAll(domainUuid, quotaUuid, type);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "find container quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContainerQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContainerQuotaDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("quotaUuid") String quotaUuid,
			@Parameter(description = "Container quota Uuid", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "Compute real time quota value. Carefull it could be time consuming.", required = false)
				@QueryParam("realtime") @DefaultValue("false")
					boolean realTime
				) throws BusinessException {
		return facade.find(domainUuid, quotaUuid, uuid, realTime);
	}

	@Path("/{uuid : .* }")
	@PUT
	@Operation(summary = "Update a container quota", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContainerQuotaDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public ContainerQuotaDto update(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("domainUuid") String domainUuid,
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("quotaUuid") String quotaUuid,
			@Parameter(description = "Container quota Uuid, if null dto.uuid is used.", required = false)
				@PathParam("uuid") String  uuid,
			@Parameter(description = "Container quota to update. Only quota, maxFileSize, override and maintenance fields can be updated. If null they will be ignored.", required = true) ContainerQuotaDto dto) throws BusinessException {
		return facade.update(domainUuid, quotaUuid, dto, uuid);
	}

}

/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SafeDetailFacade;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.SafeDetailRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


/**
 * @author Mehdi Attia
 *
 */

@Path("/{actorUuid}/safe_details")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SafeDetailRestServiceImpl extends WebserviceBase implements
		SafeDetailRestService {

	private final SafeDetailFacade safeDetailFacade;

	private String countryCode;

	private String controlKey;

	private String iufsc;

	public SafeDetailRestServiceImpl(SafeDetailFacade safeDetailFacade,
			String countryCode,
			String controlKey,
			String iufssc) {
		super();
		this.safeDetailFacade = safeDetailFacade;
		this.countryCode = countryCode;
		this.controlKey = controlKey;
		this.iufsc = iufssc;
	}

	@Path("/")
	@POST 
	@Operation(summary = "Create a safeDetail", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public SafeDetail create(
			@Parameter(description = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The safeDetail to Delete.", required = true) SafeDetail safeDetail)
			throws BusinessException {
		Validate.notEmpty(actorUuid, "actor uuid must be set.");
		Validate.notNull(safeDetail);
		safeDetail.setControlKey(controlKey);
		safeDetail.setCountryCode(countryCode);
		safeDetail.setIufsc(iufsc);
		return safeDetailFacade.create(actorUuid, safeDetail);
	}

	@Path("/{uuid : .*}")
	@DELETE
	@Operation(summary = "EXPERIMENTAL - Delete a safeDetail.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public SafeDetail delete(
			@Parameter(description = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The safeDetail uuid.", required = false) 
				@PathParam("uuid") String uuid,
			@Parameter(description = "The safeDetail to delete.", required = false) SafeDetail safeDetail) throws BusinessException {
		return safeDetailFacade.delete(actorUuid, uuid, safeDetail);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Get a SafeDetail.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public SafeDetail find(
			@Parameter(description = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@Parameter(description = "The safeDetail uuid.", required = true) 
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return safeDetailFacade.find(actorUuid, uuid);
	}

	@Path("/")
	@GET
	@Operation(summary = "EXPERIMENTAL - Get all safeDetails.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SafeDetail.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SafeDetail> findAll(
			@Parameter(description = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid)
			throws BusinessException {
		return safeDetailFacade.findAll(actorUuid);
	}
}

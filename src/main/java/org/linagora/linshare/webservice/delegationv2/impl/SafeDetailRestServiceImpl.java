/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SafeDetailFacade;
import org.linagora.linshare.mongo.entities.SafeDetail;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegationv2.SafeDetailRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @author Mehdi Attia
 *
 */

@Path("/{actorUuid}/safe_details")
@Api(value = "/rest/delegation/v2/{actorUuid}/safe_details", basePath = "/rest/delegation/v2/",
		description = "SafeDetail service.", produces = "application/json,application/xml",
		consumes = "application/json,application/xml")
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
	@ApiOperation(value = "Create a safeDetail", response = SafeDetail.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SafeDetail create(
			@ApiParam(value = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "The safeDetail to Delete.", required = true) SafeDetail safeDetail)
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
	@ApiOperation(value = "EXPERIMENTAL - Delete a safeDetail.", response = SafeDetail.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "SafeDetail not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SafeDetail delete(
			@ApiParam(value = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "The safeDetail uuid.", required = false) 
				@PathParam("uuid") String uuid,
			@ApiParam(value = "The safeDetail to delete.", required = false) SafeDetail safeDetail) throws BusinessException {
		return safeDetailFacade.delete(actorUuid, uuid, safeDetail);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a SafeDetail.", response = SafeDetail.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 404, message = "SafeDetail not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public SafeDetail find(
			@ApiParam(value = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "The safeDetail uuid.", required = true) 
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return safeDetailFacade.find(actorUuid, uuid);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "EXPERIMENTAL - Get all safeDetails.", response = SafeDetail.class, responseContainer = "Set")
	@ApiResponses({
			@ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<SafeDetail> findAll(
			@ApiParam(value = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid)
			throws BusinessException {
		return safeDetailFacade.findAll(actorUuid);
	}
}

/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.UploadPropositionExceptionRuleType;
import org.linagora.linshare.core.facade.webservice.user.UploadPropositionExceptionRuleFacade;
import org.linagora.linshare.mongo.entities.UploadPropositionExceptionRule;
import org.linagora.linshare.webservice.delegationv2.UploadPropositionExceptionRuleRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/{actorUuid}/upload_proposition_exception_rules")
@Api(value = "/rest/delegation/v2/upload_proposition_exception_rules", description = "Upload Proposition Exception Rules service")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadPropositionExceptionRuleRestServiceImpl implements UploadPropositionExceptionRuleRestService {

	private final UploadPropositionExceptionRuleFacade exceptionRuleFacade;

	public UploadPropositionExceptionRuleRestServiceImpl(UploadPropositionExceptionRuleFacade exceptionRuleFacade) {
		super();
		this.exceptionRuleFacade = exceptionRuleFacade;
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get an upload proposition exception rule")
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to create a Exception rule."),
		@ApiResponse(code = 400, message = "Bad request : missing required fields."),
		@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public UploadPropositionExceptionRule find(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Uploadproposition exception rule uuid", required = true)
				@PathParam("uuid") String uuid){
		return exceptionRuleFacade.find(actorUuid, uuid);
	}

	@Path("/list/{exceptionRuleType}")
	@GET
	@ApiOperation(value = "Get white/black list of a user")
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to create a Exception rule."),
		@ApiResponse(code = 400, message = "Bad request : missing required fields."),
		@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public List<UploadPropositionExceptionRule> findByExceptionRuleType(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Uploadproposition exception rule type", required = true)
				@PathParam("exceptionRuleType") UploadPropositionExceptionRuleType exceptionRuleType){
		return exceptionRuleFacade.findByExceptionRuleType(actorUuid, exceptionRuleType);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Add a contact to the white/black list of the upload proposition")
	@ApiResponses({ @ApiResponse(code = 403, message = "No permission to create a Exception rule."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public UploadPropositionExceptionRule create(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Payload to create an exception rule", required = true) UploadPropositionExceptionRule exceptionRule) {
		return exceptionRuleFacade.create(actorUuid, exceptionRule);
	}
}

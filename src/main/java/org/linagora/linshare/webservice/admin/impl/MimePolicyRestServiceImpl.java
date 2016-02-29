/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimePolicyFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MimePolicyRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/mime_policies")
@Api(value = "/rest/admin/mime_policies", description = "Mime policies service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MimePolicyRestServiceImpl extends WebserviceBase implements
		MimePolicyRestService {

	private final MimePolicyFacade mimePolicyFacade;

	public MimePolicyRestServiceImpl(final MimePolicyFacade mimePolicyFacade) {
		this.mimePolicyFacade = mimePolicyFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all the mime policies by domain.", response = MimePolicyDto.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public Set<MimePolicyDto> findAll(
			@ApiParam(value = "Identifier of the domain which you are looking into.", required = true) @QueryParam("domainId") String domainId,
			@ApiParam(value = "Return current and parent domain's mime policies,"
					+ " or only current domain's if onlyCurrentDomain is true.") @QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain)
			throws BusinessException {
		return mimePolicyFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MimePolicyDto find(
			@ApiParam(value = "Uuid of the mime policy to search for.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Return mime policy with mime types.") @QueryParam("full") @DefaultValue("false") boolean full)
			throws BusinessException {
		MimePolicyDto find = mimePolicyFacade.find(uuid, full);
		return find;
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a mime policy.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void head(
			@ApiParam(value = "Uuid of the mime policy to search for.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		mimePolicyFacade.find(uuid, false);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin."),
			@ApiResponse(code = 400, message = "Invalid mime policy.") })
	@Override
	public MimePolicyDto create(
			@ApiParam(value = "Policy to create.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.create(policy);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MimePolicyDto update(
			@ApiParam(value = "Policy to update.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.update(policy);
	}

	@Path("/{uuid}/enable_all")
	@PUT
	@ApiOperation(value = "Set all mime types to enable for the current mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MimePolicyDto enableAllMimeTypes(
			@ApiParam(value = "Uuid of the mime policy.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.enableAllMimeTypes(uuid);
	}

	@Path("/{uuid}/disable_all")
	@PUT
	@ApiOperation(value = "Set all mime types to disable for the current mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MimePolicyDto disableAllMimeTypes(
			@ApiParam(value = "Uuid of the mime policy.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.disableAllMimeTypes(uuid);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MimePolicyDto delete(
			@ApiParam(value = "Identifier of the mime policy to delete.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mimePolicyFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a mime policy.", response = MimePolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MimePolicyDto delete(
			@ApiParam(value = "Policy to delete.", required = true) MimePolicyDto policy)
			throws BusinessException {
		return mimePolicyFacade.delete(policy.getUuid());
	}
}

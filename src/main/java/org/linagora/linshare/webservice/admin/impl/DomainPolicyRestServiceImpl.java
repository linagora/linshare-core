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
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPolicyFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainPolicyRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/domain_policies")
@Api(value = "/rest/admin/domain_policies", description = "Domain policies service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainPolicyRestServiceImpl extends WebserviceBase implements
		DomainPolicyRestService {

	private final DomainPolicyFacade domainPolicyFacade;

	public DomainPolicyRestServiceImpl(
			final DomainPolicyFacade domainPolicyFacade) {
		this.domainPolicyFacade = domainPolicyFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all the domain policies.", response = DomainPolicyDto.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't superadmin.") })
	@Override
	public Set<DomainPolicyDto> findAll() throws BusinessException {
		return domainPolicyFacade.findAll();
	}

	@Path("/{policyId}")
	@GET
	@ApiOperation(value = "Find a domain policy.", response = DomainPolicyDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't superadmin."),
			@ApiResponse(code = 404, message = "Domain policy not found.") })
	@Override
	public DomainPolicyDto find(
			@ApiParam(value = "Identifier of the domain policy to search for.", required = true) @PathParam("policyId") String policyId)
			throws BusinessException {
		return domainPolicyFacade.find(policyId);
	}

	@Path("/{policyId}")
	@HEAD
	@ApiOperation(value = "Check if a domain policy exists.", response = DomainPolicyDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't superadmin."),
			@ApiResponse(code = 404, message = "Domain policy not found.") })
	@Override
	public void head(
			@ApiParam(value = "Identifier of the domain policy to search for.", required = true) @PathParam("policyId") String policyId)
			throws BusinessException {
		domainPolicyFacade.find(policyId);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a domain policy.", response = DomainPolicyDto.class)
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't superadmin."),
			@ApiResponse(code = 400, message = "Invalid domain policy.") })
	@Override
	public DomainPolicyDto create(
			@ApiParam(value = "Policy to create.", required = true) DomainPolicyDto policy)
			throws BusinessException {
		return domainPolicyFacade.create(policy);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a domain policy.", response = DomainPolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't superadmin.") })
	@Override
	public DomainPolicyDto update(
			@ApiParam(value = "Policy to update.", required = true) DomainPolicyDto policy)
			throws BusinessException {
		return domainPolicyFacade.update(policy);
	}

	@Path("/{policyId}")
	@DELETE
	@ApiOperation(value = "Delete a domain policy.", response = DomainPolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't superadmin.") })
	@Override
	public DomainPolicyDto delete(
			@ApiParam(value = "Identifier of the domain policy to delete.", required = true) @PathParam("policyId") String policyId)
			throws BusinessException {
		return domainPolicyFacade.delete(policyId);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a domain policy.", response = DomainPolicyDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't superadmin.") })
	@Override
	public DomainPolicyDto delete(
			@ApiParam(value = "Policy to delete.", required = true) DomainPolicyDto policy)
			throws BusinessException {
		return domainPolicyFacade.delete(policy.getIdentifier());
	}
}

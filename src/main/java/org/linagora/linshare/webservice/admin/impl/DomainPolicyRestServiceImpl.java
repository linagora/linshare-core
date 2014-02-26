/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.*;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPolicyFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainPolicyRestService;
import org.linagora.linshare.webservice.dto.DomainPolicyDto;

@Path("/domain_policies")
@Api(value = "/admin/domain_policies", description = "Domain policies service.")
public class DomainPolicyRestServiceImpl extends WebserviceBase implements
		DomainPolicyRestService {

	private final DomainPolicyFacade domainPolicyFacade;

	public DomainPolicyRestServiceImpl(
			final DomainPolicyFacade domainPolicyFacade) {
		this.domainPolicyFacade = domainPolicyFacade;
	}
	
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Find all the domain policies.", response = DomainPolicyDto.class, responseContainer = "List")
    @ApiResponses({ @ApiResponse(code = 401, message = "User isn't superadmin.") })
    @Override
	public List<DomainPolicyDto> getAll() throws BusinessException {
		domainPolicyFacade.checkAuthentication();
		return domainPolicyFacade.getAll();
	}

	@Path("/")
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Create a new domain policy.")
    @ApiResponses({
            @ApiResponse(code = 401, message = "User isn't superadmin."),
            @ApiResponse(code = 400, message = "Invalid domain policy.")})
	@Override
	public void create(DomainPolicyDto policy) throws BusinessException {
		domainPolicyFacade.checkAuthentication();
		domainPolicyFacade.create(policy);
	}

	@Path("/")
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Update an existing domain policy.")
    @ApiResponses({ @ApiResponse(code = 401, message = "User isn't superadmin.") })
	@Override
	public void update(DomainPolicyDto policy)
			throws BusinessException {
		domainPolicyFacade.checkAuthentication();
		domainPolicyFacade.update(policy);
	}

	@Path("/{identifier}")
	@GET
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Find a domain policiy.", response = DomainPolicyDto.class)
    @ApiResponses({ @ApiResponse(code = 401, message = "User isn't superadmin.") })
	@Override
    public DomainPolicyDto get(
            @ApiParam(value = "Identifier of the domain policy to search for.", required = true)
            @PathParam("identifier")
            String identifier)
            throws BusinessException {
        domainPolicyFacade.checkAuthentication();
        return domainPolicyFacade.get(identifier);
    }

	@Path("/{identifier}")
	@DELETE
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Delete a new domain policy.")
    @ApiResponses({ @ApiResponse(code = 401, message = "User isn't superadmin.") })
	@Override
	public void delete(
            @ApiParam(value = "Identifier of the domain policy to delete.", required = true)
            @PathParam("identifier")
            String identifier)
			throws BusinessException {
		domainPolicyFacade.checkAuthentication();
		domainPolicyFacade.delete(identifier);
	}
}

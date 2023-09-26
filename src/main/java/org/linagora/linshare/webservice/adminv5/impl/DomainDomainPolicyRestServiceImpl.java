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
package org.linagora.linshare.webservice.adminv5.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPolicyFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.ErrorDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.DomainDomainPolicyRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains/{domainUuid}/domain_policy")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class DomainDomainPolicyRestServiceImpl extends WebserviceBase implements DomainDomainPolicyRestService {

    private final DomainPolicyFacade domainPolicyFacade;

    public DomainDomainPolicyRestServiceImpl(DomainPolicyFacade domainPolicyFacade) {
        this.domainPolicyFacade = domainPolicyFacade;
    }

    @Path("/{uuid}/assign")
    @POST
    @Operation(summary = "It allows to assign a domain policy to this domain.", responses = {
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            schema = @Schema(
                                    implementation = Void.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "40X",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorDto.class
                            )
                    )
            )
    })
    @Override
    public void assign(
            @Parameter(description = "domain's uuid.", required = true)
            @PathParam("domainUuid") String domainUuid,
            @Parameter(description = "domain policy's uuid to assign.", required = true)
            @PathParam("uuid") String domainPolicyUuid)
            throws BusinessException {
        domainPolicyFacade.assign(domainUuid, domainPolicyUuid);
    }
}

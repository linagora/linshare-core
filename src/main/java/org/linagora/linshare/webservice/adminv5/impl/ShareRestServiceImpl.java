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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.ShareFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ShareRecipientStatisticDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.ShareRestService;
import org.linagora.linshare.webservice.utils.PageContainer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


//Class created to generate the swagger documentation
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ShareRestServiceImpl extends WebserviceBase implements ShareRestService {

	private final ShareFacade shareFacade;

	public ShareRestServiceImpl(final ShareFacade facade) {
		this.shareFacade = facade;
	}


	@Path("/topSharesByFileSize")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Find top shares ordered by file size.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ShareRecipientStatisticDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public PageContainer<ShareRecipientStatisticDto> getTopSharesByFileSize(
			@Parameter(description = "domain's uuid") @QueryParam("domainUuid") String domainUuid,
			@Parameter(description = "shares range begin date") @QueryParam("beginDate") String beginDate,
			@Parameter(description = "shares range end date") @QueryParam("endDate") String endDate,
			@Parameter(description = "page number to get", required = false) @QueryParam("page") Integer pageNumber,
			@Parameter(description = "number of elements to get.", required = false) @QueryParam("size") @DefaultValue("50") Integer pageSize)
			throws BusinessException {
		//TODO: add more test data with external users?
		PageContainer<ShareRecipientStatisticDto> container = new PageContainer<>(pageNumber, pageSize);
		return container.loadDataAndCount(shareFacade.getTopSharesByFileSize(domainUuid, beginDate, endDate));
	}

}

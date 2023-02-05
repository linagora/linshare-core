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
package org.linagora.linshare.webservice.userv5.impl;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.AsyncTaskFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentAsyncFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.annotations.NoCache;
import org.linagora.linshare.webservice.userv2.DocumentRestService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/documents")
public class DocumentRestServiceImpl extends org.linagora.linshare.webservice.userv2.impl.DocumentRestServiceImpl implements DocumentRestService {


	public DocumentRestServiceImpl(DocumentFacade documentFacade, DocumentAsyncFacade documentAsyncFacade,
			ThreadPoolTaskExecutor taskExecutor, AsyncTaskFacade asyncTaskFacade, AccountQuotaFacade accountQuotaFacade,
			boolean sizeValidation) {
		super(documentFacade, documentAsyncFacade, taskExecutor, asyncTaskFacade, accountQuotaFacade, sizeValidation);
	}

	@Path("/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Get a document.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override

	public DocumentDto find(@Parameter(description = "The document uuid.", required = true) @PathParam("uuid") String uuid,
			@Parameter(description = "If you want document shares too.", required = false) @QueryParam("withShares") @DefaultValue("false") boolean withShares)
			throws BusinessException {
		return documentFacade.find(Version.V5, uuid, withShares);
	}

	@NoCache
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Operation(summary = "Get all documents.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<DocumentDto> findAll() throws BusinessException {
		return documentFacade.findAll(Version.V5);
	}
}

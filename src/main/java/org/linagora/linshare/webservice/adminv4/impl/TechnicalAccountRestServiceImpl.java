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
package org.linagora.linshare.webservice.adminv4.impl;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;
import org.linagora.linshare.utils.Version;
import org.linagora.linshare.webservice.admin.TechnicalAccountRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/technical_accounts")
public class TechnicalAccountRestServiceImpl extends org.linagora.linshare.webservice.admin.impl.TechnicalAccountRestServiceImpl implements TechnicalAccountRestService{

	public TechnicalAccountRestServiceImpl(TechnicalAccountFacade technicalAccountFacade) {
		super(technicalAccountFacade);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto create(TechnicalAccountDto account)
			throws BusinessException {
		return technicalAccountFacade.create(account, Version.V4);
	}

}

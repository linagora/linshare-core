/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.common.dto.TechnicalAccountDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.TechnicalAccountRestService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/technical_accounts")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class TechnicalAccountRestServiceImpl extends WebserviceBase implements
		TechnicalAccountRestService {

	protected final TechnicalAccountFacade technicalAccountFacade;

	public TechnicalAccountRestServiceImpl(
			TechnicalAccountFacade technicalAccountFacade) {
		super();
		this.technicalAccountFacade = technicalAccountFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all technical accounts.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<TechnicalAccountDto> findAll() throws BusinessException {
		return technicalAccountFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto find(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return technicalAccountFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a technical account.")
	@Override
	public void head(@PathParam(value = "uuid") String uuid) throws BusinessException {
		technicalAccountFacade.find(uuid);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto update(TechnicalAccountDto account)
			throws BusinessException {
		return technicalAccountFacade.update(account);
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
		return technicalAccountFacade.create(account, 1);
	}

	@Path("/{uuid}/change_password")
	@POST
	@Operation(summary = "Change the password of a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public void changePassword(@PathParam(value = "uuid") String uuid, PasswordDto password) throws BusinessException {
		technicalAccountFacade.changePassword(uuid, password);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto delete(TechnicalAccountDto account) throws BusinessException {
		return technicalAccountFacade.delete(account);
	}

	@Path("/{uuid}")
	@DELETE
	@Operation(summary = "Delete a technical account.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = TechnicalAccountDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public TechnicalAccountDto delete(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return technicalAccountFacade.delete(uuid);
	}
}

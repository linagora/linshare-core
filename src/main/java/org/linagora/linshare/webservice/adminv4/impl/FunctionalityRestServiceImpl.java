/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.webservice.adminv4.impl;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.webservice.adminv4.FunctionalityRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public class FunctionalityRestServiceImpl extends
		org.linagora.linshare.webservice.admin.impl.FunctionalityRestServiceImpl implements FunctionalityRestService {

	public FunctionalityRestServiceImpl(FunctionalityFacade functionalityFacade) {
		super(functionalityFacade);
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all domain's functionalities.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityAdminDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<FunctionalityAdminDto> findAll(
			@QueryParam(value = "domainId") String domainId,
			@QueryParam(value = "parentId") String parentId,
			@QueryParam("tree") @DefaultValue("false") boolean tree,
			@QueryParam("subs") @DefaultValue("false") boolean withSubFunctionalities)
			throws BusinessException {
		return functionalityFacade.findAll(4, domainId, parentId, tree, withSubFunctionalities);
	}

	@Path("/{funcId}")
	@GET
	@Operation(summary = "Find a domain's functionality.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FunctionalityAdminDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public FunctionalityAdminDto find(
			@QueryParam(value = "domainId") String domainId,
			@PathParam(value = "funcId") String funcId,
			@QueryParam("tree") @DefaultValue("false") boolean tree)
			throws BusinessException {
		return functionalityFacade.find(4, domainId, funcId, tree);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a domain's functionality.")
	@Override
	public FunctionalityAdminDto update(FunctionalityAdminDto func)
			throws BusinessException {
		return functionalityFacade.update(4, func);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete a domain's functionality.")
	@Override
	public void delete(FunctionalityAdminDto func) throws BusinessException {
		functionalityFacade.delete(4, func);
	}
}

/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.facade.webservice.adminv5.DomainFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv5.DomainRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/domains")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class DomainRestServiceImpl extends WebserviceBase implements
		DomainRestService {

	private final DomainFacade domainFacade;

	public DomainRestServiceImpl(final DomainFacade domainFacade) {
		this.domainFacade = domainFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "It will return all domains where you are allowed to perform administration tasks.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<DomainDto> findAll(
			@Parameter(description = "If enable, your own domain will be returned as a list of one entry. "
					+ "Parent domains and children domains will be also provided as a tree.")
				@QueryParam("tree") @DefaultValue("false") boolean tree) {
		return domainFacade.findAll(tree);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "It will return the detail of a domain. You must be administrator of this domain to be able to get its details.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = DomainDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public DomainDto find(
			@Parameter(description = "domain's uuid.", required = true)
				@PathParam("uuid") String uuid,
			@Parameter(description = "If enable, the domain will be returned with its parent domains and  its children domains, as a tree. "
					+ "It will override 'detail' query param.")
				@QueryParam("tree") @DefaultValue("false") boolean tree,
			@Parameter(description = "If true, more sensitive information about the domain will be provided.")
				@QueryParam("detail") @DefaultValue("false") boolean detail) {
		return domainFacade.find(uuid, tree, detail);
	}

}

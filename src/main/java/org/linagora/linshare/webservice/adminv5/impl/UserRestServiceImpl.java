/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.webservice.adminv5.UserRestService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PagingResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/users")
public class UserRestServiceImpl implements UserRestService {

	private final UserFacade userFacade;

	private PagingResponseBuilder<UserDto> pageResponseBuilder= new PagingResponseBuilder<>();

	public UserRestServiceImpl(
			UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all users.", responses = { @ApiResponse(responseCode = "200") })
	@Override
	public Response findAll(
			@Parameter(description = "If the admin specify the domain he will retrieve the list of this domain, else all users of all domains will be returned.", required = false)
				@QueryParam("domain") String domainUuid,
			@Parameter(description = "The admin can choose the creation date of the users' list to filter.", required = false)
				@QueryParam("creationDate") String creationDate,
			@Parameter(description = "The admin can choose the modification date of the users' list to filter.", required = false)
				@QueryParam("modificationDate") String modificationDate,
			@Parameter(description = "The admin can filter the users' list by mail adress.", required = false)
				@QueryParam("mail") String mail,
			@Parameter(description = "The admin can filter the users' list by first name.", required = false)
				@QueryParam("firstName") String firstName,
			@Parameter(description = "The admin can filter the users' list by last name.", required = false)
				@QueryParam("lastName") String lastName,
			@Parameter(description = "The admin can filter the restricted users' list.", required = false)
				@QueryParam("restricted") Boolean restricted,
			@Parameter(description = "The admin can retrieve the users whose can create guest.", required = false)
				@QueryParam("canCreateGuest") Boolean canCreateGuest,
			@Parameter(description = "The admin can retrieve the users whose can upload.", required = false)
				@QueryParam("canUpload") Boolean canUpload,
			@Parameter(description = "The admin can retrieve the users with a choosen role.", required = false)
				@QueryParam("role") String role,
			@Parameter(description = "The admin can retrieve the users with a choosen type.", required = false)
				@QueryParam("type") String type,
			@Parameter(description = "The admin can choose the page number to visualize.", required = false)
				@QueryParam("page") Integer pageNumber,
			@Parameter(description = "The admin can choose the number of elements to visualize.", required = false)
				@QueryParam("size") Integer pageSize) throws BusinessException {
		PageContainer<UserDto> container = userFacade.findAll(null, domainUuid, creationDate, modificationDate, mail,
				firstName, lastName, restricted, canCreateGuest, canUpload, role, type, pageNumber, pageSize);
		return pageResponseBuilder.build(container);
	}
}

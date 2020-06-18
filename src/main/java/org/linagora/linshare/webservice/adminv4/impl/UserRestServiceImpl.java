/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.webservice.adminv4.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.SecondFactorDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.adminv4.UserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/users")
public class UserRestServiceImpl extends WebserviceBase implements UserRestService {

	private static final Logger logger = LoggerFactory.getLogger(UserRestServiceImpl.class);

	private final UserFacade userFacade;

	public UserRestServiceImpl(final UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	@Path("/2fa/{userUuid: .*}")
	@DELETE
	@Operation(summary = "Delete a shared key of a given user, 2fa will be disabled ", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = SecondFactorDto.class)), responseCode = "200") })
	@Override
	public SecondFactorDto delete2FA(
			@Parameter(description = "The user uuid for who shared key will be removed.", required = false)
				@PathParam("userUuid") String userUuid,
			@Parameter(description = "Second factor dto , contains the shared key.", required = true) SecondFactorDto dto)
			throws BusinessException {
		logger.info("Removing shared key and disabling 2FA ");
		return userFacade.delete2FA(userUuid, dto);
	}

	@Path("/2fa/{userUuid}")
	@GET
	@Operation(summary = "Get the 2FA state ", responses = {
			@ApiResponse(content = @Content(schema = @Schema(implementation = SecondFactorDto.class)), responseCode = "200") })
	@Override
	public SecondFactorDto find2FA(
			@Parameter(description = "user uuid to get 2fa state.", required = false) 
				@PathParam("userUuid") String userUuid)
			throws BusinessException {
		logger.info("Getting the 2FA state for current user");
		return userFacade.find2FA(userUuid);
	}
}

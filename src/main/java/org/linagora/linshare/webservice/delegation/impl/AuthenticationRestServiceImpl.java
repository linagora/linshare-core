/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.webservice.delegation.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PasswordDto;
import org.linagora.linshare.core.facade.webservice.delegation.DelegationGenericFacade;
import org.linagora.linshare.core.facade.webservice.delegation.UserFacade;
import org.linagora.linshare.core.facade.webservice.delegation.dto.AccountDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.delegation.AuthenticationRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/authentication")
@Api(value = "/rest/delegation/authentication", basePath = "/rest/delegation/", description = "Authentication delegation API",
produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class AuthenticationRestServiceImpl extends WebserviceBase implements AuthenticationRestService {

	private final DelegationGenericFacade delegationGenericFacade;

	private final UserFacade userFacade;

	public AuthenticationRestServiceImpl(final DelegationGenericFacade delegationFacade, final UserFacade userFacade) {
		this.delegationGenericFacade = delegationFacade;
		this.userFacade = userFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "No operation.")
	@Override
	public void noop() {
		return; // do nothing
	}

	@Path("/authorized")
	@GET
	@ApiOperation(value = "Check if user is authorized.", response = AccountDto.class)
	@Override
	public AccountDto isAuthorized() throws BusinessException {
		return delegationGenericFacade.isAuthorized();
	}

	@Path("/change_password")
	@POST
	@ApiOperation(value = "Change the password of the current user.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void changePassword(@ApiParam(value = "New password.", required = true) PasswordDto password) throws BusinessException {
		userFacade.changePassword(password);
	}

	@Path("/logout")
	@GET
	@ApiOperation(value = "Logout the current user.")
	@Override
	public void logout() {
		// This code is never reach because the URL will be catch by spring security before.
		// This function was created just to show the logout URL into WADL.
	}

	@Path("/version")
	@GET
	@Override
	public String getVersion() {
		return getCoreVersion();
	}
}

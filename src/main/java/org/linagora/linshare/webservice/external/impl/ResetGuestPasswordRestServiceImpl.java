/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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
package org.linagora.linshare.webservice.external.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ResetPasswordDto;
import org.linagora.linshare.core.facade.webservice.external.ResetGuestPasswordFacade;
import org.linagora.linshare.core.facade.webservice.external.dto.AnonymousUrlDto;
import org.linagora.linshare.mongo.entities.ResetGuestPassword;
import org.linagora.linshare.webservice.external.ResetGuestPasswordRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/reset_password")
@Api(value = "/rest/external/reset_password", description = "reset guest password url API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ResetGuestPasswordRestServiceImpl implements ResetGuestPasswordRestService {

	protected ResetGuestPasswordFacade facade;

	public ResetGuestPasswordRestServiceImpl(ResetGuestPasswordFacade facade) {
		super();
		this.facade = facade;
	}

	@GET
	@Path("/{uuid}")
	@ApiOperation(value = "Find an anonymous Url", response = AnonymousUrlDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public ResetGuestPassword find(
			@ApiParam(value = "uuid", required = true) @PathParam(value = "uuid") String uuid) throws BusinessException {
		return facade.find(uuid);
	}

	@PUT
	@Path("/{uuid}")
	@Override
	public ResetGuestPassword update(
			@ApiParam(value = "uuid", required = true) @PathParam(value = "uuid") String uuid,
			ResetGuestPassword reset) throws BusinessException {
		return facade.update(uuid, reset);
	}

	@POST
	@Path("/")
	@Override
	public void create(
			@HeaderParam("domain-uuid") String domainUuid, ResetPasswordDto resetDto) throws BusinessException {
		facade.create(domainUuid, resetDto);
	}

}

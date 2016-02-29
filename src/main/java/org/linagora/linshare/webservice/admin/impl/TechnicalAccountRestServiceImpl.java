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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/technical_accounts")
@Api(value = "/rest/admin/technical_accounts", description = "Technical accounts service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class TechnicalAccountRestServiceImpl extends WebserviceBase implements
		TechnicalAccountRestService {

	private final TechnicalAccountFacade technicalAccountFacade;

	public TechnicalAccountRestServiceImpl(
			TechnicalAccountFacade technicalAccountFacade) {
		super();
		this.technicalAccountFacade = technicalAccountFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all technical accounts.", response = TechnicalAccountDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin.") })
	@Override
	public Set<TechnicalAccountDto> findAll() throws BusinessException {
		return technicalAccountFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a technical account.", response = TechnicalAccountDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public TechnicalAccountDto find(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return technicalAccountFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a technical account.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void head(@PathParam(value = "uuid") String uuid) throws BusinessException {
		technicalAccountFacade.find(uuid);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a technical account.", response = TechnicalAccountDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public TechnicalAccountDto update(TechnicalAccountDto account)
			throws BusinessException {
		return technicalAccountFacade.update(account);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a technical account.", response = TechnicalAccountDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public TechnicalAccountDto create(TechnicalAccountDto account)
			throws BusinessException {
		return technicalAccountFacade.create(account);
	}

	@Path("/{uuid}/change_password")
	@POST
	@ApiOperation(value = "Change the password of a technical account.", response = TechnicalAccountDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void changePassword(@PathParam(value = "uuid") String uuid, PasswordDto password) throws BusinessException {
		technicalAccountFacade.changePassword(uuid, password);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a technical account.", response = TechnicalAccountDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public TechnicalAccountDto delete(TechnicalAccountDto account) throws BusinessException {
		return technicalAccountFacade.delete(account);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a technical account.", response = TechnicalAccountDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public TechnicalAccountDto delete(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return technicalAccountFacade.delete(uuid);
	}
}

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
import org.linagora.linshare.core.facade.webservice.admin.MailingListFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.MailingListDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailingListRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/lists")
@Api(value = "/rest/admin/lists", description = "Mailing lists administration")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailingListRestServiceImpl extends WebserviceBase implements
		MailingListRestService {

	private final MailingListFacade mailingListFacade;

	public MailingListRestServiceImpl(final MailingListFacade mailingListFacade) {
		super();
		this.mailingListFacade = mailingListFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all mailing lists.", response = MailingListDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public Set<MailingListDto> findAll() throws BusinessException {
		return mailingListFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailingListDto find(
			@ApiParam(value = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailingListFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a mailing list.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void head(
			@ApiParam(value = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailingListFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailingListDto create(
			@ApiParam(value = "Mailing list to create.", required = true) MailingListDto dto)
			throws BusinessException {
		return mailingListFacade.create(dto);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailingListDto update(
			@ApiParam(value = "Mailing list to update.", required = true) MailingListDto dto)
			throws BusinessException {
		return mailingListFacade.update(dto);
	}

	@Path("/{uuid}")
	@DELETE
	@ApiOperation(value = "Delete a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailingListDto delete(@PathParam(value = "Mailing list to delete.") String uuid)
			throws BusinessException {
		return mailingListFacade.delete(uuid);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a mailing list.", response = MailingListDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailingListDto delete(
			@ApiParam(value = "Mailing list to delete.", required = true) MailingListDto dto)
			throws BusinessException {
		return mailingListFacade.delete(dto.getUuid());
	}

	@Path("/{uuid}/contacts")
	@POST
	@ApiOperation(value = "Create a contact in a mailing list.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void createContact(
			@ApiParam(value = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Contact to create.", required = true) MailingListContactDto dto)
			throws BusinessException {
		mailingListFacade.addContact(uuid, dto);
	}

	@Path("/{uuid}/contacts")
	@DELETE
	@ApiOperation(value = "Delete a contact from a mailing list.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void deleteContact(
			@ApiParam(value = "Mailing list uuid.", required = true) @PathParam("uuid") String uuid,
			@ApiParam(value = "Contact uuid.", required = true) MailingListContactDto dto)
			throws BusinessException {
		mailingListFacade.deleteContact(dto.getUuid());
	}
}

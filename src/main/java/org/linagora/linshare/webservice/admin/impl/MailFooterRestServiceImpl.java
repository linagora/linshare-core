/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailFooterFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailFooterRestService;
import org.linagora.linshare.webservice.dto.MailFooterDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/mail_footers")
@Api(value = "/rest/admin/mail_footers", description = "Mail footers used by domains")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailFooterRestServiceImpl extends WebserviceBase implements MailFooterRestService {

	private final MailFooterFacade mailFooterFacade;

	public MailFooterRestServiceImpl(final MailFooterFacade mailFooterFacade) {
		super();
		this.mailFooterFacade = mailFooterFacade;
	}

	@Path("/")
	@GET
	@Override
	public List<MailFooterDto> findAll(
			@QueryParam(value = "domainId") String domainId)
			throws BusinessException {
		return mailFooterFacade.getMailFooters(domainId);
	}

	@Override
	@Path("/{uuid}")
	@ApiOperation(value = "Find a mail footer.", response = MailFooterDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@GET
	public MailFooterDto find(
			@ApiParam(value = "Mail footer's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailFooterFacade.find(uuid);
	}

	@Override
	@Path("/")
	@ApiOperation(value = "Create a mail footer.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void create(
			@ApiParam(value = "Mail footer to create.", required = true) MailFooterDto dto)
			throws BusinessException {
		mailFooterFacade.create(dto);
	}

	@Override
	@Path("/")
	@ApiOperation(value = "Update a mail footer.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void update(
			@ApiParam(value = "Mail footer to update.", required = true) MailFooterDto dto)
			throws BusinessException {
		mailFooterFacade.update(dto);
	}

	@Override
	@Path("/")
	@ApiOperation(value = "Delete an unused mail footer.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
    @DELETE
	public void delete(
			@ApiParam(value = "Mail footer to delete.", required = true) MailFooterDto dto)
			throws BusinessException {
		mailFooterFacade.delete(dto.getUuid());
	}
}

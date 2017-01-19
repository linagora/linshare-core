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

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailContentFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContainerDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailContentRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/mail_contents")
@Api(value = "/rest/admin/mail_contents", description = "Mail contents used by domains")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailContentRestServiceImpl extends WebserviceBase implements
		MailContentRestService {

	private final MailContentFacade mailContentFacade;

	public MailContentRestServiceImpl(final MailContentFacade mailContentFacade) {
		super();
		this.mailContentFacade = mailContentFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all mail contents.", response = MailContentDto.class, responseContainer = "Set")
	@Override
	public Set<MailContentDto> findAll(
			@QueryParam(value = "domainId") String domainId,
			@QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain)
			throws BusinessException {
		return mailContentFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a mail content.", response = MailContentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailContentDto find(
			@ApiParam(value = "Mail content's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailContentFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a mail content.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void head(
			@ApiParam(value = "Mail content's uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailContentFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a mail content.", response = MailContentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailContentDto create(
			@ApiParam(value = "Mail content to create.", required = true) MailContentDto dto)
			throws BusinessException {
		return mailContentFacade.create(dto);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a mail content.", response = MailContentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailContentDto update(
			@ApiParam(value = "Mail content to update.", required = true) MailContentDto dto)
			throws BusinessException {
		return mailContentFacade.update(dto);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete an unused mail content.", response = MailContentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailContentDto delete(
			@ApiParam(value = "Mail content to delete.", required = true) MailContentDto dto)
			throws BusinessException {
		return mailContentFacade.delete(dto.getUuid());
	}

	@Path("/{uuid}/build")
	@GET
	@ApiOperation(value = "build a mail content.", response = MailContainerDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailContainerDto fakeBuild(
			@ApiParam(value = "Mail content's uuid.", required = true)
				@PathParam("uuid") String mailContentUuid,
			@ApiParam(value = "Language to use for the build")
				@QueryParam(value = "language") String language,
			@ApiParam(value = "Mail config's uuid.")
				@QueryParam(value = "mailConfig") String mailConfigUuid,
			@ApiParam(value = "Mail flavor.")
				@QueryParam(value = "flavor") Integer flavor) {
		return mailContentFacade.fakeBuild(mailContentUuid, language, mailConfigUuid, flavor);
	}

	@Path("/build")
	@POST
	@ApiOperation(value = "build a mail content.", response = MailContainerDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public MailContainerDto fakeBuild(
			MailContentDto dto,
			@ApiParam(value = "Language to use for the build")
				@QueryParam(value = "language") String language,
			@ApiParam(value = "Mail config's uuid.")
				@QueryParam(value = "mailConfig") String mailConfigUuid,
			@ApiParam(value = "Mail flavor.")
				@QueryParam(value = "flavor") Integer flavor) {
		return mailContentFacade.fakeBuild(dto, language, mailConfigUuid, flavor);
	}

	@Path("/{uuid}/build/html")
	@GET
	@ApiOperation(value = "build a mail content and return a html file.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public Response fakeBuildHtml(
			@ApiParam(value = "Mail content's uuid.", required = true)
				@PathParam("uuid") String mailContentUuid,
			@ApiParam(value = "Language to use for the build")
				@QueryParam(value = "language") String language,
			@ApiParam(value = "Mail config's uuid.")
				@QueryParam(value = "mailConfig") String mailConfigUuid,
			@ApiParam(value = "build subject instead body")
				@QueryParam(value = "subject") @DefaultValue("false") boolean subject,
			@ApiParam(value = "Mail flavor.")
				@QueryParam(value = "flavor") Integer flavor
			) {
		return mailContentFacade.fakeBuildHtml(mailContentUuid, language, mailConfigUuid, subject, flavor);
	}

	@Path("/{uuid}/vars")
	@GET
	@ApiOperation(value = "Return available variables and their types in the context of the current mail content.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public List<ContextMetadata> getAvailableVariables(
			@ApiParam(value = "Mail content's uuid.", required = true)
				@PathParam("uuid") String mailContentUuid) {
		return mailContentFacade.getAvailableVariables(mailContentUuid);
	}

}

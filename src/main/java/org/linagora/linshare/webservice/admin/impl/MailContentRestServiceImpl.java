/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mail_contents")
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
	@Operation(summary = "Find all mail contents.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailContentDto> findAll(
			@QueryParam(value = "domainId") String domainId,
			@QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain)
			throws BusinessException {
		return mailContentFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mail content.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContentDto find(
			@Parameter(description = "Mail content's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailContentFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mail content.")
	@Override
	public void head(
			@Parameter(description = "Mail content's uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailContentFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a mail content.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContentDto create(
			@Parameter(description = "Mail content to create.", required = true) MailContentDto dto)
			throws BusinessException {
		return mailContentFacade.create(dto);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mail content.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContentDto update(
			@Parameter(description = "Mail content to update.", required = true) MailContentDto dto)
			throws BusinessException {
		return mailContentFacade.update(dto);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete an unused mail content.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContentDto delete(
			@Parameter(description = "Mail content to delete.", required = true) MailContentDto dto)
			throws BusinessException {
		return mailContentFacade.delete(dto.getUuid());
	}

	@Path("/{uuid}/build")
	@GET
	@Operation(summary = "build a mail content.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContainerDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContainerDto fakeBuild(
			@Parameter(description = "Mail content's uuid.", required = true)
				@PathParam("uuid") String mailContentUuid,
			@Parameter(description = "Language to use for the build")
				@QueryParam(value = "language") String language,
			@Parameter(description = "Mail config's uuid.")
				@QueryParam(value = "mailConfig") String mailConfigUuid,
			@Parameter(description = "Mail flavor.")
				@QueryParam(value = "flavor") Integer flavor) {
		return mailContentFacade.fakeBuild(mailContentUuid, language, mailConfigUuid, flavor);
	}

	@Path("/build")
	@POST
	@Operation(summary = "build a mail content.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContainerDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailContainerDto fakeBuild(
			MailContentDto dto,
			@Parameter(description = "Language to use for the build")
				@QueryParam(value = "language") String language,
			@Parameter(description = "Mail config's uuid.")
				@QueryParam(value = "mailConfig") String mailConfigUuid,
			@Parameter(description = "Mail flavor.")
				@QueryParam(value = "flavor") Integer flavor) {
		return mailContentFacade.fakeBuild(dto, language, mailConfigUuid, flavor);
	}

	@Path("/{uuid}/build/html")
	@GET
	@Operation(summary = "build a mail content and return a html file.")
	@Override
	public Response fakeBuildHtml(
			@Parameter(description = "Mail content's uuid.", required = true)
				@PathParam("uuid") String mailContentUuid,
			@Parameter(description = "Language to use for the build")
				@QueryParam(value = "language") String language,
			@Parameter(description = "Mail config's uuid.")
				@QueryParam(value = "mailConfig") String mailConfigUuid,
			@Parameter(description = "build subject instead body")
				@QueryParam(value = "subject") @DefaultValue("false") boolean subject,
			@Parameter(description = "Mail flavor.")
				@QueryParam(value = "flavor") Integer flavor
			) {
		return mailContentFacade.fakeBuildHtml(mailContentUuid, language, mailConfigUuid, subject, flavor);
	}

	@Path("/{uuid}/vars")
	@GET
	@Operation(summary = "Return available variables and their types in the context of the current mail content.")
	@Override
	public List<ContextMetadata> getAvailableVariables(
			@Parameter(description = "Mail content's uuid.", required = true)
				@PathParam("uuid") String mailContentUuid) {
		return mailContentFacade.getAvailableVariables(mailContentUuid);
	}

}

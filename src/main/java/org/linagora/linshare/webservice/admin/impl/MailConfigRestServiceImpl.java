/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailAttachmentFacade;
import org.linagora.linshare.core.facade.webservice.admin.MailConfigFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailConfigDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailConfigRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/mail_configs")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailConfigRestServiceImpl extends WebserviceBase implements
		MailConfigRestService {

	private final MailConfigFacade mailConfigFacade;

	protected final MailAttachmentFacade mailAttachmentFacade;

	public MailConfigRestServiceImpl(final MailConfigFacade mailConfigFacade,
			MailAttachmentFacade mailAttachmentFacade) {
		super();
		this.mailConfigFacade = mailConfigFacade;
		this.mailAttachmentFacade = mailAttachmentFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all mail configurations.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailConfigDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailConfigDto> findAll(@QueryParam(value = "domainId") String domainId,
			@QueryParam("onlyCurrentDomain") @DefaultValue("false") boolean onlyCurrentDomain) throws BusinessException {
		return mailConfigFacade.findAll(domainId, onlyCurrentDomain);
	}

	@Path("/{uuid}/mail_attachments")
	@GET
	@Operation(summary = "Find a mail configuration.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailConfigDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<MailAttachmentDto> findAllMailAttachments(
			@Parameter(description = "Mail configuration's uuid.", required = true)
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return mailAttachmentFacade.findAll(uuid);
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find a mail configuration.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailConfigDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailConfigDto find(
			@Parameter(description = "Mail configuration's uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return mailConfigFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@Operation(summary = "Find a mail configuration.")
	@Override
	public void head(
			@Parameter(description = "Mail configuration's uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		mailConfigFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create a mail configuration.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailConfigDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailConfigDto create(
			@Parameter(description = "Mail configuration to create.", required = true) MailConfigDto dto)
			throws BusinessException {
		return mailConfigFacade.create(dto);
	}

	@Path("/")
	@PUT
	@Operation(summary = "Update a mail configuration.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailConfigDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailConfigDto update(
			@Parameter(description = "Mail configuration to update.", required = true) MailConfigDto dto)
			throws BusinessException {
		return mailConfigFacade.update(dto);
	}

	@Path("/")
	@DELETE
	@Operation(summary = "Delete an unused mail configuration.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailConfigDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public MailConfigDto delete(
			@Parameter(description = "Mail configuration to delete.", required = true) MailConfigDto dto)
			throws BusinessException {
		return mailConfigFacade.delete(dto.getUuid());
	}

	@Path("/{mailConfigUuid}/mail_contents")
	@GET
	@Operation(summary = "Find all mail contents.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailContentDto> findAllContents(
			@Parameter(description = "Mail configuration's uuid.", required = true) @PathParam("mailConfigUuid") String mailConfigUuid,
			@Parameter(description = "Mail content type.", required = true) @QueryParam("mailContentType") String mailContentType)
			throws BusinessException {
		return mailConfigFacade.findAllContents(mailConfigUuid, mailContentType);
	}

	@Path("/{mailConfigUuid}/mail_footers")
	@GET
	@Operation(summary = "Find all mail footers.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = MailContentDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<MailFooterDto> findAllFooters(
			@Parameter(description = "Mail configuration's uuid.", required = true) @PathParam("mailConfigUuid") String mailConfigUuid)
			throws BusinessException {
		return mailConfigFacade.findAllFooters(mailConfigUuid);
	}
}

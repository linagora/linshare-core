/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2019 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2019. Contribute to
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

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailAttachmentFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailAttachmentRestService;
import org.linagora.linshare.webservice.utils.WebServiceUtils;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/mail_attachments")
@Api(value = "/rest/admin/mail_attachments", basePath = "/rest/admin/",
	description = "Mail attachment service.",
	produces = "application/json,application/xml", consumes = "application/json,application/xml")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailAttachmentRestServiceImpl extends WebserviceBase implements MailAttachmentRestService {

	protected final MailAttachmentFacade mailAttachmentFacade;

	protected boolean sizeValidation;

	protected final AccountQuotaFacade accountQuotaFacade;

	protected final long sizeLimit;

	public MailAttachmentRestServiceImpl(
			MailAttachmentFacade mailAttachmentFacade,
			boolean sizeValidation,
			AccountQuotaFacade accountQuotaFacade,
			long sizeLimit) {
		super();
		this.mailAttachmentFacade = mailAttachmentFacade;
		this.sizeValidation = sizeValidation;
		this.accountQuotaFacade = accountQuotaFacade;
		this.sizeLimit = sizeLimit;
	}
	@Path("/")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Create a document which will contain the uploaded file.", response = DocumentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the right role."),
			@ApiResponse(code = 404, message = "Document not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailAttachmentDto create(
				@ApiParam(value = "File stream.", required = true)
					@Multipart(value = "file", required = true) InputStream file,
				@ApiParam(value = "An optional description of an attachment.")
					@Multipart(value = "description", required = false) String description,
				@ApiParam(value = "The given file name of the uploaded attachment.", required = false)
					@Multipart(value = "filename", required = false) String givenFileName,
				@ApiParam(value = "The given metadata of the uploaded file.", required = false)
					@Multipart(value = "metadata", required = false) String metaData,
				@ApiParam(value = "file size (size validation purpose).", required = true)
					@Multipart(value = "filesize", required = true) Long fileSize,
				@ApiParam(value = "True to enable the mail attachment.", required = false)
					@Multipart(value = "enable", required = true) boolean enable,
				@ApiParam(value = "EnableForAll gives the choice to admin to apply the mail attachment for all languages.", required = false)
					@Multipart(value = "enableForAll", required = true) boolean enableForAll,
				@ApiParam(value = "The choosen mail config.", required = false)
					@Multipart(value = "mail_config", required = true) String config,
				@ApiParam(value = "Content id of the mail attachment.", required = false)
					@Multipart(value = "cid", required = false) String cid,
				@ApiParam(value = "Choose the language to apply the mail attachment for.", required = false)
					@Multipart(value = "language", required = false) Language language,
			MultipartBody body) throws BusinessException {
		checkMaintenanceMode();
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check multipart parameter named 'file')");
		}
		String fileName = getFileName(givenFileName, body);
		File tempFile = WebServiceUtils.getTempFile(file, "rest-userv2-document-entries", fileName);
		long currSize = tempFile.length();
		if (sizeValidation) {
			WebServiceUtils.checkSizeValidation(fileSize, currSize);
			if (currSize > sizeLimit) {
				throw new BusinessException(BusinessErrorCode.WEBSERVICE_BAD_REQUEST,
						"Invalid size for mail attachment, max size is 3 Mo");
			}
		}
		try {
			logger.debug("Async mode is not used");
			MailAttachmentDto create = mailAttachmentFacade.create(tempFile, fileName, description, metaData, enable, enableForAll, config, cid, language);
			return create;
		} finally {
			WebServiceUtils.deleteTempFile(tempFile);
		}
	}

	private void checkMaintenanceMode() {
		boolean maintenance = accountQuotaFacade.maintenanceModeIsEnabled();
		if (maintenance) {
			 // HTTP error 501
			throw new BusinessException(
					BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enable, uploads are disabled.");
		}
	}

	@Path("/{uuid: .*}")
	@DELETE
	@ApiOperation(value = "Delete a mail attachment.", response = Response.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the right role."),
			@ApiResponse(code = 404, message = "Mail attachment not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailAttachmentDto delete(
			@ApiParam(value = "Mail attachment uuid to delete.", required = false)
				@PathParam("uuid") String uuid,
			@ApiParam(value = "Mail attachment to delete.", required = false)
				MailAttachmentDto attachment) throws BusinessException {
		return mailAttachmentFacade.delete(uuid, attachment);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Get a mail attachment.", response = MailAttachmentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the right role."),
			@ApiResponse(code = 404, message = "Mail attachment not found."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error."), })
	@Override
	public MailAttachmentDto find(
			@ApiParam(value = "The mail attachment uuid.", required = true) 
				@PathParam("uuid") String uuid) throws BusinessException {
		return mailAttachmentFacade.find(uuid);
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all mail attachment of a domain.", response = MailAttachmentDto.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the right role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public List<MailAttachmentDto> findAll(
			@ApiParam(value = "mail configuration uuid.", required = true)
				@QueryParam("configUuid") String configUuid) throws BusinessException {
		return mailAttachmentFacade.findAll(configUuid);
	}

	@Path("/{uuid: .*}")
	@PUT
	@ApiOperation(value = "Update mail attachment by its uuid.", response = MailAttachmentDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the right role."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 404, message = "Mail attachment has not been found."),
					@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public MailAttachmentDto update(
			@ApiParam(value = "Mail attachment uuid to update.", required = true)
				@PathParam("uuid") String uuid,
			@ApiParam(value = "Mail attachment to update.", required = true)
				MailAttachmentDto attachment) throws BusinessException {
		return mailAttachmentFacade.update(attachment, uuid);
	}

	@Path("/{uuid}/audits")
	@GET
	@ApiOperation(value = "Find all mail attachments audits.", response = MailAttachmentDto.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the right role."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAudits(
		@ApiParam(value = "The mailAttachment uuid.", required = true)
			@PathParam("uuid") String uuid,
		@ApiParam(value = "Filter by type of actions..", required = false)
			@QueryParam("actions") List<LogAction> actions) throws BusinessException {
		return mailAttachmentFacade.findAllAudits(uuid, actions);
	}

	@Path("/audits")
	@GET
	@ApiOperation(value = "Find all mail attachments audits by a choosen domain.", response = MailAttachmentAuditLogEntry.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have required permission."),
					@ApiResponse(code = 400, message = "Bad request : missing required fields."),
					@ApiResponse(code = 500, message = "Internal server error."),
		})
	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAuditsByDomain(
			@ApiParam(value = "Retrieve the mail attachments audits by domain uuid.", required = false)
				@QueryParam("domainUuid") String domainUuid,
			@ApiParam(value = "Filter by type of actions..", required = false)
				@QueryParam("actions") List<LogAction> actions) {
		return mailAttachmentFacade.findAllAuditsByDomain(domainUuid, actions);
	}
}

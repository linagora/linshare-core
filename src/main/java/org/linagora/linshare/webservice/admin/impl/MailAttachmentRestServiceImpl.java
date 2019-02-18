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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailAttachmentFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
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
public class MailAttachmentRestServiceImpl extends WebserviceBase implements MailAttachmentRestService{

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
	@ApiResponses({ @ApiResponse(code = 403, message = "Current logged in account does not have the delegation role."),
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
				@ApiParam(value = "True to override on all mails.", required = false)
					@Multipart(value = "override", required = true) boolean override,
				@ApiParam(value = "The choosen mail config.", required = false)
					@Multipart(value = "mail_config", required = true) String config,
				@ApiParam(value = "True to enable asynchronous upload processing.", required = false)
					@Multipart(value = "alt", required = true) String alt,
				@ApiParam(value = "True to enable asynchronous upload processing.", required = false)
					@Multipart(value = "cid", required = false) String cid,
				@ApiParam(value = "True to enable asynchronous upload processing.", required = false)
					@Multipart(value = "language", required = false) int language,
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
			MailAttachmentDto create = mailAttachmentFacade.create(tempFile, fileName, description, metaData, enable, override, config, alt, cid, language);
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
}

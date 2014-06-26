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
package org.linagora.linshare.webservice.uploadrequest.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.dto.FineUploaderDto;
import org.linagora.linshare.webservice.uploadrequest.FineUploaderRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/fineuploader/upload")
@Api(value = "/rest/uploadrequest/fineuploader/upload", description = "upload_requests API")
@Produces({"application/json", "application/xml"})
public class FineUploaderRestServiceImpl extends WebserviceBase implements
		FineUploaderRestService {

	private static final Logger logger = LoggerFactory
			.getLogger(FineUploaderRestServiceImpl.class);

	private static final String FILE = "qqfile";
	private static final String FILE_NAME = "filename";


	public FineUploaderRestServiceImpl() {
		super();
	}

	@Path("/receiver")
	@ApiOperation(value = "Upload a file in the user space.", response = FineUploaderDto.class)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public FineUploaderDto upload(
			@ApiParam(value = "File stream", required = true)
			@Multipart(value = FILE) InputStream file,
			@ApiParam(value = "File name")
			@Multipart(value = FILE_NAME, required = false) String fileName,
			MultipartBody body) throws BusinessException {

		if (file == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		// Ensure fileName and description aren't null
		if (fileName == null || fileName.isEmpty()) {
			fileName = body.getAttachment(FILE).getContentDisposition()
					.getParameter(FILE_NAME);
		}

		try {
			byte[] bytes = fileName.getBytes("ISO-8859-1");
			fileName = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("Can not encode file name " + e1.getMessage());
		}
		try {
			return new FineUploaderDto(true, UUID.randomUUID().toString());
		} catch (Exception e) {
			return new FineUploaderDto(false);
		}
	}

	@Path("/receiver/{uuid}")
	@ApiOperation(value = "Remove a previously uploaded file by uuid", response = FineUploaderDto.class)
	@DELETE
//	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public FineUploaderDto delete(
			@ApiParam(value = "File uuid", required = true)
			@PathParam("uuid") String uuid)
			throws BusinessException {
		if (uuid == null || uuid.isEmpty()) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		try {
			return new FineUploaderDto(true);
		} catch (Exception e) {
			return new FineUploaderDto(false);
		}
	}
}

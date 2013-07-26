/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.webservice.user.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.dto.DocumentAttachement;
import org.linagora.linshare.webservice.dto.DocumentDto;
import org.linagora.linshare.webservice.dto.SimpleLongValue;
import org.linagora.linshare.webservice.user.DocumentRestService;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentRestServiceImpl extends WebserviceBase implements DocumentRestService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentRestServiceImpl.class);

	private final DocumentFacade webServiceDocumentFacade;

	public DocumentRestServiceImpl(final DocumentFacade webServiceDocumentFacade) {
		this.webServiceDocumentFacade = webServiceDocumentFacade;
	}

	@Path("/{uuid}/download")
	@GET
	@Override
	public Response getDocumentStream(@PathParam("uuid") String uuid) {
		try {
			webServiceDocumentFacade.checkAuthentication();
			DocumentDto documentDto = webServiceDocumentFacade.getDocument(uuid);
			InputStream documentStream = webServiceDocumentFacade.getDocumentStream(uuid);
			ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(documentStream, documentDto.getName(),
					documentDto.getType(), documentDto.getSize());
			return response.build();
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

	@Path("/{uuid}/thumbnail")
	@GET
	@Override
	public Response getThumbnailStream(@PathParam("uuid") String docUuid) {
		try {
			webServiceDocumentFacade.checkAuthentication();
			DocumentDto documentDto = webServiceDocumentFacade.getDocument(docUuid);
			InputStream documentStream = webServiceDocumentFacade.getThumbnailStream(docUuid);
			ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(documentStream, documentDto.getName() + "_thumb.png",
					"image/png");
			return response.build();
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

	/**
	 * get the files of the user
	 */
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<DocumentDto> getDocuments() {
		try {
			webServiceDocumentFacade.checkAuthentication();
			return webServiceDocumentFacade.getDocuments();
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

	/**
	 * upload a file in user's space. send file inside a form
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public DocumentDto uploadfile(@Multipart(value = "file") InputStream theFile,
			@Multipart(value = "description", required = false) String description,
			@Multipart(value = "filename", required = false) String givenFileName, MultipartBody body) {
		try {
			User actor = webServiceDocumentFacade.checkAuthentication();
			String fileName;
			String comment = (description == null) ? "" : description;

			if ((actor instanceof Guest && !actor.getCanUpload())) {
				throw giveRestException(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			}
			if (theFile == null) {
				throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check parameter file)");
			}
			if (givenFileName == null || givenFileName.isEmpty()) {
				// parameter givenFileName is optional
				// so need to search this information in the header of the
				// attachement (with id file)
				fileName = body.getAttachment("file").getContentDisposition().getParameter("filename");
			} else {
				fileName = givenFileName;
			}

			try {
				byte[] bytes = fileName.getBytes("ISO-8859-1");
				fileName = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				logger.error("Can not encode file name " + e1.getMessage());
			}

			return webServiceDocumentFacade.uploadfile(theFile, fileName, comment);
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

	/**
	 * here we use XOP method for large file upload
	 * 
	 * @param doca
	 */

	@POST
	@Path("/xop")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public DocumentDto addDocumentXop(DocumentAttachement doca) {
		try {
			webServiceDocumentFacade.checkAuthentication(); // raise exception
			return webServiceDocumentFacade.addDocumentXop(doca);
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

	@GET
	@Path("/userMaxFileSize")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public SimpleLongValue getUserMaxFileSize() {
		try {
			webServiceDocumentFacade.checkAuthentication();
			return new SimpleLongValue(webServiceDocumentFacade.getUserMaxFileSize());
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

	@GET
	@Path("/availableSize")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public SimpleLongValue getAvailableSize() {
		try {
			webServiceDocumentFacade.checkAuthentication();
			return new SimpleLongValue(webServiceDocumentFacade.getAvailableSize());
		} catch (Exception e) {
			throw analyseFault(e);
		}
	}

}

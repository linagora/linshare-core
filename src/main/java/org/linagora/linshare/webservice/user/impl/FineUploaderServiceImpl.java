package org.linagora.linshare.webservice.user.impl;

import java.io.InputStream;

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
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.dto.DocumentDto;
import org.linagora.linshare.webservice.dto.FineUploaderDto;
import org.linagora.linshare.webservice.user.FineUploaderService;

public class FineUploaderServiceImpl extends WebserviceBase implements
		FineUploaderService {

	private static final String FILE = "qqfile";
	private static final String FILE_NAME = "filename";

	private final DocumentFacade documentFacade;

	public FineUploaderServiceImpl(
			DocumentFacade documentFacade) {
		super();
		this.documentFacade = documentFacade;
	}

	@Path("/receiver")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public FineUploaderDto upload(@Multipart(value = FILE) InputStream file,
			@Multipart(value = FILE_NAME, required = false) String fileName,
			MultipartBody body) {
		User actor = null;

		// Authentication, permission and error checking
		try {
			actor = documentFacade.checkAuthentication();
		} catch (BusinessException e) {
			throw analyseFault(e);
		}
		if (actor instanceof Guest && !actor.getCanUpload()) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN,
					"You are not authorized to use this service");
		}
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
			DocumentDto doc = documentFacade.uploadfile(file, fileName, "");
			return new FineUploaderDto(true, doc.getUuid());
		} catch (BusinessException e) {
			return new FineUploaderDto(false);
		}
	}

	@Path("/receiver/{uuid}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public FineUploaderDto delete(@PathParam("uuid") String uuid) {
		User actor = null;

		// Authentication, permission and error checking
		try {
			actor = documentFacade.checkAuthentication();
		} catch (BusinessException e) {
			throw analyseFault(e);
		}
		if (actor instanceof Guest && !actor.getCanUpload()) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN,
					"You are not authorized to use this service");
		}
		if (uuid == null || uuid.isEmpty()) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		try {
			documentFacade.deleteFile(uuid);
			return new FineUploaderDto(true);
		} catch (BusinessException e) {
			return new FineUploaderDto(false);
		}
	}
}

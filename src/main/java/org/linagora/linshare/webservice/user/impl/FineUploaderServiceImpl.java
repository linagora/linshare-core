package org.linagora.linshare.webservice.user.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.dto.DocumentDto;
import org.linagora.linshare.webservice.dto.FineUploaderDto;
import org.linagora.linshare.webservice.dto.ThreadEntryDto;
import org.linagora.linshare.webservice.user.FineUploaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FineUploaderServiceImpl extends WebserviceBase implements FineUploaderService {

	private static final Logger logger = LoggerFactory.getLogger(FineUploaderServiceImpl.class);

	private static final String FILE = "qqfile";
	private static final String FILE_NAME = "filename";

	private final DocumentFacade documentFacade;

	private final ThreadEntryFacade threadEntryFacade;

	public FineUploaderServiceImpl(DocumentFacade documentFacade, ThreadEntryFacade threadEntryFacade) {
		super();
		this.documentFacade = documentFacade;
		this.threadEntryFacade = threadEntryFacade;
	}

	@Path("/receiver")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public FineUploaderDto upload(@Multipart(value = FILE) InputStream file, @Multipart(value = FILE_NAME, required = false) String fileName,
			MultipartBody body) throws BusinessException {
		User actor = null;

		// Authentication, permission and error checking
		actor = documentFacade.checkAuthentication();
		if (actor instanceof Guest && !actor.getCanUpload()) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
		}
		if (file == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check parameter file)");
		}
		// Ensure fileName and description aren't null
		if (fileName == null || fileName.isEmpty()) {
			fileName = body.getAttachment(FILE).getContentDisposition().getParameter(FILE_NAME);
		}

		try {
			byte[] bytes = fileName.getBytes("ISO-8859-1");
			fileName = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("Can not encode file name " + e1.getMessage());
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
	public FineUploaderDto delete(@PathParam("uuid") String uuid) throws BusinessException {
		User actor = null;

		// Authentication, permission and error checking
		actor = documentFacade.checkAuthentication();
		if (actor instanceof Guest && !actor.getCanUpload()) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
		}
		if (uuid == null || uuid.isEmpty()) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check parameter file)");
		}
		try {
			documentFacade.deleteFile(uuid);
			return new FineUploaderDto(true);
		} catch (BusinessException e) {
			return new FineUploaderDto(false);
		}
	}

	@Path("/threadentry/{threadUuid}")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public FineUploaderDto uploadThreadEntry(@PathParam("threadUuid") String threadUuid, @Multipart(value = FILE) InputStream file,
			@Multipart(value = FILE_NAME, required = false) String fileName, MultipartBody body) throws BusinessException {
		User actor = null;

		// Authentication, permission and error checking
		actor = documentFacade.checkAuthentication();
		if (actor instanceof Guest && !actor.getCanUpload()) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
		}
		if (file == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing file (check parameter file)");
		}
		if (threadUuid == null) {
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing thread Uuid(check parameter threadUuid)");
		}
		// Ensure fileName and description aren't null
		if (fileName == null || fileName.isEmpty()) {
			fileName = body.getAttachment(FILE).getContentDisposition().getParameter(FILE_NAME);
		}

		try {
			byte[] bytes = fileName.getBytes("ISO-8859-1");
			fileName = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.error("Can not encode file name " + e1.getMessage());
		}
		try {
			ThreadEntryDto doc = threadEntryFacade.uploadfile(threadUuid, file, fileName, "");
			return new FineUploaderDto(true, doc.getUuid());
		} catch (BusinessException e) {
			return new FineUploaderDto(false);
		}
	}
}

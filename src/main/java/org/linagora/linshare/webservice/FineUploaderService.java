package org.linagora.linshare.webservice;

import java.io.InputStream;

import javax.ws.rs.Path;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.webservice.dto.DocumentDto;

@Path("/fineuploader/upload")
public interface FineUploaderService {

	/**
	 * Upload method contains logic for a file upload and return the correct
	 * DocumentDto if upload was successful.
	 * 
	 * @param file
	 * @param fileName
	 * @param body
	 * @return
	 */
	public DocumentDto upload(InputStream file, String fileName,
			MultipartBody body);

}

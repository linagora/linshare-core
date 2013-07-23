package org.linagora.linshare.webservice.user;

import java.io.InputStream;

import javax.ws.rs.Path;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.webservice.dto.FineUploaderDto;

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
	public FineUploaderDto upload(InputStream file, String fileName,
			MultipartBody body);

	/**
	 * Delete an existing file.
	 * 
	 * @param file
	 * @param fileName
	 * @param body
	 * @return
	 */
	public FineUploaderDto delete(String uuid);

}

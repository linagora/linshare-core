package org.linagora.linshare.webservice.user;

import java.io.InputStream;

import javax.ws.rs.Path;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
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
	public FineUploaderDto upload(InputStream file, String fileName, MultipartBody body) throws BusinessException;

	/**
	 * Delete an existing file.
	 * 
	 * @param file
	 * @param fileName
	 * @param body
	 * @return
	 */
	public FineUploaderDto delete(String uuid) throws BusinessException;

	/**
	 * Upload method contains logic for a file upload and return the correct
	 * DocumentDto if upload was successful.
	 * 
	 * @param threadUuid
	 *            TODO
	 * @param file
	 * @param fileName
	 * @param body
	 * 
	 * @return
	 */
	public FineUploaderDto uploadThreadEntry(String threadUuid, InputStream file, String fileName, MultipartBody body) throws BusinessException;
}

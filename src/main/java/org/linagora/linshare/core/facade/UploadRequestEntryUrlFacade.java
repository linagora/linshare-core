package org.linagora.linshare.core.facade;

import java.io.InputStream;

import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadRequestEntryUrlFacade {

	boolean exists(String uuid, String urlPath);

	boolean isValid(String alea, String password);

	DocumentVo getDocument(String uploadRequestUrlUuid, String password)
			throws BusinessException;

	boolean isPasswordProtected(String uuid)
			throws BusinessException;

	public InputStream retrieveFileStream(String uploadRequestEntryUrlUuid,
			String UploadRequestEntryUuid, String password)
			throws BusinessException;
}

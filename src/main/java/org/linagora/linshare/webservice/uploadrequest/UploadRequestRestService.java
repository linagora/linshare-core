package org.linagora.linshare.webservice.uploadrequest;


import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.uploadrequest.dto.UploadRequestDto;
public interface UploadRequestRestService {

	Response find(String uuid, String password) throws BusinessException;

	UploadRequestDto create(UploadRequestDto dto) throws BusinessException;

	UploadRequestDto update(UploadRequestDto dto) throws BusinessException;

	void delete(String uuid) throws BusinessException;

	void delete(UploadRequestDto policy) throws BusinessException;
}

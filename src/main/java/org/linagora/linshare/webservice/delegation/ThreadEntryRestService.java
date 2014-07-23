package org.linagora.linshare.webservice.delegation;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadEntryDto;

public interface ThreadEntryRestService {

	ThreadEntryDto create(String ownerUuid, String threadUuid,
			InputStream theFile, String description, String givenFileName,
			MultipartBody body) throws BusinessException;

	public ThreadEntryDto find(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException;

	public List<ThreadEntryDto> findAll(String ownerUuid, String threadUuid)
			throws BusinessException;

	public ThreadEntryDto update(String ownerUuid, String threadUuid,
			ThreadEntryDto threadEntry) throws BusinessException;

	public void delete(String ownerUuid, String threadUuid,
			ThreadEntryDto threadEntry) throws BusinessException;

	public void delete(String ownerUuid, String threadUuid,
			String uuid) throws BusinessException;

	Response download(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException;

	Response thumbnail(String ownerUuid, String threadUuid, String uuid)
			throws BusinessException;

}

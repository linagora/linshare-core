package org.linagora.linshare.core.service;

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;


public interface AnonymousUrlService {
	
	public boolean exists(String uuid, String urlPath);

	public boolean isProtectedByPassword(String uuid) throws LinShareNotSuchElementException;
	
	public boolean isValid(String uuid, String password) throws LinShareNotSuchElementException;
	
	public List<AnonymousShareEntry> getAnonymousShareEntry(String anonymousUrlUuid, String password)  throws LinShareNotSuchElementException;
	
	public InputStream retrieveFileStream(String anonymousUrlUuid, String anonymousShareEntryUuid, String password) throws BusinessException ;
	
	public FileStreamResponse retrieveArchiveZipStream(String anonymousUrlUuid, String password) throws BusinessException ;
	
}

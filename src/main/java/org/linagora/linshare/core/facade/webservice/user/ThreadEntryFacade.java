package org.linagora.linshare.core.facade.webservice.user;

import java.io.InputStream;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadEntryDto;

public interface ThreadEntryFacade extends GenericFacade {
	
	public ThreadEntryDto uploadfile(String threadUuid, InputStream fi, String filename, String description) throws BusinessException;

}

package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.ThreadDto;

public interface WebServiceThreadFacade {

	public User checkAuthentication() throws BusinessException;
	public List<ThreadDto> getAllMyThread() throws BusinessException; 
}

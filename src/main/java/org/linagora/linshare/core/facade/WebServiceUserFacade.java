package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.UserDto;

public interface WebServiceUserFacade {

	public User checkAuthentication() throws BusinessException;
	public List<UserDto> getUsers() throws BusinessException;
	
}

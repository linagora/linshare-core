package org.linagora.linshare.webservice.user;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.UserDto;

public interface GuestRestService {

	UserDto find(String lsUuid) throws BusinessException;
	
	UserDto findAll(String ownerLsUuid) throws BusinessException;
	
	UserDto create(UserDto guest) throws BusinessException;

	UserDto update(UserDto guest) throws BusinessException;

	void delete(UserDto guest) throws BusinessException;

	void delete(String lsUuid) throws BusinessException;

	List<UserDto> getRestrictedContacts(String lsUuid) throws BusinessException;
	
}

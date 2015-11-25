package org.linagora.linshare.core.facade.webservice.user;

import java.util.Set;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;

public interface AutoCompleteFacade {

	Set<UserDto> findUser(String pattern)
			throws BusinessException;

	Set<String> getMail(String pattern)
			throws BusinessException;
}

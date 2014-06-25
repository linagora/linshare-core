package org.linagora.linshare.core.facade.webservice.delegation;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.PasswordDto;

public interface UserFacade {

	void changePassword(PasswordDto password) throws BusinessException;

}

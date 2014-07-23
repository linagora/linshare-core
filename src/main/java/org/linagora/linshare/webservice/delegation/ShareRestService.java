package org.linagora.linshare.webservice.delegation;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.webservice.delegation.dto.ShareDto;

public interface ShareRestService {

	ShareDto create(String ownerUuid, ShareCreationDto createDto)
			throws BusinessException;

}

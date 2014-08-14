package org.linagora.linshare.core.facade.webservice.delegation;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.delegation.dto.DocumentDto;

public interface DocumentFacade extends DelegationGenericFacade {

	List<DocumentDto> getAll(String ownerUuid) throws BusinessException;

}
